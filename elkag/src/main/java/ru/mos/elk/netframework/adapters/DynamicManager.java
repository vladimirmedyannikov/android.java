package ru.mos.elk.netframework.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import com.android.volley2.RequestQueue;
import com.android.volley2.Response.ErrorListener;
import com.android.volley2.Response.Listener;
import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.R;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.model.results.Result;
import ru.mos.elk.netframework.model.results.ResultButton;
import ru.mos.elk.netframework.model.results.ResultLink;
import ru.mos.elk.netframework.model.results.ResultOurApp;
import ru.mos.elk.netframework.model.results.ResultText;
import ru.mos.elk.netframework.model.results.ResultTitle;
import ru.mos.elk.netframework.model.results.ResultType;
import ru.mos.elk.netframework.request.ResultsRequest;

public class DynamicManager {
	
	private static final String ACTION_REFRESH = "refresh";
    private static final String SEARCH_PARAM = "search";

	private String baseURL;
    private String keyParam = "link_id";
    
    private JSONObject curParams;
    private LinkedList<JSONObject> paramsStack = new LinkedList<JSONObject>();
    
    private BaseActivity activity;
    private DynamicsAdapter adapter;
    private RequestQueue requestQueue;
    private long expireInterval = -1L;// default no caching

    private Map<ResultType, ActionInterceptor> actionInterceptors = new EnumMap<ResultType, ActionInterceptor>(ResultType.class);
    private Map<ResultType, ViewInterceptor> viewInterceptors = new EnumMap<ResultType, ViewInterceptor>(ResultType.class);
    private PageListener pageListener = null;

	public DynamicManager(BaseActivity activity, AbsListView list, String baseURL, JSONObject params){
		this.activity = activity;
		this.baseURL = baseURL==null?"":baseURL;
		this.curParams = params;
		this.requestQueue = activity.getRequestQueue();
		this.adapter = new DynamicsAdapter(activity, requestQueue, new ButtonListener() {
			
			@Override
			public void onClick(ResultButton button, int position) {
				if(ACTION_REFRESH.equals(button.getAction())){
					startService(curParams);
					return;
				}
				ActionInterceptor<ResultButton> interceptor = actionInterceptors.get(ResultType.BUTTON);
				if(interceptor!=null)
					interceptor.onAct(button,position);
			}
		});
		
		configList(list, adapter);
	}
	
	/** default keyParam value is "link_id"*/
	public final void setKeyParam(String keyParam){
		this.keyParam = keyParam;
	}
	
	public final void start(){
        startService(curParams);
	}
	
	/** no caching by default. If you want to switch off caching set expireInterval to negative number*/
	public final void setExpireInterval(long expireInterval) {
		this.expireInterval = expireInterval;
	}
	
	public final int getStackSize(){
		return paramsStack.size();
	}
	
    /** @return - true if back button pressing was managed by manager*/
    public final boolean manageBack(){
    	cancelService();
        if (paramsStack.size() == 0) {
            return false;
        } else {
            startService(paramsStack.removeFirst());
            return true;
        }   	
    }

    public void gotoLink(String linkId){
        paramsStack.addFirst(curParams);
        JSONObject params = new JSONObject();
        try {
        	params.put(keyParam, linkId);
        } catch (JSONException e) {e.printStackTrace();}
        
        cancelService();
        startService(params);
    }

    public void gotoPage(JSONObject params, boolean addToStack){
        if(addToStack)
            paramsStack.addFirst(curParams);

        cancelService();
        startService(params);
    }
    
	private void startService(JSONObject queryParams) {
		adapter.clear();
        curParams = queryParams;
        for(ViewInterceptor viewInterceptor : viewInterceptors.values())
        	viewInterceptor.onClean();
        onStart();

        Listener<List<Result>> listener = new Listener<List<Result>>() {

            @Override
            public void onResponse(List<Result> results) {
                onStop();
                for (Result result : results) {
                    ResultType type = result.getType();
                    ViewInterceptor viewInterceptor = viewInterceptors.get(type);
                    if (viewInterceptor != null && viewInterceptor.onAdd(result, adapter.getCount()))
                        continue;
                    switch (type) {
                        case TITLE:
                            activity.setTitle(result.toString());
                            break;
                        default:
                            adapter.add(result);
                            break;
                    }
                }
            }
        };
        ErrorListener errListener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				onStop();
                if(error.getErrorCode()==-1) {//no connection
                    ResultTitle title = new ResultTitle(null);
				    title.setTitle(activity.getString(R.string.elk_cant_reach_server));
				    adapter.add(title);
				    ResultButton button = new ResultButton(null);
				    button.setTitle(activity.getString(R.string.elk_refresh));
				    button.setAction(ACTION_REFRESH);
				    adapter.add(button);
                } else {
                    ResultText text = new ResultText(null);
                    text.setTitle(activity.getString(R.string.elk_error));
                    text.setBody(error.getMessage());
                    adapter.add(text);
                }
			}
		};
		
		ResultsRequest request = new ResultsRequest(baseURL, queryParams, listener, errListener);
		if(expireInterval>0L)
			request.setExpiredTime(System.currentTimeMillis() + expireInterval);
		request.setTag(getClass());
		requestQueue.add(request);
	}

    /** releases all cache entries*/
    public void invalidateAll(){
        requestQueue.getCache().invalidate(baseURL, true);
    }

    public void clearCache() {
        requestQueue.getCache().clear();
    }

    public static String makeCacheKey(Context context, String path){
        String baseUrl = API.getURL(path);
        return String.valueOf(baseUrl.hashCode());
    }

    private void cancelService(){
    	requestQueue.cancelAll(getClass());
    }
     
 	private void configList(final AbsListView list, final DynamicsAdapter adapter) {
        ((AdapterView<ListAdapter>) list).setAdapter(adapter);
 		list.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(list.getWindowToken(), 0);
			}
		});
 		list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Result result = adapter.getItem(adapter.getRealPosition(position));
                ResultType type = result.getType();
                ActionInterceptor interceptor = actionInterceptors.get(type);
                if(interceptor!=null && interceptor.onAct(result,position))
                	return;
                
                Intent intent = null;
                List<ResolveInfo> callAppsList = null;
                switch (type) {
                    case TABLE_LINK:
                    case RICH_LINK:
                    case LINK:
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(list.getWindowToken(), 0);
    
                        paramsStack.addFirst(curParams);
                        JSONObject params = new JSONObject();
                        try {
                        	params.put(keyParam, ((ResultLink) result).getLinkId());
                        } catch (JSONException e) {e.printStackTrace();}
                        
                        cancelService();
                        startService(params);
                        break;
                    case PHONE:
        				String uri = "tel:" + ((ResultText)result).getBody();
        				intent = new Intent(Intent.ACTION_CALL);
        				intent.setData(Uri.parse(uri));
        				callAppsList = activity.getPackageManager().queryIntentActivities(intent, 0);
        				if(callAppsList!=null && callAppsList.size()!=0)
        					activity.startActivity(intent);
                    	break;
                    case OUR_APP:
                    	intent = new Intent(Intent.ACTION_VIEW);
                    	intent.setData(Uri.parse(((ResultOurApp)result).getStoreLink()));
        				callAppsList = activity.getPackageManager().queryIntentActivities(intent, 0);
        				if(callAppsList!=null && callAppsList.size()!=0)
        					activity.startActivity(intent);
                    	break;
                    case WWW:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((ResultText)result).getBody()));
                        callAppsList = activity.getPackageManager().queryIntentActivities(intent, 0);
                        if(callAppsList!=null && callAppsList.size()!=0)
                            activity.startActivity(intent);
                        break;
                    default:
                    	break;
                }

            }

        });		
	}
     
 	public final void addActionInterceptor(ResultType type, ActionInterceptor<? extends Result> interceptor){
 		actionInterceptors.put(type, interceptor);
 		adapter.addClickable(type);
 	}
 	
 	public final void removeActionInterceptor(ResultType type){
 		actionInterceptors.remove(type);
 		adapter.removeClickable(type);
 	} 	

 	public final void addViewInterceptor(ResultType type, ViewInterceptor<? extends Result> interceptor){
 	 	viewInterceptors.put(type, interceptor);
 	}
 	
 	public final void removeViewInterceptor(ResultType type){
 	 	viewInterceptors.remove(type);
 	} 	
 	
 	public final void setSearchText(boolean isLocal, String searchText){
 		if(isLocal)
 			adapter.setPattern(searchText);
 		else {
			try {
				curParams.put(SEARCH_PARAM, searchText);
			} catch (JSONException e) {	e.printStackTrace();}
 			
            cancelService();
            startService(curParams);
 		}
 	}
 	
 	/** method is invoked before start single loading. This method is convenient for visualization of loading process. See also {@link #onStop()}*/
 	protected void onStart(){
        if(pageListener!=null)
            pageListener.onStartLoad();
 	}

 	/** method is invoked after finishing/cancelling single loading. This method is convenient for visualization of loading process. See also {@link #onStart()}*/
 	protected void onStop(){
        if(pageListener!=null)
            pageListener.onStopLoad();
 	}
 	
 	public final void addStyledLayout(ResultType type, String style, int layout){
 		adapter.addStyledLayout(type.toString()+style, layout);
 	}

    /** setting page listener. You can pass null here to clean listener*/
    public void setPageListener(PageListener pageListener){
        this.pageListener = pageListener;
    }

    public interface PageListener{

        public void onStartLoad();
        public void onStopLoad();
    }
}
