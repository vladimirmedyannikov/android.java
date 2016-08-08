package ru.mos.polls.popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import ru.mos.polls.AgRestoreActivity;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.helpers.FunctionalHelper;
import ru.mos.polls.instruction.InstructionActivity;
import ru.mos.polls.support.gui.AgSupportActivity;

/**
 * Класс для создания всплывающео меню внизу экрана
 */
public abstract class PopupController {

    /**
     * Всплывающее меню внизу экрана для эакрана привязки аккаунта аг к аккаунту пгу
     *
     * @param context
     * @since 1.9
     */
    public static void pgu(final Context context) {
        PopupController.PopupItemListener listener = new PopupController.PopupItemListener() {
            @Override
            public void onPopupItemSelected(PopupWindow popupWindow, int popupItemId) {
                switch (popupItemId) {
                    case PopupItem.AuthPgu.QUESTIONS_AND_ANSWERS:
                        popupWindow.dismiss();
                        FunctionalHelper.startBrowser(context, R.string.pgu_link_questions_and_answers);
                        break;
                    case PopupItem.AuthPgu.GO_TO_PGU_MOS_RU:
                        popupWindow.dismiss();
                        FunctionalHelper.startBrowser(context, R.string.pgu_link_value);
                        break;
                    case PopupItem.Auth.FEEDBACK:
                        popupWindow.dismiss();
                        AgSupportActivity.startActivity(context);
                        break;
                }
            }
        };
        PopupController.show(context, PopupItem.AuthPgu.ITEMS, listener);
    }

    /**
     * Всплывающее меню внизу экрана для экрана восстановления доступа к аг
     *
     * @param context
     * @since 1.9
     */
    public static void recovery(final Context context) {
        PopupController.PopupItemListener listener = new PopupController.PopupItemListener() {
            @Override
            public void onPopupItemSelected(PopupWindow popupWindow, int popupItemId) {
                switch (popupItemId) {
                    case PopupItem.Auth.HOW_IT_WORKS:
                        new GoogleStatistics.Auth().howItWorksClick((Activity) context);
                        InstructionActivity.startActivity(context);
                        popupWindow.dismiss();
                        break;
                    case PopupItem.Auth.FEEDBACK:
                        new GoogleStatistics.Auth().feedbackClick((Activity) context);
                        AgSupportActivity.startActivity(context);
                        popupWindow.dismiss();
                        break;
                }
            }
        };
        PopupController.show(context, PopupItem.Auth.ITEMS_RECOVERY, listener);
    }

    /**
     * Всплывающее меню внизу экрана для экрана регистрации и авторизации
     *
     * @param context
     * @param phone   номер телефона пользователя
     * @since 1.9
     */
    public static void authOrRegistry(final Context context, final String phone) {
        PopupController.PopupItemListener listener = new PopupController.PopupItemListener() {
            @Override
            public void onPopupItemSelected(PopupWindow popupWindow, int popupItemId) {
                switch (popupItemId) {
                    case PopupItem.Auth.HOW_IT_WORKS:
                        new GoogleStatistics.Auth().howItWorksClick((Activity) context);
                        InstructionActivity.startActivity(context);
                        popupWindow.dismiss();
                        break;
                    case PopupItem.Auth.RECOVERY_PASSWORD:
                        new GoogleStatistics.Auth().recoveryClick((Activity) context);
                        Intent intent = new Intent(context, AgRestoreActivity.class);
                        intent.putExtra("phone", phone);
                        context.startActivity(intent);
                        popupWindow.dismiss();
                        break;
                    case PopupItem.Auth.FEEDBACK:
                        new GoogleStatistics.Auth().feedbackClick((Activity) context);
                        AgSupportActivity.startActivity(context);
                        popupWindow.dismiss();
                        break;
                }
            }
        };
        PopupController.show(context, PopupItem.Auth.ITEMS, listener);
    }

    /**
     * Общий метод для организации всплывающего меню внизу экрана
     *
     * @param items    массив пунктов, которые будут отбражены в меню {@link PopupItem}
     * @param context
     * @param listener callback для обработки нажатия на выбранный пункт меню
     */
    public static void show(final Context context, PopupItem[] items, final PopupItemListener listener) {
        View v = View.inflate(context, R.layout.layout_popup, null);
        final PopupWindow popupWindow
                = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        ListView list = (ListView) v.findViewById(android.R.id.list);
        list.setAdapter(PopupItem.getAdapter(context, items));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onPopupItemSelected(popupWindow, (int) id);
                }
            }
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 10, 10);
        popupWindow.update(0, 0, -1, -1);
    }

    /**
     * Слушатель нажатий на пункт меню
     */
    public interface PopupItemListener {
        void onPopupItemSelected(PopupWindow popupWindow, int popupItemId);
    }
}
