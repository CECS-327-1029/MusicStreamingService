package streamingservice.serverside;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;

public class CommunicationModule {

    //this is the v
    private JsonObject clientCM;
    //this is the of k
    private int clientReq;
    //this is the history
    private String messageType;
    private HashMap<Integer, JsonObject> history = new HashMap<>();
    private Object FileHandler = new FileHandler();
    private Object MusicPlayerMaster = new MusicPlayerMaster();

    public String compute(String message) {
        this.clientCM = (JsonObject) new JsonParser().parse(message.substring(0, message.lastIndexOf("}")+1));
        this.clientReq = clientCM.get("ReqId").getAsInt();
        this.messageType = clientCM.get("MessageType").toString().replace("\"", "");
        return sendMessage();
    }

    private String sendMessage() {
        JsonObject send = new JsonObject();
        send.addProperty("MessageType", "Reply");
        send.addProperty("reqId", clientReq);
        send.addProperty("ret", checkHistory().toString());
        return send.toString();
    }

    /**method will check to see if k is in the history or not*/
    public JsonObject checkHistory(){
        //the jsonObject that will be returned
        JsonObject v;

        if(this.messageType.equals("Ack")){

            /**will delete k when server gets a ack from the client*/
            this.history.remove(clientReq);
            //return "";

        }else if(this.messageType.equals("Request")){
            /**if statement checks to see if client request is in the history. If the request is not
             * in the history then the request id is added with the method of dispatcher class. if the
             * request is already in the history then the dispatcher is not called again and v is set to
             * the value of the client req key*/
            if(!history.containsKey(clientReq)){
                //change message type to reply

                Dispatcher dispatcher = new Dispatcher();
                dispatcher.registerObject(clientCM.get("remoteMethod").getAsString(), clientCM.get("objectName").getAsString());

                v = (JsonObject) new JsonParser().parse(dispatcher.dispatch(clientCM.toString()));

                //puts the value to the key "clientReq"
                this.history.put(clientReq, v);
            }else{
                /***/
                v = this.history.get(clientReq);
            }
            return v;
        }
        //return "";
        return null;
    }
}