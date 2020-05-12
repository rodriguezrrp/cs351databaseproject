package project.db;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Map;

public class DBCommunicator implements Closeable {

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

    public ResultSet getRepReportData() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql =
                "SELECT rep.RepNum, rep.FirstName, rep.LastName, " +
                "    COUNT(customer.CustomerNum) AS `CustomerCount`, " +
                "    AVG(customer.Balance) AS `AverageBalance` " +
                "FROM customer " +
                "RIGHT JOIN rep ON customer.RepNum = rep.RepNum " +
                "GROUP BY rep.RepNum ";
        ResultSet rset = stmt.executeQuery(sql);
        return rset;
    }

    public ResultSet getCustReportData(String customerName) throws SQLException {
        String sql = "SELECT customer.CustomerName, SUM(QuotedPrice) AS OrdersPricesTotal" +
                "FROM customer " +
                "LEFT JOIN orders ON orders.CustomerNum = Customer.CustomerNum " +
                "LEFT JOIN orderline ON orders.OrderNum = orderline.orderNum " +
                "WHERE CustomerName = ?";
        PreparedStatement prepStmt = conn.prepareStatement(sql);
        prepStmt.setString(1, customerName);
        ResultSet rset = prepStmt.executeQuery();
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


    public String authLogin(String user, String pass) throws SQLException {
        // hash and all that stuff
        String hPass = pass; // currently it isn't doing anything. but it could :)
        // see if it's right
        PreparedStatement prepStmt = conn.prepareStatement("SELECT * FROM user WHERE Email = ? AND Password = ?");
        prepStmt.setString(1, user);
        prepStmt.setString(2, hPass);
        ResultSet rset = prepStmt.executeQuery();
        // if the result can go to a last row, then it does,
        //   and gets the row index to determine the length of the ResultSet
        if(rset.last() && rset.getRow() > 0) {
            return rset.getString("FirstName") + " " + rset.getString("LastName");
        }
        return null;
    }


    static final String CSV_DELIM = ",";
    static final String NEWLINE = "\n";

    public static void exportReportAsCSV(ResultSet resultSet, Path csvExportFilePath) throws IOException, SQLException {
        try(BufferedWriter fileOut = Files.newBufferedWriter(csvExportFilePath)) {
            // print CSV headers first
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int colCt = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= colCt; i++) {
                String columnLabel = resultSetMetaData.getColumnLabel(i);
                if(i > 1) fileOut.write(CSV_DELIM);
                fileOut.write(columnLabel);
            }
            // print resultSet's row contents
            while (resultSet.next()) {
                fileOut.write(NEWLINE);
                for (int i = 1; i <= colCt; i++) {
                    if(i > 1) fileOut.write(CSV_DELIM);
                    String s = resultSet.getString(i);
                    fileOut.write(s == null ? "NULL" : s);
                }
            }
            // ensure the writer finishes writing before it gets auto-closed by the try-with-resources clause
            fileOut.write(NEWLINE); // write an extra newline, just in case ;)
            fileOut.flush();
        }
    }

}
