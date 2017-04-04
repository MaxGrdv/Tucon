import database.Database;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DatabaseTests {

    @Test
    public void testAuthorization() {

        Database db = Database.getInstance();

        int res = db.authorizeUser("gigel773", "12345");

        assertEquals(1, res);

    }

    @Test
    public void testStop() {

        Database db = Database.getInstance();

        int res = db.stop();

        assertEquals(1, res);

    }

    @Test
    public void testAdding() {

        Database db = Database.getInstance();

        int res = db.addNewUser("gigel773", "12345");

        assertEquals(1, res);

    }

}
