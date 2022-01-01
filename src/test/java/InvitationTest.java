import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class InvitationTest {

    private final String URI = "jdbc:sqlite:./EventApp.db";


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class InvitationOutORMLiteDaoTest {
        private ConnectionSource connectionSource;
        private Dao<Invitation, Integer> dao;

        // create a new connection to JBApp database, create "individuals" table, and create a
        // new dao to be used by test cases
        @BeforeAll
        public void setUpAll() throws SQLException {
            connectionSource = new JdbcConnectionSource(URI);
            TableUtils.createTableIfNotExists(connectionSource, Invitation.class);
            TableUtils.createTableIfNotExists(connectionSource, Event.class);
            dao = DaoManager.createDao(connectionSource, Invitation.class);
        }

        // delete all rows in the employers table before each test case
        @BeforeEach
        public void setUpEach() throws SQLException {
            TableUtils.clearTable(connectionSource, Event.class);
            TableUtils.clearTable(connectionSource, Invitation.class);
        }

        // inserting a new record works and the data can be accessed later
        @Test
        public void testCreateReturnsCorrectData() throws SQLException {
            //set up
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e = new Event("title", null, add, date_time, indiv, true, "cs", 100, "group");

            //create instance
            Invitation inv = new Invitation(e, indiv);
            dao.create(inv);

            Assertions.assertEquals(e, dao.queryForAll().get(0).getEvent());
            Assertions.assertEquals(indiv, dao.queryForAll().get(0).getIndividual());
        }

        // inserting a multiple records works
        @Test
        public void testInsertMultiple() throws SQLException {
            //set up
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e1 = new Event("title1", null, add, date_time, indiv, true, "cs", 100, "group");
            Event e2 = new Event("title2", null, add, date_time, indiv, true, "cs", 100, "group");
            Event e3 = new Event("title3", null, add, date_time, indiv, true, "cs", 100, "group");
            Event e4 = new Event("title4", null, add, date_time, indiv, true, "cs", 100, "group");

            //create instances
            Invitation inv1 = new Invitation(e1, indiv);
            Invitation inv2 = new Invitation(e2, indiv);
            Invitation inv3 = new Invitation(e3, indiv);
            Invitation inv4 = new Invitation(e4, indiv);

            dao.create(inv1);
            dao.create(inv2);
            dao.create(inv3);
            dao.create(inv4);

            List<Invitation> ls = dao.queryForAll();
            Assertions.assertEquals(4, ls.size());

        }

        // deleting a record works
        @Test
        public void testDelete() throws SQLException {
            //set up
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e = new Event("my event", null, add, date_time, indiv, true, "cs", 100, "group");

            //create instance
            Invitation inv = new Invitation(e, indiv);
            dao.create(inv);

            //delete and make sure it worked
            int cicoID = dao.queryForAll().get(0).getID();
            dao.deleteById(cicoID);

            //no more events in db
            List<Invitation> lsRead = dao.queryForAll();
            assert (lsRead.isEmpty());


        }

    }
}

