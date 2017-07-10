package ru.mos.polls.newprofile.base.ui.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.mos.polls.R;

/**
 * Created by Trunks on 10.07.2017.
 */

public class YearDialogFragment extends DialogFragment {
    private RecyclerView mRecyclerView;
    private YearAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_kids_year, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.root);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        List<Integer> yearList = new ArrayList<>();
        for (int i = year; year - 100 < i; i--) {
            yearList.add(i);
        }
        adapter = new YearAdapter(yearList);
        mRecyclerView.setAdapter(adapter);
        return v;
    }

    public static class YearAdapter extends RecyclerView.Adapter<YearVH> {
        private List<Integer> yearList;

        public YearAdapter(List<Integer> yearList) {
            this.yearList = yearList;
        }

        @Override
        public YearVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.select_dialog_item, parent, false);
            return new YearVH(itemView);
        }

        @Override
        public void onBindViewHolder(YearVH holder, int position) {
            int year = yearList.get(position);
            holder.year.setText(String.valueOf(year));
        }

        @Override
        public int getItemCount() {
            return yearList.size();
        }
    }


    public static class YearVH extends RecyclerView.ViewHolder {
        TextView year;

        public YearVH(View itemView) {
            super(itemView);
            year = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
