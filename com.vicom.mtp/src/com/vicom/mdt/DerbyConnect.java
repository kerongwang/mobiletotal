package com.vicom.mdt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class DerbyConnect {
	private static boolean databaseStarted = false;

	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private static final String protocol = "jdbc:derby:";

	private static Connection connection = null;
	
	public static Connection getConnection(){
		if( connection == null){
			try{
				connection = CreateConnection();
			}catch(SQLException sqlE){
				System.out.println("Can't create connection to database.");
			}
		} else {
			try{
				if( connection.isClosed()){
					connection = CreateConnection();
				}
			}catch(SQLException sqlE){
				System.out.println("Can't create connection to database.");
			}
		}
		return connection;
	}
	
	private static Connection CreateConnection() throws SQLException {
		if( !databaseStarted ) return null;
		Connection conn = null;
		Properties props = new Properties();
		props.put("user", "user1");
		props.put("password", "user1");

		/*
		 The connection specifies create=true to cause
		 the database to be created. To remove the database,
		 remove the directory derbyDB and its contents.
		 The directory derbyDB will be created under
		 the directory that the system property
		 derby.system.home points to, or the current
		 directory if derby.system.home is not set.
		 */
		conn = DriverManager.getConnection(protocol + "derbyDB;create=true", props);
		System.out.println("Connected to and created database derbyDB");
		conn.setAutoCommit(true);

		return conn;
	}
	
	public synchronized static boolean startupDerby(){
		if( databaseStarted ) return true;
		try {
			/*
			 The driver is installed by loading its class.
			 In an embedded environment, this will start up Derby, since it is not already running.
			 */
			Class.forName(driver).newInstance();
			System.out.println("Loaded the appropriate driver.");
			databaseStarted = true;
		} catch (Throwable e) {
			System.out.println("exception thrown:");

			if (e instanceof SQLException) {
				printSQLError((SQLException) e);
			} else {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public synchronized static void shutdownDerby(){
		if (!databaseStarted) return;
		
		/*
		 In embedded mode, an application should shut down Derby.
		 If the application fails to shut down Derby explicitly,
		 the Derby does not perform a checkpoint when the JVM shuts down, which means
		 that the next connection will be slower.
		 Explicitly shutting down Derby with the URL is preferred.
		 This style of shutdown will always throw an "exception".
		 */
		try {
			getConnection().close();
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
			databaseStarted = false;
			System.out.println("Database did not shut down normally");
		} catch (SQLException se) {
			System.out.println("Database shut down normally");
		}
	}
	
	static void testDerby() {
		if( !databaseStarted ) return;
		Statement s = null;
		Connection conn = null;
		try{
			 conn = getConnection();
			/*
			 Creating a statement lets us issue commands against
			 the connection.
			 */
			s = conn.createStatement();

			/*
			 We create a table, add a few rows, and update one.
			 */
			try{
				s.execute("create table derbyDB(num int, addr varchar(40))");
			}catch(SQLException se){
				//s.execute("open table derbyDB(num int, addr varchar(40))");
			}
			System.out.println("Created table derbyDB");
			s.execute("insert into derbyDB values (1956,'Webster St.')");
			System.out.println("Inserted 1956 Webster");
			s.execute("insert into derbyDB values (1910,'Union St.')");
			System.out.println("Inserted 1910 Union");
			s.execute("update derbyDB set num=180, addr='Grand Ave.' where num=1956");
			System.out.println("Updated 1956 Webster to 180 Grand");

			s.execute("update derbyDB set num=300, addr='Lakeshore Ave.' where num=180");
			System.out.println("Updated 180 Grand to 300 Lakeshore");

			/*
			 We select the rows and verify the results.
			 */
			ResultSet rs = s.executeQuery("SELECT num, addr FROM derbyDB ORDER BY num");

			if (!rs.next()) {
				throw new Exception("Wrong number of rows");
			}

			if (rs.getInt(1) != 300) {
				throw new Exception("Wrong row returned");
			}

			if (!rs.next()) {
				throw new Exception("Wrong number of rows");
			}

			if (rs.getInt(1) != 1910) {
				throw new Exception("Wrong row returned");
			}

			if (rs.next()) {
				throw new Exception("Wrong number of rows");
			}

			System.out.println("Verified the rows");

			s.execute("drop table derbyDB");
			System.out.println("Dropped table derbyDB");

			/*
			 We release the result and statement resources.
			 */
			rs.close();
			System.out.println("Closed result set and statement");

			/*
			 We end the transaction and the connection.
			 */
			conn.commit();
			System.out.println("Committed transaction and closed connection");

		} catch (Throwable e) {
			System.out.println("exception thrown:");

			if (e instanceof SQLException) {
				printSQLError((SQLException) e);
			} else {
				e.printStackTrace();
			}
		}finally{
			try{
				if(s != null){
					s.execute("drop table derbyDB");
					s.close();
				}
				conn.close();
			}catch(Throwable ta){
				System.out.println("Nothing may i do when sql Exception!");
			}
		}
		System.out.println("SimpleApp finished");
	}

	static void printSQLError(SQLException e) {
		while (e != null) {
			System.out.println(e.toString());
			e = e.getNextException();
		}
	}

}
