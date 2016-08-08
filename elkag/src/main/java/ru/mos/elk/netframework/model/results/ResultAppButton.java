package ru.mos.elk.netframework.model.results;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 *
 */
public class ResultAppButton extends ResultTitle{
	private static final long serialVersionUID = 4267446099961401679L;
	
	private String screenType;
    private JSONArray params;

	public ResultAppButton(String style) {
		super(ResultType.APPBUTTON, style);
	}

    @Override
    public void fill(JSONObject json) throws JSONException {
    	super.fill(json);
        screenType = json.optString("screen_type");
        params = json.optJSONArray("params");
    }

	public String getScreenType() {
		return screenType;
	}

	public void setScreenType(String screenType) {
		this.screenType = screenType;
	}

    public JSONArray getParams() {
        return params;
    }
}
