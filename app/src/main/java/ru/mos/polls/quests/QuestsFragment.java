package ru.mos.polls.quests;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.badge.model.BadgesSource;
import ru.mos.polls.fragments.PullableFragment;
import ru.mos.polls.helpers.ListViewHelper;
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
import ru.mos.polls.social.model.SocialPostValue;
import ru.mos.polls.subscribes.gui.SubscribeActivity;

public class QuestsFragment extends PullableFragment {

    @BindView(R.id.list)
    SwipeListView listView;
    @BindView(R.id.empty)
    LinearLayout empty;
    private QuestsAdapter adapter;
    private Listener listener = new QuestsListenerStub();
    @BindView(R.id.stubOffline)
    View stubOffline;
    public static List<Quest> quests;
    private ImageView userAvatarImageView;
    private BroadcastReceiver reloadAvatarFromCacheBroadcastReceiver;
    private View listHeaderView, headerRoot;
    private Unbinder unbinder;
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quests, container, false);
        unbinder = ButterKnife.bind(this, root);
        listView.setEmptyView(empty);
        listHeaderView = View.inflate(getActivity(), R.layout.quest_user_avatar, null);
        headerRoot = ButterKnife.findById(listHeaderView, R.id.headerRoot);
        userAvatarImageView = ButterKnife.findById(listHeaderView, R.id.userAvatar);
        listView.addHeaderView(listHeaderView);
        quests = new ArrayList<>();
        adapter = new QuestsAdapter(getActivity(), quests);
        listView.setAdapter(adapter);
        ListViewHelper.clearScrollableState();
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
        doReloadAvatarFromCache();
    }

    @OnClick(R.id.refresh)
    public void refresh() {
        update(null, null);
    }

    @OnClick(R.id.subscribe)
    public void subscribe() {
        SubscribeActivity.startActivity(getActivity());
    }

    private void doReloadAvatarFromCache() {
        Bitmap bitmap = BadgesSource.getInstance().getAvatar();
        if (bitmap != null) {
            userAvatarImageView.setImageBitmap(bitmap);
            headerRoot.setVisibility(View.VISIBLE);
        } else {
            headerRoot.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reloadAvatarFromCacheBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                doReloadAvatarFromCache();
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(reloadAvatarFromCacheBroadcastReceiver, BadgeManager.RELOAD_AVATAR_FROM_CACHE_INTENT_FILTER);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(reloadAvatarFromCacheBroadcastReceiver);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        hideNewsMenu();
    }

    private void hideNewsMenu() {
        int coutNews = 0;
        if (quests != null) {
            for (Quest q : quests) {
                String type = ((BackQuest) q).getType();
                if (type.equals("news") || type.equals("results") || type.equals("other")) {
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

    public static boolean socialQuestIsAvaible() {
        if (quests != null) {
            for (Quest quest : quests) {
                if (quest instanceof ProfileQuest) {
                    if (((ProfileQuest) quest).getId().equals(ProfileQuest.ID_UPDATE_SOCIAL))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_invite_friends:
                if (listener != null) {
                    listener.onInviteFriends(true);
                }
                break;
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
        if (quests != null && adapter != null) {
            List<Quest> filtered = QuestStateController.getInstance().process(quests);
            quests.clear();
            quests.addAll(filtered);
            adapter.notifyDataSetChanged();
        }
        /**
         * Восстанавливаем позиция скролла и перезапрашиваем список заданий
         */
        ListViewHelper.restoreScrollableState(QuestsFragment.class.getName(), listView);
        update(null, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        ListViewHelper.saveScrollableState(QuestsFragment.class.getName(), listView);
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
        stubOffline.setVisibility(View.GONE);
        if (adapter == null || adapter.getCount() == 0) {
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
                ListViewHelper.saveScrollableState(QuestsFragment.class.getName(), listView);
                quests.clear();
                quests.addAll(loadedListQuests);
                adapter.notifyDataSetChanged();
                hideNewsMenu();
                adapter.getCount();
                setHideListener();
                setSwipeListener();
                listView.setVisibility(View.VISIBLE);
                ListViewHelper.restoreScrollableState(QuestsFragment.class.getName(), listView);

                if (addRespListener != null) {
                    addRespListener.onResponse(loadedQuests);
                }
                getPullToRefreshLayout().setRefreshing(false);
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
                     * Удаляем рекламные блоки из ленты
                     */
                    if (quest instanceof AdvertisementQuest) {
                        continue;
                    }

                    result.add(quest);
                }
                return result;
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String errorMessage = volleyError.getMessage();
                if (errorMessage == null
                        || TextUtils.isEmpty(errorMessage)
                        || "null".equalsIgnoreCase(errorMessage)) {
                    errorMessage = activity.getString(R.string.rempte_service_not_work);
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                listView.setVisibility(View.GONE);
                empty.setVisibility(View.GONE);
                stubOffline.setVisibility(View.VISIBLE);
                getPullToRefreshLayout().setRefreshing(false);
            }

        };
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.GET));
        QuestsRequest questsRequest = new QuestsRequest(url, null, mainListener, errorListener);
        activity.addRequest(questsRequest);
    }

    private void setHideListener() {
        if (adapter != null) {
            QuestsAdapter.HideListener hideListener = new QuestsAdapter.HideListener() {
                @Override
                public void onCancel(int position) {
                    /**
                     * При добавлении хедера,
                     * смещается клик на одну позицию
                     */
                    if (listView.getHeaderViewsCount() > 0) {
                        ++position;
                    }
                    listView.closeAnimate(position);
                }

                @Override
                public void onDelete(int position) {
                    /**
                     * При добавлении хедера,
                     * смещается клик на одну позицию
                     */
                    if (listView.getHeaderViewsCount() > 0) {
                        ++position;
                    }
                    listView.dismiss(position);
                }
            };
            adapter.setHideListener(hideListener);
        }
    }

    private void setSwipeListener() {
        if (listView != null) {
            listView.setSwipeListViewListener(new BaseSwipeListViewListener() {
                @Override
                public void onOpened(int position, boolean toRight) {
                }

                @Override
                public void onClosed(int position, boolean fromRight) {
                }

                @Override
                public void onListChanged() {
                }

                @Override
                public void onMove(int position, float x) {
                }

                @Override
                public void onStartOpen(int position, int action, boolean right) {
                }

                @Override
                public void onStartClose(int position, boolean right) {
                }

                @Override
                public void onClickFrontView(int position) {
                    /**
                     * При добавлении хедера,
                     * смещается клик на одну позицию
                     */
                    if (listView.getHeaderViewsCount() > 0 && position > 0) {
                        --position;
                    }

                    Quest quest = adapter.getItem(position);
                    if (quest != null) {
                        quest.onClick(getActivity(), listener);
                        /**
                         * Скрываем блок из ленты
                         */
                        if (isNeedHide(quest)) {
                            QuestsApiController.hide((BaseActivity) getActivity(), (BackQuest) quest, null);
                        }
                    }
                }

                @Override
                public void onClickBackView(int position) {
                }

                @Override
                public void onDismiss(int[] reverseSortedPositions) {
                    try {
                        for (int position : reverseSortedPositions) {
                            /**
                             * Убираем смещение из-за наличия хедера
                             */
                            if (listView.getHeaderViewsCount() > 0) {
                                --position;
                            }
                            hideQuest(position);
                            quests.remove(position);
                            listView.closeAnimate(position);
                        }
                        adapter.notifyDataSetChanged();
                        listView.refreshDrawableState();
                        listView.resetScrolling();
                    } catch (Exception ignored) {
                    }
                }

                private void hideQuest(int position) {
                    if (adapter.getItem(position) instanceof BackQuest) {
                        BackQuest backQuest = (BackQuest) adapter.getItem(position);
                        QuestsApiController.HideListener hideListener = new QuestsApiController.HideListener() {
                            @Override
                            public void onHide(boolean isHide) {
                                adapter.notifyDataSetChanged();
                                listView.refreshDrawableState();
                                hideNewsMenu();
                            }
                        };
                        QuestsApiController.hide((BaseActivity) getActivity(), backQuest, hideListener);
                    }
                }

                /**
                 * Из-за особенностей обработки клика для блока AdvertisementQuest вызов на
                 * удаление вызывается внутри блока {@link ru.mos.polls.quests.quest.AdvertisementQuest}
                 *
                 * @param quest
                 * @return
                 */
                private boolean isNeedHide(Quest quest) {
                    return quest instanceof NewsQuest
                            || quest instanceof OtherQuest
                            || quest instanceof ResultsQuest
                            || (quest instanceof RateAppQuest
                            && SocialQuest.ID_RATE_THIS_APP.equalsIgnoreCase(((SocialQuest) quest).getId()));
                }
            });
        }
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

        void onRateThisApplication(String appId);

        void onInviteFriends(boolean isTask);

        void onSocialPost(SocialPostValue socialPostValue);

        void onNews(String title, String linkUrl);

        void onEvent(long eventId);

        void onOther(String title, String linkUrl);

        void onResults(String title, String linkUrl);
    }

}
