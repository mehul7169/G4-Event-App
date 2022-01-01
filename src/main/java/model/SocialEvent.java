package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDateTime;
import java.util.Date;

@DatabaseTable(tableName = "socialevents")
public class SocialEvent extends Event {

    /**
     * This is a class used to represent a social event (event subtype)
     */

    /*
     * Non-default constructor
     */
    public SocialEvent(String title, String description, Address address, Date date_time, Individual host, boolean _private,  String tag, Integer capacity, String group) {
        super(title, description, address, date_time, host, _private, tag, capacity, group);
    }

    /*
    * Default constructor
     */
    public SocialEvent() {}


    /*
     * Auto-created custom class methods.
     */

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getDetails() {
        return super.getDetails();
    }
}
