package ru.mos.polls.base.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley2.Request;
import com.android.volley2.RequestQueue;
import com.android.volley2.toolbox.ImageLoader;
import com.android.volley2.toolbox.Volley;
import com.flurry.android.FlurryAgent;

import java.util.HashSet;
import java.util.Set;

import me.ilich.juggler.gui.JugglerActivity;
import ru.mos.elk.actionmode.ActionDescription;
import ru.mos.elk.actionmode.SearchDescription;
import ru.mos.elk.netframework.utils.BitmapLruCache;
import ru.mos.polls.api.API;

public class BaseActivity extends JugglerActivity {
	public static final String INTENT_LOGOUT = "ru.mos.elk.pages.LOGOUT";

    private RequestQueue requestQueue;
    private static Set<Class<? extends Activity>> authRequered = new HashSet<Class<? extends Activity>>();
    private static String flurryKey = "stub";
    private BroadcastReceiver logoutReceiver = null;

    public int addActionItem(Menu menu, final ActionDescription actionDescr) {
    	int titleRes = actionDescr.getTitleRes();
        MenuItem item;
        if(titleRes==-1)
        	item = menu.add(Menu.NONE, actionDescr.getId(), actionDescr.getOrder(), actionDescr.getTitle());
        else
        	item = menu.add(Menu.NONE, actionDescr.getId(), actionDescr.getOrder(), titleRes);
        item.setIcon(actionDescr.getIconId());
        MenuItemCompat.setShowAsAction(item, actionDescr.getActionEnum());
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                actionDescr.act(null);
                return true;
            }
        });

        return item.getItemId();
    }

    public int addSearch(Menu menu, SearchDescription searchDescr){
        MenuItem item = menu.add(Menu.NONE, searchDescr.getId(), searchDescr.getOrder(), searchDescr.getTitleRes());
        item.setIcon(searchDescr.getIconId());
        MenuItemCompat.setShowAsAction(item, searchDescr.getActionEnum());
        MenuItemCompat.setActionView(item, searchDescr.getSearchView());

        return item.getItemId();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // see addAuthRequire
        if(authRequered.contains(getClass())) {
            logoutReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
//                    finish();
                }
            };
        	registerReceiver(logoutReceiver, new IntentFilter(INTENT_LOGOUT));
        }

        requestQueue = Volley.newRequestQueue(this);
    }

    /**
     * stub for versions before 11 API for cleanning back stack. No FLAG_ACTIVITY_CLEAR_TASK page require
     */
    public static void addAuthRequire(Class<? extends Activity> clsActivity) {
        authRequered.add(clsActivity);
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }

    public <T> void addRequest(Request<T> request) {
        if(requestQueue!=null){
    	    request.setTag(getClass());
            requestQueue.add(request);
        }
    }

    public ImageLoader createImageLoader() {
		return new ImageLoader(requestQueue, new BitmapLruCache());
	}

    public <T> void addRequest(final Request<T> request, ProgressDialog dialog){
    	dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				request.cancel();
			}
		});
    	addRequest(request);
    }

    /**
     * clears all queries with path
     * @param path - path to use in clearAll logic*/
    public void invalidate(String path){
        requestQueue.getCache().invalidate(API.getURL(path), true);
    }

    public static void setFlurryKey(String key){
        flurryKey = key;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, flurryKey);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
        requestQueue.cancelAll(getClass());
    }
    
    @Override
    protected void onDestroy() {
        if(authRequered.contains(getClass()))
            unregisterReceiver(logoutReceiver);
    	requestQueue.stop();
    	requestQueue = null;
    	super.onDestroy();
    }
}
