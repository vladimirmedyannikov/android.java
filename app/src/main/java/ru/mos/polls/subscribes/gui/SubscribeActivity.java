package ru.mos.polls.subscribes.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import ru.mos.polls.NotificationController;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.subscribes.controller.SubscribesAPIController;
import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;


public class SubscribeActivity extends ToolbarAbstractActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SubscribeActivity.class);
        context.startActivity(intent);
    }

    private SwitchCompat pushDecision, emailDecision,
            emailNew, pushNew,
            emailResult, pushResult,
            emailEffected, pushEffected,//нет этих тоглев
            emailEvent, pushEvent,
            emailNews, pushNews;
    @BindView(R.id.subscribeProgress)
    ProgressBar subscribeProgress;
    private Button save;

    @BindView(R.id.subscribeContainer)
    ScrollView subscribeContainer;

    private SubscribesAPIController subscribesController = new SubscribesAPIController();

    private SparseArray<Boolean> changedList = new SparseArray<>();
    boolean[] savedArrayValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        ButterKnife.bind(this);
        findViews();
        getSubscribeState();
    }

    public void save() {
        final List<Subscription> subscriptions = getCurrentSubscribe();
        subscribesController.saveSubscribesForPolls(this, subscriptions, null, new SubscribesAPIController.SaveListener() {
            @Override
            public void onSaved(JSONObject jsonObject) {
                Toast.makeText(SubscribeActivity.this, R.string.subscribe_settings_are_saved, Toast.LENGTH_SHORT).show();
                SubscribesAPIController.sendStatisticsForProfile(subscriptions);
            }

            @Override
            public void onError(VolleyError volleyError) {
            }
        });
    }

    protected void findViews() {

        emailResult = ButterKnife.findById(this, R.id.emailResult);
        pushResult = ButterKnife.findById(this, R.id.pushResult);

        pushNew = ButterKnife.findById(this, R.id.pushNew);
        emailNew = ButterKnife.findById(this, R.id.emailNew);

        emailDecision = ButterKnife.findById(this, R.id.emailDecision);
        pushDecision = ButterKnife.findById(this, R.id.pushDecision);


        emailNews = ButterKnife.findById(this, R.id.emailNews);
        pushNews = ButterKnife.findById(this, R.id.pushNews);
        save = ButterKnife.findById(this, R.id.save);
    }

    @OnCheckedChanged({R.id.pushDecision, R.id.pushNews, R.id.pushNew, R.id.pushResult})
    void setPushListener(CompoundButton buttonView, boolean isChecked) {
        NotificationController.checkingEnablePush(getBaseContext());
        checkSaveButton(buttonView, isChecked);
    }

    @OnCheckedChanged({R.id.emailDecision, R.id.emailNew, R.id.emailResult, R.id.emailNews})
    void setEmailListener(CompoundButton buttonView, boolean isChecked) {
        checkSaveButton(buttonView, isChecked);
    }

    @OnClick(R.id.save)
    void saveSubscribe() {
        save();
        save.setEnabled(false);
        createSwitchLists();
    }

    private void checkSaveButton(CompoundButton buttonView, boolean isChecked) {
        changedList.put(buttonView.getId(), isChecked);
        boolean[] changed = new boolean[changedList.size()];
        for (int i = 0; i < changedList.size(); i++) {
            int key = changedList.keyAt(i);
            changed[i] = changedList.get(key);
        }
        save.setEnabled(!Arrays.equals(savedArrayValue, changed));
    }

    private void getSubscribeState() {
        setSubscribeProgressVisibility(true);
        SubscribesAPIController.StateListener stateListener = new SubscribesAPIController.StateListener() {
            @Override
            public void onSubscriptionsState(List<Subscription> typeChanells) {
                refreshControls(typeChanells);
                setSubscribeProgressVisibility(false);
                createSwitchLists();
            }

            @Override
            public void onError() {
                setSubscribeProgressVisibility(false);
            }
        };
        subscribesController.loadAllSubscribes(this, stateListener);
    }

    private void createSwitchLists() {
        changedList.put(pushDecision.getId(), pushDecision.isChecked());
        changedList.put(emailDecision.getId(), emailDecision.isChecked());

        changedList.put(emailNew.getId(), emailNew.isChecked());
        changedList.put(pushNew.getId(), pushNew.isChecked());

        changedList.put(emailResult.getId(), emailResult.isChecked());
        changedList.put(pushResult.getId(), pushResult.isChecked());

        changedList.put(emailNews.getId(), emailNews.isChecked());
        changedList.put(pushNews.getId(), pushNews.isChecked());

        savedArrayValue = new boolean[changedList.size()];
        for (int i = 0; i < changedList.size(); i++) {
            int key = changedList.keyAt(i);
            savedArrayValue[i] = changedList.get(key);
        }
    }


    private void setSubscribeProgressVisibility(boolean visibility) {
        subscribeProgress.setVisibility(visibility ? View.VISIBLE : View.GONE);
        subscribeContainer.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    private void refreshControls(List<Subscription> typeChannels) {
        for (Subscription subscription : typeChannels) {
            setChannel(Subscription.TYPE_POLL_RESULTS, subscription, emailResult, pushResult, null);
            setChannel(Subscription.TYPE_POLL_DECISIONS, subscription, emailDecision, pushDecision, null);
            setChannel(Subscription.TYPE_AG_NEW, subscription, emailNew, pushNew, null);
            setChannel(Subscription.TYPE_AG_SPECIAL, subscription, emailNews, pushNews, null);
        }
        save.setEnabled(false);
    }

    private void setChannel(String subscribeType, Subscription subscription, SwitchCompat email, SwitchCompat push, SwitchCompat sms) {
        if (subscribeType.equalsIgnoreCase(subscription.getType())) {
            for (Channel channel : subscription.getChannels()) {
                if (Channel.CHANNEL_EMAIL.equalsIgnoreCase(channel.getName())) {
                    if (email != null) {
                        email.setChecked(channel.isEnabled());
                    }
                } else if (Channel.CHANNEL_PUSH.equalsIgnoreCase(channel.getName())) {
                    if (push != null) {
                        push.setChecked(channel.isEnabled());
                    }
                } else if (Channel.CHANNEL_SMS.equalsIgnoreCase(channel.getName())) {
                    if (sms != null) {
                        sms.setChecked(channel.isEnabled());
                    }
                }
            }
        }
    }

    private List<Subscription> getCurrentSubscribe() {
        List<Subscription> result = new ArrayList<Subscription>(3);
        Subscription subscriptionResult = getSubscribe(Subscription.TYPE_POLL_RESULTS, emailResult, pushResult, null/*smsResult*/);
        Subscription subscriptionDecision = getSubscribe(Subscription.TYPE_POLL_DECISIONS, emailDecision, pushDecision, null/*smsDecision*/);
        Subscription subscriptionNew = getSubscribe(Subscription.TYPE_AG_NEW, emailNew, pushNew, null);
        Subscription subscriptionNews = getSubscribe(Subscription.TYPE_AG_SPECIAL, emailNews, pushNews, null);
        result.add(subscriptionResult);
        result.add(subscriptionDecision);
        result.add(subscriptionNew);
        result.add(subscriptionNews);
        return result;
    }

    private Subscription getSubscribe(String subscribeType, SwitchCompat email, SwitchCompat push, SwitchCompat sms) {
        Subscription result = new Subscription(subscribeType);
        List<Channel> channels = new ArrayList<Channel>(3);
        Channel channel;
        if (email != null) {
            channel = getChannel(Channel.CHANNEL_EMAIL, email);
            channels.add(channel);
        }
        if (push != null) {
            channel = getChannel(Channel.CHANNEL_PUSH, push);
            channels.add(channel);
        }
        if (sms != null) {
            channel = getChannel(Channel.CHANNEL_SMS, sms);
            channels.add(channel);
        }
        result.setChannels(channels);
        return result;
    }

    private Channel getChannel(String channelType, SwitchCompat subscribe) {
        Channel result = new Channel(channelType);
        result.setEnabled(subscribe.isChecked());
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
