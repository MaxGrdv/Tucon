package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {

    private Connection usersDatabase = null;
    private Connection messagesDatabase = null;

    /**
     * Constructor is passing two steps:
     * <p>
     * 1) - It checks if every database exists and in case if some of that is not
     * it creates new one
     * 2) - It connects to every database
     */

    private Database() {

        final String driverName = "org.sqlite.JDBC";

        final String usersConnectionString = "jdbc:sqlite:users.db";
        final String messagesConnectionString = "jdbc:sqlite:messages.db";

        try {
            Class.forName(driverName);
        } catch (Exception e) {
            System.out.println("No driver found.");
        }

        try {
            usersDatabase = DriverManager.getConnection(usersConnectionString);
        } catch (Exception e) {
            System.out.println("Can't get connection to [usersDatabase].");
        }

        try {
            messagesDatabase = DriverManager.getConnection(messagesConnectionString);
        } catch (Exception e) {
            System.out.println("Can't get connection to [messagesDatabase].");
        }

        try {

            String sqlRequest = "CREATE TABLE IF NOT EXISTS users(\n"
                    + "id integer PRIMARY KEY, \n"
                    + "name text NOT NULL, \n"
                    + "password text NOT NULL\n"
                    + ");";

            Statement statement = usersDatabase.createStatement();
            statement.execute(sqlRequest);

        } catch (Exception e) {
            System.out.println("Error while checking [users] table in [usersDatabase]");
        }

        try {

            String sqlRequest = "CREATE TABLE IF NOT EXISTS messages(\n"
                    + "id integer PRIMARY KEY, \n"
                    + "messages text NOT NULL\n"
                    + ");";

            Statement statement = messagesDatabase.createStatement();
            statement.execute(sqlRequest);

        } catch (Exception e) {
            System.out.println("Error while checking [messages] table in [messagesDatabase]");
        }
    }

    private static class DatabaseHolder {
        private final static Database instance = new Database();
    }

    public static Database getInstance() {
        return DatabaseHolder.instance;
    }

    /**
     * Authorizes user
     *
     * @param username - nickname of user
     * @param password - password of user
     * @return 0 - if there are no user with such username in database
     * 1 - if user is in database and his password is right
     * 2 - if user is in database and his password is wrong
     */

    public int authorizeUser(String username, String password) {

        // TODO

        return 1;
    }

    /**
     * Insert pair [username, password] to table
     *
     * @param username - nickname of new user
     * @param password - password of new user
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    public int addNewUser(String username, String password) {

        String sqlRequest = "INSERT INTO users(name, password) VALUES(?,?)";

        // TODO

        return 1;

    }

    /**
     * Closes every connection to databases
     *
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    public int stop() {

        if ((1 == closeUsersDatabaseConnection()) &
                (1 == closeMessagesDatabaseConnection())) {

            return 1;

        }

        return 0;
    }

    /**
     * Closes connection to [usersDatabase]
     *
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    private int closeUsersDatabaseConnection() {
        try {
            usersDatabase.close();
        } catch (Exception e) {
            System.out.println("Can't close connection to [usersDatabase]");
            return 0;
        }

        return 1;
    }

    /**
     * Closes connection to [messagesDatabase]
     *
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    private int closeMessagesDatabaseConnection() {
        try {
            messagesDatabase.close();
        } catch (Exception e) {
            System.out.println("Can't close connection to [messagesDatabase]");
            return 0;
        }

        return 1;
    }
}
