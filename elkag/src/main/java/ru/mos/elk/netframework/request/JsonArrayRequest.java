/**
 * 
 */
package ru.mos.elk.netframework.request;

import com.android.volley2.Response.ErrorListener;
import com.android.volley2.Response.Listener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 * @28.05.2013
 */
public class JsonArrayRequest extends GeneralRequest<JSONArray>{

	/**
	 * @param url
	 * @param requestBody
	 * @param listener
	 * @param errorListener
	 */
	public JsonArrayRequest(String url, JSONObject requestBody, Listener<JSONArray> listener, ErrorListener errorListener) {
		super(url, requestBody, listener, errorListener);
	}
	
	public JsonArrayRequest(String url, JSONObject requestBody, Listener<JSONArray> listener, ErrorListener errorListener, boolean isAddSession) {
		super(url, requestBody, listener, errorListener, isAddSession);
	}

	@Override
	protected JSONArray parseResult(JSONObject json) throws JSONException {
		return json.optJSONArray("result");
	}
	
	

}
