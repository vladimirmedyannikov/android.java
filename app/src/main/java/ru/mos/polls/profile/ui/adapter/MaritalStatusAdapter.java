package ru.mos.polls.profile.ui.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;

public class MaritalStatusAdapter extends ArrayAdapter<AgUser.MaritalStatus> {
    List<AgUser.MaritalStatus> statuses;

    public MaritalStatusAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<AgUser.MaritalStatus> objects, AgUser.Gender gender) {
        super(context, resource, objects);
        this.statuses = objects;
        setGender(gender);
    }


    public void setGender(AgUser.Gender gender) {
        for (AgUser.MaritalStatus status : statuses) {
            status.setGender(gender);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.layout_spinner_view, null);
        }
        displayMaritalStatus(convertView, getItem(position));
        return convertView;
    }

    private void displayMaritalStatus(View v, AgUser.MaritalStatus maritalStatus) {
        TextView title = (TextView) v.findViewById(R.id.spinner_item);
        title.setText(maritalStatus.toString());
    }
}
