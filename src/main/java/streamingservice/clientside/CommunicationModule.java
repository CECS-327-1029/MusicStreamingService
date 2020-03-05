package streamingservice.clientside;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;


public class CommunicationModule {

    private int req = -1;

    public Client client;

    public CommunicationModule(Client client) {
        this.client = client;
    }

    public void sendMessage(JsonObject jsonObject, String userId) {
        jsonObject.addProperty("MessageType", "Request");
        jsonObject.addProperty("ReqId", ++req);
        jsonObject.addProperty("UserId", userId);
        System.out.println("In the client's CommunicationModule.sendMessage method");
        System.out.println(jsonObject.toString());
        String reply = client.sendMessage(jsonObject.toString());
        System.out.println("Client received Message");
        System.out.println(reply);
    }

    /**method checks to see if message type is reply*/
    public String checkMessage(String v){
        JsonParser parser = new JsonParser();
        JsonObject vMessage = (JsonObject) parser.parse(v);

        if (vMessage.getAsJsonObject("MessageType").toString().equals("Reply")){

            vMessage.addProperty("MessageType", "Ack");
            //increments by 1
            req = req + 1;
        }

        return vMessage.toString();
    }

    /**method will continue to send the request until it has received a reply from the server communication module*/
    public int replyAck(String k, String v){
        //sends the k
        return req;
    }
}