package controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import model.*;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
* Class for handling all requests related to events.
 */
public class EventsController {

  /*
   * Helper method for creating events (when creating event subtypes)
   * Creates a new instance of the (general) Event.
   * param address: the event's address
   * param title: the event's title
   * param description: the event's description
   * param datetime: the event's date and time
   * param host: the event's host (an individual)
   * param priv: whether the event is private (true if it is, false if it's public)
   * param tag: the CSV string of the event's atatched content tags.
   * param capacity: the event's max capacity
   */
  public static Event createEvent(EventDetails details) {
    Event event = new Event(details.title, details.description, details.address,
            details.date_time, details.host, details._private, details.tag, details.capacity, details.group);
    System.out.println("created: " + event);
    try {
      DaoController.getEventsORMLiteDao().create(event);
      return event;
    } catch (java.sql.SQLException e) {
      return null;
    }
  }

  /*
   * Helper method for creating a new address instance.
   * req: request containing necessary parameters for new instance
   * return: newly created instance.
   */
  public static Address formatAddress(Request req) {
    String addLine1 = req.queryParams("addLine1");
    String addLine2 = req.queryParams("addLine2");
    String city = req.queryParams("city");
    String state = req.queryParams("state");
    String zip = req.queryParams("zip");

    return new Address(addLine1, addLine2, city, state, zip);
  }

  /*
   * DEBUG MENU REQUEST
   * Helper method for handling events get request (seeing list of all stored events)
   * return: view with list of all events.
   */
  public static ModelAndView eventsGet() throws SQLException {
    Map<String, Object> model = new HashMap<>();

    List<Event> ls_all = DaoController.getEventsORMLiteDao().queryForAll();
    model.put("events", ls_all);

    return new ModelAndView(model, "public/debugVMFiles/events.vm");
  }

  /*
   * Helper method for auto-inviting all individuals to a public event.
   * Note: this won't send an email because that would be very cumbersome.
   * param e: the public event that every individual user will get an invitation to.
   */
  public static void inviteAllPublicEvent(Event e) throws SQLException {

    Dao indDao = DaoController.getIndividualsORMLiteDao();
    Dao invDao = DaoController.getInvitationsORMLiteDao();

    //for all individuals, create an invitation for them to this event
    List<Individual> indList = indDao.queryForAll();
    for (Individual i : indList) {
      Invitation invitation = new Invitation(e, i);
      invDao.create(invitation);
    }
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling add events get request (page with add event form)
   * return: view with add event form.
   */
  public static ModelAndView addEventsGet() {
    Map<String, Object> model = new HashMap<String, Object>();
    return new ModelAndView(model, "public/debugVMFiles/addevents.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling social events get request (seeing list of all stored social events)
   * return: view with list of all social events.
   */
  public static ModelAndView socialGet() throws SQLException {
    List<User> ls = DaoController.getSocialEventsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("socialevents", ls);
    return new ModelAndView(model, "public/debugVMFiles/socialevents.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling add social events get request (page with add social event form)
   * return: view with add social event form.
   */
  public static ModelAndView addSocialGet() throws SQLException {
    List<Individual> ls1 = DaoController.getIndividualsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("individuals", ls1);
    return new ModelAndView(model, "public/debugVMFiles/addsocialevents.vm");
  }

  /*
   * Method for handling social events post request (creates a new instance of Social Event)
   * Also adds it to the general events table.
   * param req: the request containing the necessary info for creating the social event
   * param res: the request response variable
   * return: a json of the newly created instance
   */
  public static String socialPost(Request req, Response res) throws SQLException, ParseException, org.json.simple.parser.ParseException {

    System.out.println("in soc post");
    //collect all info from request and transform when necessary
    EventDetails details = extractEventDetails(req);

    System.out.println(details);

    //create new general event
    Event createdEvent = createEvent(details);

    //create social event
    SocialEvent socialEvent = new SocialEvent(details.title, details.description, details.address, details.date_time,
            details.host, details._private, details.tag, details.capacity, details.group);
    DaoController.getSocialEventsORMLiteDao().create(socialEvent);

    //handle error of not being able to create the event
    if (createdEvent == null) {
      System.out.println("ERROR: COULD NOT CREATE EVENT");
    } else {
      //event was successfully created: auto invite to public event
      if (!details._private) {
        inviteAllPublicEvent(createdEvent);
      }
    }

    //set res status and finish
    res.status(201);
    res.type("application/json");
    res.redirect("/");
    return new Gson().toJson(socialEvent.toString());
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling seminars get request (seeing list of all stored seminars)
   * return: view with list of all seminars.
   */
  public static ModelAndView seminarsGet() throws SQLException {
    List<User> ls = DaoController.getSeminarsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("seminars", ls);
    return new ModelAndView(model, "public/debugVMFiles/seminars.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling add seminars get request (page with add seminar form)
   * return: view with add seminar form.
   */
  public static ModelAndView addSeminarsGet() throws SQLException {
    List<Individual> ls1 = DaoController.getIndividualsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("individuals", ls1);
    return new ModelAndView(model, "public/debugVMFiles/addseminars.vm");
  }

  /*
   * Method for handling seminars post request (creates a new instance of Seminar)
   * Also adds it to the general events table.
   * param req: the request containing the necessary info for creating the seminar
   * param res: the request response variable
   * return: a json of the newly created instance
   */
  public static String seminarPost(Request req, Response res) throws SQLException, ParseException, org.json.simple.parser.ParseException {

    System.out.println("in sem post");
    //collect all info from request and transform when necessary
    EventDetails details = extractEventDetails(req);

    System.out.println(details);

    //get parameters specific to seminars
    String speaker = req.queryParams("speaker");
    String field = req.queryParams("field");
    String school = req.queryParams("school");

    //create new general event
    Event createdEvent = createEvent(details);

    //create new seminar
    Seminar seminar = new Seminar(details.title, details.description, details.address,
            details.date_time, details.host, details._private, speaker, school, field, details.tag, details.capacity, details.group);
    DaoController.getSeminarsORMLiteDao().create(seminar);

    //handle error of not being able to create the event
    if (createdEvent == null) {
      System.out.println("ERROR: COULD NOT CREATE EVENT");
    } else {
      //event was successfully created - auto invite to public event
      if (!details._private) {
        inviteAllPublicEvent(createdEvent);
      }
    }

    //set res status and finish
    res.status(201);
    res.type("application/json");
    res.redirect("/");
    return new Gson().toJson(seminar.toString());
  }

  /*
   * Method for handling social event delete request (deletes instance).
   * param req: the request containing the event's ID.
   * param res: the request response variable
   * return: a blank json
   */
  public static String socialEventDelete(Request req, Response res) throws SQLException, ParseException {

    //find event to delete
    String socEventID = req.queryParams("ID");
    List<SocialEvent> socEvents = DaoController.getSocialEventsORMLiteDao().queryForEq("ID", Integer.parseInt(socEventID));

    //attempt to delete event if found in table
    int del = 0;
    if (socEvents != null && !socEvents.isEmpty()) {
      del = DaoController.getSocialEventsORMLiteDao().deleteById(socEvents.get(0).getID());
    }

    //set res status and finish
    res.status(200);
    res.type("application/json");
    if (del > 0) {
      return new Gson().toJson(socEvents.get(0).toString());
    }
    return new Gson().toJson("{}");
  }

  /*
   * Method for handling seminar delete request (deletes instance).
   * param req: the request containing the event's ID.
   * param res: the request response variable
   * return: a blank json
   */
  public static String seminarDelete(Request req, Response res) throws SQLException, ParseException {
    //find event in table
    String semID = req.queryParams("ID");
    List<Seminar> sems = DaoController.getSeminarsORMLiteDao().queryForEq("ID", Integer.parseInt(semID));

    //attempt to delete if found in table
    int del = 0;
    if (sems != null && !sems.isEmpty()) {
      del = DaoController.getSeminarsORMLiteDao().deleteById(sems.get(0).getID());
    }

    //set res status and finish
    res.status(200);
    res.type("application/json");
    if (del > 0) {
      System.out.println("deleted seminar");
      return new Gson().toJson(sems.get(0).toString());
    }
    return new Gson().toJson("{}");
  }

  /*
   * Method for handling general event delete request (deletes instance).
   * param req: the request containing the event's title (which must be unique).
   * param res: the request response variable
   * return: a blank json
   */
  public static String eventDelete(Request req, Response res) throws SQLException, ParseException {

    //find event being deleted based on unique title
    String eventTitle = req.queryParams("title");
    List<Event> evs = DaoController.getEventsORMLiteDao().queryForEq("title", eventTitle);

    //attempt to delete if a matching instance was found in the table
    int del = 0;
    if (evs != null && !evs.isEmpty()) {
      del = DaoController.getEventsORMLiteDao().deleteById(evs.get(0).getID());
    }

    //we also need to delete entries in the CICO and Invitation tables corresponding to this event!
    int evID  = evs.get(0).getID();
    deleteEvCICOs(evID);
    deleteEvInvs(evID);

    //set res status and finish
    res.status(200);
    res.type("application/json");
    if (del > 0) {
      System.out.println("deleted event");
      return new Gson().toJson(evs.get(0).toString());
    }
    return new Gson().toJson("{}");
  }

  /*
  * Delete all entries in the CheckInCheckOut table associated with a particular event.
  * param evID: the id of the event of interest
   */
  public static void deleteEvCICOs(int evID) throws SQLException{
    List<CheckInCheckOut> cicos = DaoController.getCheckInCheckOutORMLiteDao().queryForEq("event_id", evID);

    //delete all associated cico instances
    if (cicos != null && !cicos.isEmpty()) {
      for (int i = 0; i < cicos.size(); i++) {
        DaoController.getCheckInCheckOutORMLiteDao().deleteById(cicos.get(i).getID());
      }
    }

  }

  /*
   * Delete all entries in the Invitation table associated with a particular event.
   * param evID: the id of the event of interest
   */
  public static void deleteEvInvs(int evID) throws SQLException{
    List<Invitation> invs = DaoController.getInvitationsORMLiteDao().queryForEq("event_id", evID);

    //delete all associated invitation instances
    if (invs != null && !invs.isEmpty()) {
      for (int i = 0; i < invs.size(); i++) {
        DaoController.getInvitationsORMLiteDao().deleteById(invs.get(i).getID());
      }
    }
  }

  /*
   * Method for handling create event get request (displaying "Create Event" in navbar)
   * return: the view with the create event form
   */
  public static ModelAndView createEventGet() throws SQLException {
    List<Individual> ls1 = DaoController.getIndividualsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("individuals", ls1);
    List<Event> ls2 = DaoController.getEventsORMLiteDao().queryForAll();
    model.put("events", ls2);
    List<Group> ls3 = DaoController.getGroupsORMLiteDao().queryForAll();
    model.put("groups", ls3);
    return new ModelAndView(model, "public/vmFiles/createEvent.vm");
  }

  /* Helper method for parsing an event post request and extracting
  * details of general events from it
  * param req: the post request
  * return: an EventDetails instance with the extracted parameters
   */
  private static EventDetails extractEventDetails(Request req) throws SQLException, ParseException, org.json.simple.parser.ParseException {

    //collect all info from request and transform when necessary
    String title = req.queryParams("title");
    String description = req.queryParams("description");

    String capacityStr = req.queryParams("capacity");
    int capacity = Integer.MAX_VALUE;
    if (!capacityStr.isEmpty()) {
      capacity = Integer.parseInt(capacityStr);
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
    Date datetime = format.parse(req.queryParams("datetime"));

    boolean priv = (req.queryParams("priv").equals("true"));
    Individual host = IndividualController.getLoggedIn(req.cookie("loginData"));
    Address address = formatAddress(req);
    String tag = req.queryParams("tags");
    String groupID = req.queryParams("group");
    String groupName = "";
    if (groupID != null && !groupID.isEmpty()) {
      Group group = (Group) DaoController.getGroupsORMLiteDao().queryForId(groupID);
      groupName = group.getName();
    }

    //create new address
    DaoController.getAddressesORMLiteDao().create(address);

    EventDetails details = new EventDetails(title, description, address, datetime, host, priv, tag, capacity, groupName);
    return details;
  }

  /*
  * Private inner wrapper class for carrying around event details
  * Helps eliminate duplicate code with extracting params from a request
   */
  private static class EventDetails {
    public String title;
    public String description;
    public Address address;
    public Date date_time;
    public Individual host;
    public boolean _private;
    public String tag;
    public Integer capacity;
    public String group;

    /*
    * Non-default constructor
     */
    public EventDetails(String title, String description, Address address,
                        Date date_time, Individual host, boolean _private,
                        String tag, Integer capacity, String group) {
      this.title = title;
      this.description = description;
      this.address = address;
      this.date_time = date_time;
      this.host = host;
      this._private = _private;
      this.tag = tag;
      this.capacity = capacity;
      this.group = group;
    }

    @Override
    public String toString() {
      return "EventDetails{" +
              "title='" + title + '\'' +
              ", description='" + description + '\'' +
              ", address=" + address +
              ", date_time=" + date_time +
              ", host=" + host +
              ", _private=" + _private +
              ", tag='" + tag + '\'' +
              ", capacity=" + capacity +
              ", group=" + group +
              '}';
    }
  }

}
