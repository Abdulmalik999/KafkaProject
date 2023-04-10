package org.example;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TradeManager extends AbstractBehavior<TradeManager.Stocks>{
	interface Stocks{};
	public static class StocksPriceReciver implements Stocks{
		String StockName;
		String Value;
		public StocksPriceReciver(String StockName,String Value) {
			// TODO Auto-generated constructor stub
			this.StockName = StockName;
			this.Value = Value;
		}
		public String GetStockName() {
			return StockName;
		}
		public String GetValue() {
			return Value;
		}
	}
	public static class CreateTrade implements Stocks{
		ArrayList<String> StockNames;
		double Budget;
		public CreateTrade(ArrayList<String> StockNames,double Budget) {
			this.StockNames = StockNames;
			this.Budget = Budget;
		}
		public ArrayList<String> getStockNames(){
			return StockNames;
		}
		public double getBudget() {
			return Budget;
		}
	}
	public static class DecisionReciver implements Stocks {
		int Decision;
		double Price;
		ActorRef<TradeWorker.Stock> worker;
		public DecisionReciver(int Decision,double Price,ActorRef<TradeWorker.Stock> worker) {
			this.Decision = Decision;
			this.Price = Price;
			this.worker = worker;
		}
		public int getDecision() {
			return Decision;
		}
		public double getPrice() {
			return Price;
		}
		public ActorRef<TradeWorker.Stock> getWorker(){
			return worker;
		}
	}
	private TradeManager(ActorContext<Stocks> context){
		super(context);
	}
	public static Behavior<Stocks> create(){
		return Behaviors.setup(context->new TradeManager(context));
	}	
	TradeDB TDB = new TradeDB();
	Map<String,ActorRef<TradeWorker.Stock>> Workers = new HashMap<>();
	double CurentBudget = TDB.getBudget();
	ActorRef<Audit.Stock> AuditActor;
	@Override
	public Receive<Stocks> createReceive() {
		return newReceiveBuilder()
				.onMessage(CreateTrade.class, command->{
					for(int i=0;i<command.getStockNames().size();i++){
						String StockName = command.getStockNames().get(i);
						ActorRef<TradeWorker.Stock> worker = getContext().spawn(TradeWorker.create(),StockName);
						Workers.put(StockName,worker);
						if(TDB.getStockCount(StockName)==-1){
							TDB.AddNewStock(StockName);
						}
					}
					if(CurentBudget==0) {
						CurentBudget = command.getBudget();
						TDB.UpdateBudget(CurentBudget);
					}
					AuditActor = getContext().spawn(Audit.create(), "org.example.Audit");
					return PriceReciver();
				})
				.build();
	}
	public Receive<Stocks> PriceReciver() {
		return newReceiveBuilder()
				.onMessage(StocksPriceReciver.class,this::onMessage)
				.onMessage(DecisionReciver.class,this::onReciveDecision)
				.build();
	}
	private Behavior<Stocks> onMessage(StocksPriceReciver M){
		try{
			Workers.get(M.GetStockName()).tell(new TradeWorker.StockMarket(getContext().getSelf(), M.GetValue()));
		}
		catch(Exception ex){}
		return Behaviors.same();
	}
	private Behavior<Stocks> onReciveDecision(DecisionReciver D){
		if(D.getDecision()==-1) {
			// sell
			int countWillSell = TDB.getStockCount(D.getWorker().path().name());
			CurentBudget += D.getPrice()*countWillSell;
			TDB.UpdateStock(D.getWorker().path().name(), 0);
			// tell org.example.Audit to write log
			AuditActor.tell(new Audit.Stock(D.getWorker().path().name(), -1, D.getPrice()));
		}
		else {
			// buy
			int countWillBuy = (int)(CurentBudget/D.getPrice())/2;
			int myStock = TDB.getStockCount(D.getWorker().path().name());
			CurentBudget -= D.getPrice()*countWillBuy;
			TDB.UpdateStock(D.getWorker().path().name(), (myStock+countWillBuy));
			AuditActor.tell(new Audit.Stock(D.getWorker().path().name(), 1, D.getPrice()));
		}
		TDB.UpdateBudget(CurentBudget);
		System.out.println("Current Budget:"+CurentBudget);
		return Behaviors.same();
	}
}
