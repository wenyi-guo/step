package com.google.sps.data;

import java.util.ArrayList;
import java.util.List;

/**
* Comment class for a comment object with userName, email, and content
*/
public class Comment{
    long id;
    String userName;
    String email;
    String content;
    long timestamp;

    public Comment(long id, String userName, String email, String content, long timestamp){
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getEmail(){
        return this.email;
    }

    public String getContent(){
        return this.email;
    }
}