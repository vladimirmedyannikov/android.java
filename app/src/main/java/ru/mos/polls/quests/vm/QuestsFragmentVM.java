package ru.mos.polls.newquests.vm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.vm.PullablePaginationFragmentVM;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.databinding.LayoutQuestsBinding;
import ru.mos.polls.fortesters.TestersController;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.controller.QuestStateController;
import ru.mos.polls.newquests.controller.QuestsApiController;
import ru.mos.polls.newquests.model.quest.AdvertisementQuest;
import ru.mos.polls.newquests.model.quest.BackQuest;
import ru.mos.polls.newquests.model.quest.FavoriteSurveysQuest;
import ru.mos.polls.newquests.model.quest.NewsQuest;
import ru.mos.polls.newquests.model.quest.OtherQuest;
import ru.mos.polls.newquests.model.quest.ProfileQuest;
import ru.mos.polls.newquests.model.quest.Quest;
import ru.mos.polls.newquests.model.quest.RateAppQuest;
import ru.mos.polls.newquests.model.quest.ResultsQuest;
import ru.mos.polls.newquests.model.quest.SocialQuest;
import ru.mos.polls.newquests.service.PolltaskGet;
import ru.mos.polls.newquests.ui.QuestsFragment;
import ru.mos.polls.newquests.ui.view.SpacesItemDecoration;
import ru.mos.polls.newquests.ui.view.SwipeItemTouchHelper;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.subscribes.gui.SubscribeActivity;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class QuestsFragmentVM extends PullablePaginationFragmentVM<QuestsFragment, LayoutQuestsBinding, QuestsItemAdapter> {
    public static final String ACTION_CANCEL_CLICK = "ru.mos.polls.newquests.vm.cancel_click";
    public static final String ACTION_JUST_CLICK = "ru.mos.polls.newquests.vm.just_click";
    public static final String ACTION_DELETE_CLICK = "ru.mos.polls.newquests.vm.delete_click";
    public static final String ACTION_ADVERTISEMENT_CLICK = "ru.mos.polls.newquests.vm.advertisement_click";
    public static final String ARG_QUEST = "quest";

    private Listener listener = Listener.STUB;
    private Menu menu = null;
    public List<Quest> quests;
    private Unbinder unbinder;
    public ItemTouchHelper mItemTouchHelper;
    public ItemTouchHelper.Callback callback;
    private RecyclerView.LayoutManager layoutManager;
    private boolean needRefreshAfterResume = false;

    private BroadcastReceiver cancelClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackQuest quest = (BackQuest) intent.getSerializableExtra(ARG_QUEST);
            String questId = quest.getId();
            if (adapter != null) adapter.onCancelClick(questId);
        }
    };

    private BroadcastReceiver clickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackQuest quest = (BackQuest) intent.getSerializableExtra(ARG_QUEST);
            String questId = quest.getId();
            if (adapter != null) adapter.onJustClick(questId);
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
    };

    private BroadcastReceiver deleteClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackQuest quest = (BackQuest) intent.getSerializableExtra(ARG_QUEST);
            String questId = quest.getId();
            if (adapter != null) adapter.onDeleteClick(questId);
            if (quest != null) {
                if (quest.getType().equalsIgnoreCase(FavoriteSurveysQuest.ID_POLL)) {
                    Statistics.deleteSurveyHearing();
                    GoogleStatistics.QuestsFragment.deleteSurveyHearing();
                }
                QuestsApiController.HideListener hideListener = new QuestsApiController.HideListener() {
                    @Override
                    public void onHide(boolean isHide) {
                        recyclerView.refreshDrawableState();
                        hideNewsMenu();
                    }
                };
                QuestsApiController.hide((BaseActivity) getActivity(), quest, hideListener);
            }
        }
    };

    private BroadcastReceiver advertisementClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BackQuest quest = (BackQuest) intent.getSerializableExtra(ARG_QUEST);
            UrlSchemeController.start(context, quest.getUrlScheme());
            QuestsApiController.hide((BaseActivity) context, quest, null);
        }
    };

    public QuestsFragmentVM(QuestsFragment fragment, LayoutQuestsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutQuestsBinding binding) {
        unbinder = ButterKnife.bind(this, getBinding().getRoot());
        quests = new ArrayList<>();
        adapter = new QuestsItemAdapter(quests);
        recyclerView = binding.list;
        setRecyclerView();
    }

    public void setRecyclerView() {
    /*
    * добавляет свайп на recyclerview
     */
        recyclerView.setAdapter(adapter);
        callback = new SwipeItemTouchHelper(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        layoutManager = new LinearLayoutManager(getFragment().getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2));
        recyclerView.addOnScrollListener(getScrollableListener());
        page = new Page();
        isPaginationEnable = true;
        recyclerUIComponent = new RecyclerUIComponent(adapter);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        LocalBroadcastManager.getInstance(getFragment().getContext()).registerReceiver(clickReceiver, new IntentFilter(ACTION_JUST_CLICK));
        LocalBroadcastManager.getInstance(getFragment().getContext()).registerReceiver(deleteClickReceiver, new IntentFilter(ACTION_DELETE_CLICK));
        LocalBroadcastManager.getInstance(getFragment().getContext()).registerReceiver(cancelClickReceiver, new IntentFilter(ACTION_CANCEL_CLICK));
        LocalBroadcastManager.getInstance(getFragment().getContext()).registerReceiver(advertisementClickReceiver, new IntentFilter(ACTION_ADVERTISEMENT_CLICK));
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * Фильтруем задания {@link ru.mos.polls.quests.controller.QuestStateController}
         */
        GoogleStatistics.QuestsFragment.enterQuestFragment();
        if (quests != null && adapter != null) {
            List<Quest> filtered = QuestStateController.getInstance().process(quests);
            quests.clear();
            quests.addAll(filtered);
            adapter.notifyDataSetChanged();
        }
        getActivity().setTitle(getFragment().getContext().getString(R.string.title_ag));
        if (needRefreshAfterResume) {
            doRequest();
        }
        needRefreshAfterResume = true;
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
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getFragment().getContext()).unregisterReceiver(clickReceiver);
        LocalBroadcastManager.getInstance(getFragment().getContext()).unregisterReceiver(deleteClickReceiver);
        LocalBroadcastManager.getInstance(getFragment().getContext()).unregisterReceiver(cancelClickReceiver);
        LocalBroadcastManager.getInstance(getFragment().getContext()).unregisterReceiver(advertisementClickReceiver);
        unbinder.unbind();
    }


    @Override
    public void doRequest() {
        HandlerApiResponseSubscriber<PolltaskGet.Response.Result> handler =
                new HandlerApiResponseSubscriber<PolltaskGet.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(PolltaskGet.Response.Result result) {
                        progressable = getPullableProgressable();
                        List<Quest> qlist = result.getTasks();
                        qlist = prepareQuests(qlist);
                        qlist = QuestStateController.getInstance().process(qlist);
                        quests.clear();
                        adapter.clear();
                        quests.addAll(qlist);
                        adapter.add(quests);
                        hideNewsMenu();
                        isPaginationEnable = false;
                        recyclerUIComponent.refreshUI();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }

                    @Override
                    public void onNext(GeneralResponse<PolltaskGet.Response.Result> generalResponse) {
                        super.onNext(generalResponse);
                    }
                };
        Observable<PolltaskGet.Response> responseObservable = AGApplication.api
                .getPolltasks(new PolltaskGet.Request())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
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
            if (coutNews <= 10) {
                getFragment().hideMenuItem(R.id.hideNews);
            } else {
                getFragment().showMenuItem(R.id.hideNews);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu() {
        new TestersController(getFragment().getMenu());
        hideNewsMenu();
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        TestersController.switchTestApi(menuItemId, getActivity());
        switch (menuItemId) {
            case R.id.hideNews:
                hideAllNews();
                break;
        }
    }

    /**
     * Метод удаления ненужных заданий из ленты
     * Навреное, со временем станет устаревшим
     *
     * @param quests - список загруженных заданий
     * @return "почищенный" список заданий
     */
    private List<Quest> prepareQuests(List<Quest> quests) {
        List<Quest> result = new ArrayList<>();
        for (Quest quest : quests) {
            if (quest != null) {
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
        }
        return result;
    }

    private void hideAllNews() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getFragment().getContext());
        builder.setMessage(getFragment().getContext().getResources().getString(R.string.hide_all_news_msg));
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

        Listener STUB = new Listener() {
            @Override
            public void onSurvey(long id) {

            }

            @Override
            public void onAllSurveys() {

            }

            @Override
            public void onUpdatePersonal() {

            }

            @Override
            public void onUpdateLocation() {

            }

            @Override
            public void onUpdateSocial() {

            }

            @Override
            public void onUpdateEmail() {

            }

            @Override
            public void onUpdateExtraInfo() {

            }

            @Override
            public void onUpdateFamilyInfo() {

            }

            @Override
            public void onBindToPgu() {

            }

            @Override
            public void onWizardProfile(List<String> list, int percent) {

            }

            @Override
            public void onRateThisApplication(String appId) {

            }

            @Override
            public void onInviteFriends(boolean isTask) {

            }

            @Override
            public void onSocialPost(AppPostValue appPostValue) {

            }

            @Override
            public void onNews(String title, String linkUrl) {

            }

            @Override
            public void onEvent(long eventId) {

            }

            @Override
            public void onOther(String title, String linkUrl) {

            }

            @Override
            public void onResults(String title, String linkUrl) {

            }
        };

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
}
