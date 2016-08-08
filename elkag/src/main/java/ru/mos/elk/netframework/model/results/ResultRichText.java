/**
 * 
 */
package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 * @27.08.2013
 */
public class ResultRichText extends ResultImg {
	private static final long serialVersionUID = 5588921704508700747L;

	private String body;

	public ResultRichText(String style) {
		super(ResultType.RICH_TEXT, style);
	}

    public ResultRichText(ResultType type, String style) {
        super(type, style);
    }

    @Override
    public void fill(JSONObject json) throws JSONException {
        super.fill(json);
        body = json.optString("body");
        if(!json.isNull("img"))
            src = json.optString("img");
    }

    public String getBody() {
        return body;
    }

}
