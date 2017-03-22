import java.util.LinkedList;
import java.util.TreeMap;
import java.util.logging.Logger;

public class Server implements Logic {

    private TreeMap<String, String> newMessages;

    /**
     * Gets message and writes it to getter's new messages
     *
     * @param getterName - who gets message
     * @param message    - text of message
     * @return 1 - everything alright
     * 0 - something went wrong
     */
    public Object sendMessage(String getterName, String message) {

        try {

            newMessages.put(getterName, message);

        } catch (Exception e) {

            Logger.getLogger(e.getMessage());
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
    public Object getMessage(String requester) {

        LinkedList<String> requesterMessages = new LinkedList<String>();

        while (true) {

            if (newMessages.containsKey(requester)) {

                requesterMessages.add(newMessages.get(requester));

            } else {

                break;

            }

        }

        return requesterMessages;
    }
}
