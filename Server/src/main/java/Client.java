import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.Scanner;

public class Client {

    private static String username = "";

    public static void main(String args[]) throws Exception {

        Registry registry = LocateRegistry.getRegistry("localhost", 7777);

        Logic server = (Logic) registry.lookup("Tucon");

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your username: ");
        username = sc.nextLine();

        while (true) {

            LinkedList<Info> newMessages = new LinkedList<Info>();

            System.out.print(">> ");
            String message = sc.nextLine();

            try {

                server.sendMessage(username, "DctWAT", message);

            } catch (RemoteException e) {

                System.out.println(e.getMessage());

            }

            try {

                newMessages = (LinkedList) server.getMessage("DctWAT");

            } catch (Exception e) {

                System.out.println(e.getMessage());

            }

            for (Info each : newMessages) {

                System.out.println(each.sender + ": " + each.message);

            }

        }

    }

}
