package controller;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import model.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* Class for getting all DAOs for the various databases.
 */
public class DaoController {

  /*
  * The JDBC database URI used by the app.
   */
  final static String URI = "jdbc:sqlite:./EventApp.db";

  /*
  * Get User DAO
   */
  public static Dao getUsersORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, User.class);
    return DaoManager.createDao(connectionSource, User.class);
  }

  /*
   * Get Events (general) DAO
   */
  public static Dao getEventsORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, Event.class);
    return DaoManager.createDao(connectionSource, Event.class);
  }

  /*
   * Get Individual DAO
   */
  public static Dao getIndividualsORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, Individual.class);
    return DaoManager.createDao(connectionSource, Individual.class);
  }

  /*
   * Get Group DAO
   */
  public static Dao getGroupsORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, Group.class);
    return DaoManager.createDao(connectionSource, Group.class);
  }

  /*
   * Get Address DAO
   */
  public static Dao getAddressesORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, Address.class);
    return DaoManager.createDao(connectionSource, Address.class);
  }

  /*
   * Get Invitation DAO
   */
  public static Dao getInvitationsORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, Invitation.class);
    return DaoManager.createDao(connectionSource, Invitation.class);
  }

  /*
   * Get Social Event DAO
   */
  public static Dao getSocialEventsORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, SocialEvent.class);
    return DaoManager.createDao(connectionSource, SocialEvent.class);
  }

  /*
   * Get Seminar DAO
   */
  public static Dao getSeminarsORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, Seminar.class);
    return DaoManager.createDao(connectionSource, Seminar.class);
  }

  /*
   * Get Membership DAO
   */
  public static Dao getMembershipsORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, Membership.class);
    return DaoManager.createDao(connectionSource, Membership.class);
  }

  /*
   * Get CheckInCheckOut DAO
   */
  public static Dao getCheckInCheckOutORMLiteDao() throws SQLException {
    ConnectionSource connectionSource = new JdbcConnectionSource(URI);
    TableUtils.createTableIfNotExists(connectionSource, CheckInCheckOut.class);
    return DaoManager.createDao(connectionSource, CheckInCheckOut.class);
  }


  /*
  * Method for calculating the "hotness" of an event based on check-in rate
  * param eventID: the ID of the event being queried.
  * return: the numerical value of its hotness.
   */
  public static double eventHeat(String eventID) throws SQLException {
    Dao cicoDao = getCheckInCheckOutORMLiteDao();
    List<CheckInCheckOut> cicoList = cicoDao.queryForEq("event_id", eventID);
    long cum_time_diff = 0;
    Date now = new Date(System.currentTimeMillis());
    for (CheckInCheckOut c : cicoList) {
      //when check-out time is null, it means this person is at the event
      //(they've checked in since the entry exists but haven't checked out yet)
      if (c.getCheckOutTime() == null) {
        //find how long ago they checked in
        cum_time_diff += now.getTime() - c.getCheckInTime().getTime();
      }
    }
    cum_time_diff = cum_time_diff / 36000;
    double score = 0.0;
    if (cum_time_diff != 0) {
      //score is higher when more people have checked in recently
      score = Math.pow(cicoList.size(), 2) / Math.sqrt((double) cum_time_diff);
    }
    return score;
  }

  /*
  * Method for getting the hottest event based on its calculated score.
  * return: the hottest event.
   */
  public static List<Event> getHottestEvent() throws SQLException {
    List<Event> ls = getEventsORMLiteDao().queryForAll();

    //if there are any events in the database, find the hottest one
    if (ls.size() > 0) {
      List<Event> outputList = new ArrayList<>();
      outputList.add(ls.get(0));
      double maxScore = 0.0;
      //iterate through all events
      for (Event e : ls) {
        //get the hotness score for this event
        double hScore = eventHeat(Integer.toString(e.getID()));
        if (hScore > maxScore) {
          //this has the current highest score out of what we've evaluated
          //make this event the new hottest
          outputList.remove(0);
          outputList.add(e);
          maxScore = hScore;
        }
      }
      //return list containing the single hottest event
      return outputList;
    } else {
      //if no events were stored, return an empty list
      return ls;
    }
  }
}
