package ru.mos.polls.newprofile;

import android.os.Bundle;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.R;

/**
 * Created by Trunks on 06.06.2017.
 */

public class MainFragment extends JugglerFragment {

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, this, "main")
                .commit();
    }
}
