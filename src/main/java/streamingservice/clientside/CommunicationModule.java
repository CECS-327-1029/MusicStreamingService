package streamingservice.clientside;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommunicationModule {

    private Client client;
    private int requestId = 0;

    public CommunicationModule(Client client) {
        this.client = client;
    }

    public JsonObject sendMessage(JsonObject jsonObject, String userId) {
        readyMessage(jsonObject, userId);
        String reply = client.sendMessage(jsonObject.toString());
        JsonObject message = (JsonObject) new JsonParser().parse(reply);
        if (message.has("ret")) {
            sendAcknowledgment(message);
            return message;
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

}