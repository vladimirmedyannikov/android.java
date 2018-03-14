package ru.mos.polls.subscribes.vm;

import android.support.v7.widget.SwitchCompat;
import android.util.SparseArray;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.NotificationController;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentSubscribeBinding;
import ru.mos.polls.subscribes.controller.SubscribesAPIControllerRX;
import ru.mos.polls.subscribes.gui.fragment.SubscribeFragment;
import ru.mos.polls.subscribes.model.Channel;
import ru.mos.polls.subscribes.model.Subscription;

public class SubscribeFragmentVM extends UIComponentFragmentViewModel<SubscribeFragment, FragmentSubscribeBinding> {
    private SwitchCompat pushDecision, emailDecision,
            emailNew, pushNew,
            emailResult, pushResult,
            emailEffected, pushEffected,//нет этих тоглев
            emailEvent, pushEvent,
            emailNews, pushNews,
            emailOss, pushOss;

    private SparseArray<Boolean> changedList = new SparseArray<>();
    boolean[] savedArrayValue;

    public SubscribeFragmentVM(SubscribeFragment fragment, FragmentSubscribeBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentSubscribeBinding binding) {
        emailResult = getBinding().root.findViewById(R.id.emailResult);
        pushResult = getBinding().root.findViewById(R.id.pushResult);

        pushNew = getBinding().root.findViewById(R.id.pushNew);
        emailNew = getBinding().root.findViewById(R.id.emailNew);

        emailDecision = getBinding().root.findViewById(R.id.emailDecision);
        pushDecision = getBinding().root.findViewById(R.id.pushDecision);


        emailNews = getBinding().root.findViewById(R.id.emailNews);
        pushNews = getBinding().root.findViewById(R.id.pushNews);

        emailOss = getBinding().root.findViewById(R.id.emailOss);
        pushOss = getBinding().root.findViewById(R.id.pushOss);

        setPushListener();
        setEmailListener();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getSubscribeState();
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().with(new ProgressableUIComponent()).build();
    }

    public void save() {
        final List<Subscription> subscriptions = getCurrentSubscribe();
        SubscribesAPIControllerRX.saveAllSubscribes(disposables, getFragment().getContext(), subscriptions, null, new SubscribesAPIControllerRX.SaveListener() {
            @Override
            public void onSaved() {
                Toast.makeText(getFragment().getContext(), R.string.subscribe_settings_are_saved, Toast.LENGTH_SHORT).show();
                SubscribesAPIControllerRX.sendStatisticsForProfile(subscriptions);
            }

            @Override
            public void onError() {
            }
        }, getComponent(ProgressableUIComponent.class));
    }

    private void setPushListener() {
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    NotificationController.checkingEnablePush(getFragment().getContext());
                    save();
                }
            }
        };
        pushDecision.setOnCheckedChangeListener(listener);
        pushNews.setOnCheckedChangeListener(listener);
        pushNew.setOnCheckedChangeListener(listener);
        pushResult.setOnCheckedChangeListener(listener);
        pushOss.setOnCheckedChangeListener(listener);
    }

    private void setEmailListener() {
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed())
                    save();
            }
        };
        emailDecision.setOnCheckedChangeListener(listener);
        emailNew.setOnCheckedChangeListener(listener);
        emailResult.setOnCheckedChangeListener(listener);
        emailNews.setOnCheckedChangeListener(listener);
        emailOss.setOnCheckedChangeListener(listener);
    }

    private void getSubscribeState() {
        SubscribesAPIControllerRX.StateListener listener = new SubscribesAPIControllerRX.StateListener() {
            @Override
            public void onSubscriptionsState(List<Subscription> typeChanells) {
                refreshControls(typeChanells);
                createSwitchLists();
            }

            @Override
            public void onError() {
            }
        };

        SubscribesAPIControllerRX.loadAllSubscribes(disposables, getFragment().getContext(), listener, getComponent(ProgressableUIComponent.class));
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

        changedList.put(emailOss.getId(), emailOss.isChecked());
        changedList.put(pushOss.getId(), pushOss.isChecked());

        savedArrayValue = new boolean[changedList.size()];
        for (int i = 0; i < changedList.size(); i++) {
            int key = changedList.keyAt(i);
            savedArrayValue[i] = changedList.get(key);
        }
    }

    private void refreshControls(List<Subscription> typeChannels) {
        for (Subscription subscription : typeChannels) {
            setChannel(Subscription.TYPE_POLL_RESULTS, subscription, emailResult, pushResult, null);
            setChannel(Subscription.TYPE_POLL_DECISIONS, subscription, emailDecision, pushDecision, null);
            setChannel(Subscription.TYPE_AG_NEW, subscription, emailNew, pushNew, null);
            setChannel(Subscription.TYPE_AG_SPECIAL, subscription, emailNews, pushNews, null);
            setChannel(Subscription.TYPE_OSS, subscription, emailOss, pushOss, null);
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

    private List<Subscription> getCurrentSubscribe() {
        List<Subscription> result = new ArrayList<>();
        Subscription subscriptionResult = getSubscribe(Subscription.TYPE_POLL_RESULTS, emailResult, pushResult, null/*smsResult*/);
        Subscription subscriptionDecision = getSubscribe(Subscription.TYPE_POLL_DECISIONS, emailDecision, pushDecision, null/*smsDecision*/);
        Subscription subscriptionNew = getSubscribe(Subscription.TYPE_AG_NEW, emailNew, pushNew, null);
        Subscription subscriptionNews = getSubscribe(Subscription.TYPE_AG_SPECIAL, emailNews, pushNews, null);
        Subscription subscriptionOss = getSubscribe(Subscription.TYPE_OSS, emailOss, pushOss, null);
        result.add(subscriptionResult);
        result.add(subscriptionDecision);
        result.add(subscriptionNew);
        result.add(subscriptionNews);
        result.add(subscriptionOss);
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
}
