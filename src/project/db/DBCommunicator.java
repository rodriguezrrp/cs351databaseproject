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
        rset.next();
        return rset.getString("CreditLimit");
    }


    public boolean addRep(Map<String, String> formDataMap) throws SQLException {
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

//    public boolean updateCustCredLim(Map<String, String> formDataMap) throws SQLException {
//        // Make this actually work
//        System.out.println("DBCommunicator:updateCustCredLim: formDataMap = " + formDataMap);
//        return false;
//    }
    public boolean updateCustCredLim(String custName, String newCredLimValue) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("UPDATE customer" +
                " SET CreditLimit = ? WHERE CustomerName = ?");
        prepStmt.setString(1, newCredLimValue);
        prepStmt.setString(2, custName);
        System.out.println("prepStmt.toString() = " + prepStmt.toString());
        int result = prepStmt.executeUpdate();
        System.out.println("Query result value: " + result);
        return true;
    }

}
