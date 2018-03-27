package ru.mos.polls.survey.vm;

import java.util.List;

import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentDetailsExpertsBinding;
import ru.mos.polls.survey.adapter.ExpertsAdapter;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.experts.ExpertsApiControllerRX;
import ru.mos.polls.survey.ui.DetailsExpertsFragment;

public class DetailsExpertsFragmentVM extends UIComponentFragmentViewModel<DetailsExpertsFragment, FragmentDetailsExpertsBinding>{

    private long pollId, questionId;
    private boolean isHearing;
    private DetailsExpert detailsExpert;
    private ExpertsAdapter adapter;

    public DetailsExpertsFragmentVM(DetailsExpertsFragment fragment, FragmentDetailsExpertsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().with(new RecyclerUIComponent<>(adapter)).with(new ProgressableUIComponent()).build();
    }

    @Override
    protected void initialize(FragmentDetailsExpertsBinding binding) {
        pollId = getFragment().getExtraPollId();
        questionId = getFragment().getExtraQuestionId();
        isHearing = getFragment().isExtraHearing();
        detailsExpert = getFragment().getExtraDetailsExpert();
        adapter = new ExpertsAdapter();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        loadDetailsExperts();
    }

    private void loadDetailsExperts() {
        ExpertsApiControllerRX.DetailsExpertListener listener = new ExpertsApiControllerRX.DetailsExpertListener() {
            @Override
            public void onLoaded(List<DetailsExpert> detailsExperts) {
                if (detailsExperts != null) {
                    adapter.add(detailsExperts);
                    getComponent(RecyclerUIComponent.class).getRecyclerView().scrollToPosition(getScrollPosition(detailsExperts));
                }
            }

            @Override
            public void onError() {

            }
        };
        ExpertsApiControllerRX.loadDetailExperts(disposables, getFragment().getContext(), pollId, questionId, isHearing, listener, getComponent(ProgressableUIComponent.class));
    }

    private int getScrollPosition(List<DetailsExpert> detailsExperts) {
        int result = 0;
        for (int i = 0; i < detailsExperts.size(); ++i) {
            if (detailsExpert.compare(detailsExperts.get(i))) {
                result = i;
                break;
            }
        }
        return result;
    }
}
