import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ProducerMessage;
import akka.kafka.ProducerSettings;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class Producer {
    private String topic,StockName;
    private int partition;
    private ActorSystem system;
    private ProducerSettings<String, String> producerSettings;
    public Producer(String topic,int partition,ActorSystem system,ProducerSettings<String, String> producerSettings, String StockName) {
        this.topic = topic;
        this.partition = partition;
        this.system = system;
        this.producerSettings = producerSettings;
        this.StockName = StockName;
    }
    public void send(double Message) {
        CompletionStage<Done> done =
                Source.range(0, 0)
                        .map(
                                number -> {
                                    String value = String.valueOf(Message);
                                    ProducerMessage.Envelope<String, String, Integer> msg =
                                            ProducerMessage.single(
                                                    new ProducerRecord<>(topic, partition, StockName, value), number);
                                    return msg;
                                })
                        .via(akka.kafka.javadsl.Producer.flexiFlow(producerSettings))
                        .map(
                                result -> {
                                    if (result instanceof ProducerMessage.Result) {
                                        ProducerMessage.Result<String, String, Integer> res =
                                                (ProducerMessage.Result<String, String, Integer>) result;
                                        ProducerRecord<String, String> record = res.message().record();
                                        RecordMetadata meta = res.metadata();
                                        return meta.topic()
                                                + "/"
                                                + meta.partition()
                                                + " "
                                                + res.offset()
                                                + ": "
                                                + record.value();
                                    } else if (result instanceof ProducerMessage.MultiResult) {
                                        ProducerMessage.MultiResult<String, String, Integer> res =
                                                (ProducerMessage.MultiResult<String, String, Integer>) result;
                                        return res.getParts().stream()
                                                .map(
                                                        part -> {
                                                            RecordMetadata meta = part.metadata();
                                                            return meta.topic()
                                                                    + "/"
                                                                    + meta.partition()
                                                                    + " "
                                                                    + part.metadata().offset()
                                                                    + ": "
                                                                    + part.record().value();
                                                        })
                                                .reduce((acc, s) -> acc + ", " + s);
                                    } else {
                                        return "passed through";
                                    }
                                })
                        .runWith(Sink.foreach(System.out::println), system);
    }
}
