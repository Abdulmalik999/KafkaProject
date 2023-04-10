package org.example;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

public class Audit extends AbstractBehavior<Audit.Stock> {
	public static class Stock{
		String StockName;
		int OpreationType;
		double price;
		public Stock(String StockName,int OpreationType,double price) {
			this.StockName = StockName;
			this.OpreationType = OpreationType;
			this.price = price;
		}
		public String getStockName() {
			return StockName;
		}
		public int getOpreationType() {
			return OpreationType;
		}
		public double getPrice() {
			return price;
		}
	}
	private Audit(ActorContext<Stock> context){
		super(context);
	}
	public static Behavior<Stock> create(){
		return Behaviors.setup(context->new Audit(context));
	}	
	TradeDB TDB = new TradeDB();
	@Override
	public akka.actor.typed.javadsl.Receive<Stock> createReceive() {
		return newReceiveBuilder()
		.onMessage(Stock.class, command->{
			TDB.WriteNewOpreation(command.getStockName(), command.getOpreationType(), command.getPrice());
			return Behaviors.same();
		})
		.build();
	}
}
