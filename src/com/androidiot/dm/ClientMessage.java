package com.androidiot.dm;

import com.google.gson.*;
import java.lang.reflect.Type;

public class ClientMessage {
	public String msgtype;
	public String name;
	public String timestamp;
	public String latitude;
	public String longitude;

	public String[] criteria;
	public String[] pairwiseComparisons;

	public String consistencyRatio;
	public String score;

	public ClientMessage() {
	}

	@Override
	public String toString() {
		Gson gson = new GsonBuilder().registerTypeAdapter(ClientMessage.class, new MessageSerializer()).create();
		return gson.toJson(this);
	}
}

class MessageSerializer implements JsonSerializer<ClientMessage> {
	public JsonElement serialize(final ClientMessage cm, final Type type,
			final JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("msgtype", new JsonPrimitive(cm.msgtype));

		if (cm.msgtype.compareTo("safe-location-response") == 0) {
			result.add("latitude", new JsonPrimitive(cm.latitude));
			result.add("longitude", new JsonPrimitive(cm.longitude));
			result.add("score", new JsonPrimitive(cm.score));
		} else if (cm.msgtype.compareTo("consistency-ratio") == 0) {
			result.add("value", new JsonPrimitive(cm.consistencyRatio));
		} else {
			System.err.println("Cannot serialize msgtype " + cm.msgtype);
		}
		return result;
	}
}

