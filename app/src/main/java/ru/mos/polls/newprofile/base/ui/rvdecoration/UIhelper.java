package ru.mos.polls.newprofile.base.ui.rvdecoration;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.*;

import ru.mos.polls.R;

/**
 * Created by Trunks on 11.08.2017.
 */

public class UIhelper {

    public static void setRecyclerList(RecyclerView recyclerView, Context context) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setNestedScrollingEnabled(false);
        Drawable dividerDrawable = ContextCompat.getDrawable(context, R.drawable.divider);
        android.support.v7.widget.DividerItemDecoration did = new android.support.v7.widget.DividerItemDecoration(context, android.support.v7.widget.DividerItemDecoration.VERTICAL);
        did.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(did);
    }
}
