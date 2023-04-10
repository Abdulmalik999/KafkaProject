import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class QuoteGeneratorWorkers extends AbstractBehavior<QuoteGeneratorWorkers.command> {
    double price = 0;
    ArrayList<Integer> PeakSetting = new ArrayList<>();
    int Period = 0;
    public interface command extends Serializable{}
    public static class initialActor implements command{
        private ArrayList<Integer> PeakSetting;
        private Double InitialPrice;
        public initialActor(ArrayList<Integer> PeakSetting,Double InitialPrice){
            this.PeakSetting = PeakSetting;
            this.InitialPrice = InitialPrice;
        }
        public ArrayList<Integer> getPeakSetting(){
            return PeakSetting;
        }
        public Double getInitialPrice(){
            return InitialPrice;
        }
    }
    public static class Messges implements command{
        private String Message;
        private ActorRef<QuoteGeneratorManager.command> parent;
        public Messges(String Message,ActorRef<QuoteGeneratorManager.command> ref){
            this.Message = Message;
            this.parent = ref;
        }
        public String getMessage(){
            return Message;
        }
        public ActorRef<QuoteGeneratorManager.command> getParent(){
            return parent;
        }
    }
    private QuoteGeneratorWorkers(ActorContext<QuoteGeneratorWorkers.command> context) {
        super(context);
    }
    public static Behavior<command> Setup(){
        return Behaviors.setup(QuoteGeneratorWorkers::new);
    }
    double GreenPeakGenrator(){
        double var = new Random().nextDouble();
        var = 0.05 + (0.05 - 0.01) * var;
        return (price+var*price);
    }
    double RedPeakGenrator(){
        double var = new Random().nextDouble();
        var = 0.05 + (0.05 - 0.01) * var;
        return (price-var*price);
    }
    double WhitePeakGenrator(){
        return (price);
    }
    @Override
    public Receive<QuoteGeneratorWorkers.command> createReceive() {
        return initialActor();
    }
    public Receive<QuoteGeneratorWorkers.command> initialActor(){
        return newReceiveBuilder()
                .onMessage(initialActor.class,command->{
                    PeakSetting = command.getPeakSetting();
                    price = command.getInitialPrice();
                    return GenratePrice();
                })
                .build();
    }
    public Receive<QuoteGeneratorWorkers.command> GenratePrice(){
        return newReceiveBuilder()
                .onMessage(Messges.class,command->{
                    if (command.getMessage().equals("genrate")){
                             Period++;
                             switch (PeakSetting.get(Period-1)){
                                 case 1:{
                                     price = GreenPeakGenrator();
                                     break;
                                 }
                                 case -1:{
                                     price = RedPeakGenrator();
                                     break;
                                 }
                                 default:{
                                     price = WhitePeakGenrator();
                                     break;
                                 }
                             }
                             command.getParent().tell(new QuoteGeneratorManager.ResultCommand(getContext().getSelf(),price));
                             if (Period==PeakSetting.size()){
                                 Period = 0;
                             }

                    }
                    return Behaviors.same();
                })
                .build();
    }
}
