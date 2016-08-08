package ru.mos.elk.auth;

import android.os.Bundle;

import ru.mos.elk.R;
import ru.mos.elk.Statistics;

public class RestoreActivity extends RegisterActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setMessages(R.string.elk_wait_restore, R.string.elk_succeeded_restore);

        getPhoneEdit().setText(getIntent().getCharSequenceExtra("phone"));
        Statistics.getPassword();
    }

    protected int getLayout() {
        return R.layout.activity_restore;
    }
}
