package ru.mos.polls.support.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class SubjectAdapter extends ArrayAdapter<Subject> {
    public SubjectAdapter(Context context, List<Subject> objects) {
        super(context, -1, objects);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), android.R.layout.simple_spinner_item, null);
        }
        TextView title = (TextView) convertView.findViewById(android.R.id.text1);
        Subject subject = getItem(position);
        title.setText(subject.getTitle());
        return convertView;
    }
}
