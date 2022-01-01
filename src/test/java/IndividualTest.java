import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.Individual;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndividualTest {
  private final String URI = "jdbc:sqlite:./EventApp.db";

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class IndividualORMLiteDaoTest {
    private ConnectionSource connectionSource;
    private Dao<Individual, Integer> dao;

    // create a new connection to JBApp database, create "individuals" table, and create a
    // new dao to be used by test cases
    @BeforeAll
    public void setUpAll() throws SQLException {
      connectionSource = new JdbcConnectionSource(URI);
      TableUtils.createTableIfNotExists(connectionSource, Individual.class);
      dao = DaoManager.createDao(connectionSource, Individual.class);
    }

    // delete all rows in the employers table before each test case
    @BeforeEach
    public void setUpEach() throws SQLException {

      TableUtils.clearTable(connectionSource, Individual.class);
    }

    // inserting a new record where name is null must fail, the reason being
    // there is a non-null constraint on the "name" column in "individuals" table!
    @Test
    public void testCreateNameNull() {
      //create a new individual instance
      Individual i = new Individual(null, "sports", "jdelgad9@jhu.edu");
      // try to insert into individuals table. This must fail!
      Assertions.assertThrows(SQLException.class, () -> dao.create(i));
    }

    // insert multiple individual records, and assert they were indeed added!
    @Test
    public void testReadMutipleIndividuals() throws SQLException {
      // create multiple new individual instance
      List<Individual> lsCreate = new ArrayList<>();
      lsCreate.add(new Individual("Jose", "sports", "jdelgad9@jhu.edu"));
      lsCreate.add(new Individual("John", "art", "johnny@jhu.edu"));
      lsCreate.add(new Individual("Jake", "engineering", "jakey@jhu.edu"));
      lsCreate.add(new Individual("Julius", "business", "julius@jhu.edu"));
      // try to insert them into individuals table. This must succeed!
      dao.create(lsCreate);
      // read all individuals
      List<Individual> lsRead = dao.queryForAll();
      // assert all individuals in lsCreate were inserted and can be read
      assertEquals(lsCreate, lsRead);
    }

    // insert a new individual, update its tag, then assert it was indeed updated!
    @Test
    public void testUpdateTag() throws SQLException {
      // create a new individual instance
      Individual i = new Individual("Jose", "sports", "jdelgad9@jhu.edu");
      // try to insert into individuals table. This must succeed!
      dao.create(i);
      i.setTag("business");
      dao.createOrUpdate(i);
      // assert the tag is updated successfully!
      assertEquals("business", dao.queryForEq("email", "jdelgad9@jhu.edu").get(0).getTag());
    }


  }
}
