package streamingservice.clientside;

import com.google.gson.*;

import java.io.FileReader;
import java.io.IOException;

public class ProxyInterface {

    private static final String FS = System.getProperty("file.separator");
    private static final String CATALOG_FILE = "src"+FS+"main"+FS+"java"+FS+"streamingservice"+FS+"clientside"+FS+"Catalog.json";

    public JsonObject synchExecution(String remoteMethod, Object... args) {
        JsonObject json = searchInCatalog(remoteMethod);
        return replaceParams(json, args);
    }

    /**
     * Searches the Catalog.json file for the respective information on the method.
     *
     * @param methodName the name of the remote method
     * @return {@code JsonObject} of information about the {@code methodName}
     */
    private JsonObject searchInCatalog(String methodName) {
        JsonObject wanted = null;
        try (FileReader reader = new FileReader(CATALOG_FILE)) {
            JsonArray list = (JsonArray) new JsonParser().parse(reader);
            for (int i = 0; i < list.size() && wanted == null; i++) {
                JsonObject object = list.get(i).getAsJsonObject();
                String methodInCatalog = object.get("remoteMethod").toString().replace("\"", "");
                if (methodInCatalog.equals(methodName)) {
                    wanted = object;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return wanted;
    }

    /**
     * Replaces {@code json's} param values with the values in {@code args}.
     *
     * @param json a {@code JsonObject} to represent a method's information
     * @param args list of arguments where each element will replace a value in {@code json's} param
     * @return an edited version of the passed {@code JsonObject}
     */
    private JsonObject replaceParams(JsonObject json, Object... args) {
        JsonObject paramJson = json.get("param").getAsJsonObject();
        int i = 0;
        for (String key : paramJson.keySet()) {
            if (args[i] instanceof String) {
                paramJson.addProperty(key, args[i].toString());
            } else if (args[i] instanceof Number) {
                paramJson.addProperty(key, Integer.parseInt(args[i].toString()));
            } else if (args[i] instanceof Boolean) {
                paramJson.addProperty(key, Boolean.parseBoolean(args[i].toString()));
            } else {
                paramJson.addProperty(key, new Gson().toJson(args[i]));
            }
            i++;
        }
        return json;
    }

}
