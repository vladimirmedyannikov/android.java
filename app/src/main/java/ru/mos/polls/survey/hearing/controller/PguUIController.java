package ru.mos.polls.survey.hearing.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.helpers.FunctionalHelper;
import ru.mos.polls.survey.hearing.gui.activity.PguAuthActivity;
import ru.mos.polls.survey.hearing.gui.activity.PguRebindActivity;
import ru.mos.polls.survey.hearing.gui.activity.PguVerifyActivity;


public abstract class PguUIController {
    public static void showHearingSubscribe(final BaseActivity elkActivity, String title, final long hearingId, final long meetingId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(elkActivity);
        builder.setMessage(R.string.subscribe_hearng_text);
        builder.setPositiveButton(elkActivity.getString(R.string.subscribe_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hearingSubscribe(elkActivity, hearingId, meetingId);
            }
        });
        builder.setNegativeButton(elkActivity.getString(R.string.cancel), null);
        builder.show();
    }

    public static void startPguBinding(Fragment fragment) {
        if (AgUser.isPguConnected(fragment.getActivity())) {
            PguRebindActivity.startActivity(fragment);
        } else {
            PguAuthActivity.startActivity(fragment);
        }
    }

    public static void showSimpleDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setMessage(message)
                .setPositiveButton(context.getString(R.string.close), null)
                .show();
    }

    public static void showBindToPGUDialog(final BaseActivity context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton(context.getString(R.string.ag_continue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PguVerifyActivity.startActivity(context);
                    }
                });
        builder.setNegativeButton(context.getString(R.string.cancel), null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showBindToPGUDialogForInvalidFields(final BaseActivity context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.StackedAlertDialogStyle);
        }
        builder.setNeutralButton(context.getString(R.string.go_to_pgu), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FunctionalHelper.startBrowser(context, R.string.pgu_link_value);
            }
        });
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.cancel), null);
        builder.setNegativeButton(R.string.title_nind_other_profile_pgu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PguVerifyActivity.startActivity(context);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                final Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                LinearLayout linearLayout = (LinearLayout) button.getParent();
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            } catch (Exception ignored) {
            }
        }
    }

    public static void hearingSubscribe(final BaseActivity elkActivity, long hearingId, long meetingId) {
        HearingApiController.HearingCheckListener listener = new HearingApiController.HearingCheckListener() {
            @Override
            public void onSuccess(String title, String message) {
                showSimpleDialog(elkActivity, title, message);
            }

            @Override
            public void onPguAuthError(int code, String message) {
                hearingErrorProcess(elkActivity, code, message);
            }

            @Override
            public void onError(String message) {
                showSimpleDialog(elkActivity, null, message);
            }
        };
        HearingApiController.hearingCheck(elkActivity, hearingId, meetingId, listener);
    }

    public static void hearingErrorProcess(final BaseActivity elkActivity, int code, String message) {
        switch (code) {
            case HearingApiController.ERROR_SESSION_EXPIRED:
            case HearingApiController.ERROR_PGU_NOT_ATTACHED:
            case HearingApiController.ERROR_CODE_NO_MASTER_SSO_ID:
            case HearingApiController.ERROR_PGU_SESSION_EXPIRED:
            case HearingApiController.ERROR_PGU_FLAT_NOT_VALID:
                showBindToPGUDialog(elkActivity, message);
                break;
            case HearingApiController.ERROR_FIELDS_ARE_EMPTY:
            case HearingApiController.ERROR_PGU_FLAT_NOT_MATCH:
            case HearingApiController.ERROR_AG_FLAT_NOT_MATCH:
            case HearingApiController.ERROR_PGU_USER_DATA:
                showBindToPGUDialogForInvalidFields(elkActivity, message);
                break;
            default:
                Toast.makeText(elkActivity, message, Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
