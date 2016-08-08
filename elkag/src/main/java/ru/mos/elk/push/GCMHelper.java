package ru.mos.elk.push;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public abstract class GCMHelper {
    public static final String GUID = "guid"; 
    public static final String REGISTER_PATH = "json/v0.2/push/android/register";
    public static final String PREFERENCES = "com.google.android.gcm";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_REG_ID = "regId";
    public static final String PROPERTY_ON_SERVER = "onServer";
    public static final String SENDER_ID = "434944116762"; //FIXME customize it. pass as a parameter to library
    
	public static int getAppVersion(Context context) {
	    try { 
	        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	public static String getAppVersionName(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
}
