import database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * IMPORTANT: delete all databases for correct tests results
 */

public class DatabaseTests {

    private Database db = Database.getInstance();

    @Before
    public void initialize() {
        if (0 == db.start()) {
            System.out.println("Failed to establish connections to databases.");
        }
    }

    @After
    public void end() {
        if (0 == db.stop()) {
            System.out.println("Failed to close connections to databases.");
        }
    }

    @Test
    public void testAuthorizationTrue() {

        db.addNewUser("someone", "something");

        int res = db.authorizeUser("someone", "something");

        assertEquals(1, res);

    }

    @Test
    public void testAuthorizationFalse() {

        int res = db.authorizeUser("anyone", "some");

        assertEquals(0, res);

    }

    @Test
    public void testAddingTrue() {

        int res = db.addNewUser("gigel773", "12345");

        assertEquals(1, res);

    }

    @Test
    public void testAddingFalse() {

        db.addNewUser("gigel", "12345");

        int res = db.addNewUser("gigel", "12345");

        assertEquals(2, res);

    }

    @Test
    public void testCheckingTrue() {

        int res = db.checkTables();

        assertEquals(1, res);

    }

    @Test
    public void testNewMessage() {

        int res = db.addNewMessage("5df", 12345, "somebody", "hello");

        assertEquals(1, res);

    }

    @Test
    public void testGetMessages() {

        db.addNewMessage("o8i", (int) (System.currentTimeMillis() / 1000L), "gigel", "hello");

        Object res = db.getMessages("o8i", 86400);

        assertNotEquals(null, res);

    }
}
