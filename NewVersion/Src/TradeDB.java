package org.example;

import java.sql.*;

public class TradeDB {
	 String myDriver = "com.mysql.cj.jdbc.Driver";
	 String myUrl = "jdbc:mysql://localhost/StockMarketProject?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	 
	public double getBudget() {
		 try {
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "");
			  String sql = " select * from Settings where CKey=?";
			  PreparedStatement preparedStmt = conn.prepareStatement(sql);
			  preparedStmt.setString(1, "Budget");
			  ResultSet rs = preparedStmt.executeQuery();
			  rs.next();
			  String value = rs.getString("CValue");
			  conn.close();
			  return Double.parseDouble(value);
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	public boolean UpdateBudget(double budget) {
		 try {
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "");
			  String sql = " update Settings set CValue=? where CKey=?";
			  PreparedStatement preparedStmt = conn.prepareStatement(sql);
			  preparedStmt.setString(1, Double.toString(budget));
			  preparedStmt.setString(2, "Budget");
			  preparedStmt.execute();
			  conn.close();
			  return true;
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public boolean AddNewStock(String StockName) {
		try{
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "");
			  String sql = " insert into Mywallet (stockName, count)" + " values (?,?)";
			  PreparedStatement preparedStmt = conn.prepareStatement(sql);
			  preparedStmt.setString (1, StockName);
			  preparedStmt.setInt (2, 0);
			  preparedStmt.execute();
			  conn.close();
			  return true;
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public boolean UpdateStock(String StockName,int count) {
		 try {
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "");
			  String sql = " update Mywallet set count=? where stockName=?";
			  PreparedStatement preparedStmt = conn.prepareStatement(sql);
			  preparedStmt.setInt(1, count);
			  preparedStmt.setString(2, StockName);
			  preparedStmt.execute();
			  conn.close();
			  return true;
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public int getStockCount(String StockName) {
		 try {
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "");
			  String sql = " select * from Mywallet where stockName=?";
			  PreparedStatement preparedStmt = conn.prepareStatement(sql);
			  preparedStmt.setString(1, StockName);
			  ResultSet rs = preparedStmt.executeQuery();
			  rs.next();
			  int value = -1;
			  try{
				  value = rs.getInt("count");
			  }
			  catch(Exception ex){}
			  conn.close();
			  return value;
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}
	public boolean WriteNewOpreation(String StockName,int OpreationType,double price) {
		// sell -> -1
		// buy -> 1
		try {
			Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "");
			  String sql = " insert into OpreationLog (StockName, type, price)" + " values (?, ?, ?)";
			  PreparedStatement preparedStmt = conn.prepareStatement(sql);
			  preparedStmt.setString (1, StockName);
			  preparedStmt.setInt (2, OpreationType);
			  preparedStmt.setDouble(3, price);
			  preparedStmt.execute();
			  conn.close();
			  return true;
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
