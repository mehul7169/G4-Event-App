package model;
/*
 * @author Group 4
 * @created 10/9/2021
 * @updated 10/9/2021
 */
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
import java.util.Objects;

/*
 * This is a class used to create an address object
 * to designate the location of where an
 * event is happening in our app.
 */
@DatabaseTable(tableName = "addresses")
public class Address implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer ID;
    @DatabaseField(canBeNull = false)
    private String line1;
    @DatabaseField()
    private String line2;
    @DatabaseField(canBeNull = false)
    private String city;
    @DatabaseField(canBeNull = false)
    private String state;
    @DatabaseField(canBeNull = false)
    private String ZIP;

    /*
     * Default constructor
     */
    public Address() {
        this.line1 = "";
        this.line2 = "";
        this.city = "";
        this.state = "";
        this.ZIP = "";
    }

    /*
    * Non-default constructor
     */
    public Address(String line1, String line2, String city, String state, String ZIP) {
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.ZIP = ZIP;
    }

    /**
     * Gets the first line of the address.
     * @return A string containing line 1 of the address.
     */
    public String getLine1() {
        return line1;
    }

    /**
     * Sets line 1 of address.
     * @param line1 string containing line 1 of the address.
     */
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    /**
     * Gets the second line of the address.
     * @return A string containing line 2 of the address.
     */
    public String getLine2() {
        return line2;
    }

    /**
     * Sets line 2 of address.
     * @param line2 string containing line 2 of the address.
     */
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    /**
     * Gets the city the address is located in.
     * @return A string containing the address city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city the address is located in.
     * @param city string containing city of address.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state the address is located in.
     * @return A string containing the address state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state the address is located in.
     * @param state string containing the address state.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the zipcode of the address.
     * @return A string containing the address zipcode.
     */
    public String getZIP() {
        return ZIP;
    }

    /**
     * Sets the zipcode of the address.
     * @param ZIP A string containing the address zipcode.
     */
    public void setZIP(String ZIP) {
        this.ZIP = ZIP;
    }

    @Override
    /**
     * Combines all of the address fields into 1 address.
     * @return concatenated string of all address fields to be read
     * in a more cohesive way.
     */
    public String toString() {
        if (line2 != null) {
            return (this.line1 + " " + this.line2 + " " + this.city + " " + this.state + " " + this.ZIP);
        }
        return (this.line1 + " " + this.city + " " + this.state + " " + this.ZIP);
    }

    @Override
    /**
     * Checks if two addresses are the same.
     * @param o address object to compare to address.
     * @return true or false based on whether or not all fields of the
     * address are equal to each other.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return line1.equals(address.line1) &&
                Objects.equals(line2, address.line2) &&
                city.equals(address.city) &&
                state.equals(address.state) &&
                ZIP.equals(address.ZIP);
    }

    @Override
    /**
     * Creates address hashcode.
     * @return hashcode of address object based on all fields.
     */
    public int hashCode() {
        return Objects.hash(line1, line2, city, state, ZIP);
    }
}
