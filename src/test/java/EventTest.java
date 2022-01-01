import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventTest {

    private final String URI = "jdbc:sqlite:./EventApp.db";

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class EventORMLiteDaoTest {
        private ConnectionSource connectionSource;
        private Dao<Event, Integer> dao;

        // create a new connection to JBApp database, create "individuals" table, and create a
        // new dao to be used by test cases
        @BeforeAll
        public void setUpAll() throws SQLException {
            connectionSource = new JdbcConnectionSource(URI);
            TableUtils.createTableIfNotExists(connectionSource, Event.class);
            dao = DaoManager.createDao(connectionSource, Event.class);
        }

        // delete all rows in the employers table before each test case
        @BeforeEach
        public void setUpEach() throws SQLException {
            TableUtils.clearTable(connectionSource, Event.class);
        }

        // inserting a new record where title is null must fail, the reason being
        // there is a non-null constraint on the "title" column in "events" table!
        @Test
        public void testCreateTitleNull() {
            //create a new event instance
            Individual host = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e = new Event(null, "description", add, date_time, host, true, "cs", 100, "group");
            // try to insert into event table. This must fail!
            Assertions.assertThrows(SQLException.class, () -> dao.create(e));
        }


        // deleting an event is successful
        @Test
        public void testDeleteEvent() throws SQLException {
            //create a new event instance
            Individual host = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e = new Event("title", "description", add, date_time, host, true, "cs", 100, "group");
            // insert and delete
            dao.create(e);
            int ID = dao.queryForEq("title", "title").get(0).getID();
            dao.deleteById(ID);

            //no more events in db
            List<Event> lsRead = dao.queryForAll();
            assert(lsRead.isEmpty());
        }



        // inserting a new record where description is null must pass
        @Test
        public void testCreateDescriptionNullSucceeds() throws SQLException {
            //create a new event instance
            Individual host = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e = new Event("title", null, add, date_time, host, true, "cs", 100, "group");
            // try to insert into event table. This must succeed!
            dao.create(e);
            Assertions.assertEquals("cs", dao.queryForEq("title", "title").get(0).getTag());
        }

        // inserting a new record where title is not unique (already have an event with that title) must fail, the reason being
        // there is a unique constraint on the "title" column in "events" table!
        @Test
        public void testCreateTitleUnique() throws SQLException {
            //create a new event instance
            Individual host = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e = new Event("lecture", "description", add, date_time, host, true, "cs", 100, "group");
            dao.create(e);
            //create another event with the same title
            Event cp = new Event("lecture", "diff description", add, date_time, host, true, "engineering", 100, "group");

            // try to insert into event table. This must fail!
            Assertions.assertThrows(SQLException.class, () -> dao.create(cp));
        }

        // insert multiple event records, and assert they were indeed added!
        @Test
        public void testReadMutipleEvents() throws SQLException {
            //params needed for creating event
            Individual host = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            // create multiple new event instances
            List<Event> lsCreate = new ArrayList<>();
            lsCreate.add(new Event("party", "description", add, date_time, host, true, "greek-life", 100, "group"));
            lsCreate.add(new Event("speech", "description", add, date_time, host, true, "cs", 100, "group"));
            lsCreate.add(new Event("lecture", "description", add, date_time, host, true, "math", 100, "group"));
            lsCreate.add(new Event("rager", "description", add, date_time, host, true, "dance", 10000, "group"));
            // try to insert them into events table. This must succeed!
            dao.create(lsCreate);
            // read all events
            List<Event> lsRead = dao.queryForAll();
            // assert all events in lsCreate were inserted and can be read
            assertEquals(lsCreate, lsRead);
        }

        // insert a new event, update its tags, then assert it was indeed updated!
        @Test
        public void testUpdateTag() throws SQLException {
            //params needed for creating event
            Individual host = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            // create a new event instance
            Event e = new Event("party", "description", add, date_time, host, true, "greek-life", 100, "group");
            // try to insert into events table. This must succeed!
            dao.create(e);
            e.setTag("music");
            dao.createOrUpdate(e);
            // assert the tag is updated successfully!
            assertEquals("music", dao.queryForEq("title", "party").get(0).getTag());
        }


    }
}
