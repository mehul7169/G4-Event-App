package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDateTime;
import java.util.Date;

@DatabaseTable(tableName = "seminars")
public class Seminar extends Event {

    /**
     * This is a class used to represent a seminar (event subtype)
     * @param speaker the name of the person speaking at the seminar
     * @param school the school/institution of the person speaking at the seminar
     * @param field the field/discipline of the person speaking at the seminar
     */
    @DatabaseField
    String speaker;
    @DatabaseField
    String school;
    @DatabaseField
    String field;

    /*
    * Non-default constructor
     */
    public Seminar(String title, String description, Address address, Date date_time, Individual host, boolean _private, String speaker, String school, String field, String tag, Integer capacity, String group) {
        super(title, description, address, date_time, host, _private, tag, capacity, group);
        this.speaker = speaker;
        this.school = school;
        this.field = field;
    }

    /*
     * Default constructor
     */
    public Seminar() {}


    /*
     * Getters and setters
     */
    public String getSpeaker() { return speaker; }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getSchool() { return school; }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getField() { return field; }

    public void setField(String field) {
        this.field = field;
    }

    /*
     * Auto-created custom class methods.
     */
    @Override
    public String toString() {
        return "Event{" +
                "title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", date_time=" + super.getDate_time().toString()+
                ", private=" + super.is_private() +
                ", speaker=" + speaker +
                ", school=" + school +
                ", field=" + field +
                '}';
    }

    @Override
    public String getDetails() {
        return super.getDetails();
    }
}
