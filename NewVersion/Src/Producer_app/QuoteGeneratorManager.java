import akka.actor.ActorSystem;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.kafka.ProducerSettings;
import com.typesafe.config.Config;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuoteGeneratorManager extends AbstractBehavior<QuoteGeneratorManager.command> {
    public interface command extends Serializable {}
    public static class MessageCommand implements command {
        private ArrayList<String> StockNames;
        private ArrayList<Double>InitialPrice;
        private ArrayList<ArrayList<Integer>> PeakSettings;
        private ArrayList<Integer> Partations;
        private String TopicName;
        private String ServerName;
        public MessageCommand(ArrayList<String> StockNames,ArrayList<Double> InitialPrice,ArrayList<ArrayList<Integer>> PeakSettings,ArrayList<Integer> Partations,String TopicName,String ServerName){
            this.StockNames = StockNames;
            this.InitialPrice = InitialPrice;
            this.PeakSettings = PeakSettings;
            this.Partations = Partations;
            this.TopicName = TopicName;
            this.ServerName = ServerName;
        }
        public ArrayList<String> getStockNames(){
            return StockNames;
        }
        public ArrayList<Double> getInitialPrice(){
            return InitialPrice;
        }
        public ArrayList<ArrayList<Integer>> getPeakSettings(){
            return PeakSettings;
        }
        public ArrayList<Integer> getPartations(){
            return Partations;
        }
        public String getTopicName(){
            return TopicName;
        }
        public String getServerName(){
            return ServerName;
        }
    }
    public static class ResultCommand implements command{
        private ActorRef<QuoteGeneratorWorkers.command> Worker;
        private Double Price;
        public ResultCommand(ActorRef<QuoteGeneratorWorkers.command> Worker,Double Price){
            this.Worker = Worker;
            this.Price = Price;
        }
        public ActorRef<QuoteGeneratorWorkers.command> getWorker(){
            return Worker;
        }
        public Double getPrice(){
            return Price;
        }
    }
    public static class TimerAlert implements command{}
    private QuoteGeneratorManager(ActorContext<command> context) {
        super(context);
    }
    public static Behavior<command> Setupt(){
        return Behaviors.setup(QuoteGeneratorManager::new);
    }
    Object Timer1 = new Object();
    @Override
    public Receive<command> createReceive() {
        return StartGenrate();
    }
    public Receive<command> StartGenrate() {
        return newReceiveBuilder()
                .onMessage(MessageCommand.class,command->{
                    ActorSystem system = ActorSystem.create();
                    Config config = system.settings().config().getConfig("akka.kafka.producer");
                    ProducerSettings<String, String> producerSettings = ProducerSettings.create(config, new StringSerializer(), new StringSerializer()).withBootstrapServers(command.getServerName());
                    org.apache.kafka.clients.producer.Producer<String, String> kafkaProducer = producerSettings.createKafkaProducer();
                    kafkaProducer.close();
                    Map<ActorRef<QuoteGeneratorWorkers.command>,Producer> array = new HashMap<>();
                    for (int i=0;i<command.StockNames.size();i++){
                        ActorRef<QuoteGeneratorWorkers.command> Worker = getContext().spawn(QuoteGeneratorWorkers.Setup(),command.StockNames.get(i));
                        Producer producer = new Producer(command.getTopicName(),command.getPartations().get(i),system,producerSettings, command.getStockNames().get(i));
                        array.put(Worker,producer);
                        Worker.tell(new QuoteGeneratorWorkers.initialActor(command.getPeakSettings().get(i),command.getInitialPrice().get(i)));
                    }
                    return Behaviors.withTimers(timers->{
                        timers.startTimerAtFixedRate(Timer1,new TimerAlert(), Duration.ofSeconds(60));
                        return GentarorReciving(array);
                    });
                })
                .build();
    }
    public Receive<command> GentarorReciving(Map<ActorRef<QuoteGeneratorWorkers.command>,Producer> Workers) {
        return newReceiveBuilder()
                .onMessage(TimerAlert.class,command->{
                    for ( ActorRef<QuoteGeneratorWorkers.command> key : Workers.keySet() ) {
                        key.tell(new QuoteGeneratorWorkers.Messges("genrate",getContext().getSelf()));
                    }
                    return Behaviors.same();
                })
                .onMessage(ResultCommand.class,command->{
                    //System.out.println("Stock Names is:"+Workers.get(command.getWorker()));
                    //System.out.println(command.Price);
                    Workers.get(command.getWorker()).send(command.Price);
                    return Behaviors.same();
                })
                .build();
    }
}
