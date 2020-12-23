package DB;

import User.User;
import org.json.JSONObject;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Connector {
    private final Connection connection;
    private final String tableName;

    public Connector(String tableName) throws SQLException {
        connection = getConnection();
        connection.setAutoCommit(false);
        this.tableName = tableName;
        createTableIfNotExists();
    }

    public void insertUsers(JSONObject users) throws SQLException {
        var stmt = connection.createStatement();
        var keys = users.keys();
        while (keys.hasNext()) {
            var fullname = keys.next();
            var sex = users.getInt(fullname);
            var query = String.format("INSERT INTO %s(full_name, sex) VALUES (\'%s\', %d);", tableName, fullname, sex);
            stmt.executeUpdate(query);
        }
        stmt.close();
        connection.commit();
    }

    public ArrayList<User> getUsers() throws SQLException {
        var users = new ArrayList<User>();
        var stmt = connection.createStatement();
        var query = String.format("SELECT * FROM %s", tableName);
        var res = stmt.executeQuery(query);
        while (res.next())
            users.add(new User(res.getString("full_name"), res.getInt("sex")));

        stmt.close();
        return users;
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/test_db", "root", "password");
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;
        }
    }

    private void createTableIfNotExists() throws SQLException {
        var stmt = connection.createStatement();
        var query = String.format("CREATE TABLE IF NOT EXISTS %s(id INT PRIMARY KEY AUTO_INCREMENT, " +
                "full_name VARCHAR(255), sex INT)", tableName);
        stmt.executeUpdate(query);
        stmt.close();
        connection.commit();
    }
}