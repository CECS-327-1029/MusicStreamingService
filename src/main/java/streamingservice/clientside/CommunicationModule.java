package streamingservice.clientside;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;


public class CommunicationModule {

    private JsonObject proxy = new JsonObject();
    private static int req = -1;
    private String userID;

    /**method will get the proxy json in order to add the part that is need to be added in the communication module*/
    public void setCommunicationModule (JsonObject jsObj, String uID){
        this.proxy = jsObj;
        req = req + 1;
        this.userID = uID;
    }

    /***/
    public String messageTypeRequest(){

        /**these are the the information that needs to be added in the communication module*/
        this.proxy.addProperty("MessageType", "Request");
        this.proxy.addProperty("ReqID", req);
        this.proxy.addProperty("UserId", userID);

        /**this is what will be sent over to the server side*/
        return this.proxy.toString();
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