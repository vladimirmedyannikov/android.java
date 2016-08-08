package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 *
 */
public class ResultSearch extends Result{
	private static final long serialVersionUID = 2437128211320429787L;
	
	private String placeHolder;
	private boolean isLocal; //local  or server search
	private String body;
	
	public ResultSearch(String style) {
		super(ResultType.SEARCH,style);
	}

	@Override
	public void fill(JSONObject json) throws JSONException {
		this.body = json.getString("body");
		this.isLocal = "local".equals(json.getString("mode"));
		this.placeHolder = json.getString("placeholder");
	}

	public String getPlaceHolder() {
		return placeHolder;
	}

	public void setPlaceHolder(String placeHolder) {
		this.placeHolder = placeHolder;
	}


	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

}
