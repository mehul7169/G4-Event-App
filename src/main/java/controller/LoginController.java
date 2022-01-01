package controller;

import spark.ModelAndView;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

/*
* Class for handling all requests related to logins.
 */
public class LoginController {

  /*
  * Method for handling login get request
  * param req: request containing the username cookie
  * return: the view of the login page
   */
  public static ModelAndView loginGet(Request req) {
    Map<String, Object> model = new HashMap<>();
    if (req.cookie("username") != null)
      model.put("username", req.cookie("username"));
    return new ModelAndView(model, "public/vmFiles/login.vm");
  }
}
