package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

/**
* Comment class for a comment object with userName, email, and content
*/
public class Comment{
    String userName;
    String email;
    String content;

    public Comment(String userName, String email, String content){
        this.userName = userName;
        this.email = email;
        this.content = content;
    }
}