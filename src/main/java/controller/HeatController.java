package controller;

import com.google.gson.Gson;
import model.Event;
import spark.ModelAndView;
import spark.Request;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Class for handling all request related to event hotness.
 */
public class HeatController {

  /*
  * DEBUG MENU REQUEST
  * Method for handling get request for getting the current heat score of all events.
  * return: a view containing a list of all events and their calculated heat scores.
   */
  public static ModelAndView heatGet() throws SQLException {
    List<Event> ls = DaoController.getEventsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("events", ls);
    return new ModelAndView(model, "public/debugVMFiles/geteventheat.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling post request for getting an individual event heat.
   * param req: the request containing the ID of the event being queried.
   * return: a json with the event's heat score.
   */
  public static String heatPost(Request req) throws SQLException {
    String eventID = req.queryParams("event");
    Double score = DaoController.eventHeat(eventID);
    return new Gson().toJson(score);
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling get request for getting the current hottest events.
   * return: a view containing a list of the hottest events.
   */
  public static ModelAndView hottestGet() throws SQLException {
    Map<String, Object> model = new HashMap<>();
    model.put("topevent", DaoController.getHottestEvent());
    return new ModelAndView(model, "public/debugVMFiles/gethottestevent.vm");
  }

  /*
  * DEBUG MENU REQUEST
  * Method for handling post request for hottest events
  * (retrieve a list of the hottest events). If no events exist, returns
  * "no events".
  * return: the json of the hottest event list.
   */
  public static String hottestPost() throws SQLException {
    Map<String, Object> model = new HashMap<>();
    List<Event> ls = DaoController.getEventsORMLiteDao().queryForAll();

    //if there are events stored, get the hottest
    if (ls.size() > 0) {
      Event max = ls.get(0);
      double maxScore = 0.0;
      for (Event e : ls) {
        double hScore = DaoController.eventHeat(Integer.toString(e.getID()));
        if (hScore > maxScore) {
          max = e;
          maxScore = hScore;
        }
      }
      model.put("topevent", max);
      return new Gson().toJson(max);
    } else {
      //no events stored
      model.put("topevent", "no events");
      return new Gson().toJson("no events");
    }
  }


}
