package ru.mos.polls.subscribes.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.NotificationController;
import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.event.model.EventRX;
import ru.mos.polls.subscribes.manager.SubscribeManager;
import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;
import ru.mos.polls.survey.Survey;


public class SubscribesUIController {
    private BaseActivity activity;
    /**
     * Контролы для диалогов нотификации для мероприятия и опроса
     */
    private SwitchCompat emailResult, smsResult, pushResult,
            emailDecision, smsDecision, pushDecision,
            emailEffected, smsEffected, pushEffected,
            emailEvent, pushEvent;
    private Button addToCalendar;
    private LinearLayout switchContainer;
    private TextView title;
    private ProgressDialog progressDialog;

    /**
     * Контролы для диалога сбора email
     */
    private EditText email;
    private SwitchCompat pollsDecisions, news, pollsEffected, newPolls;


    public SubscribesUIController(BaseActivity activity) {
        this.activity = activity;
    }

    public void showSubscribeDialogForPoll(final Survey survey) {
        View innerView = getViewForPoll(survey.getId(), survey.getKind().isHearing());
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveSubscribe(survey.getId(), survey.getKind().isHearing());
            }
        };
        showDialog(survey.getId(), survey.getKind().isHearing(), innerView, listener);
    }

    public void showSubscribeDialogForEvent(final Context context, final EventRX event) {
        showProgress(activity.getString(R.string.get_event_subscribe));
        SubscribesAPIControllerRX.StateListener listener = new SubscribesAPIControllerRX.StateListener() {
            @Override
            public void onSubscriptionsState(List<Subscription> typeChanells) {
                View innerView = getViewForEvent(context, typeChanells, event);
                DialogInterface.OnClickListener listener = (dialog, which) -> {
                    if (!event.isEventYetGoing()) {
                        saveEventSubscribe(event.getCommonBody().getId());
                    }
                };
                hideProgress();
                int title = R.string.save;
                if (event.isEventYetGoing()) {
                    title = R.string.close;
                }
                showDialog(event.getCommonBody().getId(), false, innerView, listener, title);
            }

            @Override
            public void onError() {
                hideProgress();
            }
        };
        SubscribesAPIControllerRX.loadEventSubscribes(activity.getDisposables(), activity, event.getCommonBody().getId(), listener);
    }

    public View getViewForEmail(EmailHelpListener emailHelpListener) {
        return getViewForEmail(emailHelpListener, false);
    }

    public View getViewForEmail(EmailHelpListener emailHelpListener, boolean forQuest) {
        View result = View.inflate(activity, R.layout.dialog_email, null);
        findViewsForEmailHelp(result, emailHelpListener, forQuest);
        return result;
    }

    private void findViewsForEmailHelp(View v, final EmailHelpListener emailHelpListener, boolean forQuest) {
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setVisibility(forQuest ? View.GONE : View.VISIBLE);

        email = ButterKnife.findById(v, R.id.email);
        pollsDecisions = ButterKnife.findById(v, R.id.emailPollsDecisions);
        news = ButterKnife.findById(v, R.id.emailNews);
        newPolls = ButterKnife.findById(v, R.id.emailNewPolls);

        Button skip = (Button) v.findViewById(R.id.btnContinue);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailHelpListener != null) {
                    emailHelpListener.onSkip();
                }
            }
        });

        View.OnClickListener saveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(activity.getString(R.string.set_emails_subscribe));
                SubscribesAPIControllerRX.SetEmailListener listener = new SubscribesAPIControllerRX.SetEmailListener() {
                    @Override
                    public void onSaved(QuestMessage message) {
                        hideProgress();
                        if (emailHelpListener != null) {
                            emailHelpListener.onSave(message);
                        }
                    }

                    @Override
                    public void onError() {
                        hideProgress();
                        if (emailHelpListener != null) {
                            emailHelpListener.onError();
                        }
                    }
                };
                SubscribesAPIControllerRX.setEmailsSubscribe(activity.getDisposables(), activity, email.getText().toString(), getCurrentSubscribeForEmail(), listener);
            }
        };

        final Button save = (Button) v.findViewById(R.id.btnSave);
        LinearLayout button = new LinearLayout(v.getContext(), null, R.style.button);
        save.setOnClickListener(saveListener);
        final Button saveForQuest = (Button) v.findViewById(R.id.saveForQuest);

        if (forQuest) {
            View container = v.findViewById(R.id.container);
            container.setVisibility(View.GONE);
            saveForQuest.setVisibility(View.VISIBLE);
            saveForQuest.setOnClickListener(saveListener);
        }


        TextWatcher watcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                save.setEnabled(email.getText().length() > 0);
                saveForQuest.setEnabled(email.getText().length() > 0);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        };
        email.addTextChangedListener(watcher);
        save.setEnabled(email.getText().length() > 0);
    }

    private View getViewForEvent(Context context, List<Subscription> subscriptions, EventRX event) {
        View result = View.inflate(activity, R.layout.layout_event_subscribe, null);
        findViewsForEvent(context, result, event);
        fillViewsForEvent(subscriptions);

        return result;
    }

    private void findViewsForEvent(final Context context, View view, final EventRX event) {
        title = ButterKnife.findById(view, R.id.eventTitle);
        emailEvent = ButterKnife.findById(view, R.id.emailEvent);
        pushEvent = ButterKnife.findById(view, R.id.pushEvent);
        switchContainer = ButterKnife.findById(view, R.id.switchContainer);

        pushEvent.setOnCheckedChangeListener(NotificationController.checkingEnablePush(context));

        addToCalendar = (Button) view.findViewById(R.id.addToCalendar);
        addToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarIntent = new Intent(Intent.ACTION_EDIT);
                calendarIntent.setType("vnd.android.cursor.item/event");
                calendarIntent.putExtra("title", event.getCommonBody().getTitle());
                calendarIntent.putExtra("beginTime", event.getMillsOfStartDate());
                calendarIntent.putExtra("endTime", event.getMillsOfEndDate());

                /**
                 * сейчас же берем первое описание из деталей
                 * т.к. раньше тут писалась пустая строка,
                 * т.к. это поле раньше было пустым для не укороченного события
                 */
                calendarIntent.putExtra("description", event.getDetails() != null ? event.getDetails().size() > 0 ? event.getDetails().get(0).getBody() : "" : "");
                calendarIntent.putExtra("eventLocation", event.getCommonBody().getPosition().getName() + " " + event.getCommonBody().getPosition().getAddress());
                context.startActivity(calendarIntent);
            }
        });

        if (event.isEventYetGoing()) {
            switchContainer.setVisibility(View.GONE);
            title.setTextColor(activity.getResources().getColor(R.color.ag_red));
            title.setText(activity.getString(R.string.event_is_going_yet));
        }
    }

    private void fillViewsForEvent(List<Subscription> subscriptions) {
        refreshControls(subscriptions);
    }

    private void showDialog(final long id, boolean isHearing, View innerView, final DialogInterface.OnClickListener onClickListener) {
        showDialog(id, isHearing, innerView, onClickListener, R.string.save);
    }

    private void showDialog(final long id, final boolean isHearing, View innerView, final DialogInterface.OnClickListener onClickListener, int title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(innerView);
        builder.setPositiveButton(title, onClickListener);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                saveSubscribe(id, isHearing);
            }
        });
        builder.show();
    }

    private void saveSubscribe(long pollId, boolean isHearing) {
        List<Subscription> subscriptions = getCurrentSubscribe();
        SubscribesAPIControllerRX.sendStatisticsForPoll(subscriptions, pollId);
        SubscribeManager.save(activity, pollId, isHearing, subscriptions);
    }

    private void saveEventSubscribe(final long eventId) {
        List<Subscription> subscriptions = new ArrayList<Subscription>(1);
        subscriptions.add(
                getSubscribe(Subscription.TYPE_EVENT_APPROACHING, emailEvent, pushEvent, null));
        long[] eventIds = new long[]{eventId};
        SubscribesAPIControllerRX.saveSubscribesForEvents(activity.getDisposables(), activity, subscriptions, eventIds, null);
    }

    private View getViewForPoll(long pollId, boolean isHearing) {
        View view = View.inflate(activity, R.layout.layout_subscribe_dialog, null);
        findViewsForPoll(view);
        fillViewForPoll(pollId, isHearing);
        initViewsForPoll(view.getContext());
        return view;
    }

    private void findViewsForPoll(View view) {
        emailResult = ButterKnife.findById(view, R.id.emailResult);
        smsResult = ButterKnife.findById(view, R.id.smsResult);
        pushResult = ButterKnife.findById(view, R.id.smsResult);
        emailDecision = ButterKnife.findById(view, R.id.smsResult);
        smsDecision = ButterKnife.findById(view, R.id.smsDecision);
        pushDecision = ButterKnife.findById(view, R.id.pushDecision);
        emailEffected = ButterKnife.findById(view, R.id.emailEffected);
        smsEffected = ButterKnife.findById(view, R.id.smsEffected);
        pushEffected = ButterKnife.findById(view, R.id.pushEffected);
    }

    private void initViewsForPoll(Context context) {
        pushResult.setOnCheckedChangeListener(NotificationController.checkingEnablePush(context));
        pushDecision.setOnCheckedChangeListener(NotificationController.checkingEnablePush(context));
        pushEffected.setOnCheckedChangeListener(NotificationController.checkingEnablePush(context));
    }

    private void fillViewForPoll(long pollId, boolean isHearing) {
        JSONObject subscriptionsJson = SubscribeManager.getSubscribe(activity, pollId, isHearing);
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        if (subscriptionsJson != null) {
            try {
                JSONArray subscriptionArray = subscriptionsJson.optJSONArray("subscriptions");
                if (subscriptionArray != null) {
                    for (int i = 0; i < subscriptionArray.length(); ++i) {
                        JSONObject subscriptionJson = subscriptionArray.getJSONObject(i);
                        Subscription subscription = Subscription.fromJson(subscriptionJson);
                        subscriptions.add(subscription);
                    }
                }
            } catch (JSONException ignored) {
            }
        }
        refreshControls(subscriptions);
    }

    private List<Subscription> getCurrentSubscribe() {
        List<Subscription> result = new ArrayList<Subscription>(3);
        Subscription subscriptionResult = getSubscribe(Subscription.TYPE_POLL_RESULTS, emailResult, pushResult, null /*smsResult*/);
        Subscription subscriptionDecision = getSubscribe(Subscription.TYPE_POLL_DECISIONS, emailDecision, pushDecision, null /*smsDecision*/);
        Subscription subscriptionEffected = getSubscribe(Subscription.TYPE_POLL_EFFECTED, emailEffected, pushEffected, null/*smsEffected*/);
        result.add(subscriptionResult);
        result.add(subscriptionDecision);
        result.add(subscriptionEffected);
        return result;
    }

    /**
     * Два типа подписок decision и effected срабатывают по одному переключателю
     *
     * @return
     */
    private List<Subscription> getCurrentSubscribeForEmail() {
        List<Subscription> result = new ArrayList<Subscription>();
        Subscription subscriptionResult = getSubscribe(Subscription.TYPE_POLL_DECISIONS, pollsDecisions, null, null);
        Subscription subscriptionDecision = getSubscribe(Subscription.TYPE_POLL_EFFECTED, pollsDecisions/*pollsEffected*/, null, null);
        Subscription subscriptionAgNew = getSubscribe(Subscription.TYPE_AG_NEW, newPolls, null, null);
        Subscription subscriptionAgSpecial = getSubscribe(Subscription.TYPE_AG_SPECIAL, news, null, null);
        result.add(subscriptionResult);
        result.add(subscriptionDecision);
        result.add(subscriptionAgNew);
        result.add(subscriptionAgSpecial);
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

    private void refreshControls(List<Subscription> subscriptions) {
        for (Subscription subscription : subscriptions) {
            setChannel(Subscription.TYPE_POLL_RESULTS, subscription, emailResult, pushResult, null/*smsResult*/);
            setChannel(Subscription.TYPE_POLL_DECISIONS, subscription, emailDecision, pushDecision, null /*smsDecision*/);
            setChannel(Subscription.TYPE_POLL_EFFECTED, subscription, emailEffected, pushEffected, null /*smsEffected*/);
            setChannel(Subscription.TYPE_EVENT_APPROACHING, subscription, emailEvent, pushEvent, null /*smsEffected*/);
        }
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

    private void showProgress(String msg) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    private void hideProgress() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception ignored) {
        }
    }

    public interface EmailHelpListener {
        void onSkip();

        void onSave(QuestMessage questMessage);

        void onError();
    }

}
