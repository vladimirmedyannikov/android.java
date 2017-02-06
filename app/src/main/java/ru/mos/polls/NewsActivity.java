package ru.mos.polls;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ru.mos.elk.api.API;
import ru.mos.polls.fragments.DynamicFragment;
import ru.mos.polls.fragments.NewsDynamicFragment;


public class NewsActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        DynamicFragment fr = NewsDynamicFragment.newInstance(getString(R.string.title_results), "", API.getURL(UrlManager.url(UrlManager.Controller.NEWS, UrlManager.Methods.GET)));

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fr, "news")
                .commit();
        Statistics.enterNews();
        GoogleStatistics.AGNavigation.enterNews();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Statistics.enterNews();
        GoogleStatistics.AGNavigation.enterNews();
    }

    public void onGoToPolls(View view) {
        Intent intent = new Intent(this, AgAuthActivity.class);
        intent.putExtra(AgAuthActivity.PASSED_ACTIVITY, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DynamicFragment df = (DynamicFragment) getSupportFragmentManager().findFragmentByTag("news");
        if (df == null || !df.canGoBack())
            super.onBackPressed();
    }

}
