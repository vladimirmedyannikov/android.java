package ru.mos.polls.push;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.TextUtils;

import com.android.volley2.RequestQueue;
import com.android.volley2.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

import ru.mos.elk.netframework.request.ResponseErrorCode;
import ru.mos.elk.simplenet.ServiceJSONClient;
import ru.mos.elk.simplenet.URLMethod;
import ru.mos.polls.api.API;

public class AutoLoadService extends Service {    
    public final static String TASK = "Task";
	public static final int GCM_REGISTER = 5;
    public static final int REFRESH_DATA = 6;

    private RequestQueue requestQueue;
	
	@Override
	public void onCreate() {
		super.onCreate();
        requestQueue = Volley.newRequestQueue(this);
	}
	
	@Override
	public void onDestroy() {
		requestQueue.stop();
		requestQueue = null;
		super.onDestroy();
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			switch (intent.getIntExtra(TASK, 0)) {
				case GCM_REGISTER:
					pushRegister();
					break;
				case REFRESH_DATA:
					break;
			}
		}
        return super.onStartCommand(intent, flags, startId);
    }

	private void pushRegister() {
		final SharedPreferences prefs = getSharedPreferences(GCMHelper.PREFERENCES, MODE_PRIVATE);
        final String guid = API.getGUID(this);

		Thread gcmThread = new Thread() {
			public void run() {
				try {
					String registrationId = prefs.getString(GCMHelper.PROPERTY_REG_ID, "");
					int storedAppVersion = prefs.getInt(GCMHelper.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
					int curAppVersion = GCMHelper.getAppVersion(getApplicationContext());
					if (TextUtils.isEmpty(registrationId) || storedAppVersion<curAppVersion) {
						GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(AutoLoadService.this);
						registrationId = gcm.register(GCMHelper.SENDER_ID);
						prefs.edit().
						putString(GCMHelper.PROPERTY_REG_ID, registrationId).
						putInt(GCMHelper.PROPERTY_APP_VERSION, curAppVersion).
						putBoolean(GCMHelper.PROPERTY_ON_SERVER, false).commit();
					}

					if(!prefs.getBoolean(GCMHelper.PROPERTY_ON_SERVER,false)){
						JSONObject params = new JSONObject();

						JSONObject deviceInfo = new JSONObject();
						deviceInfo.put("guid", guid);
						deviceInfo.put("object_id", registrationId);
						deviceInfo.put("user_agent", "Android");
						deviceInfo.put("app_version", GCMHelper.getAppVersionName(getApplicationContext()));
						params.put("device_info", deviceInfo);
						params.put("empAction", "registerPush");

						ServiceJSONClient client = new ServiceJSONClient();
						client.communicate(URLMethod.POST, API.getURL(GCMHelper.REGISTER_PATH), params);
						if(client.getErrorCode()==HttpURLConnection.HTTP_OK){
							JSONObject response = new JSONObject(client.getResponse());
							if(response.optInt("errorCode")==ResponseErrorCode.OK)
								prefs.edit().putBoolean(GCMHelper.PROPERTY_ON_SERVER, true).commit();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
		gcmThread.setName("GCM Thread");
		gcmThread.start();
	}
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
