package ru.mos.polls.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;

/**
 * Адаптер для пунктов настроек
 */
public class SettingsAdapter extends ArrayAdapter<SettingItem> {
    private int resourceId = R.layout.layout_setting;
    private Callback callback;

    public SettingsAdapter(Context context, SettingItem[] objects) {
        super(context, R.layout.layout_setting, objects);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), resourceId, null);
        }
        SettingItem settingItem = getItem(position);
        displayItem(convertView, settingItem);
        setCallback(convertView, settingItem);
        return convertView;
    }

    private void displayItem(View view, SettingItem settingItem) {
        TextView settingView = ButterKnife.findById(view, R.id.setting);
        settingView.setText(settingItem.getTextId());
        settingView.setCompoundDrawablesWithIntrinsicBounds(settingItem.getDrawableId(), 0, 0, 0);
    }

    private void setCallback(View view, final SettingItem settingItem) {
        view.findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onSettingSelected(settingItem.getId());
                }
            }
        });
    }

    public interface Callback {
        void onSettingSelected(int settingId);
    }
}
