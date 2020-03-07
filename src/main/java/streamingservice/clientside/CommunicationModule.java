package streamingservice.clientside;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import com.google.gson.JsonSyntaxException;
import streamingservice.serverside.Tuple2;


public class CommunicationModule {

    private Client client;
    private int requestId = 0;

    public CommunicationModule(Client client) {
        this.client = client;
    }

    public Object sendMessage(JsonObject jsonObject, String userId) {
        readyMessage(jsonObject, userId);
        String reply = client.sendMessage(jsonObject.toString());
        JsonObject message = (JsonObject) new JsonParser().parse(reply);
        if (message.has("ret")) {
            sendAcknowledgment(message);
            return readyOutput(message);
        } else {
            System.out.println("Something went wrong!");
        }
        return null;
    }

    private void readyMessage(JsonObject jsonObject, String userId) {
        jsonObject.addProperty("MessageType", "Request");
        jsonObject.addProperty("ReqId", requestId++);
        jsonObject.addProperty("UserId", userId);
    }

    private void sendAcknowledgment(JsonObject reply){
        JsonObject acknowledgment = new JsonObject();
        acknowledgment.addProperty("MessageType", "Ack");
        acknowledgment.addProperty("ReqId", reply.get("ReqId").getAsInt());
        client.sendMessage(acknowledgment.toString());
    }

    private Object readyOutput(JsonObject message) {
        String returnType = message.get("ReturnType").getAsString();
        if (returnType.equals("ArrayList<Tuple2<String, String>>")) {
            JsonObject returnValue = (JsonObject) new JsonParser().parse(message.get("ret").getAsString());
            System.out.println(returnValue);
            ArrayList<Tuple2<String, String>> output = new ArrayList<>();
            returnValue.entrySet().forEach(entry -> output.add(new Tuple2<>(entry.getKey(), entry.getValue() != JsonNull.INSTANCE ? entry.getValue().getAsString() : null)));
            return output;
        } else if (returnType.equals("String")) {
            return message.get("ret").getAsString();
        }
        return null;
    }

}