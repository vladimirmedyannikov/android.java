package ru.mos.elk.netframework.request;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ru.mos.elk.Constants;

public abstract class Session {
    public static final String SESSION_ID = "session_id";
    public static final String AUTH = "auth";
    
	private static final String SESSION = "ru.mos.elk.utils.SESSION";
	
	private static String session;
	private static Map<Object, OnSessionChangedListener> listeners = new HashMap<Object,OnSessionChangedListener>();
	private static SharedPreferences prefs;

	public static void initialize(Context context) {
		if(prefs==null)
			prefs = context.getSharedPreferences(Constants.SESSION_PREFS, Activity.MODE_PRIVATE);
		session = prefs.getString(SESSION, null);
	}

    /** method checks if users is authorized.
     * @param context - context for getting session. Can't be null. You can pass ApplicationContext here
     * @return true if user is authorized, false otherwise*/
	public static boolean isAuthorized(Context context){
		if(prefs==null)
			initialize(context);
		return session!=null;
	}

    /** method for very rare use. It might be removed soon
     * @return  session or null if it is empty*/
    @Deprecated
    public static String getSession(Context context){
        if(prefs==null)
        prefs = context.getSharedPreferences(Constants.SESSION_PREFS, Activity.MODE_PRIVATE);
        return session = prefs.getString(SESSION, null);
    }

	public static void setSession(String session){
		if(Session.session==session || (Session.session!=null && Session.session.equals(session))) return;

		fireSessionChanged(Session.session,session);
		Session.session=session;	
		prefs.edit().putString(SESSION, session).commit();
	}

    /** add listener to catch session changed events*/
	public static void addOnSessionChangedListener(Object key, OnSessionChangedListener listener){
		listeners.put(key, listener);
	}

	public static void removeOnSessionChangedListener(Object key){
		listeners.remove(key);
	}

	public static void clearOnSessionChangedListeners(){
		listeners.clear();
	}
	
	public interface OnSessionChangedListener{
		public void sessionChanged(String oldSession, String newSession);
	}
	
	private static void fireSessionChanged(String oldSession,String newSession) {
		for(Entry<Object, OnSessionChangedListener> entry : listeners.entrySet())
			entry.getValue().sessionChanged(oldSession, newSession);
	}

	/** this query adds session to parameters.
	 * @param requestBody - params to add session. Can be null.
	 * @return
	 */
	public static String addSession(JSONObject requestBody) {
		if(requestBody == null)
			requestBody = new JSONObject();

		try {
            JSONObject auth;
			if(requestBody.has(AUTH)) {
                auth = requestBody.getJSONObject(AUTH);
            } else {
                auth = new JSONObject();
                requestBody.put(AUTH, auth);
            }
            auth.put(SESSION_ID, (session==null) ? JSONObject.NULL : session);
            String result = requestBody.toString();
            requestBody.remove(AUTH);

            return result;

		} catch (JSONException e) {	e.printStackTrace();}
		
		return null;
	}

}
