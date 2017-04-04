import database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
