package ru.mos.polls.quests;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.UrlManager;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.fragments.PullableFragment;
import ru.mos.polls.queries.QuestsRequest;
import ru.mos.polls.quests.controller.QuestStateController;
import ru.mos.polls.quests.controller.QuestsApiController;
import ru.mos.polls.quests.quest.AdvertisementQuest;
import ru.mos.polls.quests.quest.BackQuest;
import ru.mos.polls.quests.quest.FavoriteSurveysQuest;
import ru.mos.polls.quests.quest.NewsQuest;
import ru.mos.polls.quests.quest.OtherQuest;
import ru.mos.polls.quests.quest.ProfileQuest;
import ru.mos.polls.quests.quest.Quest;
import ru.mos.polls.quests.quest.RateAppQuest;
import ru.mos.polls.quests.quest.ResultsQuest;
import ru.mos.polls.quests.quest.SocialQuest;
import ru.mos.polls.quests.view.SpacesItemDecoration;
import ru.mos.polls.quests.view.SwipeItemTouchHelper;
import ru.mos.polls.quests.view.questviewholder.QuestsViewHolder;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.subscribes.gui.SubscribeActivity;
import ru.mos.polls.util.StubUtils;

public class QuestsFragment extends PullableFragment {

    @BindView(R.id.list)
    RecyclerView listView;
    @BindView(R.id.empty)
    LinearLayout empty;
    private Listener listener = new QuestsListenerStub();
    @BindView(R.id.rootConnectionError)
    View rootConnectionError;
    public List<Quest> quests;
    private Unbinder unbinder;
    private Menu menu;
    public QuestsItemAdapter adapter;
    public ItemTouchHelper mItemTouchHelper;
    public ItemTouchHelper.Callback callback;
    private RecyclerView.LayoutManager layoutManager;
    private GoogleStatistics.QuestsFragment qf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quests, container, false);
        unbinder = ButterKnife.bind(this, root);
        qf = new GoogleStatistics.QuestsFragment();
        layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);
        listView.setHasFixedSize(true);
        listView.addItemDecoration(new SpacesItemDecoration(2));

        quests = new ArrayList<>();
        adapter = new QuestsItemAdapter(quests, itemListener);
        listView.setAdapter(adapter);
        /*
        * добавляет свайп на recyclerview
         */
        callback = new SwipeItemTouchHelper(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(listView);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.logo);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            actionBar.setCustomView(imageView, layoutParams);
        }
        setHasOptionsMenu(true);
    }

    @OnClick(R.id.internet_lost_reload)
    public void refresh() {
        update(null, null);
    }

    @OnClick(R.id.subscribe)
    public void subscribe() {
        SubscribeActivity.startActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        inflater.inflate(R.menu.main, menu);
        hideNewsMenu();
    }

    private void hideNewsMenu() {
        int coutNews = 0;
        if (quests != null) {
            for (Quest q : quests) {
                String type = ((BackQuest) q).getType();
                if (type.equals("news") || type.equals("results")) {
                    coutNews++;
                }
            }
            if (menu != null) {
                MenuItem hideNews = menu.findItem(R.id.hideNews);
                if (hideNews != null) {
                    hideNews.setVisible(coutNews > 10);
                }
            }
        }
    }

    private void listVisibility() {
        if (listView != null) {
            listView.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.INVISIBLE);
        }
        if (empty != null) {
            empty.setVisibility(adapter.getItemCount() > 0 ? View.INVISIBLE : View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_invite_friends:
                if (listener != null) {
                    listener.onInviteFriends(true);
                }
                break;*/
            //мб потом понадобится, если что раскомментировать код в menu/main.xml
            case R.id.hideNews:
                hideAllNews();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideAllNews() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.hide_all_news_msg));
        builder.setPositiveButton(R.string.ag_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Statistics.hideAllNews();
                GoogleStatistics.QuestsFragment.hideAllNews();
                QuestsApiController.HideQuestListner hideListener = new QuestsApiController.HideQuestListner() {
                    @Override
                    public void hideQuests(ArrayList<String> idsList) {
                        for (String s : idsList) {
                            for (Quest quest : quests) {
                                if (((BackQuest) quest).getId().equals(s)) {
                                    quests.remove(quest);
                                    break;
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        hideNewsMenu();
                    }
                };
                QuestsApiController.hideAllNews((BaseActivity) getActivity(), quests, hideListener);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * Фильтруем задания {@link ru.mos.polls.quests.controller.QuestStateController}
         */
        qf.enterQuestFragment();
        if (quests != null && adapter != null) {
            List<Quest> filtered = QuestStateController.getInstance().process(quests);
            quests.clear();
            quests.addAll(filtered);
            adapter.notifyDataSetChanged();
        }
        update(null, null);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener(final Response.Listener<Object> responseListener, final Response.ErrorListener errorListener) {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update(responseListener, errorListener);
            }
        };
    }

    private void update(final Response.Listener<Object> addRespListener, final Response.ErrorListener addErrListener) {
        final BaseActivity activity = ((BaseActivity) getActivity());
        rootConnectionError.setVisibility(View.GONE);
        if (adapter == null || adapter.getItemCount() == 0) {
            empty.setVisibility(View.GONE);
        }

        getPullToRefreshLayout().post(new Runnable() {
            @Override
            public void run() {
                getPullToRefreshLayout().setRefreshing(true);
            }
        });

        Response.Listener<Quest[]> mainListener = new Response.Listener<Quest[]>() {
            @Override
            public void onResponse(Quest[] loadedQuests) {
                List<Quest> loadedListQuests = Arrays.asList(loadedQuests);
                loadedListQuests = prepareQuests(loadedListQuests);
                loadedListQuests = QuestStateController.getInstance().process(loadedListQuests);
                quests.clear();
                quests.addAll(loadedListQuests);
                adapter.notifyDataSetChanged();
                hideNewsMenu();
                adapter.getItemCount();
                if (addRespListener != null) {
                    addRespListener.onResponse(loadedQuests);
                }
                getPullToRefreshLayout().setRefreshing(false);
                listVisibility();
            }

            /**
             * Метод удаления ненужных заданий из ленты
             * Навреное, со временем станет устаревшим
             * @param quests - список загруженных заданий
             * @return "почищенный" список заданий
             */
            private List<Quest> prepareQuests(List<Quest> quests) {
                List<Quest> result = new ArrayList<Quest>();
                for (Quest quest : quests) {
                    /**
                     * удаляем публичные слушания и анонсы из ленты
                     */
                    if (quest instanceof FavoriteSurveysQuest) {
                        if (((FavoriteSurveysQuest) quest).isHearing()
                                || ((FavoriteSurveysQuest) quest).isHearingPreview()) {
                            continue;
                        }
                    }
                    /**
                     * Убираем квест визарда из ленты если список задания пустой
                     */
                    if (((BackQuest) quest).getId().equalsIgnoreCase(ProfileQuest.ID_PERSONAL_WIZARD)) {
                        if (((ProfileQuest) quest).idsList.size() == 0) continue;
                    }
                    /**
                     * Удаляем рекламные блоки из ленты
                     */
                    if (quest instanceof AdvertisementQuest) {
                        continue;
                    }
                    if (((BackQuest) quest).isHidden()) {
                        continue;
                    }
                    result.add(quest);
                }
                return result;
            }
        };

        Response.ErrorListener errorListener = new StandartErrorListener(getActivity(), R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                String errorMessage = volleyError.getMessage();
                if (errorMessage == null
                        || TextUtils.isEmpty(errorMessage)
                        || "null".equalsIgnoreCase(errorMessage)) {
                    errorMessage = activity.getString(R.string.rempte_service_not_work);
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                if (listView != null) {
                    listView.setVisibility(View.GONE);
                }
                if (empty != null) empty.setVisibility(View.GONE);
                if (rootConnectionError != null) rootConnectionError.setVisibility(View.VISIBLE);
                getPullToRefreshLayout().setRefreshing(false);
            }
        };
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.GET));
        QuestsRequest questsRequest = new QuestsRequest(url, null, mainListener, errorListener);
        activity.addRequest(questsRequest);
    }

    ItemRecyclerViewListener itemListener = new ItemRecyclerViewListener() {
        @Override
        public void onClick(BackQuest quest) {
            if (quest != null) {
                if (quest.getType().equalsIgnoreCase(NewsQuest.TYPE)) {
                    BadgeManager.markNewsAsReaded(Long.parseLong(quest.getId()));
                    BadgeManager.uploadReadedNews((BaseActivity) getActivity());
                }
                quest.onClick(getActivity(), listener);
                /**
                 * Скрываем блок из ленты
                 */
                if (isNeedHide(quest)) {
                    QuestsApiController.hide((BaseActivity) getActivity(), quest, null);
                }
            }
        }

        @Override
        public void onDelete(BackQuest quest, final int position) {
            if (quest != null) {
                if (quest.getType().equalsIgnoreCase(FavoriteSurveysQuest.ID_POLL)) {
                    Statistics.deleteSurveyHearing();
                    qf.deleteSurveyHearing();
                }
                QuestsApiController.HideListener hideListener = new QuestsApiController.HideListener() {
                    @Override
                    public void onHide(boolean isHide) {
                        listView.refreshDrawableState();
                        hideNewsMenu();
                    }
                };
                QuestsApiController.hide((BaseActivity) getActivity(), quest, hideListener);
                listVisibility();
            }
        }

        @Override
        public void onCancel(QuestsViewHolder holder) {
            if (holder != null) {
                callback.clearView(listView, holder);
            }
        }
    };

    /**
     * * Из-за особенностей обработки клика для блока AdvertisementQuest вызов на
     * * удаление вызывается внутри блока {@link ru.mos.polls.quests.quest.AdvertisementQuest}
     * *
     * * @param quest
     * * @return
     */
    private boolean isNeedHide(Quest quest) {
        return quest instanceof NewsQuest
                || quest instanceof OtherQuest
                || quest instanceof ResultsQuest
                || (quest instanceof RateAppQuest
                && SocialQuest.ID_RATE_THIS_APP.equalsIgnoreCase(((SocialQuest) quest).getId()));
    }

    public interface Listener {

        void onSurvey(long id);

        void onAllSurveys();

        void onUpdatePersonal();

        void onUpdateLocation();

        void onUpdateSocial();

        void onUpdateEmail();

        void onUpdateExtraInfo();

        void onUpdateFamilyInfo();

        void onBindToPgu();

        void onWizardProfile(List<String> list, int percent);

        void onRateThisApplication(String appId);

        void onInviteFriends(boolean isTask);

        void onSocialPost(AppPostValue appPostValue);

        void onNews(String title, String linkUrl);

        void onEvent(long eventId);

        void onOther(String title, String linkUrl);

        void onResults(String title, String linkUrl);
    }

    public interface ItemRecyclerViewListener {
        void onClick(BackQuest quest);

        void onDelete(BackQuest quest, int position);

        void onCancel(QuestsViewHolder holder);
    }
}
