package com.patel.memorybookproject.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Janaki Patel on 09-12-2017.
 */

public class User {

    private String email;
    private String name;
    private String uid;

    public String getEmail() {
        return email;
    }

    public void setEmail(String fEmail) {
        email = fEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String fName) {
        name = fName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String fUid) {
        uid = fUid;
    }

    public User(String email, String name, String uid) {
        this.email = email;
        this.name = name;
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("email", email);
        result.put("name", name);
        result.put("uid", uid);

        return result;
    }
}
