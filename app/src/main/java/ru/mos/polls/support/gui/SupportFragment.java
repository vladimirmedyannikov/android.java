package ru.mos.polls.support.gui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley2.VolleyError;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.support.controller.AgSupportApiController;
import ru.mos.polls.support.model.FeedbackBody;
import ru.mos.polls.support.model.Subject;
import ru.mos.polls.support.model.SubjectAdapter;


public class SupportFragment extends Fragment {

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.subject)
    Spinner spinnerSubjects;
    @BindView(R.id.btnSendMessage)
    Button btnSend;
    @BindView(R.id.orderNumber)
    EditText orderNumber;
    @BindView(R.id.orderNumberLayout)
    LinearLayout orderNumberLayout;

    private List<Subject> subjects;
    private ArrayAdapter subjectAdapter;
    private Subject currentSubject;
    private Unbinder unbinder;
    private GoogleStatistics.Feedback statistics;

    public static Fragment newInstance() {
        return new SupportFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleHelper.setTitle(getActivity(), R.string.title_support);
        subjects = Subject.getDefault();
        subjectAdapter = new SubjectAdapter(getActivity(), subjects);
        statistics = new GoogleStatistics.Feedback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_support, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        loadSubjects();
    }

    @OnClick(R.id.btnSendMessage)
    void sendMessage() {
        sendSupportMessage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    /**
     * проверяем поле Номер Заказа на видимость
     */
    private boolean checkVisibility() {
        return orderNumberLayout.getVisibility() == View.VISIBLE;
    }

    private void loadSubjects() {
        AgSupportApiController.SubjectsListener listener = new AgSupportApiController.SubjectsListener() {
            @Override
            public void onLoad(List<Subject> subjects) {
                try {
                    if (subjects != null) {
                        SupportFragment.this.subjects = subjects;
                        subjectAdapter = new SubjectAdapter(getActivity(), SupportFragment.this.subjects);
                        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSubjects.setAdapter(subjectAdapter);
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onError() {
            }
        };
        AgSupportApiController.loadSubjects((BaseActivity) getActivity(), listener);
    }

    private void refreshCurrentSubject() {
        try {
            currentSubject = (Subject) subjectAdapter.getItem(spinnerSubjects.getSelectedItemPosition());
        } catch (Exception ignored) {
        }
    }

    private void sendSupportMessage() {
        FeedbackBody feedbackBody = new FeedbackBody();
        feedbackBody.setEmail(etEmail.getText().toString());
        feedbackBody.setMessage(etMessage.getText().toString());
        if (orderNumber != null && orderNumber.getText().length() > 0) {
            feedbackBody.setOrderNumber(Long.parseLong(orderNumber.getText().toString()));
        }
        refreshCurrentSubject();
        btnSend.setEnabled(false);
        AgSupportApiController.SendListener listener = new AgSupportApiController.SendListener() {
            @Override
            public void onSuccess() {
                statistics.feedbackSanded(getActivity(), currentSubject.getTitle());
                /**
                 * Очищаем поля при успещной отправке сообщения
                 */
                etMessage.setText("");
                spinnerSubjects.setSelection(0);
                processSendingEnabled();
            }

            @Override
            public void onError(VolleyError error) {
                statistics.errorOccurs(getActivity(), currentSubject.getTitle(), error.getMessage());
                processSendingEnabled();
            }
        };
        AgSupportApiController.sendFeedback((BaseActivity) getActivity(), feedbackBody, currentSubject, listener);
    }

    @OnTextChanged(value = {R.id.etEmail, R.id.etMessage}, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void textChange() {
        processSendingEnabled();
    }

    /**
     * ставим листенер на спиннер
     * если "магазин поощрений" то показываем поле если другое, скрываем
     */
    @OnItemSelected(R.id.subject)
    void onSubjectSelected(int position) {
        Subject subject = subjects.get(position);
        if (subject != null) {
            if (subject.getTitle().equals(Subject.WORD_SHOP_BONUS)) {
                orderNumberLayout.setVisibility(View.VISIBLE);
            } else if (checkVisibility()) {
                orderNumberLayout.setVisibility(View.GONE);
            }
        }
        if (position == 0) {
            btnSend.setEnabled(false);
        }
        if (position != 0 && etMessage.getText().length() > 0) {
            btnSend.setEnabled(true);
        }
    }

    private void findViews(View view) {
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(subjectAdapter);
        String email = getActivity().getSharedPreferences(AgUser.PREFS, Activity.MODE_PRIVATE).getString(AgUser.EMAIL, "");
        if (email.length() > 0) {
            etEmail.setText(email);
            etMessage.requestFocus();
        }
    }

    private void processSendingEnabled() {
        boolean val = TextUtils.isGraphic(etEmail.getText()) && TextUtils.isGraphic(etMessage.getText());
        refreshCurrentSubject();
        boolean isSubjectEmpty = currentSubject == null || !currentSubject.isEmpty();
        btnSend.setEnabled(val && isSubjectEmpty);
    }
}
