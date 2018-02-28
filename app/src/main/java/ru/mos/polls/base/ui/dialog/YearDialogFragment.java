package ru.mos.polls.base.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.mos.polls.R;


public class YearDialogFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private YearAdapter adapter;
    private OnItemClick listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton("Закрыть", (dialog1, which) -> dialog1.dismiss());
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog_kids_year, null, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.root);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        List<Integer> yearList = new ArrayList<>();
        for (int i = year; year - 100 < i; i--) {
            yearList.add(i);
        }
        adapter = new YearAdapter(yearList, listener);
        recyclerView.setAdapter(adapter);
        dialog.setView(v);
        return dialog.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        int dialogWidth = WindowManager.LayoutParams.MATCH_PARENT;
        int dialogHeight = getResources().getDisplayMetrics().heightPixels / 2;
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        getDialog().getWindow().setGravity(Gravity.CENTER);
    }

    public void setListener(OnItemClick listener) {
        this.listener = listener;
    }

    public static class YearAdapter extends RecyclerView.Adapter<YearVH> {
        private List<Integer> yearList;
        OnItemClick listener;

        public YearAdapter(List<Integer> yearList) {
            this.yearList = yearList;
        }

        public YearAdapter(List<Integer> yearList, OnItemClick listener) {
            this.yearList = yearList;
            this.listener = listener;
        }

        @Override
        public YearVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_dialog_single, parent, false);
            return new YearVH(itemView);
        }

        @Override
        public void onBindViewHolder(YearVH holder, int position) {
            int year = yearList.get(position);
            holder.year.setText(String.valueOf(year));
            holder.year.setOnClickListener((v) -> listener.onYearClick(year));
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
            year = (TextView) itemView.findViewById(R.id.text);
        }
    }

    public interface OnItemClick {
        void onYearClick(int year);
    }
}
