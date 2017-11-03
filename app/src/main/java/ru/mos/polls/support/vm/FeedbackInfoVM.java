package ru.mos.polls.support.vm;

import ru.mos.polls.base.vm.BaseVM;
import ru.mos.polls.databinding.LayoutSupportBinding;
import ru.mos.polls.support.model.FeedbackInfo;

/**
 * Created by matek3022 on 18.09.17.
 */

public class FeedbackInfoVM extends BaseVM<FeedbackInfo, LayoutSupportBinding> {

    public FeedbackInfoVM(FeedbackInfo model, LayoutSupportBinding viewDataBinding) {
        super(model, viewDataBinding);
    }
}
