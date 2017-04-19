package server;

import database.Database;
import org.apache.commons.lang3.StringUtils;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;


public class Server extends AbstractServer implements ServerPerformance {

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
    public List<Messages> getMessages(String message, String sender, String recipient, long time) throws RemoteException {

        if (StringUtils.isEmpty(message)) {
            throw new IllegalArgumentException("Message is empty!");
        }
        if (time <= 0) {
            throw new IllegalArgumentException("Time is not correct!");
        }
        if (StringUtils.isEmpty(sender)) {
            throw new IllegalArgumentException("Sender is empty!");

        }
        if (StringUtils.isEmpty(recipient)) {
            throw new IllegalArgumentException("Recipient is empty!");
        }
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
    public boolean sendMessage(String message, String sender, String recipient, long time) throws RemoteException {

        if (StringUtils.isEmpty(recipient)) {
            throw new IllegalArgumentException("Recipient is empty!");
        }

        Database.getInstance().addNewMessage(creatingNewDialog(), new Messages(time, sender, message));

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
     * @return true, if check-in was successful
     */
    @Override
    public boolean authorization(String login, UUID password, ClientInt newClient) throws RemoteException {

        int authorize = Database.getInstance().authorizeUser(new Authentication(login, password));
        boolean register = false;
        if (authorize == 1) {
            register = true;
        } else if (authorize == 0) {
            ArrayList<ClientInt> clients = new ArrayList<ClientInt>();
            clients.add(newClient);
            Database.getInstance().addNewUser(new Authentication(login, password), clients);
            register = false;
        } else if (authorize == 2) {
            throw new IllegalArgumentException("User is in database and his password is wrong");
        } else if (authorize == -1)
            throw new IllegalArgumentException("Something gone wrong");

        return register;
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