/**
 * 
 */
package ru.mos.elk.netframework.request;

import com.android.volley2.Response.ErrorListener;
import com.android.volley2.Response.Listener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.netframework.model.results.Result;
import ru.mos.elk.netframework.model.results.ResultType;

/**
 * @author Александр Свиридов
 * @28.05.2013
 */
public class ResultsRequest extends GeneralRequest<List<Result>> {

    private final String requestBodyWithOutSession;

    /**
	 * @param url
	 * @param requestBody
	 * @param listener
	 * @param errorListener
	 */
	public ResultsRequest(String url, JSONObject requestBody, Listener<List<Result>> listener, ErrorListener errorListener) {
		super(url, requestBody, listener, errorListener);
        this.requestBodyWithOutSession = requestBody==null? "" : requestBody.toString();
	}

	public ResultsRequest(String url, JSONObject requestBody, Listener<List<Result>> listener, ErrorListener errorListener, boolean isAddSession) {
		super(url, requestBody, listener, errorListener, isAddSession);
        this.requestBodyWithOutSession = requestBody==null? "" : requestBody.toString();
	}

	@Override
	protected List<Result> parseResult(JSONObject json) throws JSONException {
		JSONArray array = json.getJSONArray("result");
		int length = array.length();
		List<Result> results = new ArrayList<Result>(length);
        for (int i = 0; i < length; i++) {
            JSONObject jsonObj = array.getJSONObject(i);
            ResultType type = ResultType.safeValueOf(jsonObj.getString("type"));
            if (type == null) continue; //we can't parse this type
            Result result = type.getResult(jsonObj.optString("style"));
            result.fill(jsonObj);

            results.add(result);
        }
        
        return results;
	}

    public static String makeCacheKey(String url, String requestBody){
        return url.hashCode()+requestBody;
    }
}
