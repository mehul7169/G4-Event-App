package model;
import java.util.Objects;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public abstract class User {
    /**
     * This is a class used to represent an app user (general)
     * @param ID the user database ID
     * @param name the user name
     */
    @DatabaseField(generatedId = true)
    Integer ID;
    @DatabaseField(canBeNull = false)
    String name;

    //@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
   // private Group group;

    /*
    * Default Constructor
     */
    public User() {}

    /*
     * Non-default Constructor
     */
    public User(String name) {
        this.name = name;
    }

    /*
    * Getters and Setters
     */
    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * Auto-created custom class methods.
     */
    @Override
    public String toString() {
        return name;
    }

    public String toStringJSON() {
        return "User{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(ID, user.ID) &&
                Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, name);
    }
}
