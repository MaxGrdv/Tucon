package model;

import server.Info;
import server.Logic;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;


public class Client {

    private String username = "";

    private Logic server;

    public Client(String username) throws Exception {

        Registry registry = LocateRegistry.getRegistry("localhost", 7777);

        server = (Logic) registry.lookup("Tucon");

        this.username = username;

    }

    /**
     * Gets new messages for the client
     *
     * @return LinkedList {@link LinkedList<Info>} contains new messages
     */

    public LinkedList<Info> getMessages() {

        LinkedList<Info> newMessages = new LinkedList<Info>();


        try {

            newMessages.addAll(server.getMessage(username));

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return null;

        }

        return newMessages;


    }

    /**
     * Sends new message to getter
     *
     * @param usernameGetter username of getter
     * @param message        new message
     * @return 1 - successful
     * 0 - unsuccessful
     */

    public int sendMessage(String usernameGetter, String message) {

        try {

            server.sendMessage(username, usernameGetter, message);
            return 1;

        } catch (RemoteException e) {

            System.out.println(e.getMessage());
            return 0;
        }

    }

}
