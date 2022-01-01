package model;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import java.util.Objects;

/*
 * This is a class used for storing check ins/checkouts
 * of individuals into events (along with the related times).
 */
@DatabaseTable(tableName = "checkincheckout")
public class CheckInCheckOut {

    @DatabaseField(generatedId = true)
    Integer ID;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnDefinition = "int references events(ID)")
    Event event;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnDefinition = "int references individuals(ID)")
    Individual individual;
    @DatabaseField(canBeNull = true)
    Date checkInTime;
    @DatabaseField(canBeNull = true)
    Date checkOutTime;

    /*
    * Default constructor
     */
    public CheckInCheckOut() {}

    /*
     * Non-default constructor
     */
    public CheckInCheckOut(Event event, Individual individual, Date checkInTime) {
        this.event = event;
        this.individual = individual;
        this.checkInTime = checkInTime;
    }

    /*
    * Getters
     */
    public Event getEvent() {
        return event;
    }

    public Individual getIndividual() {
        return individual;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public Date getCheckOutTime() {
        return checkOutTime;
    }

    public Integer getID() {
        return ID;
    }

    /*
    * Setters
     */
    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void checkOut(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    /*
    * Auto-created custom class methods.
     */
    @Override
    public String toString() {
        return "CheckInCheckOut{" +
                "ID=" + ID +
                ", event=" + event +
                ", individual=" + individual +
                ", checkInTime=" + checkInTime +
                ", checkOutTime=" + checkOutTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckInCheckOut that = (CheckInCheckOut) o;
        return Objects.equals(ID, that.ID) &&
                Objects.equals(event, that.event) &&
                Objects.equals(individual, that.individual) &&
                Objects.equals(checkInTime, that.checkInTime) &&
                Objects.equals(checkOutTime, that.checkOutTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, event, individual, checkInTime, checkOutTime);
    }
}
