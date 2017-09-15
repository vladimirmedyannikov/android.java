package ru.mos.polls.newsupport.vm;

import android.widget.ArrayAdapter;

import java.util.List;

import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.LayoutSupportBinding;
import ru.mos.polls.newsupport.ui.adapter.SubjectAdapter;
import ru.mos.polls.newsupport.ui.fragment.SupportFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.support.Subject;
import ru.mos.polls.rxhttp.rxapi.model.support.service.SubjectsLoad;

/**
 * Created by matek3022 on 13.09.17.
 */

public class SupportFragmentVM extends UIComponentFragmentViewModel<SupportFragment, LayoutSupportBinding> {

    private List<Subject> subjects;
    private ArrayAdapter subjectAdapter;
    private Subject currentSubject;
    private Unbinder unbinder;
    private GoogleStatistics.Feedback statistics;

    public SupportFragmentVM(SupportFragment fragment, LayoutSupportBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutSupportBinding binding) {
        subjects = Subject.getDefault();
        subjectAdapter = new SubjectAdapter(getActivity(), subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statistics = new GoogleStatistics.Feedback();
        binding.subject.setAdapter(subjectAdapter);

    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        loadSubjects();
    }

    private void loadSubjects() {
        HandlerApiResponseSubscriber<SubjectsLoad.Response.Result> handler
                = new HandlerApiResponseSubscriber<SubjectsLoad.Response.Result>(getFragment().getContext(), progressable) {

            @Override
            protected void onResult(SubjectsLoad.Response.Result result) {
                subjects.addAll(result.getSubjects());
                subjectAdapter.notifyDataSetChanged();
            }
        };

        AGApplication
                .api
                .getFeedbackSubjects(new SubjectsLoad.Request())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new ProgressableUIComponent())
                .build();
    }
}
