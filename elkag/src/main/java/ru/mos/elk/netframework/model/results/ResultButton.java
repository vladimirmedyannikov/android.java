package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultButton extends ResultTitle{
	private static final long serialVersionUID = -2798005727391789964L;
	
	private String action;
    private JSONObject params;

	public ResultButton(String style) {
		super(ResultType.BUTTON, style);
	}

	@Override
	public void fill(JSONObject json) throws JSONException {
    	super.fill(json);
        this.action = json.optString("action");
        this.params = json.optJSONObject("params");
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

    public JSONObject getParams() {
        return params;
    }
}
