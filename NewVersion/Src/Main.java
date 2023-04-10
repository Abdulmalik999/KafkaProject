package org.example;

import akka.actor.typed.ActorSystem;

import java.util.ArrayList;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        ArrayList<String> Stocks = new ArrayList<>();
        double budget = 10000;
        Stocks.add("OthaimMarket");
        Stocks.add("AlrijhiBanak");
        Stocks.add("ACC");
        Stocks.add("ACWAPower");
        Stocks.add("Advanced");
        Consumer c = new Consumer("x", "Consumer2");
        ActorSystem<TradeManager.Stocks> TreaderSystem = ActorSystem.create(TradeManager.create(),"radeManager");
        TreaderSystem.tell(new TradeManager.CreateTrade(Stocks, budget));
        while (true){
            ArrayList<TradeManager.StocksPriceReciver> value =  c.Consume();
            for(int i=0;i<value.size();i++) {
                TreaderSystem.tell(value.get(i));
            }
        }
		/*
		org.example.Consumer c1 = new org.example.Consumer("x", 0, "ConsumerGroup3");
		org.example.Consumer c2 = new org.example.Consumer("x", 1, "ConsumerGroup3");
		org.example.Consumer c3 = new org.example.Consumer("x", 2, "ConsumerGroup3");
		org.example.Consumer c4 = new org.example.Consumer("x", 3, "ConsumerGroup3");
		org.example.Consumer c5 = new org.example.Consumer("x", 4, "ConsumerGroup3");
		// TODO Auto-generated method stub
		ActorSystem<Traders.StockMarket> TreaderSystem = ActorSystem.create(Traders.create(),"TreaderActor");
		while(true){
			ArrayList<Traders.StockMarket> value =  c1.Consume();
			for(int i=0;i<value.size();i++) {
				TreaderSystem.tell(value.get(i));
			}
			value =  c2.Consume();
			for(int i=0;i<value.size();i++) {
				TreaderSystem.tell(value.get(i));
			}
			value =  c3.Consume();
			for(int i=0;i<value.size();i++) {
				TreaderSystem.tell(value.get(i));
			}
			value =  c4.Consume();
			for(int i=0;i<value.size();i++) {
				TreaderSystem.tell(value.get(i));
			}
			value =  c5.Consume();
			for(int i=0;i<value.size();i++) {
				TreaderSystem.tell(value.get(i));
			}
		}
		*/
    }

}
