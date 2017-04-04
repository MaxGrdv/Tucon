package database;

import server.Info;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

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


        try {
            Class.forName(driverName);
        } catch (Exception e) {
            System.out.println("No driver found.");
            System.out.println(e.getMessage());
        }

        start();
        checkTables();
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
     * @param username nickname of user
     * @param password password of user
     * @return -1 - if something gone wrong
     * 0 - if there are no user with such username in database
     * 1 - if user is in database and his password is right
     * 2 - if user is in database and his password is wrong
     */

    public int authorizeUser(String username, String password) {

        String sqlRequest = "SELECT name, password FROM users";

        try {

            Statement statement = usersDatabase.createStatement();
            ResultSet pair = statement.executeQuery(sqlRequest);

            while (pair.next()) {
                if (username.equals(pair.getString("name"))) {

                    if (password.equals(pair.getString("password"))) {
                        return 1;
                    } else {
                        return 2;
                    }

                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to find user in database.");
            System.out.println(e.getMessage());
            return -1;
        }

        return 0;
    }

    /**
     * Insert pair [username, password] to table
     *
     * @param username nickname of new user
     * @param password password of new user
     * @return 0 - if unsuccessful
     * 1 - if successful
     * 2 - if user is already in database
     */

    public int addNewUser(String username, String password) {

        if ((1 == authorizeUser(username, "1")) |
                (2 == authorizeUser(username, "1"))) {
            return 2;
        }

        String sqlRequest = "INSERT INTO users(name, password) VALUES(?,?)";

        try {

            PreparedStatement statement = usersDatabase.prepareStatement(sqlRequest);

            statement.setString(1, username);
            statement.setString(2, password);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error while adding new user to table [users]");
            System.out.println(e.getMessage());
            return 0;
        }

        return 1;

    }

    /**
     * Adds new message to special history
     *
     * @param key     unique identifier for history
     * @param message new message
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    public int addNewMessage(String key, int time, String sender, String message) {

        String sqlRequest = "INSERT INTO messages(uKey, time, sender, message) VALUES(?,?,?,?)";

        try {

            PreparedStatement statement = messagesDatabase.prepareStatement(sqlRequest);

            statement.setString(1, key);
            statement.setInt(2, time);
            statement.setString(3, sender);
            statement.setString(4, message);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Failed to add new message.");
            System.out.println(e.getMessage());

            return 0;
        }

        return 1;
    }

    /**
     * Finds messages of special history on given period
     *
     * @param key    unique identifier for history
     * @param period time period we want to get messages from
     * @return {@link List} of requested messages
     */

    public List getMessages(String key, long period) {

        long lowBoard = System.currentTimeMillis() / 1000L - period;

        List<Info> messages = new LinkedList<Info>();

        String sqlRequest = "SELECT uKey, time, sender, message FROM messages";

        try {

            Statement statement = messagesDatabase.createStatement();
            ResultSet record = statement.executeQuery(sqlRequest);

            while (record.next()) {

                if (key.equals(record.getString("uKey"))) {
                    if (lowBoard < record.getInt("time")) {
                        messages.add(new Info(record.getString("sender"),
                                record.getString("message")));
                    }
                }

            }

        } catch (SQLException e) {
            System.out.println("Failed to get messages");
            System.out.println(e.getMessage());

            return null;
        }

        return messages;
    }

    /**
     * Closes both connections to databases
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

    /**
     * Starts both connections to databases
     *
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    public int start() {

        if ((1 == connectToMessagesDatabase()) & (1 == connectToUsersDatabase())) {
            return 1;
        }

        return 0;

    }

    /**
     * Connects to [usersDatabase]
     *
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    private int connectToUsersDatabase() {

        final String usersConnectionString = "jdbc:sqlite:C:\\Users\\gigel\\IdeaProjects\\Tucon\\Database\\db\\users.db";

        try {
            usersDatabase = DriverManager.getConnection(usersConnectionString);
        } catch (Exception e) {
            System.out.println("Can't get connection to [usersDatabase].");
            System.out.println(e.getMessage());
            return 0;
        }

        return 1;
    }

    /**
     * Connects to [messagesDatabase]
     *
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    private int connectToMessagesDatabase() {

        final String messagesConnectionString = "jdbc:sqlite:C:\\Users\\gigel\\IdeaProjects\\Tucon\\Database\\db\\messages.db";

        try {
            messagesDatabase = DriverManager.getConnection(messagesConnectionString);
        } catch (Exception e) {
            System.out.println("Can't get connection to [messagesDatabase].");
            System.out.println(e.getMessage());
            return 0;
        }

        return 1;
    }

    /**
     * Checks if both tables are ready to be used
     *
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    public int checkTables() {

        if ((1 == checkUsersTable()) & (1 == checkMessagesTable())) {
            return 1;
        }

        return 0;
    }

    /**
     * Checks if [users] table exists
     * In case if not creates new one
     *
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    private int checkUsersTable() {
        try {

            String sqlRequest = "CREATE TABLE IF NOT EXISTS users(\n"
                    + "id INTEGER PRIMARY KEY, \n"
                    + "name TEXT NOT NULL, \n"
                    + "password TEXT NOT NULL\n"
                    + ");";

            Statement statement = usersDatabase.createStatement();
            statement.execute(sqlRequest);

        } catch (Exception e) {
            System.out.println("Error while checking [users] table in [usersDatabase]");
            return 0;
        }

        return 1;
    }

    /**
     * Checks if [messages] table exists
     * In case if not creates new one
     *
     * @return 0 - if unsuccessful
     * 1 - if successful
     */

    private int checkMessagesTable() {
        try {

            String sqlRequest = "CREATE TABLE IF NOT EXISTS messages(\n"
                    + "id INTEGER PRIMARY KEY, \n"
                    + "uKey TEXT NOT NULL, \n"
                    + "time INTEGER, \n"
                    + "sender TEXT NOT NULL, \n"
                    + "message TEXT NOT NULL\n"
                    + ");";

            Statement statement = messagesDatabase.createStatement();
            statement.execute(sqlRequest);

        } catch (Exception e) {
            System.out.println("Error while checking [messages] table in [messagesDatabase]");
            return 0;
        }

        return 1;
    }
}
