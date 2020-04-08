package project.db;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

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
        // initialize connection
        System.out.println("initializing database communication stuff");
        try {
            //register JDBC driver
            Class.forName ("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println ("Cannot load the driver");
            System.exit(1);  //abnormal termination
        }
        String url = "jdbc:mysql://localhost:3306/tal";
        conn = DriverManager.getConnection(url, "root", "");
    }

    // just placeholder, I haven't worked out what we'll do with it yet lol

    public ResultSet getCustomerData() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql ="SELECT * FROM customer";
        ResultSet rset = stmt.executeQuery(sql);
        return rset;
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
}
