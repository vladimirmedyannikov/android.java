package ru.mos.polls.fragments;

import android.os.Bundle;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.netframework.adapters.ActionInterceptor;
import ru.mos.elk.netframework.adapters.DynamicManager;
import ru.mos.elk.netframework.model.results.ResultTableLink;
import ru.mos.elk.netframework.model.results.ResultType;
import ru.mos.polls.Statistics;
import ru.mos.polls.WebViewActivity;
import ru.mos.polls.quests.controller.QuestsApiController;

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

        DynamicManager manager = getManager();
        manager.clearCache();
        manager.addActionInterceptor(ResultType.TABLE_LINK, new ActionInterceptor<ResultTableLink>() {
            @Override
            public boolean onAct(ResultTableLink element, int i) {
                Statistics.readNews(i);
                onBeforeActivityStart(element);
                if (element.isNeedHideTask()) {
                    QuestsApiController.hideNews((BaseActivity) getActivity(), element.getId(), null);
                }
                WebViewActivity.startActivity(getActivity(),
                        element.getTitle(),
                        element.getLinkId(),
                        String.valueOf(element.getId()),
                        true,
                        true);
                return true;
            }
        });
    }

    protected void onBeforeActivityStart(ResultTableLink element) {
    }
}
