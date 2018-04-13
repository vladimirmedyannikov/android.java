package ru.mos.polls.support.vm;

import android.app.Activity;
import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import me.ilich.juggler.change.Remove;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.LayoutSupportBinding;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.rxhttp.session.Session;
import ru.mos.polls.support.model.Subject;
import ru.mos.polls.support.service.FeedbackSend;
import ru.mos.polls.support.service.SubjectsLoad;
import ru.mos.polls.support.ui.adapter.SubjectAdapter;
import ru.mos.polls.support.ui.fragment.SupportFragment;
import ru.mos.polls.util.GuiUtils;
import ru.mos.polls.util.NetworkUtils;

public class SupportFragmentVM extends UIComponentFragmentViewModel<SupportFragment, LayoutSupportBinding> {

    private List<Subject> subjects;
    private ArrayAdapter subjectAdapter;
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
        setSubjectsAdapter();
        setEmailView();
        loadSubjects();
    }

    public void setSubjectsAdapter() {
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
    }

    public void setEmailView() {
        String email = getActivity().getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE).getString(AgUser.EMAIL, "");
        if (email.length() > 0) {
            getBinding().etEmail.setText(email);
            getBinding().etMessage.requestFocus();
        }
    }

    private Subject getCurrentSubject() {
        return (Subject) subjectAdapter.getItem(getBinding().subject.getSelectedItemPosition());
    }

    private void loadSubjects() {
        HandlerApiResponseSubscriber<SubjectsLoad.Response.Result> handler
                = new HandlerApiResponseSubscriber<SubjectsLoad.Response.Result>(getFragment().getContext(), getProgressable()) {

            @Override
            protected void onResult(SubjectsLoad.Response.Result result) {
                subjects.addAll(result.getSubjects());
                subjectAdapter.notifyDataSetChanged();
            }
        };
        if (NetworkUtils.hasInternetConnection(getActivity())) {
            Observable<SubjectsLoad.Response> responseObservabl = AGApplication
                    .api
                    .getFeedbackSubjects(new SubjectsLoad.Request())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
            disposables.add(responseObservabl.subscribeWith(handler));
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.internet_failed_to_connect), Toast.LENGTH_SHORT).show();
            getProgressable().end();
        }
    }

    public void sendMessage() {
        getBinding().btnSendMessage.setEnabled(false);
        GuiUtils.hideKeyboard(getFragment().getView());
        HandlerApiResponseSubscriber<String> handler
                = new HandlerApiResponseSubscriber<String>(getFragment().getContext(), getProgressable()) {

            @Override
            protected void onResult(String response) {
                statistics.feedbackSanded(getCurrentSubject().getTitle());
                /**
                 * Очищаем поля при успещной отправке сообщения
                 */
                getBinding().etMessage.setText("");
                getBinding().orderNumber.setText("");
                getBinding().subject.setSelection(0);
                processSendingEnabled();
                GuiUtils.displayOkMessage(getActivity(), R.string.succeeded_support, (dialog, which) -> {
                    if (getFragment().isStartWithNewActivity()) {
                        getFragment().navigateTo().state(Remove.closeCurrentActivity());
                    } else {
                        getFragment().navigateTo().state(Remove.last());
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                statistics.errorOccurs(getCurrentSubject().getTitle(), throwable.getMessage());
                processSendingEnabled();
                GuiUtils.showKeyboard(getBinding().etEmail);
            }

            @Override
            public void onNext(@NonNull GeneralResponse<String> generalResponse) {
                super.onNext(generalResponse);
                if (generalResponse.getErrorCode() != 0) {
                    statistics.errorOccurs(getCurrentSubject().getTitle(), generalResponse.getErrorMessage());
                    processSendingEnabled();
                    GuiUtils.showKeyboard(getBinding().etEmail);
                }
            }
        };

        Observable<FeedbackSend.Response> responseObservabl = AGApplication
                .api
                .sendFeedback(new FeedbackSend.Request(getCurrentSubject().getId(),
                        getBinding().etEmail.getText().toString(),
                        getBinding().etMessage.getText().toString(),
                        getBinding().orderNumber.getText().toString(),
                        Session.get().getSession(), null))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservabl.subscribeWith(handler));
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
