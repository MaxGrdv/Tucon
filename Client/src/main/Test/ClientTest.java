import model.Client;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientTest {

    @Test
    public void testSendMessage() throws Exception {

        Client obj = new Client("gigel773");

        int result = obj.sendMessage("Maks", "hello");

        assertEquals(1, result);

    }

}
