package org.study.green.tablesaw;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.study.green.str.StrUtil;
import tech.tablesaw.api.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class TableSawDB {
     Logger logger = LoggerFactory.getLogger(getClass());

    public TableSawDB() {

        Dotenv dotenv = Dotenv.load();
        String mysqlHost = StrUtil.getString(dotenv, "MYSQL.HOST");
        String user = StrUtil.getString(dotenv,"MYSQL.USER");
        String password = StrUtil.getString(dotenv,"MYSQL.PASSWORD");
        String port = StrUtil.getString(dotenv,"MYSQL.PORT");
        String schema = StrUtil.getString(dotenv,"MYSQL.SCHEMA");

        System.out.println("user = " + user);
        System.out.println("mysqlHost = " + mysqlHost);
        System.out.println("port = " + port);
        System.out.println("user = " + user);
        System.out.println("schema = " + schema);

        Connection con = null;
        try {
            String formatUrl = "jdbc:mariadb://%s:%s/%s";

            String url = String.format(formatUrl, mysqlHost, port, schema);
            System.out.println("url = " + url);

            Class.forName("org.mariadb.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Successfully connected to MySQL database");

            StringBuilder sb = new StringBuilder();

            sb.append("select 1 as tes1 ;");

            ResultSet resultSet = con.prepareStatement(sb.toString()).executeQuery();

            Table db = Table.read().db(resultSet);
            System.out.println("db.rowCount() = " + db.rowCount());

            db.stream().forEach(row -> {
                System.out.println("row = " + row);
            });

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                System.err.println("Cannot close connection: " + e.getMessage());
            }
        }

//        Table.read().db
    }



    public static void main(String[] args) {
        new TableSawDB();

    }
}
