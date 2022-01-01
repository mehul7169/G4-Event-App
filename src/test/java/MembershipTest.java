import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.*;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

public class MembershipTest {


    private final String URI = "jdbc:sqlite:./EventApp.db";


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class MembershipORMLiteDaoTest {
        private ConnectionSource connectionSource;
        private Dao<Membership, Integer> dao;

        // create a new connection to JBApp database, create "individuals" table, and create a
        // new dao to be used by test cases
        @BeforeAll
        public void setUpAll() throws SQLException {
            connectionSource = new JdbcConnectionSource(URI);
            TableUtils.createTableIfNotExists(connectionSource, Membership.class);
            TableUtils.createTableIfNotExists(connectionSource, Event.class);
            dao = DaoManager.createDao(connectionSource, Membership.class);
        }

        // delete all rows in the employers table before each test case
        @BeforeEach
        public void setUpEach() throws SQLException {
            TableUtils.clearTable(connectionSource, Event.class);
            TableUtils.clearTable(connectionSource, Membership.class);
        }

        // inserting a new record works and the data can be accessed later
        @Test
        public void testCreateReturnsCorrectData() throws SQLException {
            //set up
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Group g = new Group("Track");
            //create instance
            Membership mem = new Membership(g, indiv);
            dao.create(mem);

            Assertions.assertEquals(g.getName(), dao.queryForAll().get(0).getGroup().getName());
            Assertions.assertEquals(indiv, dao.queryForAll().get(0).getIndividual());
        }

        // inserting a multiple records works
        @Test
        public void testInsertMultiple() throws SQLException {

            //set up
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Group g1 = new Group("Track");
            Group g2 = new Group("Cross Country");
            Group g3 = new Group("WiCS");
            Group g4 = new Group("SWE++");

            //create instances
            Membership mem1 = new Membership(g1, indiv);
            Membership mem2 = new Membership(g2, indiv);
            Membership mem3 = new Membership(g3, indiv);
            Membership mem4 = new Membership(g4, indiv);

            dao.create(mem1);
            dao.create(mem2);
            dao.create(mem3);
            dao.create(mem4);

            List<Membership> ls = dao.queryForAll();
            Assertions.assertEquals(4, ls.size());

        }

        // deleting a record works
        @Test
        public void testDelete() throws SQLException {

            //set up
            Individual indiv = new Individual("Sydney", "cs", "sfriede5@jh.edu");
            Group g = new Group("Track");
            //create instance
            Membership mem = new Membership(g, indiv);
            dao.create(mem);

            //delete and make sure it worked
            int cicoID = dao.queryForAll().get(0).getID();
            dao.deleteById(cicoID);

            //no more events in db
            List<Membership> lsRead = dao.queryForAll();
            assert (lsRead.isEmpty());


        }

    }
}
