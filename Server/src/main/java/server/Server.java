package server;

import database.Database;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;


public class Server extends AbstractServer implements ServerPerformance {
    private HashMap<String, ClientInt> clients;
    public Server() throws RemoteException {
        super(6666);
    }

    /**
     * Method that allows you to receive messages
     *
     * @param message   content of message
     * @param sender    who send the  message
     * @param recipient who get the message
     * @param time      the moment when  you receive new message
     * @return list of messages
     */
    @Override
    public List<Messages> getMessages(String message, String sender, String recipient, Timestamp time) throws RemoteException {

        List<Messages> messages;
        messages = Database.getInstance().getMessages(creatingNewDialog(), time);
        return messages;
    }

    /**
     * Method that allows you to send message
     *
     * @param message   content of message
     * @param sender    who send the  message
     * @param recipient who get the message
     * @param time      the moment when  you send  new message
     * @return true, if the message was sent successfully
     */
    @Override
    public boolean sendMessage(String message, String sender, String recipient, Timestamp time) throws RemoteException {

        Database.getInstance().addNewMessage(creatingNewDialog(), new Messages(sender, message));

        return true;
    }


    /**
     * Method that allows you to create a new dialogue/conversation
     *
     * @return special key of dialog
     */
    @Override
    public UUID creatingNewDialog() throws RemoteException {
        return UUID.randomUUID();
    }

    /**
     * Method that allows checking login and password
     *
     * @param login    login of user
     * @param password password of user
     * @param register checking choice between registration and authorisation
     * @param client   remote object
     * @return true, if check-in was successful
     */
    @Override
    public boolean authorization(String login, UUID password, boolean register, ClientInt client) throws RemoteException {

        if (register) {
            Database.getInstance().authorizeUser(new Authentication(login, password));
        } else {
            Database.getInstance().addNewUser(new Authentication(login, password));
            this.clients.put(login, client);
        }
        return true;
    }


    @Override
    public void launchServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(getServerPort());
            Remote stub = UnicastRemoteObject.exportObject(new Server(), 0);
            registry.rebind("Chat", stub);

        } catch (Exception e) {
            if (log != null) {
                log.log(Level.SEVERE, "don't launch", e);
            }
        }
    }


    @Override
    public void stopServer() throws ServerExitException {
        try {
            UnicastRemoteObject.unexportObject(new Server(), true);
        } catch (RemoteException e) {
            if (log != null) {
                log.log(Level.SEVERE, "don't stop", e);
            }
        }

    }

    public static void main(String[] args) throws Exception {
        new Server().launchServer();
    }
}