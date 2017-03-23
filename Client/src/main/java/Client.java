import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.Scanner;

public class Client {

    private static String username = "";
    private static String usernameGetter = "";

    private static Logic server;

    public static void main(String args[]) throws Exception {

        Registry registry = LocateRegistry.getRegistry("localhost", 7777);

        server = (Logic) registry.lookup("Tucon");

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your username: ");
        username = sc.nextLine();

        System.out.print("Enter getter username: ");
        usernameGetter = sc.nextLine();

        Thread getMessages = new Thread(new Runnable() {

            public void run() {

                LinkedList<Info> newMessages = new LinkedList<Info>();

                while (true) {

                    try {

                        newMessages = (LinkedList) server.getMessage(username);

                    } catch (Exception e) {

                        System.out.println(e.getMessage());

                    }

                    for (Info each : newMessages) {

                        System.out.println(each.sender + ": " + each.message);

                    }

                    newMessages.clear();

                }

            }
        });

        Thread sendMessages = new Thread(new Runnable() {

            public void run() {

                Scanner sc = new Scanner(System.in);

                while (true) {

                    System.out.print(">> ");
                    String message = sc.nextLine();

                    try {

                        server.sendMessage(username, usernameGetter, message);

                    } catch (RemoteException e) {

                        System.out.println(e.getMessage());

                    }

                }
            }

        });

        sendMessages.start();
        getMessages.start();

    }


}
