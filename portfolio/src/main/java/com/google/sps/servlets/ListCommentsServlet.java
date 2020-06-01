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
  int page = 5;
  int num = 5;
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String numString = request.getParameter("num");
    String pageString = request.getParameter("page");
    String order = request.getParameter("order");
    try {
      num = Integer.parseInt(numString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + numString);
      num = 5;
    }
    try {
      page = Integer.parseInt(pageString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + pageString);
      page = 1;
    }

    Query query;
    if(order.equals("descending")){
        query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    }
    else{
        query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    }
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<Comment>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String userName = (String) entity.getProperty("userName");
      String email = (String) entity.getProperty("email");
      String content = (String) entity.getProperty("content");
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(userName, email, content, timestamp);
      comments.add(comment);
    }

    if(comments.size() >= page*num){
        comments = comments.subList((page-1)*num, page*num);
    }
    else if(comments.size() >= (page-1)*num){
        comments = comments.subList((page-1)*num, comments.size());
    }
    else{
        comments.clear();
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }
}