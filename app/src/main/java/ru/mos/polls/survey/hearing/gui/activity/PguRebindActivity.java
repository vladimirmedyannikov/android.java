package ru.mos.polls.survey.hearing.gui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.R;

@Deprecated
public class PguRebindActivity extends PguAuthActivity {
    protected View rebind;
    protected TextView pguTitle;

    public static void startActivity(Fragment fragment) {
        Intent intent = new Intent(fragment.getActivity(), PguRebindActivity.class);
        fragment.startActivityForResult(intent, REQUEST_AUTH_RESULT);
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, PguRebindActivity.class);
        activity.startActivityForResult(intent, REQUEST_AUTH_RESULT);
    }

    @Override
    protected void findViews() {
        super.findViews();
        rebind = ButterKnife.findById(this, R.id.rebind);
        pguTitle = ButterKnife.findById(this, R.id.pguTitle);
        pguBindFragment.setVisibility(View.GONE);
        rebind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pguBindFragment.setVisibility(View.VISIBLE);
                rebind.setVisibility(View.GONE);
                pguTitle.setText(R.string.pgu_auth_message);
            }
        });

    }

    @OnClick(R.id.rebind)
    void rebind() {
        pguBindFragment.setVisibility(View.VISIBLE);
        rebind.setVisibility(View.GONE);
        pguTitle.setText(R.string.pgu_auth_message);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_pgu_rebind;
    }
}
