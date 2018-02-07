package com.patel.memorybookproject.model;

/**
 * Created by Janki Patel on 12/1/2017.
 */

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;


public class AddMemoriesModel implements Serializable {

    private String name;
    private String message;

    private Long date;
    private String imageUrl;

    public AddMemoriesModel() {

    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap<String, Object> toFirebaseObject() {
        HashMap<String, Object> toAdd = new HashMap<String, Object>();
        toAdd.put("name", name);
        toAdd.put("message", message);
        toAdd.put("date", date);

        if (!TextUtils.isEmpty(imageUrl)) {
            toAdd.put("imageUrl", imageUrl);
        }
        return toAdd;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String fImageUrl) {
        this.imageUrl = fImageUrl;
    }
}
