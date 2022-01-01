package controller;

import model.*;
import spark.ModelAndView;
import spark.Request;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Class for handling requests related to searching/filtering through the event feed.
 */
public class SearchController {

  /*
  * Handle get request for filtered events based on content tags.
  * param req: the get request containing the desired content tag to filter events by.
  * return: the view containing the results of this filter.
   */
  public static ModelAndView filterTagGet(Request req) throws SQLException {
    //initiate view
    Map<String, Object> model = new HashMap<>();

    //get filter params
    String tag = req.queryParams("tag");

    //if null, return everything
    if (tag == null) {
      List<SocialEvent> allSoc = DaoController.getSocialEventsORMLiteDao().queryForAll();
      List<Seminar> allSem = DaoController.getSeminarsORMLiteDao().queryForAll();
      model.put("socialevents", allSoc);
      model.put("seminars", allSem);

    } else {
      //param is not null
      //NOTE: we need to do .contains() because the tags are stored as CSV strings with multiple tag values
      // i.e. cannot just do queryForEq

      tag = tag.toLowerCase();

      //find social events with this tag
      List<Event> allSoc = DaoController.getSocialEventsORMLiteDao().queryForAll();
      List<Event> socMatch = findEventsWithTag(allSoc, tag);

      //find seminars with this tag
      List<Event> allSem = DaoController.getSeminarsORMLiteDao().queryForAll();
      List<Event> semMatch = findEventsWithTag(allSem, tag);

      //put in model
      model.put("socMatch", socMatch);
      model.put("semMatch", semMatch);
    }

    //put all other necessary attributes in this view
    List<Individual> allIndiv = DaoController.getIndividualsORMLiteDao().queryForAll();
    model.put("individuals", allIndiv);
    List<Event> allEvents = DaoController.getEventsORMLiteDao().queryForAll();
    model.put("events", allEvents);
    List<Group> allGroups = DaoController.getGroupsORMLiteDao().queryForAll();
    model.put("groups", allGroups);

    //return new view of displayed filter results
    return new ModelAndView(model, "public/vmFiles/homepageFilterByTag.vm");
  }

  /*
  * Helper method for getting a list of events with a specific content tag.
  * (Clears duplicate code for getting lists of event subtypes)
  * param all: the list of all events (of this subtype)
  * param tag: the tag of interest
  * return: a list of events (a subset of all) with this tag
   */
  public static List<Event> findEventsWithTag(List<Event> all, String tag) {
    List<Event> matches = new ArrayList<>();
    for (Event e : all) {
      String tags = e.getTag();
      if (tags != null) {
        //the csv string of tags for this event contains the tag of interest
        if (tags.contains(tag)) {
          matches.add(e);
        }
      }
    }
    return matches;
  }

  /*
   * Handle get request for filtered events based on title/description.
   * param req: the get request containing the desired keywords to search events by.
   * return: the view containing the results of this search.
   */
  public static ModelAndView filterText(Request req) throws SQLException {
    //initiate new view
    Map<String, Object> model = new HashMap<>();

    //retrieve search param
    String searchString = req.queryParams("searchString");

    //if search string is null or empty, return all events
    if (searchString == null || searchString.isEmpty()) {
      List<SocialEvent> allSoc = DaoController.getSocialEventsORMLiteDao().queryForAll();
      List<Seminar> allSem = DaoController.getSeminarsORMLiteDao().queryForAll();
      model.put("socialevents", allSoc);
      model.put("seminars", allSem);

    } else {
      //search string is not null
      searchString = searchString.toLowerCase();

      //find matching social events
      List<Event> allSoc = DaoController.getSocialEventsORMLiteDao().queryForAll();
      List<Event> socMatch = findEventsWithSearchString(allSoc, searchString);

      //find matching seminars
      List<Event> allSem = DaoController.getSeminarsORMLiteDao().queryForAll();
      List<Event> semMatch = findEventsWithSearchString(allSem, searchString);

      //put these in model
      model.put("socialevents", socMatch);
      model.put("seminars", semMatch);
    }

    //figure out what to put for topEvent
    //if this is an empty or null search, just put all hottest events
    if (searchString == null || searchString.isEmpty()) {
      model.put("topevent", DaoController.getHottestEvent());
    } else {
      //otherwise, put only the hottest events that match this search string
      List<Event> allHottest = DaoController.getHottestEvent();
      List<Event> hottestMatch = findEventsWithSearchString(allHottest, searchString);
      model.put("topevent", hottestMatch);
    }

    //put all other necessary attributes in view
    List<Individual> allIndiv = DaoController.getIndividualsORMLiteDao().queryForAll();
    model.put("individuals", allIndiv);
    List<Event> allEvents = DaoController.getEventsORMLiteDao().queryForAll();
    model.put("events", allEvents);
    List<Group> allGroups = DaoController.getGroupsORMLiteDao().queryForAll();
    model.put("groups", allGroups);

    //return new view
    return new ModelAndView(model, "public/vmFiles/homepage.vm");
  }

  /*
   * Helper method for getting a list of events with a specific search string in their title/description.
   * (Clears duplicate code for getting lists of event subtypes)
   * param all: the list of all events (of this subtype)
   * param ss: the search string of interest
   * return: a list of events (a subset of all) with this search string
   */
  public static List<Event> findEventsWithSearchString(List<Event> all, String ss) {
    List<Event> matches = new ArrayList<>();
    for (Event e : all) {
      String e_title = e.getTitle().toLowerCase();
      String e_description = e.getDescription().toLowerCase();
      if (e_title.contains(ss) || e_description.contains(ss)) {
        //some part of this event's title or description contains the search keyword/string
        matches.add(e);
      }
    }
    return matches;
  }
}
