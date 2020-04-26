package streamingservice.serverside;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class CommunicationModule {

    //this is the v
    private JsonObject clientCM;
    //this is the of k
    private int clientReq;
    //this is the history
    private String messageType;
    private HashMap<Integer, JsonObject> history = new HashMap<>();

    private Dispatcher dispatcher = new Dispatcher();

    public CommunicationModule() {
        FileHandler fileHandler = new FileHandler();
        dispatcher.registerObject(fileHandler, "FileHandler");

        SongDispatcher songDispatcher = new SongDispatcher();
        dispatcher.registerObject(songDispatcher, "SongDispatcher");

    }

    public String receiveMessage(String message) {
        this.clientCM = (JsonObject) new JsonParser().parse(message.substring(0, message.lastIndexOf("}")+1));
        this.clientReq = clientCM.get("ReqId").getAsInt();
        this.messageType = clientCM.get("MessageType").toString().replace("\"", "");
        if (messageType.equals("Ack")) {
            this.history.remove(clientReq);
            return "";
        }
        return sendMessage();
    }

    private String sendMessage() {
        JsonObject send = new JsonObject();
        send.addProperty("MessageType", "Reply");
        send.addProperty("ReqId", clientReq);
        send.addProperty("ReturnType", clientCM.get("return").getAsString());

        JsonObject returnJson = checkHistory();
        for (Map.Entry<String, JsonElement> entry : returnJson.entrySet()) {
            send.addProperty(entry.getKey(), entry.getValue().getAsString());
        }

        return send.toString();
    }

    /**method will check to see if k is in the history or not*/
    public JsonObject checkHistory(){
        // the jsonObject that will be returned
        JsonObject returnValue;
        /*
         * if statement checks to see if client request is in the history. If the request is not
         * in the history then the request id is added with the method of dispatcher class. if the
         * request is already in the history then the dispatcher is not called again and v is set to
         * the value of the client req key*/
        if(!history.containsKey(clientReq)){
            //change message type to reply

            //dispatcher.registerObject(clientCM.get("remoteMethod").getAsString(), clientCM.get("objectName").getAsString());
            returnValue = (JsonObject) new JsonParser().parse(dispatcher.dispatch(clientCM.toString()));

            //puts the value to the key "clientReq"
            this.history.put(clientReq, returnValue);
        }else{
            returnValue = this.history.get(clientReq);
        }
        return returnValue;
    }
}