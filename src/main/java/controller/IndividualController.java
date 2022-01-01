package controller;

import com.google.gson.Gson;
import model.Event;
import model.Individual;
import model.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import java.sql.SQLException;
import java.util.*;

import static controller.DaoController.getIndividualsORMLiteDao;

/*
* Class for handling all requests related to individuals.
 */
public class IndividualController {

  /*
  * DEBUG MENU REQUEST
  * Method for handling get request for viewing all registered individuals.
  * return: the view containing a list of all individuals.
   */
  public static ModelAndView individualGet() throws SQLException {
    List<User> allInd = getIndividualsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("individuals", allInd);
    return new ModelAndView(model, "public/debugVMFiles/individuals.vm");
  }

  /*
   * Method for handling get request for viewing the individual page.
   * (profile icon on navbar)
   * param req: the get request (need for cookies)
   * return: the view with the individual page's content.
   */
  public static ModelAndView individualPageGet(Request req) throws SQLException, ParseException{
    List<Individual> allInd = getIndividualsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    Individual loggedIn = IndividualController.getLoggedIn(req.cookie("loginData"));
    model.put("user", loggedIn);
    return new ModelAndView(model, "public/vmFiles/individualPage.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling get request for the page where a user can create a new individual.
   * return: the view containing an add individual form.
   */
  public static ModelAndView addIndividualsGet() {
    Map<String, Object> model = new HashMap<String, Object>();
    return new ModelAndView(model, "public/debugVMFiles/addindividuals.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling get request for the page where a user can edit individual's content tags.
   * return: the view containing an edit individual form.
   */
  public static ModelAndView editIndividualsGet() {
    Map<String, Object> model = new HashMap<String, Object>();
    return new ModelAndView(model, "public/debugVMFiles/editindividuals.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling get request for the page where you select an individual
   * to get a recommended event based on content tags.
   * return: the view containing a list of individuals to select.
   */
  public static ModelAndView recommendedEventsGet() throws SQLException {
    List<Individual> allInd = DaoController.getIndividualsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("users", allInd);
    return new ModelAndView(model, "public/debugVMFiles/recommendedEvents.vm");
  }

  /*
  * Method for handling post request for getting recommended events based on a
  * given individual's content tags (displays the list of recommendations.
  * param req: the request containing the individual's ID.
  * param res: the response variable for the request.
  * return: the view containing the list of recommended events.
   */
  public static ModelAndView recommendedEventsPost(Request req, Response res) throws SQLException, ParseException {
    //figure out who we're collecting recommendations for
    String name = getLoggedIn(req.cookie("loginData")).getName();
    List<Individual> allInd = DaoController.getIndividualsORMLiteDao().queryForAll();
    Individual user = new Individual();
    for (Individual individual : allInd) {
      if (name.equals(individual.getName())) {
        user = individual;
      }
    }

    //tokenize user tags to search for matches
    String userTags = user.getTag();
    String[] tokens = userTags.split(",");

    // get list of events who have content tags that the user has
    // attached to their profile
    List<Event> recc = new ArrayList<>();
    List<Event> allEvents = DaoController.getEventsORMLiteDao().queryForAll();
    for (Event event : allEvents) {
      String eventTag = event.getTag();
      for (String tok : tokens) {
        if (eventTag.contains(tok)) {
          recc.add(event);
        }
      }
    }

    //return new view of recommendation results
    res.status(201);
    Map<String, Object> model = new HashMap<>();

    //if no event was found, just return first event in list
    if(recc.size() == 0 && allEvents.size() != 0) {
      recc.add(allEvents.get(0));
    }

    model.put("events", recc);
    return new ModelAndView(model, "public/vmFiles/homepageR.vm");
  }


  /*
  * Method for handling post request for individuals (creates a new individual profile).
  * param req: the request contianing the necessary info for creating an individual instance.
  * param res: the response varibale for this request.
  * return: the json of the newly created individual.
   */
  public static String individualsPost(Request req, Response res) throws SQLException {
    //extract info from request
    String name = req.queryParams("name");
    String tags = req.queryParams("tags");
    String email = req.queryParams("email");

    //create new individual and return json
    Individual i = new Individual(name, tags, email);
    getIndividualsORMLiteDao().create(i);
    res.status(201);
    res.type("application/json");
    res.redirect("/individuals");
    return new Gson().toJson(i.toString());
  }

  /*
   * Method for handling update request for individuals (edits their tags).
   * param req: the request containing the idenitfying info of the individual being updated.
   * and the new tags.
   * param res: the response variable for this request.
   * return: the json of the newly updated individual.
   */
  public static String individualUpdate(Request req, Response res) throws SQLException, org.json.simple.parser.ParseException {
    //get identifying info
    String tags = req.queryParams("tags");

    //find individual based on email
    Individual indiv = IndividualController.getLoggedIn(req.cookie("loginData"));



    //update this individual's tags
    indiv.setTag(tags);
    int upd;
    upd = DaoController.getIndividualsORMLiteDao().update(indiv);
    //update res status and return json
    res.status(200);
    res.type("application/json");
    if (upd > 0) {
      return new Gson().toJson(indiv.toString());
    }
    res.redirect("/individualPage");
    return new Gson().toJson("{}");
  }

  public static Individual getLoggedIn(String jwt) throws org.json.simple.parser.ParseException, SQLException {
    String payload = jwt.split("\\.")[1];

    JSONParser parser = new JSONParser();

    JSONObject data = (JSONObject) (parser.parse(new String(Base64.getUrlDecoder().decode(payload))));

    List<Individual> inds = DaoController.getIndividualsORMLiteDao().queryForEq("email", data.get("email"));
    return inds.get(0);
  }

}
