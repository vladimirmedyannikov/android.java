package ru.mos.polls.event.gui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.common.controller.LocationController;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.common.model.Message;
import ru.mos.polls.common.model.Position;
import ru.mos.polls.event.controller.EventAPIController;
import ru.mos.polls.event.gui.view.DrawableAlignedButton;
import ru.mos.polls.event.model.Event;
import ru.mos.polls.event.model.EventDetail;
import ru.mos.polls.event.model.Filter;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.subscribes.controller.SubscribesUIController;
import ru.mos.social.callback.PostCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.PostValue;
import ru.mos.social.model.social.Social;


public class EventActivity extends ToolbarAbstractActivity {
    private static final String EXTRA_EVENT_ID = "extra_event_id";
    private static final String EXTRA_LAT = "extra_lat";
    private static final String EXTRA_LON = "extra_lon";
    private static final String EXTRA_FILTER = "extra_filter";

    public static void startActivity(Context context, long eventId) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        intent.putExtra(EXTRA_FILTER, "");
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Event event, Filter filter) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, event.getId());
        intent.putExtra(EXTRA_LAT, event.getPosition().getLat());
        intent.putExtra(EXTRA_LON, event.getPosition().getLon());
        intent.putExtra(EXTRA_FILTER, filter.toString());
        context.startActivity(intent);
    }

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.title)
    TextView titleCheckIn;
    @BindView(R.id.subtitle)
    TextView subtitleCheckIn;
    @BindView(R.id.comment)
    DrawableAlignedButton comment;
    @BindView(R.id.checkIn)
    LinearLayout checkIn;
    @BindView(R.id.buttonContainer)
    LinearLayout buttonContainer;
    @BindView(R.id.avrRating)
    RatingBar avrRating;
    @BindView(R.id.imageViewPager)
    ViewPager imagesViewPager;
    @BindView(R.id.screen)
    LinearLayout screen;
    @BindView(R.id.shortContainer)
    LinearLayout detailContainer;
    @BindView(R.id.pin)
    ImageView pin;

    private long eventId;
    private Position currentPosition;
    private Position eventPosition;
    private Event event;
    private Filter filter;

    private LocationController locationController;
    private SocialController socialController;
    private UIEventBuilder uiEventBuilder = new UIEventBuilder();
    private MenuItem subscribeMenuItem;
    private boolean isEventLoaded, isGPSEnableDialogShowed, isRuntimePermissionRejected;

    private PostCallback postCallback = new PostCallback() {
        @Override
        public void postSuccess(Social social, @Nullable PostValue postValue) {
            SocialUIController.sendPostingResult(EventActivity.this, (AppPostValue) postValue, null);
        }

        @Override
        public void postFailure(Social social, @Nullable PostValue postValue, Exception e) {
            SocialUIController.sendPostingResult(EventActivity.this, (AppPostValue) postValue, e);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * мероприятия временно отключены
         */
        ButterKnife.bind(this);
        boolean isEventIdExist = getEventId();
        getFilter();
        getEventPosition();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uiEventBuilder.findViews();
        socialController = new SocialController(this);
        socialController.getEventController().registerCallback(postCallback);
        Statistics.enterEventTicket(eventId);
        GoogleStatistics.Events.enterEventTicket(eventId);
        SocialUIController.registerPostingReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LocationController.isLocationNetworkProviderEnabled(this) || LocationController.isLocationGPSProviderEnabled(this)) {
            if (!isRuntimePermissionRejected) {
                getLocationController();
            }
        } else {
            if (!isGPSEnableDialogShowed) {
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refreshEvent();
                    }
                };
                LocationController.showDialogEnableLocationProvider(this, cancelListener);
                isGPSEnableDialogShowed = true;
            } else {
                refreshEvent();
            }
        }
    }

    @Override
    protected void onDestroy() {
        SocialUIController.unregisterPostingReceiver(this);
        socialController.getEventController().unregisterAllCallback();
        if (locationController != null) {
            locationController.disconnect();
        }
        super.onDestroy();
    }

    public void checkIn() {
        EventAPIController.CheckInListener checkInListener = new EventAPIController.CheckInListener() {
            @Override
            public void onChecked(int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state, Message message) {
                Statistics.enterCheckIn(eventId, true, false);
                GoogleStatistics.Events.enterCheckIn(eventId, true, false);
                if (message != null && !message.isEmpty()) {
                    message.showCustomMessage(EventActivity.this, null, AppPostValue.Type.CHECK_IN, eventId, new Runnable() {
                        @Override
                        public void run() {
                            EventActivity.this.finish();
                        }
                    });
                } else {
                    showSuccessCheckInDialog(freezedPoints, spentPoints, allPoints, currentPoints, state);
                }
                event.setChecked();
                uiEventBuilder.processCheckIn();
                processMenu();
            }

            @Override
            public void onError(VolleyError volleyError) {
                String errorMessage = String.format(getString(R.string.error_occurs), volleyError.getMessage());
                Toast.makeText(EventActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        };
        EventAPIController.checkIn(this, event.getId(), currentPosition, checkInListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AGApplication.IS_LOCAL_SUBSCRIBE_ENABLE) {
            getMenuInflater().inflate(R.menu.subscribe, menu);
            subscribeMenuItem = menu.findItem(R.id.action_subscribe);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_subscribe:
                SubscribesUIController subscribesUIController = new SubscribesUIController(this);
                subscribesUIController.showSubscribeDialogForEvent(this, event);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void processMenu() {
        if (AGApplication.IS_LOCAL_SUBSCRIBE_ENABLE && subscribeMenuItem != null) {
            boolean isEnable = !event.isCheckIn() && !event.hasPastDate();
            subscribeMenuItem.setVisible(isEnable);
        }
    }

    private void getLocationController() {
        locationController = LocationController.getInstance(this);
        locationController.connect(this);
        locationController.setOnPositionListener(new LocationController.OnPositionListener() {
            @Override
            public void onGet(Position position) {
                currentPosition = position;
                if (!isEventLoaded) {
                    isEventLoaded = true;
                    refreshEvent();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (locationController.onRequestPermissionsResult(this, requestCode, grantResults)) {
            getLocationController();
        } else {
            isRuntimePermissionRejected = true;
            refreshEvent();
        }
    }

    private String processMessage(int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state) {
        String suitableString1 = PointsManager
                .getSuitableString(EventActivity.this, R.array.survey_done_message_1, event.getPoints());
        String message1 = String.format(suitableString1, event.getPoints());
        String suitableString2 = PointsManager
                .getSuitableString(EventActivity.this, R.array.survey_done_message_2, currentPoints);
        String message2 = String.format(suitableString2, currentPoints);
        return message1 + message2;
    }

    /**
     * Вывод диалога об успешном чекине
     *
     * @param freezedPoints
     * @param spentPoints
     * @param allPoints
     * @param currentPoints
     * @param state
     */
    private void showSuccessCheckInDialog(int freezedPoints, final int spentPoints, int allPoints, int currentPoints, String state) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.success_checkin);
        String message = processMessage(freezedPoints, spentPoints, allPoints, currentPoints, state);
        String message2 = getString(R.string.survey_dialog_for_share_message);
        builder.setMessage(message + "\n" + message2);
        builder.setPositiveButton(R.string.ag_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(R.string.ag_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
                    @Override
                    public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                        socialController.post(socialPostValue, socialPostValue.getSocialId());
                    }

                    @Override
                    public void onCancel() {
                        EventActivity.this.finish();
                    }
                };
                SocialUIController.showSocialsDialogForEvent(EventActivity.this, event, listener);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialController.onActivityResult(requestCode, resultCode, data);
    }

    private void refreshEvent() {
        EventAPIController.EventListener eventListener = new EventAPIController.EventListener() {
            @Override
            public void onGetEvent(Event event) {
                EventActivity.this.event = event;
                refreshControls(event);
                uiEventBuilder.setUIVisible(View.VISIBLE);
                processMenu();
                isEventLoaded = true;
            }

            @Override
            public void onError(VolleyError volleyError) {
                String errorMessage = String.format(getString(R.string.error_occurs), volleyError.getMessage());
                Toast.makeText(EventActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                uiEventBuilder.setUIVisible(View.VISIBLE);
                isEventLoaded = false;
            }
        };
        uiEventBuilder.setUIVisible(View.GONE);
        EventAPIController.loadEvent(this, eventId, currentPosition, eventListener);
    }

    private void refreshControls(Event event) {
        uiEventBuilder.build(event);
    }

    private boolean getEventId() {
        Intent intent = getIntent();
        eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1);
        UrlSchemeController.startEvent(this, new UrlSchemeController.IdListener() {
            @Override
            public void onDetectedId(Object id) {
                eventId = (Long) id;
            }
        });
        boolean result = eventId != -1;
        if (!result) {
            finish();
        }
        return result;
    }

    private void getFilter() {
        String filterValue = getIntent().getStringExtra(EXTRA_FILTER);
        filter = Filter.fromFilter(filterValue);
    }

    /**
     * Позиция выбранного в списке меропрятие
     */
    private void getEventPosition() {
        double lat = getIntent().getDoubleExtra(EXTRA_LAT, 0);
        double lon = getIntent().getDoubleExtra(EXTRA_LON, 0);
        eventPosition = new Position(lat, lon);
    }

    /**
     * Класс инкапсулирует построение пользовательского интерфейса, наполнение его данными
     */
    class UIEventBuilder {
        private Event event;
        private boolean isInit;

        private View.OnClickListener shareListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
                    @Override
                    public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                        socialController = new SocialController(EventActivity.this);
                        socialController.post(socialPostValue, socialPostValue.getSocialId());
                    }

                    @Override
                    public void onCancel() {
                        EventActivity.this.finish();
                    }
                };
                SocialUIController.showSocialsDialogForEvent(EventActivity.this, event, listener);
            }
        };

        private View.OnClickListener checkInListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Проверяем включена ли функция определения местоположения,
                 * если нет, то показываем диалог с предложением включить
                 */
                if (!locationController.isLocationGPSProviderEnabled(EventActivity.this) && !locationController.isLocationNetworkProviderEnabled(EventActivity.this)) {
                    locationController.showDialogEnableLocationProvider(EventActivity.this, null);
                    return;
                }
                /**
                 * Текущие координаты пользователя не определены,
                 * выполнить checkIn не получится, показываем диалог с предлжением подождать
                 */
                if (currentPosition == null || currentPosition.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                    String message = "";
                    if (LocationController.isLocationGPSProviderEnabled(EventActivity.this) && !LocationController.isLocationNetworkProviderEnabled(EventActivity.this)) {
                        message = String.format(getResources().getString(R.string.current_position_not_detected), getResources().getString(R.string.location_provader_network));
                    }
                    if (LocationController.isLocationNetworkProviderEnabled(EventActivity.this) && !LocationController.isLocationGPSProviderEnabled(EventActivity.this)) {
                        message = String.format(getResources().getString(R.string.current_position_not_detected), getResources().getString(R.string.location_provader_GPS));
                    }
                    builder.setMessage(message);
                    builder.setPositiveButton(R.string.survey_done_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            locationController.goToGPSSettings(EventActivity.this);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, null);
                    builder.show();
                    return;
                }
                /**
                 * Проверяем удовлетворяет ли текущие местоположение минимальному расстоянию для чекина
                 */
                if (event.isCheckInEnable(currentPosition)) {
                    checkIn();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                    String message = String.format(
                            EventActivity.this.getString(R.string.distance_is_too_big),
                            String.valueOf(event.getMaxCheckInDistance()));
                    builder.setMessage(message);
                    builder.setPositiveButton(R.string.survey_done_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    Statistics.enterCheckIn(eventId, false, true);
                    GoogleStatistics.Events.enterCheckIn(eventId, false, true);
                }
            }
        };

        public void build(Event event) {
            if (event == null) {
                return;
            }
            this.event = event;
            if (!isInit) { //на случай если забыли вызвать findViews() ранее
                findViews();
            }
            setScreenTitle();
            setImages();
            setName();
            setAvrRating();
            setAddress();
            setPreviewDetails();
            processCheckIn();
        }

        public void findViews() {
            isInit = true;
        }

        @OnClick(R.id.comment)
        void comnets() {
            Intent intent = EventCommentsListActivity.getStartIntent(EventActivity.this, eventId, event.getType());
            startActivity(intent);
        }

        public void setUIVisible(int visible) {
            screen.setVisibility(visible);
        }

        private void setScreenTitle() {
            String title = getString(R.string.about_event);
            Event.Type type = event.getType();
            if (type == Event.Type.PLACE) {
                title = getString(R.string.about_place);
            }
            TitleHelper.setTitle(EventActivity.this, title);
        }

        private void setImages() {
            List<String> imageLinks = event.getImgLinks();
            List<View> imageViews = new ArrayList<View>(imageLinks.size());
            if (imageLinks != null && imageLinks.size() > 0) {
                for (String url : imageLinks) {
                    View imageView = getImageView(url);
                    imageViews.add(imageView);
                }
                PagerAdapter imagePagerAdapter = new ImagePagerAdapter(imageViews);
                imagesViewPager.setAdapter(imagePagerAdapter);
                imagesViewPager.setCurrentItem(0);
            } else {
                imagesViewPager.setVisibility(View.GONE);
            }
        }

        private View getImageView(String url) {
            View view = View.inflate(EventActivity.this, R.layout.layout_event_images, null);
            final ProgressBar loadingImageProgress = ButterKnife.findById(view, R.id.loadingImageProgress);
            final ImageView imageView = ButterKnife.findById(view, R.id.image);
            ImageLoader imageLoader = AGApplication.getImageLoader();
            imageLoader.loadImage(url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    loadingImageProgress.setVisibility(View.GONE);
                    String errorMessage = String.format(getString(R.string.error_occurs), failReason.getType().toString());
                    Toast.makeText(EventActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        loadingImageProgress.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
            return view;
        }

        private void setPreviewDetails() {
            detailContainer.removeAllViews();
            List<EventDetail> details = event.getDetails();
            if (details != null) {
                for (EventDetail eventDetail : event.getDetails()) {
                    View detailView = View.inflate(EventActivity.this, R.layout.listitem_event_detail, null);
                    setDetailTitle(detailView, eventDetail);
                    setDetailBody(detailView, eventDetail);
                    setDetailMoreEvent(detailView, eventDetail);
                    detailContainer.addView(detailView);
                }
            }
        }

        private void setDetailTitle(View view, EventDetail eventDetail) {
            TextView title = ButterKnife.findById(view, R.id.title);
            title.setText(eventDetail.getTitle());
        }

        private void setDetailBody(View view, final EventDetail eventDetail) {
            final TextView body = ButterKnife.findById(view, R.id.body);
            body.setText(eventDetail.getBody());
            /**
             * Если количество строк в превью детали меньше максимального допустимого,
             * не показываем строку "Читать далее"
             * и убираем многоточие у превью описания детали
             */
            final TextView more = ButterKnife.findById(view, R.id.more);
            ViewTreeObserver vto = body.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver obs = body.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    if (body.getLineCount() <= eventDetail.getMinRowCount()) {
                        more.setVisibility(View.GONE);
                        body.setEllipsize(null);
                    } else {
                        body.setMaxLines(eventDetail.getMinRowCount());
                    }
                }
            });
        }

        private void setDetailMoreEvent(View view, final EventDetail eventDetail) {
            final TextView more = ButterKnife.findById(view, R.id.more);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventDetailActivity.startActivity(EventActivity.this, eventDetail);
                }
            });
        }

        private void setName() {
            name.setText(event.getTitle());
        }

        private void setAddress() {
            String firstAddress = event.getPosition().getName();
            String secondAddress = event.getPosition().getAddress();
            String result = "";
            if (TextUtils.isEmpty(firstAddress) && TextUtils.isEmpty(secondAddress)) {
                address.setVisibility(View.GONE);
                return;
            }
            if (!TextUtils.isEmpty(firstAddress)) {
                result = firstAddress;
            }
            if (!TextUtils.isEmpty(secondAddress)) {
                if (!TextUtils.isEmpty(result)) {
                    result += "\n" + secondAddress;
                } else {
                    result += secondAddress;
                }
            }
            address.setText(result);
        }

        private void processCheckIn() {
            int visibility = View.VISIBLE;
            /**
             * проверка оп датам
             */
            if (event.hasPastDate()) {
                visibility = View.GONE;
            }
            /**
             * проверка по типу фильтра,
             * возможно по датам не надо бует проверять,
             * так как инф о типе фильра добавят в json мероприятия
             */
            if (filter != null && !TextUtils.isEmpty(filter.toString()) && !"null".equalsIgnoreCase(filter.toString())) {
                if (Filter.PAST == filter) {
                    visibility = View.GONE;
                }
            }
            buttonContainer.setVisibility(visibility);

            /**
             * Так как комментарии недоступны, скрываем кнопку
             */
            comment.setVisibility(View.GONE);
            /**
             * Отметиться здесь\Вы уже отметились
             */
            String result;
            if (event.isCheckIn()) {
                result = getString(R.string.share);
                titleCheckIn.setText(result);
                subtitleCheckIn.setVisibility(View.GONE);
                pin.setVisibility(View.GONE);
                checkIn.setOnClickListener(shareListener);
            } else {
                result = getString(R.string.checkin_here);
                /**
                 * + 10 баллов
                 */
                String points = String.format(getString(R.string.add_count),
                        String.valueOf(event.getPoints()),
                        PointsManager.getPointUnitString(EventActivity.this, event.getPoints()));
                titleCheckIn.setText(result);
                subtitleCheckIn.setText(points);
                if (event.getPoints() == 0) {
                    subtitleCheckIn.setVisibility(View.GONE);
                }
                checkIn.setOnClickListener(checkInListener);
            }
        }

        private void setAvrRating() {
            if (event.getAvrRating() > 0) {
                avrRating.setRating((float) event.getAvrRating());
//                avrRating.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Адаптер для картинок
         */
        private class ImagePagerAdapter extends PagerAdapter {
            final List<View> pages;

            public ImagePagerAdapter(List<View> pages) {
                this.pages = pages;
            }

            @Override
            public Object instantiateItem(View collection, int position) {
                View v = pages.get(position);
                ((ViewPager) collection).addView(v, 0);
                return v;
            }

            @Override
            public void destroyItem(View collection, int position, Object view) {
                ((ViewPager) collection).removeView((View) view);
            }

            @Override
            public int getCount() {
                return pages.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view.equals(object);
            }
        }
    }
}