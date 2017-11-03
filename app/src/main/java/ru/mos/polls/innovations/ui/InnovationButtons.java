package ru.mos.polls.innovations.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.innovations.ui.activity.InnovationActivity;

public class InnovationButtons extends RelativeLayout {
    private TextView send;
    private InnovationActivity activity;

    public InnovationButtons(Context context) {
        super(context);
        init();
    }

    public InnovationButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InnovationButtons(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("NewApi")
    public InnovationButtons(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        removeAllViews();
        View v = getView();
        addView(v);
    }

    public void setActivity(InnovationActivity activity) {
        this.activity = activity;
    }

    public View getView() {
        View result = View.inflate(getContext(), R.layout.layout_innovation_buttons, null);
        send = ButterKnife.findById(result, R.id.send);
        setSendButtonDisabled();
        return result;
    }

    public void setSendButtonDisabled() {
        send.setText(R.string.result_innovation);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.rateInnovation();
            }
        });
    }

    public void setSendButtonEnable(boolean current) {
        if (send != null)
            send.setEnabled(current);
    }

    public void setListener(View.OnClickListener listener) {
        send.setOnClickListener(listener);
    }

    public void renderPassedButton() {
        send.setText(R.string.share);
        send.setBackgroundDrawable(getResources().getDrawable(R.drawable.poll_navigation_green_selector));
        send.setTextColor(getResources().getColor(R.color.white));
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.shareSocial();
            }
        });
    }

    public interface CallBack {
        public void rateInnovation();

        public void shareSocial();
    }
}
