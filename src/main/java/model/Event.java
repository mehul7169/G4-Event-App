package model;
/**
 * @author Group 4
 * @created 10/9/2021
 * @updated 10/9/2021
 */

import java.util.Date;
import java.util.Objects;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "events")
public class Event {
    /**
     * This is a class used to represent an event object created
     * by a host group or individual user, and can be either public
     * or private.
     * @param title the title of the event
     * @param description description of activities at the event, what kind of event
     * @param address address object that indicates where event is happening
     * @param date_time date and time event is occurring
     * @param host user object who created the event
     * @param _private toggle for whether the event is open or closed to the public
     * @param group the optional group representing this event
     * @param numAttendees the current number of people at this event (continuously updated)
     */

    @DatabaseField(generatedId = true)
    private Integer ID;
    @DatabaseField(canBeNull = false, unique = true)
    private String title;
    @DatabaseField()
    private String description;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnDefinition = "int references addresses(ID) on delete cascade on UPDATE CASCADE")
    private Address address;
    @DatabaseField(canBeNull = false)
    private Date date_time;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true,
            foreignAutoRefresh = true, columnDefinition = "integer references user(id) " +
            "on delete cascade on update cascade")
    private Individual host;
    @DatabaseField(canBeNull = false)
    private boolean _private;
    @DatabaseField
    private String tag;
    @DatabaseField
    private Integer capacity;
    @DatabaseField
    private String groupName;
    @DatabaseField
    private int numAttendees;


    /*
    * Default constructor
    */
    public Event() {}

    /*
     * Non-default constructor
     */
    public Event(String title, String description, Address address, Date date_time, Individual host, boolean _private, String tag, Integer capacity, String group) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.date_time = date_time;
        this.host = host;
        this._private = _private;
        this.tag = tag;
        this.capacity = capacity;
        this.groupName = group;
        this.numAttendees = 0;

    }

    /*
    * Getters and setters.
     */
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the title of the event.
     * @return A string containing the title of event.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title of event to designated string.
     * @param title string containing event title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the event description.
     * @return string of event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description of event to designated string.
     * @param description string containing event description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets address object associated with event.
     * @return address object of event
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets address of event to address object.
     * @param address object containing event location
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Gets date and time an event is happening
     * @return date object containing date and time of event
     */
    public Date getDate_time() {
        return date_time;
    }

    /**
     * Sets date and time of event to Date object.
     * @param date_time date object
     */
    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }

    public int getNumAttendees() {
        return numAttendees;
    }

    public void setNumAttendees(int numAttendees) {
        this.numAttendees = numAttendees;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Gets the user that is hosting/created the event
     * @return user object host
     */
    public Individual getHost() {
        return host;
    }

    /**
     * Sets a user object as the host of the event
     * @param host user object
     */
    public void setHost(Individual host) {
        this.host = host;
    }

    /**
     * Check whether an event is public or private
     * @return true/false if group is private or not
     */
    public boolean is_private() {
        return _private;
    }

    /**
     * Set event to public or private
     * @param _private boolean object indicating if event is open/closed
     */
    public void set_private(boolean _private) {
        this._private = _private;
    }

    public int getID() {
        return this.ID;
    }

    /**
     * Gets the event tag
     * @return ContentTags object tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets a ContentTags object as tag of event
     * @param
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getGroup() {
        return groupName;
    }

    public void setGroup(String group) {
        this.groupName = group;
    }


    /*
    * Auto-created methods for custom classes.
     */

    @Override
    public String toString() {
        return "Event{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", address=" + address +
                ", date_time=" + date_time +
                ", host=" + host +
                ", _private=" + _private +
                ", tag='" + tag + '\'' +
                ", capacity=" + capacity +
                ", group=" + groupName +
                '}';
    }

    /*
    * Get the details of an event (address and capacity)
    * Used for different views.
    * return: the details string.
     */
    public String getDetails() {
        if (this.groupName != null && !this.groupName.isEmpty()) {
            return " Address: " + address.toString() +
                    " \r\n Group: " + groupName;
        } else {
            return " Address: " + address.toString();
        }

    }

    /**
     * Compare two events and return true if all event fields are
     * equal, and false otherwise.
     * @param o event object to compare to event
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return _private == event._private &&
                Objects.equals(title, event.title) &&
                Objects.equals(description, event.description) &&
                Objects.equals(address, event.address) &&
                Objects.equals(date_time, event.date_time) &&
                Objects.equals(host, event.host) &&
                Objects.equals(tag, event.tag);
    }


    /**
     * Creates event hashcode.
     * @return hashcode of event object based on all fields.
     */
    public int hashCode() {
        return Objects.hash(title, description, address, date_time, host, _private, tag);
    }
}
