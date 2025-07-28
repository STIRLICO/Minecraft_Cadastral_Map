

import java.sql.*;

/**
 * Сервис входа: проверяет логин и пароль, на соответствие записанным в базу данных.
 */
public class LoginService {

    private static String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";

    static {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            String sql = "Create table users (ID int primary key, username varchar(50), password varchar(50))";
            Statement statement = connection.createStatement();
            statement.execute(sql);
            statement.executeUpdate("Insert into users (ID, username, password) values (1, 'alice', 'alice')");
            statement.executeUpdate("Insert into users (ID, username, password) values (2, 'bob', 'bob')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean login(String username, String password) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE username=\'" + username +  "\' AND password=\'" + password + "\'");
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


}