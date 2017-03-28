package server;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.TreeMap;

public class Server implements Logic {

    private TreeMap<String, Info> newMessages = new TreeMap<String, Info>();

    /**
     * Gets message and writes it to getter's new messages
     *
     * @param getterName - who gets message
     * @param message    - text of message
     * @return 1 - everything alright
     * 0 - something went wrong
     */
    public Object sendMessage(String senderName, String getterName, String message) {

        try {

            newMessages.put(getterName, new Info(senderName, message));
            System.out.println("New message from " + getterName);
            System.out.println("Message: " + message);

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
            return null;

        }

        return 1;
    }

    /**
     * Sends new messages to requester
     *
     * @param requester - who request new messages
     * @return - LinkedList {@link LinkedList} of new messages for requester
     */
    public LinkedList<Info> getMessage(String requester) {

        LinkedList<Info> requesterMessages = new LinkedList<Info>();

        while (true) {

            if (newMessages.containsKey(requester)) {

                requesterMessages.add(newMessages.get(requester));
                newMessages.remove(requester);

            } else {

                break;

            }

        }

        return requesterMessages;
    }

    public static void main(String args[]) throws Exception {

        // Create something like registration form
        Registry registry = LocateRegistry.createRegistry(9090);

        // Create new object for server
        final Logic server = new Server();

        // Create stub to bind new client
        Remote stub = UnicastRemoteObject.exportObject(server, 0);

        // Bind new client to some name
        registry.bind("Tucon", stub);

        while (true) {

            Thread.sleep(Integer.MAX_VALUE);

        }

    }

}
