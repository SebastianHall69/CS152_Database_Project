/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {

	// reference to physical database connection.
	private Connection _connection = null;

	// handling the keyboard inputs through a BufferedReader
	// This variable can be global for convenience.
	static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));

	// Current signed in user
	public User current_user = null;

	/**
	 * Creates a new instance of Retail shop
	 *
	 * @param hostname the MySQL or PostgreSQL server hostname
	 * @param database the name of the database
	 * @param username the user name used to login to the database
	 * @param password the user login password
	 * @throws java.sql.SQLException when failed to make a connection.
	 */
	public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");

			// obtain a physical connection
			this._connection = DriverManager.getConnection(url, user, passwd);
			System.out.println("Done");
		}catch (Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
			System.out.println("Make sure you started postgres on this machine");
			System.exit(-1);
		}//end catch
	}//end Retail

	// Method to calculate euclidean distance between two latitude, longitude pairs. 
	public double calculateDistance (double lat1, double long1, double lat2, double long2){
		double t1 = (lat1 - lat2) * (lat1 - lat2);
		double t2 = (long1 - long2) * (long1 - long2);
		return Math.sqrt(t1 + t2); 
	}
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 *
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 */
	public void executeUpdate (String sql) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
		stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 *
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 ** obtains the metadata object for the returned result set.  The metadata
		 ** contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		// iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
				}
				System.out.println();
				outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}//end executeQuery

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 *
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 ** obtains the metadata object for the returned result set.  The metadata
		 ** contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		// iterates through the result set and saves the data returned by the query.
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>();
		while (rs.next()){
			List<String> record = new ArrayList<String>();
			for (int i=1; i<=numCol; ++i)
				record.add(rs.getString (i));
			result.add(record);
		}//end while
		stmt.close ();
		return result;
	}//end executeQueryAndReturnResult

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 *
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		// iterates through the result set and count nuber of results.
		while (rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current
	 * value of sequence used for autogenerated keys
	 *
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();

		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next())
			return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
			// ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 *
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
					"Usage: " +
					"java [-classpath <classpath>] " +
					Retail.class.getName () +
					" <dbname> <port> <user>");
			System.err.println("num args: " + args.length);
			return;
		}//end if

		Greeting();
		Retail esql = null;
		try{
			// use postgres JDBC driver.
			Class.forName ("org.postgresql.Driver").newInstance ();
			// instantiate the Retail object and creates a physical
			// connection.
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			esql = new Retail (dbname, dbport, user, "");

			boolean keep_on = true;
			while(keep_on) {
				// Login
				keep_on = login_menu(esql);

				// While logged in, give users choices
				while(esql.current_user != null) {
					if(esql.current_user.type().equals("manager")) {
						managerOptions(esql);
					} else if(esql.current_user.type().equals("admin")) {
						adminOptions(esql);
					} else {
						userOptions(esql);
					}
				}
			}//end while
		}catch(Exception e) {
			System.err.println (e.getMessage ());
		}finally{
			// make sure to cleanup the created table and close the connection.
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if
			}catch (Exception e) {
				// ignored.
			}//end try
		}//end try
	}//end main

	public static boolean login_menu(Retail esql) {
		System.out.println("MAIN MENU");
		System.out.println("---------");
		System.out.println("1. Create user");
		System.out.println("2. Log in");
		System.out.println("9. < EXIT");
		switch (readChoice()){
			case 1: CreateUser(esql); break;
			case 2: LogIn(esql); break;
			case 9: return false;
			default : System.out.println("Unrecognized choice!"); break;
		}
		return true;
	}

	public static void managerOptions(Retail esql) {
		System.out.println("MAIN MENU");
		System.out.println("---------");
		System.out.println("1.  View Stores within 30 miles");
		System.out.println("2.  View Product List");
		System.out.println("3.  Place a Order");
		System.out.println("4.  View 5 recent orders");
		System.out.println("5.  View order info on a store");
		System.out.println("6.  Update Product");
		System.out.println("7.  View 5 recent Product Updates Info");
		System.out.println("8.  View 5 Popular Items");
		System.out.println("9.  View 5 Popular Customers");
		System.out.println("10. Place Product Supply Request to Warehouse");
		System.out.println(".........................");
		System.out.println("20. Log out");

		switch (readChoice()){
			case 1: viewStores(esql); break;
			case 2: viewProducts(esql); break;
			case 3: placeOrder(esql); break;
			case 4: viewRecentOrders(esql); break;
			case 5: viewStoreOrders(esql); break;
			case 6: updateProduct(esql); break;
			case 7: viewRecentUpdates(esql); break;
			case 8: viewPopularProducts(esql); break;
			case 9: viewPopularCustomers(esql); break;
			case 10: placeProductSupplyRequests(esql); break;
			case 20: esql.current_user = null; break;
			default : System.out.println("Unrecognized choice!"); break;
		}
	}

	public static void userOptions(Retail esql) {
		System.out.println("MAIN MENU");
		System.out.println("---------");
		System.out.println("1. View Stores within 30 miles");
		System.out.println("2. View Product List");
		System.out.println("3. Place a Order");
		System.out.println("4. View 5 recent orders");
		System.out.println(".........................");
		System.out.println("20. Log out");

		switch (readChoice()){
			case 1: viewStores(esql); break;
			case 2: viewProducts(esql); break;
			case 3: placeOrder(esql); break;
			case 4: viewRecentOrders(esql); break;
			case 20: esql.current_user = null; break;
			default : System.out.println("Unrecognized choice!"); break;
		}
	}


	public static void adminOptions(Retail esql) {
		System.out.println("MAIN MENU");
		System.out.println("---------");
		System.out.println("1. View User Data");
		System.out.println("2. View Product Data");
		System.out.println("3. Update User Data");
		System.out.println("4. Update Product Data");
		System.out.println(".........................");
		System.out.println("20. Log out");

		switch (readChoice()){
			case 1: viewUserData(esql); break;
			case 2: viewProductData(esql); break;
			case 3: updateUserData(esql); break;
			case 4: updateProductData(esql); break;
			case 20: esql.current_user = null; break;
			default : System.out.println("Unrecognized choice!"); break;
		}
	}

	public static void Greeting(){
		System.out.println(
				"\n\n*******************************************************\n" +
				"              User Interface                         \n" +
				"*******************************************************\n");
	}//end Greeting

	/*
	 * Reads the users choice given from the keyboard
	 * @int
	 **/
	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	/*
	 * Creates a new user
	 **/
	public static void CreateUser(Retail esql){
		try{
			System.out.print("\tEnter name: ");
			String name = in.readLine();
			System.out.print("\tEnter password: ");
			String password = in.readLine();
			System.out.print("\tEnter latitude: ");   
			double latitude = Double.parseDouble(in.readLine()); //enter lat value between [0.0, 100.0]
			System.out.print("\tEnter longitude: "); //enter long value between [0.0, 100.0]
			double longitude = Double.parseDouble(in.readLine());
			String type="customer";

			// Enforce latitude / longitude constraints
			if(latitude < 0 || latitude > 100 || longitude < 0 || longitude > 100) {
				System.out.println("Invalid latitude / longitude. Range [0, 100].");
				return;
			}

			// Enforce unique username, can be DB constraint or trigger
			String query = String.format("SELECT * FROM users WHERE name='%s';", name);
			if(esql.executeQuery(query) > 0) {
				System.out.println("Username '" + name + "' already exists. Please login instead.");
				return;
			}
			query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

			esql.executeUpdate(query);
			System.out.println ("User successfully created!");
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end CreateUser


	/*
	 * Check log in credentials for an existing user
	 * @return User login or null is the user does not exist
	 **/
	public static void LogIn(Retail esql){
		try{
			System.out.print("\tEnter name: ");
			String name = in.readLine();
			System.out.print("\tEnter password: ");
			String password = in.readLine();

			String query = String.format("SELECT userid, type, latitude, longitude FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
			List<List<String>> user_data = esql.executeQueryAndReturnResult(query);

			if(user_data.size() > 0) {
				esql.current_user = new User();
				esql.current_user.setName(name);
				esql.current_user.setUserid(Integer.parseInt(user_data.get(0).get(0)));
				esql.current_user.setType(user_data.get(0).get(1));
				esql.current_user.setLatitude(Double.parseDouble(user_data.get(0).get(2)));
				esql.current_user.setLongitude(Double.parseDouble(user_data.get(0).get(3)));
			} else {
				System.out.println("Username / Password login not found");
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}
	}//end

	// Rest of the functions definition go in here
	/* Maybe combine Stores and Product list.
	   After logging in initial, find the closest store and display the products at that given store
	   Add a function to change the current store and use viewStores to give the user options
	   */
	//View Stores within 30 miles
	public static List<List<String>> getClosestStores(Retail esql)
	{
		try{
			String query = String.format("SELECT * FROM Store");
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			//System.out.print(result);
			List<List<String>> in_range_result = new ArrayList<List<String>>(); 

			for(List<String> i:result)
			{
				double store_lat = Double.parseDouble(i.get(2));
				double store_long = Double.parseDouble(i.get(3));
				double user_lat = esql.current_user.latitude();
				double user_long = esql.current_user.longitude();
				double distance = esql.calculateDistance(store_lat, store_long, user_lat, user_long);
				if(distance <= 30.0)
				{
					List<String> record = i;
					record.add(String.valueOf(distance));
					in_range_result.add(record);
				}
			}
			//System.out.print(in_range_result);
			//maybe sort the distances before returning
			/*Collections.sort(in_range_result, (result_1, result_2) -> {
			  return Integer.parseInt(result_1.get(6)) - Integer.parseInt(result_2.get(6));
			  });*/
			return in_range_result;
		}catch(Exception e){
			System.err.println(e.getMessage());
			return null;
		}


	}
	//add a user parameter
	public static void viewStores(Retail esql)
	{
		List<List<String>> closest_store = getClosestStores(esql);
		System.out.print("Stores located within 30 miles:\n");
		for(List<String> i:closest_store)
		{
			System.out.println("Store ID: " + i.get(0));
			System.out.println("Store Name: " + i.get(1));
			System.out.printf("Distance Away: %.2f miles\n\n", Double.parseDouble(i.get(6)));
		}
	}
	//View Product List, needs store id
	public static void viewProducts(Retail esql)
	{
		try{
			//return view of all items in the given store
			System.out.print("Enter store id: ");
			int storeID = Integer.parseInt(in.readLine());
			String query = String.format("SELECT productname, numberofunits, priceperunit FROM Product WHERE storeID = %d;", storeID);
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			
			// Show results
			for(List<String> product : result) {
				System.out.println("Name:  " + product.get(0).trim());
				System.out.println("Stock: " + product.get(1).trim());
				System.out.println("Price: $" + product.get(2).trim() + "\n");
			}
			if(result.size() < 1) {
				System.out.println("No result found\n");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}

	}


	public static boolean checkIfStoreIsInRange(Retail esql, int storeID)
	{
		try
		{
			String check_query = String.format("SELECT * FROM Store WHERE storeID = %d", storeID);
			List<List<String>> check_result = esql.executeQueryAndReturnResult(check_query);
			if(check_result.size() > 0)
			{
				double store_lat = Double.parseDouble(check_result.get(0).get(2));
				double store_long = Double.parseDouble(check_result.get(0).get(3));
				double user_lat = esql.current_user.latitude();
				double user_long = esql.current_user.longitude();
				return esql.calculateDistance(store_lat, store_long, user_lat, user_long) <= 30.0;
			}

		} catch(Exception e){
			System.out.println("The store given does not exist.");
			System.err.println(e.getMessage());
			return false;
		}

		return false;
	}

	//check if the user is within 30 miles
	public static void placeOrder(Retail esql)
	{

		try{
			System.out.print("Enter store id: ");
			int storeID = Integer.parseInt(in.readLine());
			System.out.print("Enter product name: ");
			String productName = in.readLine();
			System.out.print("Enter quantity: ");
			int unitsOrdered = Integer.parseInt(in.readLine());

			//check if the store is within 30 miles of the user
			if(checkIfStoreIsInRange(esql, storeID) == false) {
				System.out.printf("Store #%d is outside of your 30 mile range\n", storeID);
				return;
			}

			// Query product availability
			String query = String.format("SELECT numberOfUnits FROM Product WHERE storeID = %d and productName = '%s';", storeID, productName);
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			if(result.size() < 1) {
				System.out.printf("Could not find product '%s' at store with id %d\n", productName, storeID);
				return;
			}

			// check if there is enough quantity available 
			int quantity_available = Integer.parseInt(result.get(0).get(0));
			if(unitsOrdered > quantity_available) {
				System.out.println("There is not enough quantity in store to fulfil the order request.");
				return;
			}

			// submit the order and update product quantity
			quantity_available -= unitsOrdered;
			query = String.format("INSERT INTO Orders(customerID, storeID, productName, unitsOrdered) VALUES (%d, %d, '%s', %d);", esql.current_user.userid(), storeID, productName, unitsOrdered);
			esql.executeUpdate(query);
			query = String.format("UPDATE Product SET numberOfUnits = %d WHERE storeID = %d AND productName = '%s';", quantity_available, storeID, productName);
			esql.executeUpdate(query);
			query = String.format("SELECT numberOfUnits FROM Product WHERE storeID = %d and productName = '%s';", storeID, productName); // This doesn't do anything. Just left in from testing?
			esql.executeQuery(query);
			System.out.println("Order was successfully added!");
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}


	public static void viewRecentOrders(Retail esql) 
	{
		try{
			String query = String.format("SELECT S.name, O.storeid, O.productname, O.unitsordered, O.ordertime FROM orders O, store S WHERE O.storeid = S.storeid AND O.customerID = %d ORDER BY O.orderTime DESC LIMIT 5;", esql.current_user.userid());
			List<List<String>> result = esql.executeQueryAndReturnResult(query);

			for(List<String> order : result) {
				System.out.printf("Store: #%s\n", order.get(0));
				System.out.printf("Store id: #%s\n", order.get(1));
				System.out.printf("Product: %s\n", order.get(2));
				System.out.printf("Quantity: %s\n", order.get(3));
				System.out.printf("Time: %s\n\n", order.get(4));
			}
			if(result.size() < 1) {
				System.out.println("No recent orders found");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	public static void viewStoreOrders(Retail esql) {
		try {
			System.out.print("Enter store id: ");
			int store_id = Integer.parseInt(in.readLine());

			// Check if manages store
			String query = String.format("SELECT * FROM store where storeid = %d AND managerid = %d;", store_id, esql.current_user.userid());
			if(esql.executeQuery(query) == 0) {
				System.out.printf("Manager #%d does not manage store #%d. You cannot view these orders, fool\n", esql.current_user.userid(), store_id);
				return;
			}

			// List orders
			query = String.format("SELECT O.ordernumber, O.storeid, O.ordertime, U.name, O.productname, O.unitsordered FROM orders O, store S, users U WHERE S.storeid = %d AND S.storeid = O.storeid AND O.customerid = U.userid;", store_id);
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			for(List<String> order : result) {
				System.out.printf("Order: #%s\n", order.get(0));
				System.out.printf("Store: #%s\n", order.get(1));
				System.out.printf("Date: %s\n", order.get(2));
				System.out.printf("Customer: %s\n", order.get(3));
				System.out.printf("Product: %s\n", order.get(4));
				System.out.printf("Quantity: %s\n\n", order.get(5));
			}
			if(result.size() < 1) {
				System.out.printf("No orders found for store #%d\n", store_id);
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void updateProduct(Retail esql) {
		try {
			System.out.print("Enter store id: ");
			int store_id = Integer.parseInt(in.readLine());

			// Check if manages store
			String query = String.format("SELECT * FROM store where storeid = %d AND managerid = %d;", store_id, esql.current_user.userid());
			if(esql.executeQuery(query) == 0) {
				System.out.printf("Manager #%d does not manage store #%d. You cannot update these products, fool\n", esql.current_user.userid(), store_id);
				return;
			}

			// Get update information
			System.out.print("Enter product name: ");
			String product_name = in.readLine();
			System.out.print("Enter new quantity (-1 to keep same value): ");
			int quantity = Integer.parseInt(in.readLine());
			System.out.print("Enter new price (-1 to keep same value): ");
			double price = Double.parseDouble(in.readLine());

			// Determine type of update
			if(quantity + price < -1) {
				System.out.println("You gave no information to update");
				return;
			}
			String updates = "SET ";
			if(quantity >= 0 && price >= 0) updates += String.format("numberofunits = %d, priceperunit = %f", quantity, price);
			else if(quantity >= 0) updates += String.format("numberofunits = %d", quantity);
			else if(price >= 0) updates += String.format("priceperunit = %f", price);

			// Update product table
			query = String.format("UPDATE product %s WHERE storeid = %d AND productname = '%s';", updates, store_id, product_name);
			esql.executeUpdate(query);

			// Update productupdates table
			query = String.format("INSERT INTO productupdates (managerid, storeid, productname, updatedon) VALUES (%d, %d, '%s', NOW());", esql.current_user.userid(), store_id, product_name);
			esql.executeUpdate(query);
			System.out.println("Update Successful");
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	public static void viewRecentUpdates(Retail esql) {
		try {
			System.out.print("Enter store id: ");
			int store_id = Integer.parseInt(in.readLine());

			// Check if manages store
			String query = String.format("SELECT * FROM store where storeid = %d AND managerid = %d;", store_id, esql.current_user.userid());
			if(esql.executeQuery(query) == 0) {
				System.out.printf("Manager #%d does not manage store #%d. You cannot view these updates, fool\n", esql.current_user.userid(), store_id);
				return;
			}

			query = String.format("SELECT updatenumber, managerid, productname, updatedon FROM productupdates WHERE storeid = %d ORDER BY updatedon DESC LIMIT 5;", store_id);
			List<List<String>> result = esql.executeQueryAndReturnResult(query);

			for(List<String> update : result) {
				System.out.printf("Update: #%s\n", update.get(0));
				System.out.printf("Manager: #%s\n", update.get(1));
				System.out.printf("Product: #%s\n", update.get(2));
				System.out.printf("Time: #%s\n\n", update.get(3));
			}
			if(result.size() < 1) {
				System.out.println("No recent updates found");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	public static void viewPopularProducts(Retail esql) {
		try {
			System.out.print("Enter store id: ");
			int store_id = Integer.parseInt(in.readLine());

			// Check if manages store
			String query = String.format("SELECT * FROM store where storeid = %d AND managerid = %d;", store_id, esql.current_user.userid());
			if(esql.executeQuery(query) == 0) {
				System.out.printf("Manager #%d does not manage store #%d. You cannot view these popular products, fool\n", esql.current_user.userid(), store_id);
				return;
			}

			// Print results
			query = String.format("SELECT productname, COUNT(ordernumber) FROM orders WHERE storeid = %d GROUP BY productname ORDER BY COUNT(ordernumber) DESC LIMIT 5;", store_id);
			List<List<String>> result = esql.executeQueryAndReturnResult(query);

			for(List<String> product : result) {
				System.out.printf("Product: %s\n", product.get(0));
				System.out.printf("Order count: %s\n\n", product.get(1));
			}
			if(result.size() < 1) {
				System.out.println("No popular products found");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	public static void viewPopularCustomers(Retail esql) {
		try {
			System.out.print("Enter store id: ");
			int store_id = Integer.parseInt(in.readLine());

			// Check if manages store
			String query = String.format("SELECT * FROM store where storeid = %d AND managerid = %d;", store_id, esql.current_user.userid());
			if(esql.executeQuery(query) == 0) {
				System.out.printf("Manager #%d does not manage store #%d. You cannot view these popular customers, fool\n", esql.current_user.userid(), store_id);
				return;
			}

			// Print results
			query = String.format("SELECT O.customerid, U.name, COUNT(O.ordernumber) FROM orders O, users U WHERE O.customerid = U.userid GROUP BY O.customerid, U.name ORDER BY COUNT(ordernumber) DESC LIMIT 5;", store_id);
			List<List<String>> result = esql.executeQueryAndReturnResult(query);

			for(List<String> customer : result) {
				System.out.printf("Customer id: #%s\n", customer.get(0));
				System.out.printf("Customer: #%s\n", customer.get(1));
				System.out.printf("Order count: #%s\n\n", customer.get(2));
			}
			if(result.size() < 1) {
				System.out.println("No popular customers found");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	public static void placeProductSupplyRequests(Retail esql) {
		try {
			// Read input
			System.out.print("Enter store id: ");
			int store_id = Integer.parseInt(in.readLine());
			System.out.print("Enter product name: ");
			String product_name = in.readLine();
			System.out.print("Enter units requested: ");
			int quantity = Integer.parseInt(in.readLine());
			System.out.print("Enter warehouse id: ");
			int warehouse_id = Integer.parseInt(in.readLine());

			// Check if store exists
			if(esql.executeQuery(String.format("SELECT * FROM store WHERE storeid = %d;", store_id)) == 0) {
				System.out.printf("Store #%d does not exist\n", store_id);
				return;
			}

			// Check if product exists in store
			if(esql.executeQuery(String.format("SELECT * FROM product WHERE storeid = %d AND productname = '%s';", store_id, product_name)) == 0) {
				System.out.printf("Product '%s' is not carried at store #%d\n", product_name, store_id);
				return;
			}

			// Check if warehouse exists
			if(esql.executeQuery(String.format("SELECT * FROM warehouse WHERE warehouseid = %d;", warehouse_id)) == 0) {
				System.out.printf("Warehouse #%d does not exist\n", warehouse_id);
				return;
			}

			// Validate quantity
			if(quantity < 1) {
				System.out.printf("Must have a postive value for units requested\n");
				return;
			}
			
			// Place supply request
			String query = String.format("INSERT INTO productsupplyrequests (managerid, warehouseid, storeid, productname, unitsrequested) VALUES (%d, %d, %d, '%s', %d);", esql.current_user.userid(), warehouse_id, store_id, product_name, quantity);
			esql.executeUpdate(query);

			// Update product info and product update table
			query = String.format("UPDATE product SET numberofunits = numberofunits + %d WHERE storeid = %d AND productname = '%s';", quantity, store_id, product_name);
			esql.executeUpdate(query);
			query = String.format("INSERT INTO productupdates (managerid, storeid, productname, updatedon) VALUES (%d, %d, '%s', NOW());", esql.current_user.userid(), store_id, product_name);
			esql.executeUpdate(query);
			System.out.println("Supply request successfully placed");
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void viewUserData(Retail esql) {
		try {
			// Get choice
			System.out.printf("1.) View All Users\n2.) Search User By ID\n3.) Search User By Name\n");
			System.out.print("Choice: ");
			int choice = Integer.parseInt(in.readLine());

			// Construct query
			String query = "";
			if(choice == 1) {
				query = "SELECT * FROM users;";
			} else if(choice == 2) {
				System.out.print("Enter User ID: ");
				int user_id = Integer.parseInt(in.readLine());
				query = String.format("SELECT * FROM users WHERE userid = %d;", user_id);
			} else if(choice == 3) {
				System.out.print("Enter User Name: ");
				String name = in.readLine();
				query = String.format("SELECT * FROM users WHERE name = '%s';", name);
			}

			// Execute and display results
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			for(List<String> user : result) {
				System.out.printf("userid: %s\n", user.get(0));
				System.out.printf("name: %s\n", user.get(1));
				System.out.printf("password: %s\n", user.get(2));
				System.out.printf("latitude: %s\n", user.get(3));
				System.out.printf("longitude: %s\n", user.get(4));
				System.out.printf("type: %s\n\n", user.get(5));
			}
			if(result.size() < 1) {
				System.out.println("No Users Found");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void viewProductData(Retail esql) {
		try {
			// Get choice
			System.out.printf("1.) View All Products\n2.) Search Product By Store ID\n3.) Search Product By Store ID / Name\n4.) Search Product By Name\n");
			System.out.print("Choice: ");
			int choice = Integer.parseInt(in.readLine());

			// Construct query
			String query = "";
			if(choice == 1) {
				query = "SELECT * FROM product;";
			} else if(choice == 2) {
				System.out.print("Enter Store ID: ");
				int store_id = Integer.parseInt(in.readLine());
				query = String.format("SELECT * FROM product WHERE storeid = %d;", store_id);
			} else if(choice == 3) {
				System.out.print("Enter Store ID: ");
				int store_id = Integer.parseInt(in.readLine());
				System.out.print("Enter Product Name: ");
				String name = in.readLine();
				query = String.format("SELECT * FROM product WHERE storeid = %d AND productname = '%s';", store_id, name);
			} else if(choice == 4) {
				System.out.print("Enter Product Name: ");
				String name = in.readLine();
				query = String.format("SELECT * FROM product WHERE productname = '%s';", name);
			}

			// Execute and display results
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			for(List<String> product : result) {
				System.out.printf("storeid: %s\n", product.get(0));
				System.out.printf("productname: %s\n", product.get(1));
				System.out.printf("numberofunits: %s\n", product.get(2));
				System.out.printf("priceperunit: %s\n\n", product.get(3));
			}
			if(result.size() < 1) {
				System.out.println("No Products Found");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void updateUserData(Retail esql) {
		try {
			System.out.print("Enter User ID: ");
			int user_id = Integer.parseInt(in.readLine());

			// Check if user exists
			String query = String.format("SELECT * FROM users WHERE userid = %d;", user_id);
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			if(result.size() < 1) {
				System.out.printf("User with id #%d not found\n", user_id);
				return;
			}
			List<String> user = result.get(0);

			// Get updated info
			System.out.println("\nCurrent Info");
			System.out.printf("name: %s\n", user.get(1));
			System.out.printf("password: %s\n", user.get(2));
			System.out.printf("latitude: %s\n", user.get(3));
			System.out.printf("longitude: %s\n", user.get(4));
			System.out.printf("type: %s\n\n", user.get(5));

			System.out.print("Enter name (empty string to keep old value): ");
			String name = in.readLine().trim();
			System.out.print("Enter password (empty string to keep old value): ");
			String password = in.readLine().trim();
			System.out.print("Enter latitude (-1 for to keep old value): ");
			double latitude = Double.parseDouble(in.readLine());
			System.out.print("Enter longitude (-1 for to keep old value): ");
			double longitude = Double.parseDouble(in.readLine());
			System.out.print("Enter type (empty string to keep old value): ");
			String type = in.readLine().trim();

			// Validate data
			if(name.isEmpty()) {
				name = user.get(1);
			}
			if(password.isEmpty()) {
				password = user.get(2);
			}
			if(latitude < 0) {
				latitude = Double.parseDouble(user.get(3));
			} else if(latitude > 100) {
				System.out.printf("Invalid latitude %f. Must be in range [0, 100]\n", latitude);
				return;
			}
			if(longitude < 0) {
				longitude = Double.parseDouble(user.get(4));
			} else if(longitude > 100) {
				System.out.printf("Invalid longitude %f. Must be in range [0, 100]\n", longitude);
				return;
			}
			if(type.isEmpty()) {
				type = user.get(5);
			} else if(!(type.equals("customer") || type.equals("manager") || type.equals("admin"))) {
				System.out.printf("Invalid type '%s'. Must be either customer, manager, or admin\n", type);
				return;
			}

			// Update user
			query = String.format("UPDATE users SET name='%s', password='%s', latitude=%f, longitude=%f, type='%s' WHERE userid=%d;", name, password, latitude, longitude, type, user_id);
			esql.executeUpdate(query);
			System.out.println("Successfully Updated User\n");
		} catch(Exception e) {
			System.err.println("ERROR IN DATA INPUT: " + e.getMessage());
		}
	}

	public static void updateProductData(Retail esql) {
		try {
			System.out.print("Enter Store ID: ");
			int store_id = Integer.parseInt(in.readLine());
			System.out.print("Enter Product Name: ");
			String product_name = in.readLine();

			// Check if product exists
			String query = String.format("SELECT * FROM product WHERE storeid=%d AND productname='%s';", store_id, product_name);
			List<List<String>> result = esql.executeQueryAndReturnResult(query);
			if(result.size() < 1) {
				System.out.printf("Product '%s' in store #%d not found\n", product_name, store_id);
				return;
			}
			List<String> product = result.get(0);

			// Get updated info
			System.out.println("\nCurrent Info");
			System.out.printf("storeid: %s\n", product.get(0));
			System.out.printf("productname: %s\n", product.get(1));
			System.out.printf("numberofunits: %s\n", product.get(2));
			System.out.printf("priceperunit: %s\n", product.get(3));

			System.out.print("Enter numberofunits (-1 for to keep old value): ");
			int number_of_units = Integer.parseInt(in.readLine());
			System.out.print("Enter priceperunit (-1 for to keep old value): ");
			double price_per_unit = Double.parseDouble(in.readLine());

			// Validate data
			if(number_of_units < 0) {
				number_of_units = Integer.parseInt(product.get(2));
			}
			if(price_per_unit < 0) {
				price_per_unit = Double.parseDouble(product.get(3));
			}

			// Update Product
			query = String.format("UPDATE product SET numberofunits=%d, priceperunit=%f WHERE storeid=%d AND productname='%s';", number_of_units, price_per_unit, store_id, product_name);
			esql.executeUpdate(query);
			System.out.println("Successfully Updated Product\n");
		} catch(Exception e) {
			System.err.println("ERROR IN DATA INPUT: " + e.getMessage());
		}
	}
}//end Retail

