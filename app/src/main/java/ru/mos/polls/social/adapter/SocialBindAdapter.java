package ru.mos.polls.social.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ru.mos.polls.R;
import ru.mos.polls.social.model.AppBindItem;
import ru.mos.polls.social.model.AppSocial;

public class SocialBindAdapter extends ArrayAdapter<AppSocial> {

    private Listener listener;
    AppSocial social;

    public SocialBindAdapter(Context context, List<AppSocial> objects) {
        super(context, -1, objects);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        SocialHolder holder;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.item_bind_social, null);
            holder = new SocialHolder(v);
            v.setTag(holder);
        } else {
            holder = (SocialHolder) v.getTag();
        }
        social = getItem(position);
        displayTitle(holder, social);
        displayIcon(holder, social);
        return v;
    }

    private void displayTitle(SocialHolder v, AppSocial social) {
        int resTitle = AppBindItem.getTitle(social.getId());
        v.title.setText(resTitle);
    }

    private void displayIcon(SocialHolder v, final AppSocial social) {
        int drawableId = AppBindItem.getBindResId(social.getId());
        int visibility = View.VISIBLE;
        boolean enable = false;
        if (!social.isLogon()) {
            drawableId = AppBindItem.getUnBindResId(social.getId());
            visibility = View.GONE;
            enable = true;
        }
        v.icon.setEnabled(enable);
        v.icon.setImageResource(drawableId);
        v.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!social.isLogon() && listener != null) {
                    listener.onBindClick(social);
                }
            }
        });
        v.iconClose.setVisibility(visibility);
        v.iconClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCloseClick(social);
                }
            }
        });
    }

    static class SocialHolder {
        @BindView(R.id.socialTitle)
        AppCompatTextView title;
        @BindView(R.id.socialIcon)
        CircleImageView icon;
        @BindView(R.id.socialIconClose)
        AppCompatImageView iconClose;

        SocialHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    public interface Listener {
        void onBindClick(AppSocial social);

        void onCloseClick(AppSocial social);
    }
}
