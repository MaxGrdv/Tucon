package database;

import server.Info;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private Connection usersDatabase = null;
    private Connection messagesDatabase = null;
    private Logger logger = Logger.getAnonymousLogger();

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
            logger.log(Level.ALL, "No driver found.", e);
        }

        start();
        checkTables();
    }

    private static class DatabaseHolder {
        private final static Database instance = new Database();

        private DatabaseHolder() {
        }
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

        Statement statement = null;
        ResultSet pair = null;

        try {

            statement = usersDatabase.createStatement();
            pair = statement.executeQuery(sqlRequest);

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

            logger.log(Level.ALL, "Failed to find user in database: ", e);
            return -1;

        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                logger.log(Level.ALL, "Failed to close statement: ", e);
            }

            try {
                if (pair != null) {
                    pair.close();
                }
            } catch (SQLException e) {
                logger.log(Level.ALL, "Failed to close result set: ", e);
            }
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
     * 2 - if username is already in database
     */

    public int addNewUser(String username, String password) {

        if ((1 == authorizeUser(username, "1")) ||
                (2 == authorizeUser(username, "1"))) {
            return 2;
        }

        String sqlRequest = "INSERT INTO users(name, password) VALUES(?,?)";
        PreparedStatement statement = null;

        try {

            statement = usersDatabase.prepareStatement(sqlRequest);

            statement.setString(1, username);
            statement.setString(2, password);

            statement.executeUpdate();

        } catch (SQLException e) {

            logger.log(Level.ALL, "Error while adding new user to table [users]", e);
            return 0;

        } finally {
            try {

                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                logger.log(Level.ALL, "Failed to close prepared statement: ", e);
            }
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

    public int addNewMessage(String key, long time, String sender, String message) {

        String sqlRequest = "INSERT INTO messages(uKey, time, sender, message) VALUES(?,?,?,?)";
        PreparedStatement statement = null;

        try {

            statement = messagesDatabase.prepareStatement(sqlRequest);

            statement.setString(1, key);
            statement.setLong(2, time);
            statement.setString(3, sender);
            statement.setString(4, message);

            statement.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.ALL, "Failed to add new message.", e);

            return 0;

        } finally {

            try {

                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                logger.log(Level.ALL, "Failed to close prepared statement: ", e);
            }

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

        Statement statement = null;
        ResultSet record = null;

        try {

            statement = messagesDatabase.createStatement();
            record = statement.executeQuery(sqlRequest);

            while (record.next()) {

                messages = putMessage(messages, key, record, lowBoard);

            }

        } catch (SQLException e) {
            logger.log(Level.ALL, "Failed to get messages", e);
        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                logger.log(Level.ALL, "Failed to close statement: ", e);
            }

            try {
                if (record != null) {
                    record.close();
                }
            } catch (SQLException e) {
                logger.log(Level.ALL, "Failed to close record: ", e);
            }
        }

        return messages;
    }

    /**
     * Puts new message to list which is containing all requested messages
     *
     * @param messages list of messages
     * @param key      unique key of special history
     * @param record   record in database
     * @param lowBoard board of time
     * @return list of messages with added new message
     */

    private List<Info> putMessage(List<Info> messages,
                                  String key,
                                  ResultSet record,
                                  long lowBoard) {
        try {
            if ((key.equals(record.getString("uKey"))) &&
                    (lowBoard < record.getInt("time"))) {

                messages.add(new Info(record.getString("sender"),
                        record.getString("message")));

            }
        } catch (Exception e) {
            logger.log(Level.ALL, "Error while reading DB ", e);
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

        if ((1 == closeUsersDatabaseConnection()) &&
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

            logger.log(Level.ALL, "Can't close connection to [usersDatabase]", e);
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

            logger.log(Level.ALL, "Can't close connection to [messagesDatabase]", e);
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

        if ((1 == connectToMessagesDatabase()) && (1 == connectToUsersDatabase())) {
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

        final String usersConnectionString = "jdbc:sqlite:C:\\Users\\gigel\\IdeaProjects\\Tucon\\Database\\users.db";

        try {
            usersDatabase = DriverManager.getConnection(usersConnectionString);
        } catch (Exception e) {

            logger.log(Level.ALL, "Can't get connection to [usersDatabase].", e);
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

        final String messagesConnectionString = "jdbc:sqlite:C:\\Users\\gigel\\IdeaProjects\\Tucon\\Database\\messages.db";

        try {
            messagesDatabase = DriverManager.getConnection(messagesConnectionString);
        } catch (Exception e) {

            logger.log(Level.ALL, "Can't get connection to [messagesDatabase].", e);
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

        if ((1 == checkUsersTable()) && (1 == checkMessagesTable())) {
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

        Statement statement = null;

        try {

            String sqlRequest = "CREATE TABLE IF NOT EXISTS users(\n"
                    + "id INTEGER PRIMARY KEY, \n"
                    + "name TEXT NOT NULL, \n"
                    + "password TEXT NOT NULL\n"
                    + ");";

            statement = usersDatabase.createStatement();
            statement.execute(sqlRequest);

        } catch (Exception e) {

            logger.log(Level.ALL, "Error while checking [users] table in [usersDatabase]", e);
            return 0;

        } finally {

            try {

                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                logger.log(Level.ALL, "Failed to close statement ", e);
            }
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

        Statement statement = null;

        try {

            String sqlRequest = "CREATE TABLE IF NOT EXISTS messages(\n"
                    + "id INTEGER PRIMARY KEY, \n"
                    + "uKey TEXT NOT NULL, \n"
                    + "time INTEGER, \n"
                    + "sender TEXT NOT NULL, \n"
                    + "message TEXT NOT NULL\n"
                    + ");";

            statement = messagesDatabase.createStatement();
            statement.execute(sqlRequest);

        } catch (Exception e) {

            logger.log(Level.ALL, "Error while checking [messages] table in [messagesDatabase]", e);
            return 0;

        } finally {

            try {

                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                logger.log(Level.ALL, "Failed to close statement ", e);
            }
        }

        return 1;
    }
}
