/**
 * 
 */
package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 * @07.06.2013
 */
public class ResultOurApp extends ResultRichText{
	private static final long serialVersionUID = 4737892420891680808L;

	private String storeLink;
	
	public ResultOurApp(String style){
		super(ResultType.OUR_APP, style);
	}
	
	@Override
	public void fill(JSONObject json) throws JSONException {
		super.fill(json);
        if(!json.isNull("link_to_icon"))
	        src = json.optString("link_to_icon");
		storeLink = json.optString("link_to_store");
	}

	public String getStoreLink() {
		return storeLink;
	}

}
