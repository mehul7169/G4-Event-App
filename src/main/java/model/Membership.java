package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "memberships")
public class Membership {

    /**
     * This is a class used to represent an individual's membership in a group
     * @param ID the ID of this membership within the database
     * @param group the group this individual belongs to
     * @param individual the individual the membership pertains to
     */
    @DatabaseField(generatedId = true)
    Integer ID;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnDefinition = "int references groups(ID)")
    Group group;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnDefinition = "int references individuals(ID)")
    Individual individual;

    /*
     * Default constructor
     */
    public Membership() {}

    /*
     * Non-default constructor
     */
    public Membership(Group group, Individual individual) {
        this.group = group;
        this.individual = individual;
    }

    /*
     * Getters and setters
     */
    public Group getGroup() {
        return this.group;
    }

    public Individual getIndividual() {
        return individual;
    }

    public Integer getID() {
        return ID;
    }
}
