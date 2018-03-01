package ru.mos.polls.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.util.Dialogs;
import ru.mos.polls.R;
import ru.mos.polls.helpers.ListViewHelper;
import ru.mos.polls.util.NetworkUtils;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link DynamicFragment#newInstance} factory method to
 * create an newInstance of this fragment.
 */
public class DynamicFragment extends PullableFragment {

    public static final String BASE_URL = "ru.mos.elk.BASE_URL";
    public static final String PARAMS = "ru.mos.elk.PARAMS";
    public static final String DEF_TITLE = "ru.mos.elk.DEF_TITLE";
    public static final String KEY_PARAM = "ru.mos.elk.KEY_PARAM";
    public static final String EXPIRE_INTERVAL = "ru.mos.elk.EXPIRE_INTERVAL";
    public static final long NO_CACHING = -1L;
//    private static final long DEF_EXPIRE_INTERVAL = Constants.DAY;

    //    private BaseActivity activity;
//    private DynamicManager manager;
//    private boolean isForwardMotion = true;
//    private boolean isClean;
//    private ResultSearch search;
//    private String prevLocalSearch;
//    private List<ResultAppButton> appButtons = new ArrayList<ResultAppButton>();
//    private AppButtonAction appButtonAction = new AppButtonAction() {
//        @Override
//        public ActionDescription makeAction(ResultAppButton appButton) {
//            return null;
//        }
//    };
    @BindView(android.R.id.list)
    AbsListView list;
    private Bundle savedInstanceState;
    @BindView(android.R.id.empty)
    public TextView empty;
    @BindView(R.id.root)
    public View root;
    @BindView(R.id.rootConnectionError)
    public View rootConnectionError;
    private boolean delayStart = false;

    public static DynamicFragment newInstance(String defTitle, String params, String baseUrl) {
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        args.putString(DEF_TITLE, defTitle);
        args.putString(PARAMS, params);
        args.putString(BASE_URL, baseUrl);

        fragment.setArguments(args);
        return fragment;
    }

    public static DynamicFragment newInstance(String defTitle, String params, String baseUrl, String keyParam, long expireInterval) {
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        args.putString(DEF_TITLE, defTitle);
        args.putString(PARAMS, params);
        args.putString(BASE_URL, baseUrl);
        args.putString(KEY_PARAM, keyParam);
        args.putLong(EXPIRE_INTERVAL, expireInterval);

        fragment.setArguments(args);
        return fragment;
    }

    public DynamicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dynamic, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkInternetConnection();
        empty.setText(getEmptyViewText());
    }

    public String getEmptyViewText() {
        return "";
    }

    public boolean checkInternetConnection() {
        if (NetworkUtils.hasInternetConnection(getActivity())) {
            hideErrorConnectionViews();
            return true;
        } else {
            setErrorConneсtionView();
            return false;
        }
    }

    public void hideErrorConnectionViews() {
        if (rootConnectionError.getVisibility() == View.VISIBLE) {
            rootConnectionError.setVisibility(View.GONE);
        }
        if (root.getVisibility() == View.GONE) {
            root.setVisibility(View.VISIBLE);
        }
    }

    public void setErrorConneсtionView() {
        rootConnectionError.setVisibility(View.VISIBLE);
        root.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
//        if (args == null)
//            args = new Bundle();
//        activity = (BaseActivity) getActivity();
//
//        if (args.containsKey(DEF_TITLE)) {
//            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
//                activity.getSupportActionBar().setTitle(args.getString(DEF_TITLE));
//            } else {
//                activity.setTitle(args.getString(DEF_TITLE));
//            }
//        }
//
//        JSONObject params = null;
//        try {
//            if (args.containsKey(PARAMS))
//                params = new JSONObject(args.getString(PARAMS));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if (params == null)
//            params = new JSONObject();
//
//        list.setEmptyView(getView().findViewById(android.R.id.empty));
//        manager = new DynamicManager(activity, list, args.getString(BASE_URL), params) {
//            @Override
//            protected void onStart() {
//                SwipeRefreshLayout ptrLayout = getPullToRefreshLayout();
//                if (ptrLayout != null)
//                    ptrLayout.setRefreshing(true);
//            }
//
//            @Override
//            protected void onStop() {
//                SwipeRefreshLayout ptrLayout = getPullToRefreshLayout();
//                if (ptrLayout != null)
//                    ptrLayout.setRefreshing(false);
//
//                ListViewHelper.restoreScrollableState(DynamicFragment.this.savedInstanceState, (ListView) list);
//            }
//
//        };
//        if (args.containsKey(KEY_PARAM))
//            manager.setKeyParam(args.getString(KEY_PARAM));
//        long expInterval = args.getLong(EXPIRE_INTERVAL, DEF_EXPIRE_INTERVAL);
//        if (expInterval != NO_CACHING)
//            manager.setExpireInterval(expInterval);
//
//        manager.addActionInterceptor(ResultType.PHONE, getPhoneInterceptor());
//        manager.addViewInterceptor(ResultType.APPBUTTON, getAppButtonInterceptor());
//        manager.addActionInterceptor(ResultType.LINK, getStupidLinkInterceptor());
//        manager.addViewInterceptor(ResultType.SEARCH, getSearchInterceptor());
//
//        if (!delayStart) {
//            manager.start();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        String tag = NewsDynamicFragment.class.getName();
//        ListViewHelper.restoreScrollableState(tag, (android.widget.ListView) list);
    }

    @Override
    public void onStop() {
        super.onStop();
//        String tag = NewsDynamicFragment.class.getName();
//        ListViewHelper.saveScrollableState(tag, (android.widget.ListView) list);
    }


//    public final void addViewInterceptor(ResultType type, ViewInterceptor<? extends Result> interceptor) {
//        manager.addViewInterceptor(type, interceptor);
//    }
//
//    public final void addActionInterceptor(ResultType type, ActionInterceptor<? extends Result> interceptor) {
//        manager.addActionInterceptor(type, interceptor);
//    }
//
//    public final void setAppButtonAction(AppButtonAction appButtonAction) {
//        this.appButtonAction = appButtonAction;
//    }
//
//    public DynamicManager getManager() {
//        return manager;
//    }
//
//    private ActionInterceptor<ResultLink> getStupidLinkInterceptor() {
//        return new ActionInterceptor<ResultLink>() {
//
//            @Override
//            public boolean onAct(ResultLink resultLink, int i) {
//                isForwardMotion = true;
//                return false;
//            }
//        };
//    }
//
//    public boolean canGoBack() {
//        return manager.manageBack();
//    }
//
//    public void onBackPressed() {
//        isForwardMotion = false;
//        if (isClean)
//            prevLocalSearch = null;
//        isClean = (prevLocalSearch != null && !isClean);
//    }

//    private ActionInterceptor<ResultText> getPhoneInterceptor() {
//        return new ActionInterceptor<ResultText>() {
//
//            @Override
//            public boolean onAct(final ResultText phone, int i) {
//                Dialogs.showYesNoDialog(activity, -1, R.string.elk_phone_call, R.string.elk_yes, R.string.elk_no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int buttonId) {
//                        if (buttonId == DialogInterface.BUTTON_POSITIVE) {
//                            String uri = "tel:" + phone.getBody();
//                            Intent intent = new Intent(Intent.ACTION_CALL);
//                            intent.setData(Uri.parse(uri));
//                            startActivity(intent);
//                        }
//                    }
//                }, null);
//                return true;
//            }
//        };
//    }

//    private ViewInterceptor<ResultAppButton> getAppButtonInterceptor() {
//        return new ViewInterceptor<ResultAppButton>() {
//
//            @Override
//            public boolean onAdd(ResultAppButton button, int i) {
//                if (button == null)
//                    appButtons.clear();
//                else
//                    appButtons.add(button);
//                getActivity().supportInvalidateOptionsMenu();
//                return true;
//            }
//
//            @Override
//            public void onClean() {
//
//            }
//        };
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
//        if (search != null) {
//            SearchDescription desc = new SearchDescription();
//            desc.setTitle(R.string.elk_search);
//            desc.setIconId(R.drawable.elk_search);
//            desc.setSearchView(getSearchView());
//            activity.addSearch(menu, desc);
//        }
//        for (ResultAppButton button : appButtons) {
//            ActionDescription action = appButtonAction.makeAction(button);
//            if (action != null)  //check if we know about this action
//                activity.addActionItem(menu, action);
//        }
//
//        super.onCreateOptionsMenu(menu, menuInflater);
//    }
//
//    private ViewInterceptor<ResultSearch> getSearchInterceptor() {
//
//        return new ViewInterceptor<ResultSearch>() {
//
//            @Override
//            public boolean onAdd(ResultSearch searchElement, int i) {
//                if (search != null && searchElement == null)
//                    isClean = false;
//                search = searchElement;
//                if (search != null && search.isLocal() && prevLocalSearch != null) {
//                    search.setBody(prevLocalSearch);
//                }
//                activity.supportInvalidateOptionsMenu();
//                return true;
//            }
//
//            @Override
//            public void onClean() {
//
//            }
//        };
//    }
//
//    private SearchView getSearchView() {
//        final SearchView searchView = new SearchView(activity);
//        searchView.setVisibility(View.VISIBLE);
//        searchView.setQueryHint(search.getPlaceHolder());
//        searchView.setIconifiedByDefault(isForwardMotion);
//        searchView.setIconified(TextUtils.isEmpty(search.getBody()) || !isForwardMotion);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                if (search != null && !search.isLocal()) {
//                    search = null;
//                    manager.setSearchText(false, query);
//                }
//
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (search.isLocal()) {
//                    isClean = true;
//                    prevLocalSearch = newText;
//                }
//                if (search.isLocal() && newText.length() >= Constants.THRESHOLD)
//                    manager.setSearchText(true, newText);
//                else
//                    manager.setSearchText(true, null);
//
//                return false;
//            }
//        });
//        searchView.setQuery(search.getBody(), false);
//
//        return searchView;
//    }
//
//    public void setDelayStart(boolean delayStart) {
//        this.delayStart = delayStart;
//    }
//
//    @Override
//    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(Response.Listener<Object> responseListener, Response.ErrorListener errorListener) {
//        return new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh();
//            }
//        };
//    }
//
//    public void refresh() {
//        manager.invalidateAll();
//        manager.start();
//    }

//    @OnClick(R.id.internet_lost_reload)
//    public void onReloadClick() {
//        if (checkInternetConnection()) {
//            refresh();
//        }
}


