package ru.mos.polls.informer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.BuildConfig;
import ru.mos.polls.R;
import ru.mos.polls.util.GuiUtils;

/**
 * Проверка доступности новой версии приложения {@link #process(BaseActivity)}
 * @since 2.3.0
 */

public class InformerUIController {

    public interface Compare {
        int EQUALS = 0;
        int MORE = -1;
        int LESS = 1;
    }
    /**
     * Проверка доступности новой версии приложения</br>
     * Если пользователю ранее показвалось окно о доступности новой верии для текущуй версии {@link BuildConfig#VERSION_NAME},
     * то проверка доступности новой версии выполняться не будет {@link Manager}
     * @param elkActivity {@link BaseActivity}
     */
    public static void process(final BaseActivity elkActivity) {
        if (!Manager.wasShow(elkActivity)) {
            final InformetApiController.Callback callback = new InformetApiController.Callback() {
                @Override
                public void onGet(String actualAppVersion) {
                    if (actualAppVersion != null
                            && compareVersionNames(BuildConfig.VERSION_NAME, actualAppVersion) == Compare.MORE) {
                        displayNotification(elkActivity, actualAppVersion);
                    }
                }

                @Override
                public void onError() {
                }
            };
            InformetApiController.loadActualAppVersion(elkActivity, callback);
        }
    }

    /**
     * Отображение предложения обновиться
     * @param context {@link Context}
     * @param newVersion наименование версии доступного обновления {@link String}
     */
    public static void displayNotification(final Context context, String newVersion) {
        String message = String.format(context.getString(R.string.informer_message_about_new_version), newVersion);
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Manager.setShow(context);
            }
        };
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                GuiUtils.browseAppInGooglePlayMarket(context);
                Manager.setShow(context);
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.update_app)
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, cancelListener)
                .setPositiveButton(R.string.update, okListener)
                .show();
    }

    /**
     * Сравенение {@link Compare} строк наименование версий
     * @param oldVersionName - текущее наименованиве версии
     * @param newVersionName - новое наименование версии
     * @return {@link Compare#EQUALS} - равны, {@link Compare#MORE} - newVersionName > oldVersionName, {@link Compare#LESS} - newVersionName < oldVersionName
     */
    private static int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = Compare.EQUALS;
        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);
        for (int i = 0; i < maxIndex; i ++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);
            if (oldVersionPart < newVersionPart) {
                res = Compare.MORE;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = Compare.LESS;
                break;
            }
        }
        if (res == Compare.EQUALS && oldNumbers.length != newNumbers.length) {
            res =  oldNumbers.length > newNumbers.length ? Compare.LESS : Compare.MORE;
        }
        return res;
    }

    /**
     * Менеджер для хранения информации о том, что уже предлагаось ранее обновиться для текущей версии приложения
     */
    public static class Manager {
        private static final String PREFS = "informet_prefs";
        private static final String WAS_SHOW = "was_show";
        private static final String APP_VERSION = "app_version";

        public static boolean wasShow(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            return prefs.getBoolean(WAS_SHOW, false)
                    && BuildConfig.VERSION_NAME.equalsIgnoreCase(prefs.getString(APP_VERSION, ""));
        }

        public static void setShow(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                    .putBoolean(WAS_SHOW, true)
                    .putString(APP_VERSION, BuildConfig.VERSION_NAME)
                    .apply();
        }
    }
}
