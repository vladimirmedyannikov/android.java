package ru.mos.polls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.ilich.juggler.change.Add;
import me.ilich.juggler.states.State;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.webview.state.WebViewState;

/**
 * Активность для обработки пушей
 */

public class PushProcessActivity extends BaseActivity {
    /**
     * экстра параметр, определяющий какой именно экран следует открыть
     */
    private static final String TYPE = "type";

    /**
     * для пушей AgNew
     */
    private static final String TYPE_AG_NEW = "type_ag_new";
    private static final String INFORMATION_TITLE = "information_title_extra";
    private static final String INFORMATION_URL = "information_url_extra";
    private static final String ONLY_LOAD_FIRST_URL = "only_load_first_url";
    private static final String ID = "id";
    private static final String IS_SHARE_ENABLE = "is_share_enable";

    public static Intent getIntentForAgNew(Context context, String title, String linkUrl, String id) {
        Intent intent = new Intent(context, PushProcessActivity.class);
        intent.putExtra(INFORMATION_TITLE, title);
        intent.putExtra(INFORMATION_URL, linkUrl);
        intent.putExtra(ONLY_LOAD_FIRST_URL, true);
        intent.putExtra(IS_SHARE_ENABLE, true);
        intent.putExtra(ID, id);
        /**
         * экстра параметр, определяющий какой именно экран следует открыть
         */
        intent.putExtra(TYPE, TYPE_AG_NEW);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra(TYPE) != null) {
            State state = null;
            switch (getIntent().getStringExtra(TYPE)) {
                case TYPE_AG_NEW:
                    state = new WebViewState(
                            getIntent().getStringExtra(INFORMATION_TITLE),
                            getIntent().getStringExtra(INFORMATION_URL),
                            getIntent().getStringExtra(ID),
                            getIntent().getBooleanExtra(ONLY_LOAD_FIRST_URL, false),
                            getIntent().getBooleanExtra(IS_SHARE_ENABLE, false));
                    break;
            }
            if (state != null) {
                navigateTo().state(Add.newActivity(state, BaseActivity.class));
            }
        }
        finish();
    }
}
