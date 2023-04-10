package org.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;

public class TradeWorker extends AbstractBehavior<TradeWorker.Stock> {
	public interface Stock{};
	public static class StockMarket implements Stock{
		private ActorRef<TradeManager.Stocks> Manager;
		private String Value;
		public StockMarket(ActorRef<TradeManager.Stocks> Manager, String Value) {
			this.Manager = Manager;
			this.Value = Value;
		}
		public ActorRef<TradeManager.Stocks> getManager() {
			return Manager;
		}
		public double GetValue() {
			return (Double.parseDouble(Value));
		}
	}
	private TradeWorker(ActorContext<Stock> context){
		super(context);
	}
	public static Behavior<Stock> create(){
		return Behaviors.setup(context->new TradeWorker(context));
	}

	@Override
	public Receive<Stock> createReceive() {
		// TODO Auto-generated method stub
		return newReceiveBuilder()
				.onMessage(StockMarket.class,M->{
					//System.out.println("Current:"+M.GetValue()+" for: "+M.GetStockName());
					if(StockBehivour.size()==0 && Previous==-1) {
						Previous = M.GetValue();
					}
					else {
						ReadStockSignal(M);
					}
					if(StockBehivour.size()>6) {
						int x = StockBehivour.get(0);
						StockBehivour.remove(0);
						if(x==1) {
							M.getManager().tell(new TradeManager.DecisionReciver(1, M.GetValue(), getContext().getSelf()));
							//System.out.println("will Increase");
						}
						else if(x==-1){
							M.getManager().tell(new TradeManager.DecisionReciver(-1, M.GetValue(), getContext().getSelf()));
							//System.out.println("will be decrase");
						}
					}
					return this;
				}) //// String Message
				.build();
	}
	double Previous = -1;
	ArrayList<Integer> StockBehivour = new ArrayList<>();
	private void ReadStockSignal(StockMarket M) {
		if(M.GetValue()>Previous) {
			StockBehivour.add(1);
		}
		else if(M.GetValue()==Previous) {
			StockBehivour.add(0);
		}
		else {
			StockBehivour.add(-1);
		}
		Previous = M.GetValue();
	}
}
