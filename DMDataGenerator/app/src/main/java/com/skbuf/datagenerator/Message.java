package com.skbuf.datagenerator;

import android.location.Location;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Message implements Comparable {
    final static String MSG_TYPE_FRIENDS = "friends";
    final static String MSG_TYPE_PREF = "safe-location-preferences";
    final static String MSG_TYPE_LOCATION = "client-location";
    final static String MSG_TYPE_SAFE = "safe-location";
    final static String MSG_TYPE_REQUEST = "safe-location-request";
    final static String MSG_TYPE_DELETE_CLIENTS = "delete-clients";
    final static String MSG_TYPE_DELETE_SAFE_LOCATIONS = "delete-safe-locations";

    String msgtype;

    // messageType = friends
    String name;
    List<String> friends = new ArrayList<String>();

    // messageType = safe-location-preferences
    private List<String> criteria;
    private List<Float> pairwiseComparisons;

    // messageType = client-location + safe
    private Long timestamp;
    private Location location;
    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double longitude;

    public Message(String type) {
        this.msgtype = type;
    }

    public Message(String type, Long timestamp, String name) {
        this.msgtype = type;
        this.timestamp =  timestamp;
        this.name = name;
    }

    public Message(String type, String username, List<String> friends) {
        this.name = username;
        this.friends = friends;
        this.msgtype = type;
    }

    public Message(String type, String username, List<String> criteria, List<Float> pref) {
        this.name = username;
        this.criteria = criteria;
        this.pairwiseComparisons = pref;
        this.msgtype = type;
    }

    public Message(String type, String username, Long timestamp, Location location) {
        this.name = username;
        this.timestamp = timestamp;
        this.location = location;
        this.msgtype = type;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void decreaseTimestampBy(Long offset) {
        this.timestamp = this.timestamp - offset;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Float> getPref() {
        return this.pairwiseComparisons;
    }

    public void setPref(List<Float> pref) {
        this.pairwiseComparisons = pref;
    }

    public List<String> getCriteria() {
        return this.criteria;
    }

    public void setCriteria(List<String> criteria) {
        this.criteria = criteria;
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getMessageType() {
        return msgtype;
    }

    public void setMessageType(String messageType) {
        this.msgtype = messageType;
    }

    public String toString() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageSerializer()).create();
        return gson.toJson(this);
    }

    public static class MessageSerializer implements JsonSerializer<Message> {
        public JsonElement serialize(final Message msg, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("msgtype", new JsonPrimitive(msg.getMessageType()));

            if (msg.msgtype.equals(Message.MSG_TYPE_FRIENDS)) {
                result.add("name", new JsonPrimitive(msg.getUsername()));
                JsonArray friendsArray = new JsonArray();
                for (String friend : msg.getFriends())
                    friendsArray.add(new JsonPrimitive(friend));
                result.add("friends", friendsArray);
            } else if (msg.msgtype.equals(Message.MSG_TYPE_PREF)) {
                result.add("name", new JsonPrimitive(msg.getUsername()));

                JsonArray criteriaArray = new JsonArray();
                for (String criteria : msg.getCriteria())
                    criteriaArray.add(new JsonPrimitive(criteria));
                result.add("criteria", criteriaArray);

                JsonArray prefArray = new JsonArray();
                for (Float friend : msg.getPref())
                    prefArray.add(new JsonPrimitive(friend));
                result.add("pairwiseComparisons", prefArray);
            } else if (msg.msgtype.equals(Message.MSG_TYPE_LOCATION) ||
                    msg.msgtype.equals(Message.MSG_TYPE_SAFE)) {
                result.add("name", new JsonPrimitive(msg.getUsername()));
                result.add("timestamp", new JsonPrimitive(msg.getTimestamp()));
                result.add("latitude", new JsonPrimitive(msg.getLatitude()));
                result.add("longitude", new JsonPrimitive(msg.getLongitude()));
            } else if (msg.msgtype.equals(Message.MSG_TYPE_REQUEST)) {
                result.add("timestamp", new JsonPrimitive(msg.getTimestamp()));
                result.add("name", new JsonPrimitive(msg.getUsername()));
            }

            return result;
        }
    }

    @Override
    public int compareTo(Object msg) {
        Long timestampOther = ((Message)msg).getTimestamp();

        return (int)(this.timestamp - timestampOther);
    }

}
