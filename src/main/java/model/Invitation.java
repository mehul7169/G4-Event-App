package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "invitations")
public class Invitation {

    /**
     * This is a class used to represent an invitation of an individual to an event
     * @param ID the ID of this invitation within the database
     * @param event the event this individual is invited to
     * @param individual the individual the invitation pertains to
     */
    @DatabaseField(generatedId = true)
    Integer ID;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnDefinition = "int references events(ID)")
    Event event;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnDefinition = "int references individuals(ID)")
    Individual individual;

    /*
     * Non-default constructor
     */
    public Invitation(Event event, Individual individual) {
        this.event = event;
        this.individual = individual;
    }

    /*
     * Default constructor
     */
    public Invitation() {}

    public Individual getIndividual() {
        return this.individual;
    }

    public Event getEvent(){ return this.event; }

    /* Getters */

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    /*
    * To-String method for custom class.
     */
    @Override
    public String toString() {
        return "Invitation{" +
                "ID=" + ID +
                ", event=" + event +
                ", individual=" + individual +
                '}';
    }
}
