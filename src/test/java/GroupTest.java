import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Group;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupTest {

    private final String URI = "jdbc:sqlite:./EventApp.db";

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class GroupORMLiteDaoTest {
        private ConnectionSource connectionSource;
        private Dao<Group, Integer> dao;

        // create a new connection to JBApp database, create "individuals" table, and create a
        // new dao to be used by test cases
        @BeforeAll
        public void setUpAll() throws SQLException {
            connectionSource = new JdbcConnectionSource(URI);
            TableUtils.createTableIfNotExists(connectionSource, Group.class);
            dao = DaoManager.createDao(connectionSource, Group.class);
        }

        // delete all rows in the employers table before each test case
        @BeforeEach
        public void setUpEach() throws SQLException {

            TableUtils.clearTable(connectionSource, Group.class);
        }

        // inserting a new record where name is null must fail, the reason being
        // there is a non-null constraint on the "name" column in "group" table!
        @Test
        public void testCreateNameNull() {
            //create a new individual instance
            Group i = new Group(null);
            // try to insert into individuals table. This must fail!
            Assertions.assertThrows(SQLException.class, () -> dao.create(i));
        }

        // insert multiple group records, and assert they were indeed added!
        @Test
        public void testReadMutipleGroups() throws SQLException {
            // create multiple new individual instance
            List<Group> lsCreate = new ArrayList<>();
            lsCreate.add(new Group("Track"));
            lsCreate.add(new Group("Cross Country"));
            lsCreate.add(new Group("WiCS"));
            lsCreate.add(new Group("SWE++"));
            // try to insert them into groups table. This must succeed!
            dao.create(lsCreate);
            // read all groups
            List<Group> lsRead = dao.queryForAll();
            // assert all groups in lsCreate were inserted and can be read
            assertEquals(4, lsRead.size());
        }

        // delete a group then assert it was indeed deleted!
        @Test
        public void testDelete() throws SQLException {
            // create a new group instance
            Group i = new Group("Track");
            // try to insert into groups table. This must succeed!
            dao.create(i);
            //delete and assert the db is empty after
            int ID = dao.queryForEq("name", "Track").get(0).getID();
            dao.deleteById(ID);

            //no more events in db
            List<Group> lsRead = dao.queryForAll();
            assert(lsRead.isEmpty());
        }
    }
}
