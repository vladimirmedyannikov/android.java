/**
 * 
 */
package ru.mos.elk.netframework.request;

import com.android.volley2.Response.ErrorListener;
import com.android.volley2.Response.Listener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 * @28.05.2013
 */
public class JsonObjectRequest extends GeneralRequest<JSONObject>{

	/**
	 * @param url
	 * @param requestBody
	 * @param listener
	 * @param errorListener
	 */
	public JsonObjectRequest(String url, JSONObject requestBody, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(url, requestBody, listener, errorListener);
	}
	
	public JsonObjectRequest(String url, JSONObject requestBody, Listener<JSONObject> listener, ErrorListener errorListener, boolean isAddSession) {
		super(url, requestBody, listener, errorListener, isAddSession);
	}

	@Override
	protected JSONObject parseResult(JSONObject json) throws JSONException {
		return json.optJSONObject("result");
	}
	

}
