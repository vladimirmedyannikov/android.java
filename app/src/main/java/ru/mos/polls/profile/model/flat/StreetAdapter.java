package ru.mos.polls.profile.model.flat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ru.mos.polls.R;


public class StreetAdapter extends ArrayAdapter<Value> {

    private final LayoutInflater inflater;
    private final int resource;

    public StreetAdapter(Context context, int resource) {
        super(context, resource);
        inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            ViewHolder h = new ViewHolder();
            h.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            h.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            convertView.setTag(h);
        }

        Value value = getItem(position);
        ViewHolder h = (ViewHolder) convertView.getTag();
        h.tvTitle.setText(value.getLabel());
        String territory = value.getTerritory();
        if(territory==null) {
            h.tvDescription.setVisibility(View.GONE);
        } else {
            h.tvDescription.setVisibility(View.VISIBLE);
            h.tvDescription.setText(territory);
        }

        return convertView;
    }

    private class ViewHolder{
        TextView tvTitle;
        TextView tvDescription;
    }
}
