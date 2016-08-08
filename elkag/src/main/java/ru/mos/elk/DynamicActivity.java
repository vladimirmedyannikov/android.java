package ru.mos.elk;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.mos.elk.actionmode.ActionDescription;
import ru.mos.elk.actionmode.SearchDescription;
import ru.mos.elk.netframework.adapters.ActionInterceptor;
import ru.mos.elk.netframework.adapters.AppButtonAction;
import ru.mos.elk.netframework.adapters.DynamicManager;
import ru.mos.elk.netframework.adapters.ViewInterceptor;
import ru.mos.elk.netframework.model.results.Result;
import ru.mos.elk.netframework.model.results.ResultAppButton;
import ru.mos.elk.netframework.model.results.ResultLink;
import ru.mos.elk.netframework.model.results.ResultSearch;
import ru.mos.elk.netframework.model.results.ResultText;
import ru.mos.elk.netframework.model.results.ResultType;

/**
 * Created by Александр Свиридов on 29.08.13.
 */
public class DynamicActivity extends BaseActivity {

    public static final String BASE_URL = "ru.mos.elk.BASE_URL";
    public static final String PARAMS = "ru.mos.elk.PARAMS";
    public static final String DEF_TITLE = "ru.mos.elk.DEF_TITLE";
    public static final String KEY_PARAM = "ru.mos.elk.KEY_PARAM";
    public static final String EXPIRE_INTERVAL = "ru.mos.elk.EXPIRE_INTERVAL";
    public static final String NO_CACHING = "ru.mos.elk.NO_CACHING";
    public static final String DELAY_START = "ru.mos.elk.DELAY_START";
    private static final long DEF_EXPIRE_INTERVAL = Constants.DAY;

    private DynamicManager manager;
    private boolean isForwardMotion = true;
    private boolean isClean;
    private ResultSearch search;
    private String prevLocalSearch;
    private List<ResultAppButton> appButtons = new ArrayList<ResultAppButton>();
    private AppButtonAction appButtonAction = new AppButtonAction() {
        @Override
        public ActionDescription makeAction(ResultAppButton appButton) {
            return null;
        }
    };
    private boolean delayStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(getLayout());
        setSupportProgressBarIndeterminateVisibility(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(DEF_TITLE))
            setTitle(bundle.getString(DEF_TITLE));
        JSONObject params = null;
        try {
            if(bundle.containsKey(PARAMS))
                params = new JSONObject(bundle.getString(PARAMS));
            else
                params = new JSONObject();
        } catch (JSONException e) {
            e.printStackTrace();
            params = new JSONObject();
        }
        delayStart = bundle.getBoolean(DELAY_START);

        manager = new DynamicManager(this, (AbsListView) findViewById(android.R.id.list), bundle.getString(BASE_URL), params);
        manager.setPageListener(new DynamicManager.PageListener() {
            @Override
            public void onStartLoad() {
                setSupportProgressBarIndeterminateVisibility(true);
            }

            @Override
            public void onStopLoad() {
                setSupportProgressBarIndeterminateVisibility(false);
            }
        });
        if (bundle.containsKey(KEY_PARAM))
            manager.setKeyParam(bundle.getString(KEY_PARAM));
        if (!bundle.getBoolean(NO_CACHING, false))
            manager.setExpireInterval(bundle.getLong(EXPIRE_INTERVAL, DEF_EXPIRE_INTERVAL));
        manager.addActionInterceptor(ResultType.PHONE, getPhoneInterceptor());
        manager.addViewInterceptor(ResultType.APPBUTTON, getAppButtonInterceptor());
        manager.addActionInterceptor(ResultType.LINK, getStupidLinkInterceptor());
//        manager.addViewInterceptor(ResultType.SEARCH, getSearchInterceptor());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!delayStart)
            manager.start();
    }

    public final void addViewInterceptor(ResultType type, ViewInterceptor<? extends Result> interceptor) {
        manager.addViewInterceptor(type, interceptor);
    }

    public final void addActionInterceptor(ResultType type, ActionInterceptor<? extends Result> interceptor) {
        manager.addActionInterceptor(type, interceptor);
    }

    public final void setAppButtonAction(AppButtonAction appButtonAction){
        this.appButtonAction = appButtonAction;
    }

    public DynamicManager getManager(){
        return manager;
    }

    private ActionInterceptor<ResultLink> getStupidLinkInterceptor() {
        return new ActionInterceptor<ResultLink>() {

            @Override
            public boolean onAct(ResultLink element, int position) {
                isForwardMotion = true;
                return false;
            }
        };
    }

    @Override
    public void onBackPressed() {
        isForwardMotion = false;
        if (isClean)
            prevLocalSearch = null;
        isClean = (prevLocalSearch != null && !isClean);
        if (!manager.manageBack())
            super.onBackPressed();
    }

    private ActionInterceptor<ResultText> getPhoneInterceptor() {
        return new ActionInterceptor<ResultText>() {

            @Override
            public boolean onAct(final ResultText phone, int position) {
                Dialogs.showYesNoDialog(DynamicActivity.this, -1, R.string.elk_phone_call, R.string.elk_yes, R.string.elk_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int buttonId) {
                        if (buttonId == DialogInterface.BUTTON_POSITIVE) {
                            String uri = "tel:" + phone.getBody();
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(uri));
                            startActivity(intent);
                        }
                    }
                }, null);
                return true;
            }
        };
    }

    private ViewInterceptor<ResultAppButton> getAppButtonInterceptor() {
        return new ViewInterceptor<ResultAppButton>() {

            @Override
            public boolean onAdd(ResultAppButton button, int position) {
                appButtons.add(button);
                supportInvalidateOptionsMenu();
                return true;
            }

            @Override
            public void onClean() {
               appButtons.clear();
               supportInvalidateOptionsMenu();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (search != null) {
            SearchDescription desc = new SearchDescription();
            desc.setTitle(R.string.elk_search);
            desc.setIconId(R.drawable.elk_search);
            desc.setSearchView(getSearchView());
            addSearch(menu, desc);
        }
        for (ResultAppButton button : appButtons) {
            ActionDescription action = appButtonAction.makeAction(button);
            if (action != null)  //check if we know about this action
                addActionItem(menu, action);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private SearchView getSearchView() {
        final SearchView searchView = new SearchView(this);
        searchView.setVisibility(View.VISIBLE);
        searchView.setQueryHint(search.getPlaceHolder());
        searchView.setIconifiedByDefault(isForwardMotion);
        searchView.setIconified(TextUtils.isEmpty(search.getBody()) || !isForwardMotion);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (search != null && !search.isLocal()) {
                    search = null;
                    manager.setSearchText(false, query);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (search.isLocal()) {
                    isClean = true;
                    prevLocalSearch = newText;
                }
                if (search.isLocal() && newText.length() >= Constants.THRESHOLD)
                    manager.setSearchText(true, newText);
                else
                    manager.setSearchText(true, null);

                return false;
            }
        });
        searchView.setQuery(search.getBody(), false);

        return searchView;
    }

    public void setDelayStart(boolean delayStart) {
        this.delayStart = delayStart;
    }

    public int getLayout() {
        return R.layout.activity_dynamic;
    }
}

