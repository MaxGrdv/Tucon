package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

public interface Logic extends Remote {

    /**
     * Gets message and writes it to getter's new messages
     *
     * @param getterName - who gets message
     * @param message    - text of message
     * @return 1 - everything alright
     * 0 - something went wrong
     */

    Object sendMessage(String senderName, String getterName, String message) throws RemoteException;

    /**
     * Sends new messages to requester
     *
     * @param requester - who request new messages
     * @return - LinkedList {@link java.util.LinkedList} of new messages for requester
     */

    LinkedList getMessage(String requester) throws RemoteException;

}
