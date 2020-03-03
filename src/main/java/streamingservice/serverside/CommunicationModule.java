package streamingservice.serverside;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;

public class CommunicationModule {

    //this is the v
    private JsonObject clientCM;
    //this is the of k
    private String clientReq;
    //this is the history
    private String messageType;
    private HashMap<String, JsonObject> history = new HashMap<String, JsonObject>();


    public void setCommunicationModule (String v){
        JsonParser parser = new JsonParser();
        this.clientCM = (JsonObject) parser.parse(v);
        //this.clientReq = k;
        this.clientReq = clientCM.getAsJsonObject("ReqID").toString();
        this.messageType = clientCM.getAsJsonObject("MessageType").toString();
    }

    /**method will check to see if k is in the history or not*/
    public String checkHistory(){
        //the jsonObject that will be returned
        JsonObject v;

        if(this.messageType.equals("Ack")){

            /**will delete k when server gets a ack from the client*/
            this.history.remove(clientReq);
            return "";

        }else if(this.messageType.equals("Request")){

            /**if statement checks to see if client request is in the history. If the request is not
             * in the history then the request id is added with the method of dispatcher class. if the
             * request is already in the history then the dispatcher is not called again and v is set to
             * the value of the client req key*/
            if(!history.containsKey(clientReq)){

                //change message type to reply





                Dispatcher dispatcher = new Dispatcher();
                JsonParser parser = new JsonParser();
                v = (JsonObject) parser.parse(dispatcher.dispatch(clientCM.toString()));

                //puts the value to the key "clientReq"
                this.history.put(clientReq, v);
            }else{
                /***/
                v = this.history.get(clientReq);
            }
            return v.toString();
        }
        return "";
    }
}