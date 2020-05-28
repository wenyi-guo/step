package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

/**
* Comments class for a list of Comment objects
*/
public class Comments{
    List<Comment> comments = new ArrayList<Comment>();

    // Get the comments in the Comment objects
    public List<Comment> getComments(){
        return this.comments;
    }

    // Add a comment to the comments list
    public void addComment(Comment content){
        this.comments.add(content);
    }
}


