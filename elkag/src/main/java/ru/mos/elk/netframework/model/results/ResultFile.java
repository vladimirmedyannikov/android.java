package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов on 17.06.2014.
 */
public class ResultFile extends ResultTitle {

    private String fileUrl;
    private String mimeType;

    protected ResultFile(String style) {
        super(ResultType.FILE, style);
    }

    @Override
    public void fill(JSONObject json)  throws JSONException {
        super.fill(json);
        JSONObject data = json.optJSONObject("data");
        if(data!=null) {
            fileUrl = data.optString("title");
            mimeType = data.optString("mime");
        }
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getMimeType() {
        return mimeType;
    }
}
