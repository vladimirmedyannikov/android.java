package ru.mos.elk.simplenet;

import org.json.JSONObject;


public class ServiceJSONClient extends AbstractServiceClient {

    public ServiceJSONClient() {
    }

    public void communicate(URLMethod method, String urlString, JSONObject jsonBody) {
        content = realCommunicate(method, urlString, jsonBody == null ? "" : jsonBody.toString());
    }
    
    public void communicate(URLMethod method, String urlString, String jsonBodyString) {
        content = realCommunicate(method, urlString, jsonBodyString == null ? "" : jsonBodyString);
    }

    @Override
    protected String getContentType() {
        return "application/json";
    }

}
