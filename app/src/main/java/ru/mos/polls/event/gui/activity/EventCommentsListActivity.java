package ru.mos.polls.event.gui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.common.controller.ScrollableController;
import ru.mos.polls.event.controller.EventAPIController;
import ru.mos.polls.event.model.CommentPageInfo;
import ru.mos.polls.event.model.Event;
import ru.mos.polls.event.model.EventComment;
import ru.mos.polls.helpers.ListViewHelper;


public class EventCommentsListActivity extends BaseActivity {

    private static final String EXTRA_EVENT_ID = "event_id";
    private static final String EXTRA_EVENT_TYPE = "event_type";

    public static Intent getStartIntent(Context context, long evenId, Event.Type type) {
        Intent intent = new Intent(context, EventCommentsListActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, evenId);
        String value = "";
        if (type != null) {
            value = type.toString();
        }
        intent.putExtra(EXTRA_EVENT_TYPE, value);
        return intent;
    }

    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.emptyMessage)
    TextView emptyMessage;

    private long eventId;

    private CommentPageInfo currentCommentPageInfo;
    private EventComment myComment;
    private List<EventComment> eventComments = new ArrayList<EventComment>();

    private ScrollableController scrollableController = new ScrollableController(new ScrollableController.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
            getNextEventCommentList();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_event_comments_list);
        ButterKnife.bind(this);
        setEventId();
        findViews();
        Statistics.enterEventsComments(eventId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        processVisible();
        refreshEventCommentList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.enent_comments_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                result = true;
                break;
            case R.id.action_comment_add:
                final Intent intent;
                String type = getIntent().getStringExtra(EXTRA_EVENT_TYPE);
                if (myComment == null) {
                    intent = EventCommentAddActivity.createIntent(this, eventId, type);
                } else {
                    intent = EventCommentAddActivity.createIntent(this, eventId, myComment, type);
                }
                startActivity(intent);
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }


    private void setEventId() {
        eventId = getIntent().getLongExtra(EXTRA_EVENT_ID, -1);
        if (eventId == -1) {
            finish();
        }
    }

    private void findViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * Подгрузка списка комментариев по достижении конца списка
         */
        listView.setOnScrollListener(scrollableController);
    }

    private void processVisible() {
        if (eventComments != null && eventComments.size() > 0) {
            emptyMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            emptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
    }

    /**
     * Получение первых 10 комментариев, вызывается из onCreate() и на pull to refresh.
     * При успешном зарпосе комментариев старая лента комментариев затирается, список обновляется и
     * скроллируется на первую позицию
     */
    private void refreshEventCommentList() {
        EventAPIController.EventCommentListListener listener = new EventAPIController.EventCommentListListener() {
            @Override
            public void onGetEventCommentList(EventComment myComment, List<EventComment> eventComments, CommentPageInfo commentPageInfo) {
                /**
                 * Удаляем все комментарии из ленты комментариев, и записываем свои комментарий первым
                 */
                EventCommentsListActivity.this.eventComments.clear();
                if (myComment != null && !myComment.isEmpty()) {
                    eventComments.add(0, myComment);
                }
                /**
                 * Поддерживаем актуальное состояние списка, объекта своего комментария и информации о странице комментариев
                 */
                EventCommentsListActivity.this.eventComments.addAll(eventComments);
                EventCommentsListActivity.this.setTitle(getString(R.string.comments) + " (" + eventComments.size() + ")");
                EventCommentsListActivity.this.myComment = myComment;
                EventCommentsListActivity.this.currentCommentPageInfo = commentPageInfo;

                refreshUI();
            }

            @Override
            public void onError(VolleyError volleyError) {
                String errorMessage = String.format(getString(R.string.error_occurs), volleyError.getMessage());
                Toast.makeText(EventCommentsListActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        };
        EventAPIController.loadEventCommentList(this, eventId, listener);
    }

    /**
     * Получение истории комментариев, вызывается при достижении конца списка
     * На момент вызова метода отключаем работу ScrollableController-а,
     * чтобы пользователь несколько раз этот метод не вызвал.
     */
    private void getNextEventCommentList() {
        setSupportProgressBarIndeterminateVisibility(true);
        EventAPIController.EventCommentListListener listener = new EventAPIController.EventCommentListListener() {
            @Override
            public void onGetEventCommentList(EventComment myComment, List<EventComment> eventComments, CommentPageInfo commentPageInfo) {
                setSupportProgressBarIndeterminateVisibility(false);
                /**
                 * Выводим сообщение на экран, что подгружена история комментариев,
                 * а иначе пользователь может не понять, что лента комментариев обновлена
                 */
                if (eventComments.size() > 0) {
                    Toast.makeText(EventCommentsListActivity.this,
                            R.string.new_comments_added, Toast.LENGTH_SHORT).show();
                }
                /**
                 * Поддерживаем актуальное состоняие списка, объекта своего комментария и информации о странице комментариев
                 */
                EventCommentsListActivity.this.eventComments.addAll(eventComments);
                EventCommentsListActivity.this.myComment = myComment;
                EventCommentsListActivity.this.currentCommentPageInfo = commentPageInfo;

                refreshUI();
                scrollableController.setAllowed(true);
            }

            @Override
            public void onError(VolleyError volleyError) {
                setSupportProgressBarIndeterminateVisibility(false);
                scrollableController.setAllowed(true);
            }
        };
        if (currentCommentPageInfo != null) { // не было комментариев, перезапрашиаем последние 10 коментариев
            scrollableController.setAllowed(false);
            currentCommentPageInfo.incrementPage();
            EventAPIController.loadEventCommentList(this, eventId, currentCommentPageInfo, listener);
        } else {
            refreshEventCommentList();
        }
    }

    /**
     * При обновлении списка, сохраняется состояние скроллинга списка
     */
    private void refreshUI() {
        ArrayAdapter adapter = new CommentsAdapter(this, R.layout.listitem_event_comment, eventComments);
        String tag = EventCommentsListActivity.class.getName();
        ListViewHelper.saveScrollableState(tag, listView);
        listView.setAdapter(adapter);
        ListViewHelper.restoreScrollableState(tag, listView);
        processVisible();
    }

    /**
     * Удаление комментария, вызывается по нажатию на пункт контекстного меню "Удалить"
     * Если удаление прошло успешно, то запрашиваем последние 10 коментариев, другие затираем
     */
    private void deleteComment() {
        EventAPIController.UpdateEventCommentListener listener = new EventAPIController.UpdateEventCommentListener() {
            @Override
            public void onUpdated(boolean isUpdated) {
                if (isUpdated) {
                    refreshEventCommentList();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                String errorMessage = String.format(getString(R.string.error_occurs), volleyError.getMessage());
                Toast.makeText(EventCommentsListActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        };
        EventAPIController.deleteComment(EventCommentsListActivity.this, eventId, listener);
    }

    private void updateComment() {
        String type = getIntent().getStringExtra(EXTRA_EVENT_TYPE);
        Intent startIntent = EventCommentAddActivity.createIntent(this, eventId, myComment, type);
        startActivity(startIntent);
    }

    /**
     * Адаптер для списка комментариев
     */
    class CommentsAdapter extends ArrayAdapter<EventComment> {
        private final int resource;

        public CommentsAdapter(Context context, int resource, List<EventComment> objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = View.inflate(getContext(), resource, null);
            final EventComment eventComment = getItem(position);
            setAuthor(convertView, eventComment);
            setDate(convertView, eventComment);
            setRating(convertView, eventComment);
            setBody(convertView, eventComment);
            setContextMenu(convertView, eventComment);
            setTitle(convertView, eventComment);
            return convertView;
        }

        private void setAuthor(View view, EventComment eventComment) {
            TextView author = ButterKnife.findById(view, R.id.author);
            author.setText(eventComment.getAuthor());
        }

        private void setDate(View view, EventComment eventComment) {
            TextView date = ButterKnife.findById(view, R.id.date);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date value = new Date(Math.round(eventComment.getUpdateDate() * 1000.0));
            String result = sdf.format(value);
            date.setText(result);
        }

        private void setRating(View view, EventComment eventComment) {
            RatingBar rating = ButterKnife.findById(view, R.id.rating);
            rating.setRating((float) eventComment.getRating());
        }

        private void setTitle(View view, EventComment eventComment) {
            TextView title = ButterKnife.findById(view, R.id.title);
            if (TextUtils.isEmpty(eventComment.getTitle())) {
                title.setVisibility(View.GONE);
            } else {
                title.setVisibility(View.VISIBLE);
                title.setText(eventComment.getTitle());
            }
        }

        private void setBody(View view, EventComment eventComment) {
            TextView body = ButterKnife.findById(view, R.id.body);
            if (TextUtils.isEmpty(eventComment.getBody())) {
                body.setVisibility(View.GONE);
            } else {
                body.setVisibility(View.VISIBLE);
                body.setText(eventComment.getBody());
            }
        }

        /**
         * View для контекстного меню
         *
         * @param view
         * @param eventComment
         */
        private void setContextMenu(View view, EventComment eventComment) {
            /**
             * Показываем view для контекстного меню только для комментария,
             * который можно редактировать, то есть своего комментария
             */
            if (eventComment.isEditable()) {
                ImageView expandedMenu = ButterKnife.findById(view, R.id.expandedMenu);
                expandedMenu.setVisibility(View.VISIBLE);
            }
        }

        @OnClick(R.id.expandedMenu)
        void expandedMenu(View v) {
            showPopup(v);
        }

        /**
         * Вызов диалога контекстного меню для своего комментария
         *
         * @param v
         */
        private void showPopup(View v) {
            PopupMenu popup = new PopupMenu(EventCommentsListActivity.this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.comment, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    boolean result = false;
                    switch (menuItem.getItemId()) {
                        case R.id.action_edit:
                            updateComment();
                            result = true;
                            break;
                        case R.id.action_delete:
                            deleteComment();
                            result = true;
                            break;
                    }
                    return result;
                }
            });
            popup.show();
        }
    }
}