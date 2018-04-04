package ru.mos.polls.auth.vm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import ru.mos.polls.AGApplication;
import ru.mos.polls.AgRegisterActivity;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.WebViewActivity;
import ru.mos.polls.auth.state.AgPhoneConfirmState;
import ru.mos.polls.auth.state.AuthState;
import ru.mos.polls.auth.ui.AgAuthFragment;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentAgAuthBinding;
import ru.mos.polls.event.gui.activity.EventActivity;
import ru.mos.polls.innovations.ui.activity.InnovationActivity;
import ru.mos.polls.maskedettext.MaskedEditText;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.service.ProfileGet;
import ru.mos.polls.profile.ui.activity.AchievementActivity;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;
import ru.mos.polls.support.state.SupportState;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.polls.util.GuiUtils;
import ru.mos.polls.util.PermissionsUtils;
import ru.mos.polls.webview.state.WebViewState;

public class AgAuthFragmentVM extends UIComponentFragmentViewModel<AgAuthFragment, FragmentAgAuthBinding> {

    private static final int SMS_PERMISSION_REQUEST = 9825;
    protected Toolbar toolbar;
    public static final String SKIP_ACTIVITY = "ru.mos.polls.auth.SKIP_ACTIVITY";
    public static final String PASSED_ACTIVITY = "ru.mos.polls.auth.PASSED_ACTIVITY";
    public static final String JUST_AUTHORIZE = "ru.mos.polls.auth.JUST_AUTHORIZE";
    private GoogleStatistics.Auth statistics = new GoogleStatistics.Auth();
    protected static final int ERROR_CODE_423 = 423;
    protected static final int REQUEST_REGISTER = 1;
    protected static final int REQUEST_RESTORE = 2;
    CheckBox cbAgreeOffer;

    MaskedEditText codeCountry;
    MaskedEditText etLogin;
    TextView tvError;

    public AgAuthFragmentVM(AgAuthFragment fragment, FragmentAgAuthBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentAgAuthBinding binding) {
        cbAgreeOffer = binding.root.findViewById(R.id.cbAgreeOffer);
        codeCountry = binding.etCodeCountry;
        etLogin = binding.etLogin;
        tvError = binding.tvError;
        binding.registeredInService.setOnClickListener(v -> getFragment().navigateTo(new AuthState(etLogin.getUnmaskedText()), ru.mos.polls.base.activity.BaseActivity.class));
        etLogin.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                doRequestAction();
            }
            return true;
        });
        binding.help.setOnClickListener(view -> {
                    GuiUtils.hideKeyboard(etLogin);
                    statistics.helpClick();
                    new GoogleStatistics.Auth().feedbackClick();
                    getFragment().navigateTo(new SupportState(true), ru.mos.polls.base.activity.BaseActivity.class);
                }
        );
        binding.etLogin.setOnFocusChangeListener((view, b) -> {
            if (b)
                tvError.setVisibility(View.GONE);
        });
        binding.etPassword.setOnFocusChangeListener((view, b) -> {
            if (b)
                tvError.setVisibility(View.GONE);
        });
        binding.btnRegister.setOnClickListener(v -> {
            statistics.registryClick();
            Intent intent = new Intent(getFragment().getContext(), AgRegisterActivity.class);
            getFragment().startActivity(intent);
        });
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().with(new ProgressableUIComponent()).build();
    }


    @Override
    public void onViewCreated() {
        super.onViewCreated();
        configureEdits();
        /**
         * код по умолчанию +7, при этом без возможности редактирования
         */
        codeCountry.setText("7");
        codeCountry.setEnabled(false);
        if (PermissionsUtils.SMS.isGranted(getFragment().getContext())) {
            GuiUtils.showKeyboard(etLogin);
        }
    }

    @Override
    public void onOptionsItemSelected(int menuItemId) {
        switch (menuItemId) {
            case R.id.action_confirm:
                doRequestAction();
                break;
        }
        super.onOptionsItemSelected(menuItemId);
    }

    public void doRequestAction() {
        if (checkCodeText() && checkPhoneText()) {
            onClickLogin(etLogin);
        } else {
            Toast.makeText(getFragment().getContext(), "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
    }

    protected void configureEdits() {
        TextView offer = getBinding().root.findViewById(R.id.tvOffer);
        Spannable text = new SpannableString(getFragment().getString(R.string.ag_agree_with_offer));
        int startPosition = GuiUtils.getStartPosition(getFragment().getString(R.string.ag_agree_with_offer), getFragment().getString(R.string.offerta_offer));
        int endPosition = GuiUtils.getEndPosition(getFragment().getString(R.string.ag_agree_with_offer), getFragment().getString(R.string.offerta_offer));
        text.setSpan(new ForegroundColorSpan(getFragment().getResources().getColor(R.color.green_light)), startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new UnderlineSpan(), startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {
                GuiUtils.hideKeyboard(v);
                statistics.offerClick();
                getFragment().navigateTo(new WebViewState(
                        getFragment().getString(R.string.title_offer),
                        getFragment().getString(R.string.url_offer),
                        null,
                        false,
                        false), BaseActivity.class);
            }
        }, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        offer.setText(text);
        offer.setMovementMethod(LinkMovementMethod.getInstance());
        String phone = AgUser.getPhone(getFragment().getContext());
        if (phone != null && phone.length() > 1) {
            etLogin.setText(phone.substring(1));
        }
        checkPermissions();
    }

    public void onClickLogin(View view) {
        GuiUtils.hideKeyboard(view);
        tvError.setVisibility(View.GONE);
        HandlerApiResponseSubscriber<Object> handler = new HandlerApiResponseSubscriber<Object>(getFragment().getContext(), getComponent(ProgressableUIComponent.class)) {
            @Override
            protected void onResult(Object result) {
                getFragment().navigateToCloseCurrActivity();
                getFragment().navigateTo(new AgPhoneConfirmState(etLogin.getUnmaskedText()), BaseActivity.class);
            }

            @Override
            public void onHasError(GeneralResponse<Object> generalResponse) {
                super.onHasError(generalResponse);
                if (generalResponse != null) {
                    if (generalResponse.getErrorCode() != ERROR_CODE_423)
                        onLoginFault(generalResponse.getErrorMessage());
                    showErrorDialog(generalResponse.getErrorCode());
                }
            }
        };
        ProfileGet.RecoveryRequest recoveryRequest = new ProfileGet.RecoveryRequest();
        recoveryRequest.setMsisdn(codeCountry.getUnmaskedText() + etLogin.getUnmaskedText());
        recoveryRequest.setClientInfo(new ProfileGet.ClientInfoBean("Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")", Build.MODEL + " (" + Build.MANUFACTURER + ")"));
        AGApplication.api
                .recoveryPassword(recoveryRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler);
    }

    public boolean checkCodeText() {
        return codeCountry.getUnmaskedText().length() > 0;
    }

    public boolean checkPhoneText() {
        return etLogin.getUnmaskedText().length() == 10;
    }

    private void checkPermissions() {
        if (!PermissionsUtils.SMS.isGranted(getFragment().getContext())) {
            PermissionsUtils.SMS.request(getFragment(), SMS_PERMISSION_REQUEST);
        }
    }

    @AfterPermissionGranted(SMS_PERMISSION_REQUEST)
    public void onRequestPermissionsResult() {
        etLogin.post(new Runnable() {
            @Override
            public void run() {
                etLogin.requestFocus();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        GuiUtils.hideKeyboard(etLogin);
    }

    protected void onLoginFault(String errorMessage) {
        ScrollView sv = getBinding().root;
        sv.pageScroll(View.FOCUS_DOWN);
        tvError.setText(errorMessage);
        tvError.setVisibility(View.VISIBLE);
        tvError.requestFocus();
        Statistics.auth(etLogin.getUnmaskedText(), false);
        statistics.check(false);
        statistics.errorOccurs(errorMessage);
    }

    protected void showErrorDialog(int error) {
        switch (error) {
            case ERROR_CODE_423:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getFragment().getContext());
                dialog.setMessage(R.string.auth_error_code_msg_423)
                        .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton(R.string.restore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                break;
        }
    }

    /**
     * Запуск после логина по uri
     *
     * @param intent
     */
    private void startFromUri(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            String host = uri.getHost();
            Intent startIntent = new Intent(getFragment().getContext(), MainActivity.class);
            if ("events".equalsIgnoreCase(host)
                    || "pollTasks".equalsIgnoreCase(host)
                    || "news".equalsIgnoreCase(host)) {
                startIntent = new Intent(getFragment().getContext(), MainActivity.class);
            } else if ("event".equalsIgnoreCase(host)) {
                startIntent = new Intent(getFragment().getContext(), EventActivity.class);
            } else if ("new".equalsIgnoreCase(host) || "advertisement".equalsIgnoreCase(host)) {
                startIntent = new Intent(getFragment().getContext(), WebViewActivity.class); // TODO: 21.03.18  метод не используется, мб удалить?
            } else if ("poll".equalsIgnoreCase(host)) {
                startIntent = new Intent(getFragment().getContext(), SurveyActivity.class);
            } else if ("novelty".equalsIgnoreCase(host)) {
                startIntent = new Intent(getFragment().getContext(), InnovationActivity.class);
            } else if ("achievement".equalsIgnoreCase(host)) {
                startIntent = new Intent(getFragment().getContext(), AchievementActivity.class);
            }
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.setData(uri);
            getFragment().startActivity(startIntent);
            getFragment().navigateToCloseCurrActivity();
        }
    }

    /**
     * Перехват клика по ссылке html
     */
    public static class OfferLinkMovementMethod extends LinkMovementMethod {

        private static OfferLinkMovementMethod linkMovementMethod = new OfferLinkMovementMethod();

        private OfferLinkMovementMethod.LinkListener linkListener;

        public void setLinkListener(OfferLinkMovementMethod.LinkListener linkListener) {
            this.linkListener = linkListener;
        }

        public boolean onTouchEvent(android.widget.TextView widget, android.text.Spannable buffer, android.view.MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
                if (link.length != 0) {
                    String url = link[0].getURL();
                    if (url.startsWith("http")) {
                        if (linkListener != null) {
                            linkListener.onClicked();
                        }
                    }
                }
            }

            return super.onTouchEvent(widget, buffer, event);
        }

        public static OfferLinkMovementMethod getInstance() {
            return linkMovementMethod;
        }

        public interface LinkListener {
            void onClicked();
        }
    }
}
