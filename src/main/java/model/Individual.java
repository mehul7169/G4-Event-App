package model;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "individuals")
public class Individual extends User {

    /**
     * This is a class used to represent an individual user profile of the app (a subtype of user)
     * @param tag the content tags associated with this profile (a csv string)
     * @param email the individual user's email
     */

    @DatabaseField
    private String tag;
    @DatabaseField(canBeNull = false)
    private String email;

    /*
     * Default constructor
     */
    public Individual() {
        super("");
        tag = "";
        email = "";
    }

    /**
     * This is a class used to represent an individual, which is an
     * extension of the user class.
     * @param name string of name of the user (ID is already created)
     */
    public Individual(String name, String tag, String email) {
        super(name);
        this.tag = tag;
        this.email = email;
    }

    /*
    * Getters and setters.
     */
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*
    * To-String method for custom class.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
