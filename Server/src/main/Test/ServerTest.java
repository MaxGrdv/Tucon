import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServerTest {

    @Test
    public void testSending() {

        Server srv = new Server();

        Object result = srv.sendMessage("gigel773", "DctWAT", "Hello");

        assertEquals(1, result);

    }

}
