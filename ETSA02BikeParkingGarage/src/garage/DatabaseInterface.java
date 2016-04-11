package garage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseInterface {
	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;
	private static String url = "jdbc:mysql://localhost/bicycleGarage?";
	private static String user = "operator";
	private static String password = "ParkaCykel";

	public static int put(String name, String id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(
					"select * from users where id = '" + id + "' and name = '" + name + "'");
			if (resultSet.next()) {
				connect.close();
				return 0;
			} else {
				Random rand = new Random();
				int pin = rand.nextInt(1000000);
				boolean ok = false;
				resultSet = statement.executeQuery("select * from users where pin = '" + pin + "'");
				while (!ok) {
					if (resultSet.next()) {
						pin = rand.nextInt(1000000);
						resultSet = statement.executeQuery("select * from users where pin = '" + pin + "'");
					} else {
						ok = true;
					}
				}
				preparedStatement = connect.prepareStatement("insert into bicycleGarage.users values (?, ?, ?)");
				preparedStatement.setString(1, id);
				preparedStatement.setString(2, name);
				preparedStatement.setInt(3, pin);
				preparedStatement.executeUpdate();
				connect.close();
				return pin;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	public static int addBike(String name, String id, String frameNbr) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(
					"select * from users where id = '" + id + "' and name = '" + name + "'");
			if (!resultSet.next()) {
				connect.close();
				return 2;
			}
			
			resultSet = statement.executeQuery("select * from bikes where framenbr = '" + frameNbr + "' and owner = '" + id + "'");
			if (resultSet.next()) {
				connect.close();
				return 0;
			}
			resultSet = statement.executeQuery("select * from bikes where framenbr = '" + frameNbr + "'");
			if (resultSet.next()) {
				connect.close();
				return 3;
			}
			resultSet = statement.executeQuery("select * from bikes where owner = '" + id + "'");
			if (resultSet.next()) {
				resultSet.last();
				if (resultSet.getRow() == 3) {
					connect.close();
					return 1;
				}
			}
			Random rand = new Random();
			int barcode = rand.nextInt(100000);
			boolean ok = false;
			resultSet = statement.executeQuery("select * from bikes where barcode = '" + barcode + "'");
			while (!ok) {
				if (resultSet.next()) {
					barcode = rand.nextInt(100000);
					resultSet = statement.executeQuery("select * from bikes where barcode = '" + barcode + "'");
				} else {
					ok = true;
				}
			}
			preparedStatement = connect.prepareStatement("insert into  bicycleGarage.bikes values (?, ?, ?, ?)");
			preparedStatement.setInt(1, barcode);
			preparedStatement.setString(2, id);
			preparedStatement.setString(3, frameNbr);
			preparedStatement.setInt(4, 0);
			preparedStatement.executeUpdate();
			connect.close();
			return 4;
		} catch (Exception e) {
			return -1;
		}
	}

	public static int removeUser(String name, String id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(
					"select * from users where id = '" + id + "' and name = '" + name + "'");
			if (!resultSet.next()) {
				connect.close();
				return 0;
			}
			statement.executeUpdate(
					"delete from users where id = '" + id + "' and name = '" + name + "'");
			statement.executeUpdate("delete from bikes where owner = '" + id + "'");
			connect.close();
			return 1;
		} catch (Exception e) {
			return -1;
		}
	}

	public static int removeBike(String name, String id, String frameNbr) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(
					"select * from users where id = '" + id + "' and name = '" + name + "'");
			if (!resultSet.next()) {
				connect.close();
				return 1;
			}
			resultSet = statement.executeQuery("select * from bikes where framenbr = '" + frameNbr + "' and owner = '"
					+ id + "'");
			if (!resultSet.next()) {
				connect.close();
				return 0;
			}
			statement.executeUpdate("delete from bikes where framenbr = '" + frameNbr + "'");
			connect.close();
			return 2;
		} catch (Exception e) {
			return -1;
		}
	}

	public static Set<String> getBikes(String name, String id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(
					"select * from users where id = '" + id + "' and name = '" + name + "'");
			if (!resultSet.next()) {
				connect.close();
				return null;
			}
			resultSet = statement.executeQuery("select * from bikes where owner = '" + id + "'");
			Set<String> bikes = new HashSet<String>();
			resultSet.next();
			while (!resultSet.isLast()) {
				bikes.add(resultSet.getString(3));
				resultSet.next();
			}
			bikes.add(resultSet.getString(3));
			connect.close();
			return bikes;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String[] findOwner(String frameNbr) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(
					"select * from bikes where framenbr = '" + frameNbr + "'");
			if (!resultSet.next()) {
				connect.close();
				return null;
			}
			String id = resultSet.getString(2);
			resultSet = statement.executeQuery(
					"select * from users where id = '" + id + "'");
			String[] owner = new String[2];
			resultSet.next();
			owner[0] = resultSet.getString(2);
			owner[1] = resultSet.getString(1);
			connect.close();
			return owner;
		} catch (Exception e) {
			return null;
		}
	}

	public static String findBarcode(String frameNbr) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(
					"select * from bikes where framenbr = '" + frameNbr + "'");
			if (!resultSet.next()) {
				connect.close();
				return null;
			}
			String barcode = resultSet.getString(1);
			connect.close();
			return barcode;
		} catch (Exception e) {
			return null;
		}
	}

	public static String findPincode(String name, String id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(
					"select * from users where name = '" + name + "' and id = '" + id + "'");
			if (!resultSet.next()) {
				connect.close();
				return null;
			}
			StringBuilder sb = new StringBuilder();
			int pin = resultSet.getInt(3);
			int count = pin;
			while (count < 100000) {
				sb.append(0);
				count *= 10;
			}
			sb.append(pin);
			connect.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getAllUsers() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			resultSet = statement.executeQuery(
					"select * from users");
			if (!resultSet.next()) {
				connect.close();
				return null;
			}
			StringBuilder sb = new StringBuilder();
			while(!resultSet.isLast()) {
				
			}
			sb.append("\n" + resultSet.getString(2) + "\t" + resultSet.getString(1) + "\t");
			int pin = resultSet.getInt(3);
			int count = pin;
			while (count < 100000) {
				sb.append(0);
				count *= 10;
			}
			sb.append(pin + "\n");
			connect.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getUserLog(String name, String id, Timestamp timeStart, Timestamp timeEnd) {
		// TODO Hämta användarloggen mellan två tidpunkter
		return null;
	}
	
	public static String getBikeLog(String frameNbr, Timestamp timeStart, Timestamp timeEnd) {
		//TODO
		return null;
	}
	
	public static boolean checkPin(int pin) {
		// TODO tänkt som metod för att kolla pinkod vid inträde
		return false;
	}
}