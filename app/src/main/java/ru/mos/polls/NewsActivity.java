package ru.mos.polls;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import me.ilich.juggler.change.Add;
import ru.mos.polls.auth.state.AgAuthState;
import ru.mos.polls.base.ui.BaseActivity;
import ru.mos.polls.news.ui.NewsFragment;


public class NewsActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

//        DynamicFragment fr = NewsDynamicFragment.newInstance(getString(R.string.title_results), "", API.getURL(UrlManager.url(UrlManager.V250, UrlManager.Controller.NEWS, UrlManager.Methods.GET)));

        Fragment fragment = NewsFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, "news")
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
//        Intent intent = new Intent(this, AgAuthActivity.class);
//        intent.putExtra(AgAuthActivity.PASSED_ACTIVITY, MainActivity.class);
//        startActivity(intent);
        navigateTo().state(Add.newActivity(new AgAuthState(), BaseActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
//        DynamicFragment df = (DynamicFragment) getSupportFragmentManager().findFragmentByTag("news");
//        if (df == null || !df.canGoBack())
            super.onBackPressed();
    }

}
