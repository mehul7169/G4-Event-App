package controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import model.*;
import org.json.simple.parser.ParseException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import java.sql.SQLException;
import java.util.*;

//imports for sending emails
import javax.mail.*;
import javax.mail.internet.*;
import javax.security.auth.login.AccountExpiredException;

/*
* Class for handling requests related to joining events (via invites and check ins/outs)
* or groups (via membership).
 */
public class JoinController {

  // registered email for EventG4 (for sending invite emails)
  private static final String EVENTG4_EMAIL = "eventg4app@gmail.com";

  /*
  * DEBUG MENU REQUEST
  * Method for handling get request for invitations (viewing all invitations).
  * return: view containing a list of all invitations
   */
  public static ModelAndView invitationsGet() throws SQLException {
    List<Invitation> ls = DaoController.getInvitationsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("invitations", ls);
    return new ModelAndView(model, "public/invitations.vm");
  }

  /*
  * Method for handling post request for invitations.
  * Creates and stores a new invitation (inviting an individual to an event).
  * param req: request containing the individual's ID and the event's ID.
  * param res: the request response variable.
  * return: the JSON of the newly created invitation.
   */
  public static String invitationsPost(Request req, Response res) throws SQLException {

    //create invitation
    Event event = (Event) DaoController.getEventsORMLiteDao().queryForId(req.queryParams("event"));
    Individual individual = (Individual) DaoController.getIndividualsORMLiteDao().queryForId(req.queryParams("individual"));
    Invitation invitation = new Invitation(event, individual);
    DaoController.getInvitationsORMLiteDao().create(invitation);

    //send email to invited individual with details
    sendInviteEmail(individual, event);

    //redirect and finish
    res.redirect("/invite");
    return new Gson().toJson(invitation.toString());
  }

  /*
  * Helper method for sending invitation emails to users.
  * When an individual is invited to an event, they will receive an email
  * notifying them, along with the event's details.
  * param in: the individual receiving the invite.
  * param ev: the event they're being invited to.
   */
  public static void sendInviteEmail(Individual in, Event ev) {
    //get name and event title for email body
    String name = in.getName();
    String eventTitle = ev.getTitle();

    // get the recipient email address
    String to = in.getEmail();
    // get the sender email address (the app's email)
    String from = EVENTG4_EMAIL;
    // Mention the SMTP server address. Below Gmail's SMTP server is being used to send email
    String host = "smtp.gmail.com";
    // Get system properties
    Properties properties = System.getProperties();
    // Setup mail server
    properties.put("mail.smtp.host", host);
    properties.put("mail.smtp.port", "465");
    properties.put("mail.smtp.ssl.enable", "true");
    properties.put("mail.smtp.auth", "true");
    // Get the Session object.// and pass username and password
    Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(EVENTG4_EMAIL, "eventG4Group4");
      }
    });
    // Used to debug SMTP issues
    session.setDebug(true);
    try {
      // create a default MimeMessage object.
      MimeMessage message = new MimeMessage(session);
      // set from field of email header
      message.setFrom(new InternetAddress(from));
      // set to field of email header
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      // set the email subject
      message.setSubject("You've been invited to an event!");
      // set the email body
      message.setText(name + ",\n\n" + "You've been invited to " + eventTitle + "!" +"\nDescription: " +
              ev.getDescription() + "\n" + ev.getDate_time().toString() + "\nHost: " + ev.getHost().toString() +
              "\nPlease login into your EventG4 account to view the event. \n\nBest," +
              "\nEventG4");
      System.out.println("sending...");
      // send email
      Transport.send(message);
      System.out.println("Sent message successfully....");
    } catch (MessagingException mex) {
      //catch exceptions
      mex.printStackTrace();
    }
  }

  /*
  * Helper method for figuring out how many people are currently at an event
  * (number of people who have checked in and have yet to check out).
  * Useful for knowing if checking into this event should be disallowed
  * and for displaying the current capacity.
  * param e: the event being queried.
  * return: the number of people at the event.
   */
  public static int howManyPeopleAreAtAnEvent(Event e) throws SQLException {
    System.out.println(e);

    Dao cicoDao = DaoController.getCheckInCheckOutORMLiteDao();
    //get list of all check-in entries that have been created for this event
    List<CheckInCheckOut> checkedInList = cicoDao.queryForEq("event_id", e.getID());
    //keep count of how many people are currently at the event
    int numPeople = 0;
    //iterate over all CICO entries
    for (CheckInCheckOut cico : checkedInList) {
      //a person is at the event if: 1. their check-in time is non-null, and 2. their check-out time is null
      if (cico.getCheckOutTime() == null && cico.getCheckInTime() != null) {
        numPeople ++;
      }
    }
    //return count
    System.out.println(numPeople);
    return numPeople;
  }

  /*
  * Helper method for updating the number of attendees at an event.
  * Updates in both the general event table and the subtype-specific table for this event
  * param event: the event we're updating
   */
  public static void updateNumAttendees(Event event) throws SQLException{
    //get new num attendees
    int numAttendees = howManyPeopleAreAtAnEvent(event);

    //update in general events table
    event.setNumAttendees(numAttendees);
    DaoController.getEventsORMLiteDao().update(event);

    //update in either seminars or social events table (works because event titles must be unique)

    //see if event is a social event
    List<SocialEvent> socList = DaoController.getSocialEventsORMLiteDao().queryForEq("title", event.getTitle());
    if (!socList.isEmpty()) {
      //we found a social event with the same title, so it must be a social event
      SocialEvent soc = socList.get(0);
      //update numAttendees in soc event table
      soc.setNumAttendees(numAttendees);
      DaoController.getSocialEventsORMLiteDao().update(soc);
    }

    //see if this event is a seminar
    List<Seminar> semList = DaoController.getSeminarsORMLiteDao().queryForEq("title", event.getTitle());
    if (!semList.isEmpty()) {
      //we found a seminar with the same title, so it must be a seminar
      Seminar sem = semList.get(0);
      //update numAttendees in soc event table
      sem.setNumAttendees(numAttendees);
      DaoController.getSeminarsORMLiteDao().update(sem);
    }
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling get request for memberships.
   * For viewing all memberships (individuals belonging to groups).
   * return: the view containing a list of all memberships.
   */
  public static ModelAndView membershipsGet() throws SQLException {
    List<Membership> ls = DaoController.getMembershipsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("memberships", ls);
    return new ModelAndView(model, "public/memberships");
  }

  /*
   * Method for handling get request for invitations.
   * Returns a view for the page where a user can select an individual to
   * invite to a selected event ("Send Invite" on navbar).
   * return: the view for sending invites.
   */
  public static ModelAndView inviteToEventGet() throws SQLException {
    List<Individual> ls1 = DaoController.getIndividualsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("individuals", ls1);
    //only provide private events because individuals are already
    //auto-invited to public events
    List<Event> ls2 = DaoController.getEventsORMLiteDao().queryForEq("_private", true);
    model.put("events", ls2);
    return new ModelAndView(model, "public/vmFiles/invite.vm");
  }

  /*
   * Method for handling get request for checking in.
   * Returns a view for the page where a user can select an event to
   * check into ("Check In" on navbar).
   * return: the view for checking in.
   */
  public static ModelAndView checkInEventGet() throws SQLException {
    List<Individual> ls1 = DaoController.getIndividualsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("individuals", ls1);
    List<Event> ls2 = DaoController.getEventsORMLiteDao().queryForAll();
    model.put("events", ls2);
    return new ModelAndView(model, "public/vmFiles/checkInEvent.vm");
  }

  /*
   * Method for handling post request for memberships.
   * Creates and stores a new instance of an individual's membership in a group.
   * param req: request containing the individual's ID and the group's ID.
   * return: the JSON of the newly created membership instance.
   */
  public static String membershipPost(Request req) throws SQLException {

    //get IDs of the group and individual we're creating the membership object for
    Group group = (Group) DaoController.getGroupsORMLiteDao().queryForId(req.queryParams("group"));
    Individual individual = (Individual) DaoController.getIndividualsORMLiteDao().queryForId(req.queryParams("individual"));

    //create new membership
    Membership membership = new Membership(group, individual);
    DaoController.getMembershipsORMLiteDao().create(membership);

    return new Gson().toJson(membership.toString());
  }

  /*
   * DEBUG MENU REQUEST
   * (deprecated) Method for handling get request for invitations.
   * Returns a view for the page where a user can select an individual to
   * invite to a selected event ("Send Invite" on navbar).
   * return: the view for sending invites.
   */
  public static ModelAndView inviteGet() throws SQLException {
    List<Event> ls = DaoController.getEventsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("events", ls);
    List<User> ls2 = DaoController.getIndividualsORMLiteDao().queryForAll();
    model.put("individuals", ls2);
    return new ModelAndView(model, "public/debugVMFiles/inviteindividual.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling get request for adding individuals to groups.
   * Returns a view for the page where a user can select an individual to
   * add to a group.
   * return: the view for adding members
   */
  public static ModelAndView addMemberGet() throws SQLException {
    Map<String, Object> model = new HashMap<>();
    List<User> ls2 = DaoController.getIndividualsORMLiteDao().queryForAll();
    model.put("individuals", ls2);
    List<Group> ls = DaoController.getGroupsORMLiteDao().queryForAll();
    model.put("groups", ls);
    return new ModelAndView(model, "public/vmFiles/addmember.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * (deprecated) Method for handling get request for check ins.
   * Returns a view for the page where a user can select an individual to
   * check into a selected event.
   * return: the view for checking in.
   */
  public static ModelAndView checkInGet(Request req) throws SQLException, ParseException {
    List<Event> ls = new ArrayList<>();
    List<Invitation> inv = DaoController.getInvitationsORMLiteDao().queryForAll();
    for (Invitation i: inv) {
      if (i.getIndividual() == IndividualController.getLoggedIn(req.cookie("loginData"))){
        ls.add(i.getEvent());
      }
    }
    Map<String, Object> model = new HashMap<>();
    model.put("events", ls);
    List<User> ls2 = DaoController.getIndividualsORMLiteDao().queryForAll();
    model.put("individuals", ls2);
    return new ModelAndView(model, "public/debugVMFiles/checkin.vm");
  }

  /*
  * Method for handling check-in post request.
  * Updates check in time to mark an individual as being checked into an event
  * param req: the request containing the event being checked into and the individual
  * checking in. Redirects user back to homepage after checking them in.
  * param res: the response variable for this request.
  * return: the view of the homepage (for redirect)
   */
  public static ModelAndView checkInPost(Request req, Response res) throws SQLException, ParseException {
    System.out.println("in check in post");
    System.out.println(req.queryParams("event"));
    System.out.println(req.queryParams("indCheckIn"));
    //get event being checked into and the individual checking in
    Event event = (Event) DaoController.getEventsORMLiteDao().queryForId(req.queryParams("event"));
    Individual individual = IndividualController.getLoggedIn(req.cookie("loginData"));

    //if this person has already checked into this event before, don't make another object (just update it)
    //check if this event-individual entry already exists
    List<CheckInCheckOut> cicoList = DaoController.getCheckInCheckOutORMLiteDao().queryForEq("event_id", event.getID());
    Boolean entryExists = false;

    for (CheckInCheckOut c : cicoList) {
      if (c.getIndividual().getID() == individual.getID()) {
        entryExists = true;
        //entry exists, so just update checkIn time to current time and update checkout time to null
        System.out.println("this person has checked into this event before");
        Date now = new Date(System.currentTimeMillis());
        c.setCheckInTime(now);
        c.setCheckOutTime(null);
        //update entry in database
        DaoController.getCheckInCheckOutORMLiteDao().update(c);
      }
    }

    if (!entryExists) {
      //no entry existed, so create a new one
      System.out.println("this person has not checked into this event before - making a new instance");
      Date now = new Date(System.currentTimeMillis());
      //checkout time set to null upon construction
      CheckInCheckOut CICO = new CheckInCheckOut(event, individual, now);
      DaoController.getCheckInCheckOutORMLiteDao().create(CICO);
    }

    //update number of people at this event
    updateNumAttendees(event);

    //initiate new view of homepage
    Map<String, Object> model = new HashMap<>();

    //fill with necessary attributes
    List<SocialEvent> allSoc = DaoController.getSocialEventsORMLiteDao().queryForAll();
    model.put("socialevents", allSoc);
    List<Seminar> allSem  = DaoController.getSeminarsORMLiteDao().queryForAll();
    model.put("seminars", allSem);
    List<Event> allEv = DaoController.getEventsORMLiteDao().queryForAll();
    model.put("events", allEv);
    List<Individual> allInd = DaoController.getIndividualsORMLiteDao().queryForAll();
    model.put("individuals", allInd);
    List<Group> allGroups = DaoController.getGroupsORMLiteDao().queryForAll();
    model.put("groups", allGroups);
    model.put("topevent", DaoController.getHottestEvent());

    //return new view
    return new ModelAndView(model, "public/vmFiles/homepage.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling get request for check outs.
   * Returns a view for the page where a user can select an individual to
   * check out of a selected event (that they're already checked into).
   * return: the view for checking out.
   */
  public static ModelAndView checkOutGet(Request req) throws SQLException, ParseException {
    List<Event> ls = new ArrayList<>();
    List<Invitation> inv = DaoController.getInvitationsORMLiteDao().queryForAll();
    for (Invitation i: inv) {
      if (i.getIndividual() == IndividualController.getLoggedIn(req.cookie("loginData"))){
        ls.add(i.getEvent());
      }
    }
    Map<String, Object> model = new HashMap<>();
    model.put("events", ls);
    List<User> ls2 = DaoController.getIndividualsORMLiteDao().queryForAll();
    model.put("individuals", ls2);
    return new ModelAndView(model, "public/vmFiles/checkout.vm");
  }

  /*
   * Method for handling post requests for check outs.
   * Sets an individual's checkout time for this event
   * to mark them as not currently at the event (that they're already checked into).
   * param req: the request containing the individual's ID and the event's ID.
   * param res: the response variable for this request
   * return: the json of this newly created checkout instance.
   */
  public static String checkOutPost(Request req, Response res) throws SQLException, ParseException {

    System.out.println("in check out");
    System.out.println(req.queryParams("indCheckOut"));
    System.out.println(req.queryParams("event"));
    //find check-in-check-out entry in database
    Event event = (Event) DaoController.getEventsORMLiteDao().queryForId(req.queryParams("event"));
    Individual individual = IndividualController.getLoggedIn(req.cookie("loginData"));

    //get list of all CICO items for this individual (will likely be more efficient than searching through an event list)
    List<CheckInCheckOut> cicoLs = DaoController.getCheckInCheckOutORMLiteDao().queryForEq("individual_id", individual.getID());
    System.out.println(cicoLs);
    CheckInCheckOut toUpdate = null;
    //iterate over until we find entry with this event
    for (CheckInCheckOut c : cicoLs) {
      if (c.getEvent().getID() == event.getID()) {
        //found it! collect this entry so we can update it
        toUpdate = c;
        break;
      }
    }
    //update entry in database
    if (toUpdate != null) {
      //set check out time to now and set check in time to null (so they cannot check out again later)
      Date now = new Date(System.currentTimeMillis());
      toUpdate.setCheckOutTime(now);
      toUpdate.setCheckInTime(null);
      DaoController.getCheckInCheckOutORMLiteDao().update(toUpdate);
    }

    //update number of people at this event
    updateNumAttendees(event);

    res.redirect("/");
    return new Gson().toJson(toUpdate.toString());
  }

  /*
  * DEBUG MENU REQUEST
  * Method for handling a get request for seeing who's invited to an event.
  * return: a view containing all events and all the individuals who're invited to them.
   */
  public static ModelAndView invitedListGet() throws SQLException {
    List<Event> ls = DaoController.getEventsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("events", ls);
    return new ModelAndView(model, "public/vmFiles/findwhoscheckedin.vm");
  }

  /*
  * Method for handling a post request for getting a list of individuals
  * checked into an event.
  * param req: the request containing the event being queried's ID.
  * return: a json of the list of individuals.
   */
  public static String checkedInListPost(Request req) throws SQLException {
    //get list of all CICO entries related to this event
    String eventID = req.queryParams("event");
    List<CheckInCheckOut> cicoList = DaoController.getCheckInCheckOutORMLiteDao().queryForEq("event_id", eventID);

    //array for holding individuals checked into the event
    List<Individual> indList = new ArrayList<>();
    //iterate over all entries and populate indList
    for (CheckInCheckOut c : cicoList) {
      //this person is checked in only if their check in time in nonnull and checkout time is null
      if (c.getCheckOutTime() == null && c.getCheckInTime() != null) {
        indList.add(c.getIndividual());
      }
    }
    //return list
    return new Gson().toJson(indList);
  }

  /*
  * DEBUG MENU REQUEST
  * Method for handling get requests for seeing who's invited to an event
  * return: a view containing all events
   */
  public static ModelAndView invitedSearchGet() throws SQLException {
    List<Event> ls = DaoController.getEventsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("events", ls);
    return new ModelAndView(model, "public/vmFiles/findwhosinvited.vm");
  }

  /*
  * Method for handling a post request for seeing who's invited to an event.
  * param req: the request containing the event of interest.
  * return: a json of the list of individual's who've been invited to the event.
   */
  public static String invitedSearchPost(Request req) throws SQLException {
    //get list of all invitation entries  with this event
    String eventID = req.queryParams("event");
    List<Invitation> invlist = DaoController.getInvitationsORMLiteDao().queryForEq("event_id", eventID);
    //list for holding invited individuals
    List<Individual> indList = new ArrayList<>();
    //populate indList
    for (Invitation i : invlist) {
      indList.add(i.getIndividual());
    }
    //return list
    return new Gson().toJson(indList);
  }
}
