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

public class Message {
    final static String MSG_TYPE_FRIENDS = "friends";
    final static String MSG_TYPE_PREF = "safe-location-preferences";
    final static String MSG_TYPE_LOCATION = "client-location";
    final static String MSG_TYPE_SAFE = "safe-location";
    final static String MSG_TYPE_REQUEST = "safe-location-request";

    String messageType;


    // messageType = friends
    String username;
    List<String> friends = new ArrayList<String>();

    // messageType = safe-location-preferences
    private List<String> criteria;
    private List<Float> pref;

    // messageType = client-location + safe
    private Long timestamp;
    private Location location;

    public Message(String type, Long timestamp) {
        this.messageType = type;
        this.timestamp = timestamp;
    }

    public Message(String type, String username, List<String> friends) {
        this.username = username;
        this.friends = friends;
        this.messageType = type;
    }

    public Message(String type, String username, List<String> criteria, List<Float> pref) {
        this.username = username;
        this.criteria = criteria;
        this.pref = pref;
        this.messageType = type;
    }

    public Message(String type, String username, Long timestamp, Location location) {
        this.username = username;
        this.timestamp = timestamp;
        this.location = location;
        this.messageType = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Float> getPref() {
        return this.pref;
    }

    public void setPref(List<Float> pref) {
        this.pref = pref;
    }

    public List<String> getCriteria() {
        return this.criteria;
    }

    public void setCriteria(List<String> criteria) {
        this.criteria = criteria;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String toString() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageSerializer()).create();
        return gson.toJson(this);
    }

    public static class MessageSerializer implements JsonSerializer<Message> {
        public JsonElement serialize(final Message msg, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("msgtype", new JsonPrimitive(msg.getMessageType()));

            if (msg.messageType.equals(Message.MSG_TYPE_FRIENDS)) {
                result.add("name", new JsonPrimitive(msg.getUsername()));
                JsonArray friendsArray = new JsonArray();
                for (String friend : msg.getFriends())
                    friendsArray.add(new JsonPrimitive(friend));
                result.add("friends", friendsArray);
            } else if (msg.messageType.equals(Message.MSG_TYPE_PREF)) {
                result.add("name", new JsonPrimitive(msg.getUsername()));

                JsonArray criteriaArray = new JsonArray();
                for (String criteria : msg.getCriteria())
                    criteriaArray.add(new JsonPrimitive(criteria));
                result.add("criteria", criteriaArray);

                JsonArray prefArray = new JsonArray();
                for (Float friend : msg.getPref())
                    prefArray.add(new JsonPrimitive(friend));
                result.add("pairwiseComparisons", prefArray);
            } else if (msg.messageType.equals(Message.MSG_TYPE_LOCATION) ||
                    msg.messageType.equals(Message.MSG_TYPE_SAFE)) {
                result.add("name", new JsonPrimitive(msg.getUsername()));
                result.add("timestamp", new JsonPrimitive(msg.getTimestamp()));
                result.add("latitude", new JsonPrimitive(msg.getLocation().getLatitude()));
                result.add("longitude", new JsonPrimitive(msg.getLocation().getLongitude()));
            } else if (msg.messageType.equals(Message.MSG_TYPE_REQUEST)) {
                result.add("timestamp", new JsonPrimitive(msg.getTimestamp()));
            }

            return result;
        }
    }
}
