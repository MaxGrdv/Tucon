import org.junit.Test;
import server.Authentication;
import server.Messages;
import server.Server;

import java.util.UUID;

import static org.junit.Assert.assertEquals;


public class ServerTest {


    @Test
    public void testSendMessage() throws Exception {
        Server server = new Server();
        boolean result = server.sendMessage("hello", "sender", "recipient", (int) (System.currentTimeMillis() / 1000L));
        assertEquals(true, result);
    }

    @Test
    public void testCreatingNewDialog() throws Exception {
        Server server = new Server();
        UUID result = server.creatingNewDialog();
        assertEquals(UUID.randomUUID(), result);
    }


    @Test(expected = IllegalArgumentException.class)
    public void doEmptyMessage() throws Exception {
        assertEquals(null, new Messages(-1L, "sender", "message"));
    }

    @Test(expected = NullPointerException.class)
    public void doEmptyAuthentication() throws Exception {
        assertEquals(null, new Authentication(null, UUID.randomUUID()));
    }

    @Test
    public void testLaunchServer() throws Exception {
        Server server = new Server();
        server.launchServer();

    }

}