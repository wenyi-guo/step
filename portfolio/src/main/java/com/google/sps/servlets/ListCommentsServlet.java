// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for listing comments. */
@WebServlet("/get-comments")
public class ListCommentsServlet extends HttpServlet {  
  int num = 5;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {    
    String numString = request.getParameter("num");
    // Convert the input to an int.
    try {
      num = Integer.parseInt(numString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numString);
      num = 5;
    }
    response.sendRedirect("/index.html");
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<Comment>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String userName = (String) entity.getProperty("userName");
      String email = (String) entity.getProperty("email");
      String content = (String) entity.getProperty("content");

      Comment comment = new Comment(userName, email, content);
      comments.add(comment);
    }
    if(comments.size() > num){
        comments = comments.subList(0, num);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }


}
