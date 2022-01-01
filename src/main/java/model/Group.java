package model;
/**
 * @author Group 4
 * @created 10/9/2021
 * @updated 10/9/2021
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;
/*Leaving some code commented because this worked too before. Might need it later if we run into problems with group model.
*/
@DatabaseTable(tableName = "groups")
public class Group {

    /**
     * This is a class used to represent a group, which is a collection
     * of individual users stored in a map.
     */
    @DatabaseField(generatedId = true)
    Integer ID;
    @DatabaseField(canBeNull = false)
    String name;

//    public Group() {
//    }
//
//    public Group(String name) {
//        super(name);
//    }
//
//    @Override
//    public String toString() {
//        return "Group{" +
//                "ID=" + ID +
//                ", name='" + name + '\'' +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Group group = (Group) o;
//        return ID.equals(group.ID) &&
//                name.equals(group.name);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(ID, name);
//    }

    /*
    * Default constructor
     */
    public Group() {}

    /*
     * Non-default constructor
     */
    public Group(String name) {
        this.name = name;
    }

    /*
    * Getters and setters
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
    * Auto-created methods for custom classes.
     */
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
