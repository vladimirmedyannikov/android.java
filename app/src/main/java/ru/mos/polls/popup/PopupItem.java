package ru.mos.polls.popup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;

/**
 * Структура данных, описывающая пункт всплывающего внизу экрана меню
 *
 * @since 1.9
 */
public class PopupItem {
    /**
     * Используется на экране привязки аккаунта аг к аккаунту пгу
     */
    public interface AuthPgu {
        int QUESTIONS_AND_ANSWERS = 0;
        int GO_TO_PGU_MOS_RU = 1;
        int FEEDBACK = 2;

        PopupItem[] ITEMS = new PopupItem[]{
                new PopupItem(QUESTIONS_AND_ANSWERS, R.string.questions_and_answers),
                new PopupItem(GO_TO_PGU_MOS_RU, R.string.go_to_pgu_mos_ru),
                new PopupItem(FEEDBACK, R.string.feedback)
        };
    }

    /**
     * Используется на экранах авторизации, регистрации и восстановления
     */
    public interface Auth {
        int HOW_IT_WORKS = 0;
        int RECOVERY_PASSWORD = 1;
        int FEEDBACK = 2;
        int REPEAT_CONFIRMATION_CODE = 4;
        int YET_HAS_CONFIRMATION_CODE = 5;

        PopupItem[] ITEMS_RECOVERY = new PopupItem[]{
                /*new PopupItem(HOW_IT_WORKS, R.string.how_it_works),*/
                new PopupItem(FEEDBACK, R.string.feedback)
        };

        PopupItem[] ITEMS = new PopupItem[]{
                /*new PopupItem(HOW_IT_WORKS, R.string.how_it_works),*/
                new PopupItem(RECOVERY_PASSWORD, R.string.recovery_password),
                new PopupItem(FEEDBACK, R.string.feedback)
        };

        PopupItem[] REQUEST_CONFIRM_CODE = new PopupItem[]{
                new PopupItem(YET_HAS_CONFIRMATION_CODE, R.string.has_code),
                /*new PopupItem(HOW_IT_WORKS, R.string.how_it_works),*/
                new PopupItem(FEEDBACK, R.string.feedback)
        };

        PopupItem[] SET_PASSWORD = new PopupItem[]{
                new PopupItem(REPEAT_CONFIRMATION_CODE, R.string.repeat_confirmation_code),
                /*new PopupItem(HOW_IT_WORKS, R.string.how_it_works),*/
                new PopupItem(FEEDBACK, R.string.feedback)
        };
    }

    private int id;
    private int title;

    public PopupItem(int id, int title) {
        this.id = id;
        this.title = title;
    }

    public int getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    /**
     * Создание адаптера для списка пунктов меню
     *
     * @param context
     * @param items   массив пунктов меню, которые буду отображены в меню
     * @return
     */
    public static ArrayAdapter getAdapter(Context context, PopupItem[] items) {
        return new Adapter(context, items);
    }

    /**
     * Пункты меню отображаем в ListView, поэтому потребуется адаптер
     */
    private static class Adapter extends ArrayAdapter<PopupItem> {

        public Adapter(Context context, PopupItem[] objects) {
            super(context, -1, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.layout_ag_simple_text, null);
            }
            PopupItem popupItem = getItem(position);
            TextView text = ButterKnife.findById(convertView, R.id.text);
            text.setText(popupItem.getTitle());
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }
    }
}
