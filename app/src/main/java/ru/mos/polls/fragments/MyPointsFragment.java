package ru.mos.polls.fragments;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.volley2.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.PointsManager;
import ru.mos.polls.PromoController;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.db.PollsData;
import ru.mos.polls.db.PollsProvider;
import ru.mos.polls.helpers.ActionBarHelper;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.model.PointHistory;


public class MyPointsFragment extends StatusFragment implements DialogInterface.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int POINT_HISTORY_ID = 4;

    public static MyPointsFragment newInstance() {
        return new MyPointsFragment();
    }

    private MyPointsAdapter adapter;
    @BindView(R.id.tvCurrentPointsUnit)
    TextView tvCurrentPointsUnit;

    public MyPointsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleHelper.setTitle(getActivity(), R.string.my_points);
        setHasOptionsMenu(true);
        Statistics.enterBonus();
        GoogleStatistics.AGNavigation.enterBonus();
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * Оставляем исопльзование кастомного экшен бара для этого экрана, т.к
         * иконка нестандартная
         */
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.shopPromoCode();
                GoogleStatistics.AGNavigation.shopPromoCode();
                PromoController.showInputDialog((BaseActivity) getActivity());
            }
        };
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            View promoView = View.inflate(getActivity(), R.layout.view_promo, null);
            promoView.setOnClickListener(onClickListener);
            actionBar.setCustomView(promoView);
            Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) promoView.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ActionBarHelper.hideCustomActionBar((AppCompatActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_points, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final FragmentActivity activity = getActivity();
        AbsListView list = ButterKnife.findById(view,android.R.id.list);
        list.setEmptyView(view.findViewById(android.R.id.empty));
        adapter = new MyPointsAdapter(activity, null);
        (list).setAdapter(adapter);
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        if (loaderManager.getLoader(POINT_HISTORY_ID) == null) {
            loaderManager.initLoader(POINT_HISTORY_ID, null, this);
        } else {
            loaderManager.restartLoader(POINT_HISTORY_ID, null, this);
        }

        /**
         * убрал условие запроса не чаще, чем раз в PointsManger.isExpired(context)
         * пользователь не всегда догадыватеся сам обновить историю
         * полагает, что баллы не начислены и пишет в техподдержку
         */
        PointsManager.refreshPointHistory((BaseActivity) activity, new Response.Listener<Object>() {
            @Override
            public void onResponse(Object o) {
                processTitleBalance();
                processStatus();
                processPoints();
                processPointUnits();
            }
        }, null);

        processTitleBalance();
        processStatus();
        processPoints();
        processPointUnits();
    }

    @OnClick(R.id.shop)
    void goToShop() {
        Statistics.shopBuy();
        GoogleStatistics.AGNavigation.shopBuy();
    }
    @OnClick(R.id.tvCurrentPointsUnit)
    void showPopupPoints(View v){
        showPopup(v);
    }
    @Override
    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(Response.Listener<Object> responseListener, final Response.ErrorListener errorListener) {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /**
                 * Обновляем историю начисления баллов
                 * и статус пользователя
                 */
                PointsManager.refreshPointHistory((BaseActivity) getActivity(), new Response.Listener<Object>() {
                    @Override
                    public void onResponse(Object o) {
                        getPullToRefreshLayout().setRefreshing(false);
                        processTitleBalance();
                        processPoints();
                        processPointUnits();
                        processStatus();
                    }
                }, errorListener);
            }
        };
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle a) {
        /**
         * все баллы
         */
        String[] selection = null;
        String where = null;
        String[] args = null;
        String orderBy = PollsData.TPointHistory.WRITE_OFF_DATE + " desc";
        /**
         * баллы в зависимости от текущего типа
         */
        if (currentAction != null && !currentAction.isAll()) {
            where = PollsData.TPointHistory.ACTION + " = ?";
            args = new String[]{currentAction.toString()};
        }
        return new CursorLoader(
                getActivity(),
                PollsProvider.getContentUri(PollsData.TPointHistory.URI_CONTENT),
                selection,
                where,
                args,
                orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void processPointUnits() {
        int points = PointsManager.getPoints(getActivity(), currentAction);
        tvCurrentPointsUnit.setText(PointsManager.getPointUnitString(getActivity(), points));
    }

    /**
     * Контекстный диалог выбора списка баллов
     *
     * @param v - view, клик по которому вызывает данный диалог
     */
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.points_popup, popup.getMenu());
        /**
         * Задание кастомных view для пунктов меню, пока не работает
         */
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                boolean result = false;
                switch (menuItem.getItemId()) {
                    case R.id.action_debit:
                        currentAction = PointHistory.Action.DEBIT;
                        result = true;
                        break;
                    case R.id.action_credit:
                        currentAction = PointHistory.Action.CREDIT;
                        result = true;
                        break;
                    case R.id.action_all:
                        currentAction = PointHistory.Action.ALL;
                        result = true;
                        break;
                }
                processStatus();
                processTitleBalance();
                processPoints();
                processPointUnits();
                getActivity()
                        .getSupportLoaderManager()
                        .restartLoader(POINT_HISTORY_ID, null, MyPointsFragment.this);

                return result;
            }
        });
        popup.show();
    }

    /**
     * Кастомизация пунктов контекстного меню
     *
     * @param menu
     */
    private void allItemsCustomize(Menu menu) {
        setCustomItemMenu(menu, R.id.action_credit);
        setCustomItemMenu(menu, R.id.action_debit);
        setCustomItemMenu(menu, R.id.action_all);
    }

    /**
     * Задание кастомного view для пункта меню
     *
     * @param menu
     * @param itemId
     */
    private void setCustomItemMenu(Menu menu, int itemId) {
        MenuItem item = menu.findItem(itemId);
        if (item != null) {
            View view = View.inflate(getActivity(), R.layout.i_point_menu, null);
            TextView count = ButterKnife.findById(view, R.id.count);
            TextView title = ButterKnife.findById(view, R.id.title);
            int countValue = 0;
            switch (itemId) {
                case R.id.action_debit:
                    countValue = getCount(PointHistory.Action.DEBIT);
                    count.setText(String.valueOf(countValue));
                    title.setText(getString(R.string.action_current_points));
                    break;
                case R.id.action_credit:
                    countValue = getCount(PointHistory.Action.CREDIT);
                    count.setText(String.valueOf(countValue));
                    title.setText(getString(R.string.action_credit_points));
                    break;
                case R.id.action_all:
                    countValue = getCount(PointHistory.Action.ALL);
                    count.setText(String.valueOf(countValue));
                    title.setText(getString(R.string.action_all_points));
                    break;
            }
            //TODO как установить view MenuItem
        }
    }

    /**
     * Подсчет количества баллов по типу action
     *
     * @param action
     * @return
     */
    private int getCount(PointHistory.Action action) {
        /**
         * все
         */
        String[] selection = null;
        String where = null;
        String[] args = null;
        /**
         * по типам
         */
        if (currentAction != null && !currentAction.isAll()) {
            where = PollsData.TPointHistory.ACTION + " = ?";
            args = new String[]{action.toString()};
        }
        ContentResolver cr = getActivity().getContentResolver();
        Uri uri = PollsProvider.getContentUri(PollsData.TPointHistory.URI_CONTENT);

        Cursor c = cr.query(uri, selection, where, args, null);
        int result = 0;
        if (c != null) {
            result = c.getCount();
        }
        return result;
    }
}
