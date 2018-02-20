package ru.mos.polls.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import ru.mos.polls.R;


public abstract class PullableFragment extends Fragment {

    protected SwipeRefreshLayout ptrLayout;
    protected Unbinder unbinder;
    protected CompositeDisposable disposables;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ptrLayout = (SwipeRefreshLayout) view.findViewById(R.id.ptrLayout);

        if (ptrLayout != null) {
            ptrLayout.setColorSchemeResources(R.color.green_light, R.color.green_light/*, R.color.lightGreen*/);
            StopRefreshListener listener = new StopRefreshListener();
            ptrLayout.setOnRefreshListener(getOnRefreshListener(listener, listener));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ptrLayout.setProgressViewOffset(false, 0,200);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder !=null){
            unbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    public SwipeRefreshLayout getPullToRefreshLayout() {
        return ptrLayout;
    }

    protected void setRefreshing() {
        if (ptrLayout != null) {
            ptrLayout.post(new Runnable() {
                @Override
                public void run() {
                    ptrLayout.setRefreshing(true);
                }
            });
        }
    }

    public abstract SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(Response.Listener<Object> responseListener, Response.ErrorListener errorListener);

    private class StopRefreshListener implements Response.Listener<Object>,
            Response.ErrorListener,
            SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            ptrLayout.setRefreshing(false);
        }

        @Override
        public void onResponse(Object jsonObject) {
            ptrLayout.setRefreshing(false);
        }


        @Override
        public void onRefresh() {
        }
    }
}
