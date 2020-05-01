package project.db;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

public class DBCommunicator implements Closeable {

    /* TODO ideas:
        Should we rework this class to make and close connections every time data is requested,
          instead of one connection that is required to last the entire application's lifetime?
        If we are going to, it will need some reworking of the connection-closing handling,
          because the ResultSet must be done with before the connection which created it can be closed.
          Maybe, in that case, we can pass the entire connection creation and stuff (through a supplier?)
            so that whatever code uses that ResultSet can handle the connection closing by itself?
            Or wrap ResultSet in some sort of handler that will close them individually when they're done being used?
     */

    private Connection conn;

    public DBCommunicator() throws SQLException {
        System.out.println("initializing database communication stuff");
        try {
            //register JDBC driver
            Class.forName ("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println ("Cannot load the driver");
            System.exit(1);  //abnormal termination
        }
        // initialize connection
        String url = "jdbc:mysql://localhost:3306/tal";
        conn = DriverManager.getConnection(url, "root", "");
        // TODO maybe gracefully handle a connection failure?
    }


    public ResultSet getCustomerData() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql ="SELECT * FROM customer";
        ResultSet rset = stmt.executeQuery(sql);
        return rset;
    }

    public ResultSet getRepData() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql ="SELECT * FROM rep";
        ResultSet rset = stmt.executeQuery(sql);
        return rset;
    }

    public ResultSet getOrdersData() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql ="SELECT * FROM orders";
        ResultSet rset = stmt.executeQuery(sql);
        return rset;
    }

    public ResultSet getListOfCustNames() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql ="SELECT CustomerName FROM customer";
        ResultSet rset = stmt.executeQuery(sql);
        return rset;
    }

    public String getCredLimOfCust(String custName) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT CreditLimit FROM customer WHERE CustomerName = ?");
        prepStmt.setString(1, custName);
        ResultSet rset = prepStmt.executeQuery();
        return rset.getString("CreditLimit");
    }


    public boolean addRep(Map<String, String> formDataMap) throws SQLException {
//        Statement stmt = conn.createStatement();
//        // TODO: FIXME: Rework this method to use PreparedStatement to avoid SQL injection!
//        //  This whole method was just a quick and dirty solution!
//        String[] expectedColumns = {"LastName", "FirstName", "Street", "City", "State", "PostalCode", "Commission", "Rate"};
//        String[] values = new String[expectedColumns.length];
//        // "INSERT INTO rep (columnNames...) VALUES (respective values...)"
//        // setup a string builder (yes, this is VERY BAD PRACTICE; should use PreparedStatement)
//        StringBuilder sqlBldr = new StringBuilder("INSERT INTO rep (");
//        boolean errored = false; // used for aborting in case of missing data
//        // append in all the column names
//        for (int i = 0; i < expectedColumns.length; i++) {
//            String columnKey = expectedColumns[i];
//            if (i != 0) sqlBldr.append(", ");
//            sqlBldr.append(columnKey);
//            if(formDataMap.containsKey(columnKey)) {
//                // store the value that came from the form (via the param formDataMap)
//                //   so it can be appended into the sql later
//                values[i] = formDataMap.get(columnKey);
//            } else {
//                // print the data missing from the map, for debug reasons
//                System.err.println("DBCommunicator's addRep needed a value for \""+columnKey+"\" but formDataMap did not contain any!");
//                errored = true;
//            }
//        }
//        if(errored) return false; // abort the function due to missing required data?
//        // append in all the corresponding values
//        sqlBldr.append(") VALUES (");
//        for (int i = 0; i < values.length; i++) {
//            String value = values[i];
//            if(i != 0) sqlBldr.append(", ");
//            sqlBldr.append(value);
//        }
//        sqlBldr.append(");");
//        // get the sql string
//        String sql = sqlBldr.toString();
//        System.out.println("Executing SQL query: \""+sql+"\""); // print the sql for debug reasons
//        int result = stmt.executeUpdate(sql);
//        System.out.println("Query result value: " + result);


//        boolean prevautocommit = conn.getAutoCommit();
//        System.out.println("prevautocommit = " + prevautocommit);
//        conn.setAutoCommit(false);
        PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO rep" +
                " (LastName, FirstName, Street, City, State, PostalCode, Commission, Rate)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        prepStmt.setString(1, formDataMap.get("LastName"));
        prepStmt.setString(2, formDataMap.get("FirstName"));
        prepStmt.setString(3, formDataMap.get("Street"));
        prepStmt.setString(4, formDataMap.get("City"));
        prepStmt.setString(5, formDataMap.get("State"));
        prepStmt.setString(6, formDataMap.get("PostalCode"));
        prepStmt.setString(7, formDataMap.get("Commission"));
        prepStmt.setString(8, formDataMap.get("Rate"));
        System.out.println("prepStmt.toString() = " + prepStmt.toString());
        int result = prepStmt.executeUpdate();
//        conn.rollback();
//        conn.setAutoCommit(prevautocommit);

        System.out.println("Query result value: " + result);
        return true;
    }


    // handle proper de-connecting and stuff
    @Override
    public void close() {
        System.out.println("closing database communication stuff");
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(" ...done closing database communication stuff!");
    }

    public boolean updateCustCredLim(Map<String, String> formDataMap) throws SQLException {
        return false;
        // TODO: FIXME: FIX THIS make it work
    }

}
