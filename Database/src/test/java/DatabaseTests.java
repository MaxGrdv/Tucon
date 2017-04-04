import database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
    public void testAuthorization() {

        int res = db.authorizeUser("gigel773", "12345");

        assertEquals(1, res);

    }

    @Test
    public void testAdding() {

        int res = db.addNewUser("gigel773", "12345");

        assertEquals(1, res);

    }

    @Test
    public void testChecking() {

        int res = db.checkTables();

        assertEquals(1, res);

    }
}
