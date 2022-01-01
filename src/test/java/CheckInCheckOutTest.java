import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Address;
import model.CheckInCheckOut;
import model.Event;
import model.Individual;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class CheckInCheckOutTest {
    private final String URI = "jdbc:sqlite:./EventApp.db";


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CheckInCheckOutORMLiteDaoTest {
        private ConnectionSource connectionSource;
        private Dao<CheckInCheckOut, Integer> dao;

        // create a new connection to JBApp database, create "individuals" table, and create a
        // new dao to be used by test cases
        @BeforeAll
        public void setUpAll() throws SQLException {
            connectionSource = new JdbcConnectionSource(URI);
            TableUtils.createTableIfNotExists(connectionSource, CheckInCheckOut.class);
            TableUtils.createTableIfNotExists(connectionSource, Event.class);
            dao = DaoManager.createDao(connectionSource, CheckInCheckOut.class);
        }

        // delete all rows in the employers table before each test case
        @BeforeEach
        public void setUpEach() throws SQLException {
            TableUtils.clearTable(connectionSource, Event.class);
            TableUtils.clearTable(connectionSource, CheckInCheckOut.class);
        }

        // inserting a new record works and the data can be accessed later
        @Test
        public void testCreateReturnsCorrectData() throws SQLException {
            //set up
            Date checkIn = new Date(System.currentTimeMillis());
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e = new Event("title", null, add, date_time, indiv, true, "cs", 100, "group");

            //create instance
            CheckInCheckOut cico = new CheckInCheckOut(e, indiv, checkIn);
            dao.create(cico);

            Assertions.assertEquals(e, dao.queryForAll().get(0).getEvent());
            Assertions.assertEquals(indiv, dao.queryForAll().get(0).getIndividual());
        }

        // inserting a multiple records works
        @Test
        public void testInsertMultiple() throws SQLException {

            //set up
            Date checkIn = new Date(System.currentTimeMillis());
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e1 = new Event("title1", null, add, date_time, indiv, true, "cs", 100, "group");
            Event e2 = new Event("title2", null, add, date_time, indiv, true, "cs", 100, "group");
            Event e3 = new Event("title3", null, add, date_time, indiv, true, "cs", 100, "group");
            Event e4 = new Event("title4", null, add, date_time, indiv, true, "cs", 100, "group");

            //create instances
            CheckInCheckOut cico1 = new CheckInCheckOut(e1, indiv, checkIn);
            CheckInCheckOut cico2 = new CheckInCheckOut(e2, indiv, checkIn);
            CheckInCheckOut cico3 = new CheckInCheckOut(e3, indiv, checkIn);
            CheckInCheckOut cico4 = new CheckInCheckOut(e4, indiv, checkIn);

            dao.create(cico1);
            dao.create(cico2);
            dao.create(cico3);
            dao.create(cico4);

            List<CheckInCheckOut> ls = dao.queryForAll();
            Assertions.assertEquals(4, ls.size());

        }

        // deleting a record works
        @Test
        public void testDelete() throws SQLException {

            //set up
            Date checkIn = new Date(System.currentTimeMillis());
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e = new Event("my event", null, add, date_time, indiv, true, "cs", 100, "group");

            //create instance
            CheckInCheckOut cico = new CheckInCheckOut(e, indiv, checkIn);
            dao.create(cico);

            //delete and make sure it worked
            int cicoID = dao.queryForAll().get(0).getID();
            dao.deleteById(cicoID);

            //no more events in db
            List<CheckInCheckOut> lsRead = dao.queryForAll();
            assert(lsRead.isEmpty());


        }
        @Test
        public void testCheckOutTimeIsNullUponCheckIn() throws SQLException {
            //set up
            Date checkIn = new Date(System.currentTimeMillis());
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Address add = new Address("334 E University", null, "Baltimore", "MD", "20688");
            Date date_time = new Date(System.currentTimeMillis());
            Event e = new Event("title", null, add, date_time, indiv, true, "cs", 100, "group");

            //create instance
            CheckInCheckOut cico = new CheckInCheckOut(e, indiv, checkIn);
            dao.create(cico);

            //check to see that the check out time is null
            List<CheckInCheckOut> cicoLS = dao.queryForAll();
            assert(cicoLS.get(0).getCheckOutTime() == null);
        }

    }
}
