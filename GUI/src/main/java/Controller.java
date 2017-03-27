import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Client;
import server.Info;

import java.util.LinkedList;

public class Controller {

    @FXML
    private Button button;
    @FXML
    private TextField inputMessage;
    @FXML
    private TextField username;
    @FXML
    private TextField getterName;
    @FXML
    private TextArea chat;
    @FXML
    private Button login;

    private Client user;

    @FXML
    public void logIn() throws Exception {

        user = new Client((String) username.getText());

        Thread gettingMessages = new Thread(new Runnable() {

            public void run() {

                while (true) {

                    LinkedList<Info> newMessages = user.getMessages();

                    for (Info each : newMessages) {

                        chat.appendText(each.getSender() + ": " + each.getMessage() + "\n");

                    }
                }
            }
        });

        gettingMessages.start();

    }

    @FXML
    int sendMessage() throws Exception {

        String message = inputMessage.getText();

        chat.appendText(user.getUsername() + ": " + message + "\n");

        inputMessage.setText("");

        if ("".equals(message)) {

            return 0;

        } else {

            user.sendMessage(getterName.getText(), message);

        }

        return 1;

    }

}
