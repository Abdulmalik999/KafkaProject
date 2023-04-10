import akka.actor.typed.ActorSystem;

import java.util.ArrayList;
import java.util.Arrays;
public class Main {

    public static void main(String[] args) {
        String TopicName;
        String ServerName;
        ArrayList<String> StockNames = new ArrayList<>();
        ArrayList<Double> InitialPrice = new ArrayList<>();
        ArrayList<ArrayList<Integer>> PeakSettings = new ArrayList<>();
        ArrayList<Integer> Partations = new ArrayList<>();

        // Kafka settings
        TopicName = "x";
        ServerName = "localhost:9092";
        /////////////////

        // stocks details
        StockNames.add("OthaimMarket");
        InitialPrice.add(1000.0);
        PeakSettings.add(new ArrayList<Integer>(Arrays.asList(1,-1,1,0,1,-1,0)));
        Partations.add(0);

        StockNames.add("AlrijhiBanak");
        InitialPrice.add(30.0);
        PeakSettings.add(new ArrayList<Integer>(Arrays.asList(1,-1,1,0,1,-1,0)));
        Partations.add(1);

        StockNames.add("ACC");
        InitialPrice.add(70.0);
        PeakSettings.add(new ArrayList<Integer>(Arrays.asList(1,-1,1,0,1,-1,0)));
        Partations.add(2);

        StockNames.add("ACWAPower");
        InitialPrice.add(40.0);
        PeakSettings.add(new ArrayList<Integer>(Arrays.asList(1,-1,1,0,1,-1,0)));
        Partations.add(3);

        StockNames.add("Advanced");
        InitialPrice.add(30.0);
        PeakSettings.add(new ArrayList<Integer>(Arrays.asList(1,-1,1,0,1,-1,0)));
        Partations.add(4);

        ////////////////

        ActorSystem Actor = ActorSystem.create(QuoteGeneratorManager.Setupt(),"QuoteGenerator");
        Actor.tell(new QuoteGeneratorManager.MessageCommand(StockNames,InitialPrice,PeakSettings,Partations,TopicName,ServerName));
    }
}
