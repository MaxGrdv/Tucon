package model;

import server.Info;
import server.Logic;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;


public class Client {

    private String username = "";

    private Logic server;
    private Registry registry;

    public Client(String username) {

        try {
            registry = LocateRegistry.getRegistry("95.37.168.128", 7777);
        } catch (RemoteException e) {

            System.out.println(e.getMessage());
            return;

        }
        try {

            try {
                server = (Logic) registry.lookup("Tucon");
            } catch (NotBoundException e) {

                System.out.println(e.getMessage());
                return;

            }
        } catch (RemoteException e) {

            System.out.println(e.getMessage());
            return;

        }

        this.username = username;

    }

    public String getUsername() {
        return username;
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
