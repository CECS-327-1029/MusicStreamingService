package streamingservice.serverside;

import com.google.gson.JsonObject;

public class ProxyInterface {

    public JsonObject syncExecution(String remoteMethod, Object... param)
    {
        for (Object arg : param)
        {
            System.out.println(arg);
        }
        return null;
    }
    

}
