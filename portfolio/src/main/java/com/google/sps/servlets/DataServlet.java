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
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    /**
    * The class of type Comment, including user name, email, and content.
    */
    private class Comment{
        String userName;
        String email;
        String content;

        public Comment(String userName, String email, String content){
            this.userName = userName;
            this.email = email;
            this.content = content;
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject comments = new JSONObject();
        ArrayList<Comment> list = new ArrayList<Comment>(); 
        list.add(new Comment("wenyiguo", "wenyiguo@google.com", "First comment!"));
        list.add(new Comment("julialiu", "juliasliu@google.com", "Second comment!"));
        list.add(new Comment("prashantmalyala", "mvprashant@google.com", "Third comment!"));
      
        comments.put("data", list);

        String json = convertToJsonUsingGson(comments);
    
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

   /**
   * Converts a Comments list instance into a JSON string using the Gson library.
   */
  private String convertToJsonUsingGson(JSONObject comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }
}
