package persistence;

import org.json.JSONObject;

//code based on CPSC 210 Json Serialization Starter
public interface Writable { 
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
