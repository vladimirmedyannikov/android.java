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
public class StringRequest extends GeneralRequest<String>{

	/**
	 * @param url
	 * @param requestBody
	 * @param listener
	 * @param errorListener
	 */
	public StringRequest(String url, JSONObject requestBody, Listener<String> listener, ErrorListener errorListener) {
		super(url, requestBody, listener, errorListener);
	}

	public StringRequest(String url, JSONObject requestBody, Listener<String> listener, ErrorListener errorListener, boolean isAddSession) {
		super(url, requestBody, listener, errorListener, isAddSession);
	}
	
	@Override
	protected String parseResult(JSONObject json) throws JSONException {
		return json.optString("result");
	}

}
