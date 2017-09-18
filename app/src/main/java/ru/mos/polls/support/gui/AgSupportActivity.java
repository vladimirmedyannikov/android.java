package ru.mos.polls.support.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.gui.JugglerActivity;
import me.ilich.juggler.states.VoidParams;
import ru.mos.polls.newsupport.state.SupportState;


public class AgSupportActivity extends JugglerActivity {
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AgSupportActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_support);
//        final FragmentManager fragmentManager = getSupportFragmentManager();
//        if (savedInstanceState == null) {
//            fragmentManager
//                    .beginTransaction()
//                    .add(R.id.container, SupportFragment.newInstance())
//                    .commit();
//        }
        navigateTo().state(Add.newActivity(new SupportState(VoidParams.instance()), AgSupportActivity.class));
    }
}
