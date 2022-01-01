package controller;

import model.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

//import java.awt.desktop.SystemEventListener;
import java.sql.SQLException;
import java.util.*;

import static controller.DaoController.getIndividualsORMLiteDao;

/*
* Class for handling all requests related to the homepage/root view.
 */
public class IndexController {

  /*
  * Method for decoding a user based on who is logged in and creating
  * an instance of Individual based on this user.
  * (Auto-creates individuals)
  * param jwt: the string with the login info used for creating the individual.
   */
  public static void decodeUser(String jwt) throws SQLException, ParseException {
    System.out.println(jwt);
    String payload = jwt.split("\\.")[1];

    JSONParser parser = new JSONParser();

    JSONObject data = (JSONObject) (parser.parse(new String(Base64.getUrlDecoder().decode(payload))));

    //find individual based on email
    System.out.println(data.get("email"));
    List<Individual> inds = DaoController.getIndividualsORMLiteDao().queryForEq("email", data.get("email"));
    Individual indiv;
    try {
      indiv = inds.get(0);

    }
    catch (IndexOutOfBoundsException ignored) {
      indiv = null;
    }

    if (indiv == null){
      Individual i = new Individual(data.get("name").toString(), "", data.get("email").toString());
      getIndividualsORMLiteDao().create(i);
    }

  }

  /*
  * Method for creating the view for the homepage.
  * param req: the get request.
  * return: this new view
   */
  public static ModelAndView indexController(Request req) throws SQLException, ParseException {
    //get all necessary items stored in database and put them in the model
    Map<String, Object> model = new HashMap<>();

    List<SocialEvent> allSoc = DaoController.getSocialEventsORMLiteDao().queryForAll();
    model.put("socialevents", allSoc);

    List<Seminar> allSem = DaoController.getSeminarsORMLiteDao().queryForAll();
    model.put("seminars", allSem);

    List<Event> allEv = DaoController.getEventsORMLiteDao().queryForAll();
    model.put("events", allEv);

    List<Individual> allInd = DaoController.getIndividualsORMLiteDao().queryForAll();
    model.put("individuals", allInd);

    List<Group> allGroups = DaoController.getGroupsORMLiteDao().queryForAll();
    model.put("groups", allGroups);

    model.put("topevent", DaoController.getHottestEvent());

    if (!Objects.equals(req.cookie("loginData"), null) && !Objects.equals(req.cookie("loginData"), "dead")) {
      decodeUser(req.cookie("loginData"));
    }

    return new ModelAndView(model, "public/vmFiles/homepage.vm");
  }

  /*
  * DEBUG MENU METHOD
  * Method for handling get request for logging into (old) app.
  * param req: the request containing the username cookie.
  * return: view of index/debug menu.
   */
  public static ModelAndView loginGetController(Request req) {
    Map<String, Object> model = new HashMap<>();
    if (req.cookie("username") != null)
      model.put("username", req.cookie("username"));
    return new ModelAndView(model, "public/debugVMFiles/index.vm");
  }

  /*
  * DEBUG MENU REQUEST
  * Method for handling post request for logging in (to old app)
  * param req: the request containing the entered username (to be stored as a cookie).
  * param res: the response variable for this request.
  * return: the view of the index/debug menu after logging in.
   */
  public static Response loginPostController(Request req, Response res) {
    String username = req.queryParams("username");
    res.cookie("username", username);
    res.redirect("/index");
    return res;
  }
}
