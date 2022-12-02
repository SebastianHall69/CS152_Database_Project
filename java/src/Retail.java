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

            boolean keepon = true;
            while(keepon) {
                // These are sample SQL statements
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Create user");
                System.out.println("2. Log in");
                System.out.println("9. < EXIT");
                switch (readChoice()){
                    case 1: CreateUser(esql); break;
                    case 2: LogIn(esql); break;
                    case 9: keepon = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
                }//end switch
                if (esql.current_user != null) {
                    boolean usermenu = true;
                    while(usermenu) {
                        System.out.println("MAIN MENU");
                        System.out.println("---------");
                        System.out.println("1. View Stores within 30 miles");
                        System.out.println("2. View Product List");
                        System.out.println("3. Place a Order");
                        System.out.println("4. View 5 recent orders");

                        //the following functionalities basically used by managers
                        System.out.println("5. Update Product");
                        System.out.println("6. View 5 recent Product Updates Info");
                        System.out.println("7. View 5 Popular Items");
                        System.out.println("8. View 5 Popular Customers");
                        System.out.println("9. Place Product Supply Request to Warehouse");

                        System.out.println(".........................");
                        System.out.println("20. Log out");
                        switch (readChoice()){
                            case 1: viewStores(esql); break;
                            case 2: viewProducts(esql); break;
                            case 3: placeOrder(esql); break;
                            case 4: viewRecentOrders(esql); break;
                            case 5: updateProduct(esql); break;
                            case 6: viewRecentUpdates(esql); break;
                            case 7: viewPopularProducts(esql); break;
                            case 8: viewPopularCustomers(esql); break;
                            case 9: placeProductSupplyRequests(esql); break;

                            case 20: usermenu = false; esql.current_user = null; break;
                            default : System.out.println("Unrecognized choice!"); break;
                        }
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
            String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
            System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
            String longitude = in.readLine();

            String type="Customer";

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
   public static List<List<String>> getClosestStores(Retail esql, double user_lat, double user_long)
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
            double distance = esql.calculateDistance(store_lat, store_long, user_lat, user_long);
            if(distance < 30.0)
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
      double user_lat = 42.96338;
      double user_long = 58.46449;
      List<List<String>> closest_store = getClosestStores(esql, user_lat, user_long);
      System.out.print("Stores located within 30 miles:\n");
      for(List<String> i:closest_store)
      {
         System.out.println("Store ID: " + i.get(0));
         System.out.println("Store Name: " + i.get(1));
         System.out.println("Distance Away: " + i.get(6) + " miles");
      }
   }
	//View Product List, needs store id
   public static void viewProducts(Retail esql)
   {
      //return view of all items in the given store
      int storeID = 5;
      try{
         String query = String.format("SELECT * FROM Product WHERE storeID = %s", storeID);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         System.out.println(result);
      }catch(Exception e){
         System.err.println(e.getMessage());
      }

   }


    //check if the user is within 30 miles
    public static void placeOrder(Retail esql)
    {
        int storeID = 5;
        String productName = "Pudding"; 
        int unitsOrdered = 3;
        //check if there is enough quantity available 
        try{
            String query = String.format("SELECT numberOfUnits FROM Product WHERE storeID = %d and productName = '%s'", storeID, productName);
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
            if(result.size() > 0)
            {
                int quantity_available = Integer.parseInt(result.get(0).get(0));

                if(unitsOrdered <= quantity_available)
                {
                    quantity_available -= unitsOrdered;
                    //submit the order
                    query = String.format("INSERT INTO Orders(customerID, storeID, productName, unitsOrdered) VALUES (%d, %d, '%s', %d)", esql.current_user.userid(), storeID, productName, unitsOrdered);
                    esql.executeUpdate(query);
                    //update product quantity
                    query = String.format("UPDATE Product SET numberOfUnits = %d WHERE storeID = %d AND productName = '%s'", quantity_available, storeID, productName);
                    esql.executeUpdate(query);
                    query = String.format("SELECT numberOfUnits FROM Product WHERE storeID = %d and productName = '%s'", storeID, productName);
                    esql.executeQuery(query);
                }
            }


        }catch(Exception e){
            System.err.println(e.getMessage());

        }

    }


    public static void viewRecentOrders(Retail esql) 
    {
        try{
            String query = String.format("SELECT * FROM Orders WHERE customerID = %s ORDER BY orderTime DESC LIMIT 5", esql.current_user.userid());
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
            System.out.println(result);

        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    public static void updateProduct(Retail esql) {}
    public static void viewRecentUpdates(Retail esql) {}
    public static void viewPopularProducts(Retail esql) {}
    public static void viewPopularCustomers(Retail esql) {}
    public static void placeProductSupplyRequests(Retail esql) {}

}//end Retail

