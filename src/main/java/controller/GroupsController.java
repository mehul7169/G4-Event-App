package controller;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import model.*;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Class for handling all requests related to groups.
 */
public class GroupsController {

  /*
  * DEBUG MENU REQUEST
  * Method for handling get request for groups
  * return: view containing a list of all groups.
   */
  public static ModelAndView groupsGet() throws SQLException {
    List<User> ls = DaoController.getGroupsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("groups", ls);
    return new ModelAndView(model, "public/debugVMFiles/groups.vm");
  }

  /*
   * DEBUG MENU REQUEST
   * Method for handling get request for adding groups
   * return: view containing a form for creating a new group.
   */
  public static ModelAndView addGroupGet() throws SQLException {
    List<Group> ls = DaoController.getGroupsORMLiteDao().queryForAll();
    Map<String, Object> model = new HashMap<>();
    model.put("groups", ls);
    List<Individual> ls1 = DaoController.getIndividualsORMLiteDao().queryForAll();
    model.put("individuals", ls1);
    return new ModelAndView(model, "public/vmFiles/addGroup.vm");
  }

  /*
   * Method for handling get request for adding groups ("Add Group" on navbar).
   * return: view containing a form for creating a new group.
   */
  public static ModelAndView addGroupsGet() {
    Map<String, Object> model = new HashMap<String, Object>();
    return new ModelAndView(model, "public/debugVMFiles/addgroups.vm");
  }

  /*
   * Method for handling post request for adding groups (creates a new group).
   * param req: the request containing the group name.
   * return: json of the newly created group
   */
  public static String groupsPost(Request req, Response res) throws SQLException {
    //get group info from request and create group
    String name = req.queryParams("name");
    Group g = new Group(name);
    DaoController.getGroupsORMLiteDao().create(g);

    //set res status and return json
    res.status(201);
    res.type("application/json");
    res.redirect("/groups");
    return new Gson().toJson(g.toString());
  }

  /*
   * Method for handling post request for deleting a group instance.
   * param req: the request containing the group ID.
   * param res: the response variable for the request.
   * return: a blank json.
   */
  public static String groupDelete(Request req, Response res) throws SQLException, ParseException {
    //get hold of the group being deleted
    String groupID = req.queryParams("ID");
    List<Group> groups = DaoController.getGroupsORMLiteDao().queryForEq("ID", Integer.parseInt(groupID));
    int del = 0;

    //delete this group if it was found
    if (groups != null  && !groups.isEmpty()) {
      del = DaoController.getGroupsORMLiteDao().deleteById(groups.get(0).getID());
    }

    //set res status and return json
    res.status(200);
    res.type("application/json");
    if (del > 0) {
      return new Gson().toJson(groups.get(0).toString());
    }
    return new Gson().toJson("{}");
  }

  /*
   * Method for retrieving a list of group members (individuals).
   * param req: the request containing the group ID.
   * return: a json string of the individual members of this group.
   */
  public static String getGroupMembers(Request req) throws SQLException, ParseException {

    //get ID of the group in question and find all corresponding entries in
    //membership table (memberships associated with this group)
    String groupID = req.queryParams("group");
    List<Membership> memlist = DaoController.getMembershipsORMLiteDao().queryForEq("group_id", groupID);

    //list for holding the members of this group
    List<Individual> indList = new ArrayList<>();
    //iterate through all memberships and add the corresponding individual to our list
    for (Membership m : memlist) {
      indList.add(m.getIndividual());
    }
    //return list of members
    return new Gson().toJson(indList);
  }
}
