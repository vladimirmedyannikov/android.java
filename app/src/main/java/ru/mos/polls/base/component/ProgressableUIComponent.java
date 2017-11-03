package ru.mos.polls.base.component;

import android.graphics.PorterDuff;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;

public class ProgressableUIComponent extends UIComponent implements Progressable {
    @BindView(R.id.progress)
    protected View progressView;
    @BindView(R.id.root)
    protected View rootView;
    private boolean sendEvents = true;

    @Override
    public void onViewCreated(View layout) {
        super.onViewCreated(layout);
        ButterKnife.bind(this, layout);
    }

    @Override
    public void begin() {
        if (sendEvents) AGApplication.bus().send(new Events.ProgressableEvents(Events.ProgressableEvents.BEGIN));
        if (progressView instanceof ProgressBar) {
            ((ProgressBar) progressView).getIndeterminateDrawable()
                    .setColorFilter(getProgressColor(), PorterDuff.Mode.SRC_IN);
        }
        progressView.setVisibility(View.VISIBLE);
        rootView.setVisibility(View.GONE);
    }

    @Override
    public void end() {
        if (sendEvents) AGApplication.bus().send(new Events.ProgressableEvents(Events.ProgressableEvents.END));
        progressView.setVisibility(View.GONE);
        rootView.setVisibility(View.VISIBLE);
        Animation animation = getAnimationForContent();
        rootView.startAnimation(animation);
    }

    public void setSendEvents(boolean b) {
        sendEvents = b;
    }

    protected Animation getAnimationForContent() {
        return AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
    }

    protected int getProgressColor() {
        int result = 0;
        try {
            result = getContext().getResources().getColor(R.color.green_light);
        } catch (Exception ignored) {
        }
        return result;
    }
}
