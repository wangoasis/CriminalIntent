package com.example.yjr.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

public class Crime {

    private static final String JSON_CRIME_UUID = "JSON_CRIME_UUID";
    private static final String JSON_CRIME_TITLE = "JSON_CRIME_TITLE";
    private static final String JSON_CRIME_DATE = "JSON_CRIME_DATE";
    private static final String JSON_CRIME_ISSOLVED = "JSON_CRIME_ISSOLVED";
    private static final String JSON_CRIME_PHOTO = "JSON_CRIME_PHOTO";

    public Crime() {
        ID = UUID.randomUUID();
        date = new Date();
    }

    public Crime(JSONObject jsonObject) throws JSONException {
        ID = UUID.fromString(jsonObject.getString(JSON_CRIME_UUID));
        title = jsonObject.getString(JSON_CRIME_TITLE);
        date = new Date(jsonObject.getLong(JSON_CRIME_DATE));
        isSolved = jsonObject.getBoolean(JSON_CRIME_ISSOLVED);
        if(jsonObject.has(JSON_CRIME_PHOTO))
            photo = new Photo(jsonObject.getString(JSON_CRIME_PHOTO));
    }


    public JSONObject toJSON() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_CRIME_UUID, ID.toString());
        jsonObject.put(JSON_CRIME_TITLE, title);
        jsonObject.put(JSON_CRIME_ISSOLVED, isSolved);
        jsonObject.put(JSON_CRIME_DATE, date.getTime());

        if(photo!=null)
            jsonObject.put(JSON_CRIME_PHOTO, photo);

        return jsonObject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getID() {
        return ID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean isChecked) {
        this.isSolved = isChecked;
    }

    @Override
    public String toString() {
        return this.title;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    private String title;
    private UUID ID;
    private Date date;
    private boolean isSolved;
    private Photo photo;
}
