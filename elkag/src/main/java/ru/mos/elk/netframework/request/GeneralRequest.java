/**
 * 
 */
package ru.mos.elk.netframework.request;

import com.android.volley2.Cache;
import com.android.volley2.NetworkResponse;
import com.android.volley2.ParseError;
import com.android.volley2.Response;
import com.android.volley2.Response.ErrorListener;
import com.android.volley2.Response.Listener;
import com.android.volley2.VolleyError;
import com.android.volley2.VolleyLog;
import com.android.volley2.toolbox.HttpHeaderParser;
import com.android.volley2.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * @author Александр Свиридов
 * @28.05.2013
 */
public abstract class GeneralRequest<T> extends JsonRequest<T> {
	public static final String CACHE_FLAG = "IS_CACHED";
	
	private static final String ERROR_CODE = "errorCode";
	private static final String ERROR_MESSAGE = "errorMessage";
	protected static final String RESULT = "result";
    private final String paramsWithoutSession;

    private long expireTime = -1L;
	
    /**
	 * @param url
	 * @param requestBody
	 * @param listener
	 * @param errorListener
	 */
	public GeneralRequest(String url, JSONObject requestBody, Listener<T> listener, ErrorListener errorListener) {
		this(url, requestBody, listener, errorListener, true);
	}
	
	public GeneralRequest(String url, JSONObject requestBody, Listener<T> listener, ErrorListener errorListener, boolean isAddSession) {
		super(Method.POST, url, (isAddSession) ? Session.addSession(requestBody) : (requestBody==null?null:requestBody.toString()), listener, errorListener);
		setShouldCache(false);
        this.paramsWithoutSession = requestBody==null? null:requestBody.toString();
	}

	@Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
        	String strJson = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        	VolleyLog.d(strJson, null);
//          Statistics.serviceRequest(getUrl(), getRequestBody(), strJson);
            JSONObject json = new JSONObject(strJson);
            if(response.headers.containsKey(CACHE_FLAG))
            	return Response.success(parseResult(json), null);
            
            Session.setSession(json.isNull(Session.SESSION_ID) ? null : json.getString(Session.SESSION_ID)); 
            int errorCode = json.optInt(ERROR_CODE);
            if (errorCode != ResponseErrorCode.OK) {
            	VolleyError error = new VolleyError(errorCode == ResponseErrorCode.UNAUTHORIZED? "Логин или пароль указаны неверно.":json.optString(ERROR_MESSAGE));
            	error.setErrorCode(errorCode);
            	return Response.error(error); 
            }
            T result = parseResult(json);
            return Response.success(result, getGeneralCacheEntry(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
	protected VolleyError parseNetworkError(VolleyError error) {
		if(error.getErrorCode() == ResponseErrorCode.WRONG_RESPONSE) {
			return new VolleyError("Не удается соединиться с сервером");
		}
			
		return error;
	}
	
	private Cache.Entry getGeneralCacheEntry(NetworkResponse response){
		if(expireTime<System.currentTimeMillis())
			return null;
		
        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.ttl = entry.softTtl = expireTime;
        entry.responseHeaders = response.headers;
        
        return entry;
	}

    @Override
    public String getCacheKey() {
        return getUrl()+paramsWithoutSession;
    }

    protected abstract T parseResult(JSONObject json) throws JSONException;

	public long getExpiredTime() {
		return expireTime;
	}

	public void setExpiredTime(long expireTime) {
		setShouldCache(expireTime>0L);
		this.expireTime = expireTime;
	}
}
