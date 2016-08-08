package ru.mos.polls;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.widget.CompoundButton;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ru.mos.polls.helpers.FunctionalHelper;

/**
 * Инкапсулирует логику проверки на получение уведовлений<br/>
 * <a href="http://stackoverflow.com/questions/11649151/android-4-1-how-to-check-notifications-are-disabled-for-the-application?answertab=votes#tab-top"></a>
 *
 * @since 1.0
 */
public class NotificationController {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    /**
     * Проверяем и показываем диалог с предложением включить в настроках приложения галочку "Показывать уведомления"<br/>
     * Если отключен показ уведомлений, то пользоваетлю будет предложено включить их на экране настроек приложения
     * {@link FunctionalHelper#showInstalledAppDetailsForAg(Context)}
     *
     * @param context текущий контекст
     */
    public static void offerEnablePushes(final Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isNotificationEnabled(context)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                String message = context.getString(android.os.Build.VERSION.SDK_INT >= 23 ?
                        R.string.notification_message_for_m : R.string.notification_message_for_before_lolipop);
                builder.setMessage(message);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.settings_on, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FunctionalHelper.showInstalledAppDetailsForAg(context);
                    }
                });
                builder.show();
            }
        }
    }

    /**
     * Проверка наличия установленого флага "Показывать уведомления", которая срабатывает по переключению
     * {@link android.support.v7.widget.SwitchCompat}
     *
     * @param context текущий контекст
     * @return callback
     */
    public static CompoundButton.OnCheckedChangeListener checkingEnablePush(final Context context) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    NotificationController.offerEnablePushes(context);
                }
            }
        };
    }

    /**
     * Проверка наличия установленного флага "Показывать уведомления" в настройках приложения<br/>
     * Используется для версий ОС Android от 4.3 (android sdk api 19)
     *
     * @param context текущий контекст
     * @return true - если флаг установлен
     */
    public static boolean isNotificationEnabled(Context context) {
        boolean result = false;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            Class appOpsClass = null;
            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                result = (int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED;
            } catch (Exception ignored) {
            }
        }
        return result;
    }

}
