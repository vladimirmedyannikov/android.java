package ru.mos.elk.push;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.HashMap;
import java.util.Map;

import ru.mos.elk.netframework.request.Session;

/** Simple realization of push receiving class. If push intent contains "action" key, then it will be proceeded as "emp push". Otherwise nonEmpAction class will be used
 * For emp pushed to be shown guid in push parameters and in application must be equal.
 * For all emp pushed notification will be shown in notification bar*/
public class GCMBroadcastReceiver extends BroadcastReceiver{

    public static int messageNotifyId = 1;
    private static final String ACTION = "action";
    private static final String MSG_BODY = "msgBody";
    private static final String MSG_TITLE = "msgTitle";

    private static final Map<String,PushAction> actions = new HashMap<String, PushAction>();

    private static PushAction nonEmpAction;

    public static void addAction(String actionName, PushAction pushAction){
        actions.put(actionName,pushAction);
    }

    public static void setNonEmpAction(PushAction pushAction){
        nonEmpAction = pushAction;
    }

    @Override
	public void onReceive(Context context, Intent intent) {
        Log.i("SERVICES", "Push Recieved");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String msgType = gcm.getMessageType(intent);
		if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(msgType)) {
			Bundle bundle = intent.getExtras();
            if(!bundle.containsKey(ACTION) && nonEmpAction!=null) { //some non EMP push
                nonEmpAction.onPushRecived(intent);
                return;
            }

            PushAction action = actions.get(bundle.getString(ACTION));
            if(action==null) { //unknown action
                Log.i("SERVICES", "Unknown EMP Push");
                return;
            }

            String msgBody = bundle.getString(MSG_BODY);
            String msgTitle = bundle.getString(MSG_TITLE);
            boolean isAuthReq = action.isAuthRequired();
            if(!checkGuid(context,bundle.getString(GCMHelper.GUID))) {
                return;
            }
            if(!isAuthReq ||(isAuthReq && Session.isAuthorized(context))){
                action.onPushRecived(intent);
                if (!action.isPushNotValid()) {
                    notifyUser(context, msgTitle, msgBody, action);
                }
            }
		}
	}

    private boolean checkGuid(Context context, String pushGuid) {
        SharedPreferences prefs = context.getSharedPreferences(GCMHelper.PREFERENCES, Activity.MODE_PRIVATE);
        String guid = prefs.getString(GCMHelper.GUID,"");
        return guid.equals(pushGuid);
    }

    private void notifyUser(Context context, String msgTitle, String msgBody, PushAction action) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        int largeIconRes = action.getLargeIcon();
        if (largeIconRes != PushAction.LARGE_ICON_NOT_SET) {
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIconRes));
        }
                builder.setSmallIcon(action.getSmallIcon());
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(msgTitle);
        builder.setContentText(msgBody);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, action.getNotifyIntent(), PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setTicker(msgTitle);
        builder.setStyle(new NotificationCompat.BigTextStyle().
                setBigContentTitle(msgTitle).
                bigText(msgBody));
        if (action.getPushChanel() != null) {
            builder.setChannelId(action.getPushChanel().getId());
        }
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(++messageNotifyId, notification);

    }

    public interface PushAction{
        int LARGE_ICON_NOT_SET = 0;

        /** method for making some additional actions when recieve push notification. For example, when receive push you might want to refresh some data
         *  @param intent  full push intent. */
        void onPushRecived(Intent intent);

        /** method returns intent which will be used to start activity when user tap on notification in notification bar*/
        Intent getNotifyIntent();

        /** some push notifications require authorization, so it won't be shown if user made logout or something*/
        boolean isAuthRequired();

        int getSmallIcon();

        int getLargeIcon();

        boolean isPushNotValid();

        PushChannel getPushChanel();
    }

    //FIXME method to send new registrationId to 3rd party server!!
}
