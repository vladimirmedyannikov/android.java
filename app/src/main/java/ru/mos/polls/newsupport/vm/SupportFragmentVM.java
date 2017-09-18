package ru.mos.polls.newsupport.vm;

import android.app.Activity;
import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.List;

import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.LayoutSupportBinding;
import ru.mos.polls.newprofile.service.model.EmptyResult;
import ru.mos.polls.newsupport.ui.adapter.SubjectAdapter;
import ru.mos.polls.newsupport.ui.fragment.SupportFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.support.Subject;
import ru.mos.polls.rxhttp.rxapi.model.support.service.FeedbackSend;
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
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new ProgressableUIComponent())
                .build();
    }

    @Override
    protected void initialize(LayoutSupportBinding binding) {
        subjects = Subject.getDefault();
        subjectAdapter = new SubjectAdapter(getActivity(), subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statistics = new GoogleStatistics.Feedback();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getBinding().subject.setAdapter(subjectAdapter);
        getBinding().subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Subject subject = subjects.get(position);
                if (subject != null) {
                    if (subject.getTitle().equals(Subject.WORD_SHOP_BONUS)) {
                        getBinding().orderNumberLayout.setVisibility(View.VISIBLE);
                    } else if (getBinding().orderNumberLayout.getVisibility() == View.VISIBLE) {
                        getBinding().orderNumberLayout.setVisibility(View.GONE);
                        getBinding().orderNumber.setText("");
                    }
                }
                if (position == 0) {
                    getBinding().btnSendMessage.setEnabled(false);
                }
                if (position != 0 && getBinding().etMessage.getText().length() > 0) {
                    getBinding().btnSendMessage.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String email = getActivity().getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE).getString(AgUser.EMAIL, "");
        if (email.length() > 0) {
            getBinding().etEmail.setText(email);
            getBinding().etMessage.requestFocus();
        }
        loadSubjects();
    }

    private Subject getCurrentSubject() {
        return (Subject) subjectAdapter.getItem(getBinding().subject.getSelectedItemPosition());
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

    public void sendMessage() {
        getBinding().btnSendMessage.setEnabled(false);
        HandlerApiResponseSubscriber<EmptyResult[]> handler
                = new HandlerApiResponseSubscriber<EmptyResult[]>(getFragment().getContext(), progressable) {

            @Override
            protected void onResult(EmptyResult[] result) {
                statistics.feedbackSanded(currentSubject.getTitle());
                /**
                 * Очищаем поля при успещной отправке сообщения
                 */
                getBinding().etMessage.setText("");
                getBinding().orderNumber.setText("");
                getBinding().subject.setSelection(0);
                processSendingEnabled();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                statistics.errorOccurs(currentSubject.getTitle(), throwable.getMessage());
                processSendingEnabled();
            }
        };

        AGApplication
                .api
                .sendFeedback(new FeedbackSend.Request(getCurrentSubject().getId(),
                        getBinding().etEmail.getText().toString(),
                        getBinding().etMessage.getText().toString(),
                        getBinding().orderNumber.getText().toString(),
                        Session.getSession(getActivity())))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    @BindingAdapter("app:onClick")
    public static void bindOnClick(View view, final Runnable runnable) {
        view.setOnClickListener(v -> runnable.run());
    }

    @BindingAdapter("app:onTextChange")
    public static void bindOnTextChange(EditText et, final Runnable runnable) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runnable.run();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void processSendingEnabled() {
        boolean val = TextUtils.isGraphic(getBinding().etEmail.getText()) && TextUtils.isGraphic(getBinding().etMessage.getText());
        boolean isSubjectEmpty = getCurrentSubject() == null || !getCurrentSubject().isEmpty();
        getBinding().btnSendMessage.setEnabled(val && isSubjectEmpty);
    }
}
