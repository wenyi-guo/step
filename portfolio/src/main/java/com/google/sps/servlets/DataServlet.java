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
import com.google.sps.data.Comment;
import com.google.sps.data.Comments;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {
    private Comments comments = new Comments();
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject commentsdata = new JSONObject();
        commentsdata.put("data", comments);
        String json = convertToJsonUsingGson(commentsdata);
    
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the input from the form.
        Comment userComment = getUserComment(request);

        comments.addComment(userComment);

        // Redirect back to the HTML page.
        response.sendRedirect("/index.html");
    }

   /**
   * Converts a Comments list instance into a JSON string using the Gson library.
   */
  private String convertToJsonUsingGson(JSONObject comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  /** Returns the comment entered by the user. */
  private Comment getUserComment(HttpServletRequest request) {
    // Get the input from the form.
    String userName = request.getParameter("user-name");
    String email = request.getParameter("email");
    String content = request.getParameter("content");

    Comment comment = new Comment(userName, email, content);
    return comment;
  }
}
