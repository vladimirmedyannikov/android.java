package ru.mos.polls.fragments;

import android.os.Bundle;

@Deprecated
public class AgDynamicFragment extends DynamicFragment {

    public static DynamicFragment newInstance(String defTitle, String params, String baseUrl) {
        DynamicFragment fragment = new AgDynamicFragment();
        Bundle args = new Bundle();
        args.putString(DEF_TITLE, defTitle);
        args.putString(PARAMS, params);
        args.putString(BASE_URL, baseUrl);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

}
