package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 *
 */
public class ResultText extends ResultTitle {
	private static final long serialVersionUID = -35236088869958099L;
	
	private String body;

    public ResultText(String style) {
        super(ResultType.TEXT, style);
    }

    protected ResultText(ResultType type,String style) {
        super(type, style);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public void fill(JSONObject json) throws JSONException{
        super.fill(json);
        body = json.optString("body");
    }

    @Override
    public String toString() {
        return getTitle() + "\n" + getBody();
    }
}
