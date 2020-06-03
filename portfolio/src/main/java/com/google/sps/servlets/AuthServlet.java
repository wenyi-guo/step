package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import org.json.simple.JSONObject;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Only logged-in users can see the form
    UserService userService = UserServiceFactory.getUserService();
    boolean isLoggedIn;
    response.setContentType("application/json;");
    JSONObject obj = new JSONObject();
    if (userService.isUserLoggedIn()) {
        isLoggedIn = true;
        String urlToRedirectToAfterUserLogsOut = "/";
        String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
        obj.put("loginStatus", isLoggedIn);
        obj.put("URL", logoutUrl);
        response.getWriter().println(obj);
    } else {
        isLoggedIn = false;
        String urlToRedirectToAfterUserLogsIn = "/";
        String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
        obj.put("loginStatus", isLoggedIn);
        obj.put("URL", loginUrl);
        response.getWriter().println(obj);
    }
    
  }
}
