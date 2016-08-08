package ru.mos.polls;

import android.os.Bundle;

import ru.mos.polls.fragments.DynamicFragment;


public class ToolbarDynamicActivity extends ToolbarAbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_toolbar);

        String title = getIntent().getStringExtra(DynamicFragment.DEF_TITLE);
        String url = getIntent().getStringExtra(DynamicFragment.BASE_URL);
        String params = getIntent().getStringExtra(DynamicFragment.PARAMS);

        DynamicFragment fr = DynamicFragment.newInstance(title, params, url);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fr)
                .commit();
    }
}
