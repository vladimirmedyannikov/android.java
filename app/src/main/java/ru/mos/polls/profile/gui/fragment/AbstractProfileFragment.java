package ru.mos.polls.profile.gui.fragment;

import android.support.v4.app.Fragment;

import butterknife.Unbinder;
import ru.mos.elk.profile.AgUser;

/**
 * Базовый экран для фрагментов профиля
 *
 * @since 1.9
 */
public abstract class AbstractProfileFragment extends Fragment {
    /**
     * Список идентификаторов фрагментов,
     * используются в ChangeListener.onChange(int fragmentId);
     */
    public static final int EMAIL_ID = 0;
    public static final int USER_PERSONAL_ID = 1;
    public static final int FAMILY_ID = 2;
    public static final int ADDRESS_ID = 3;
    public static final int WORK_ID = 4;
    public static final int BINDING_ID = 5;
    public Unbinder unbinder;
    /**
     * Callback для реагирования на ввод пользователя
     */
    protected ChangeListener changeListener = ChangeListener.STUB;

    public void setChangeListener(ChangeListener changeListener) {
        if (changeListener == null) {
            changeListener = ChangeListener.STUB;
        }
        this.changeListener = changeListener;
    }

    /**
     * Вызывается для заполнения модели профиля
     * значениями контролов фрагмента
     *
     * @param agUser текущее состояние профиля
     */
    public void updateAgUser(AgUser agUser) {
    }

    /**
     * Вызывается для заполнения контролов фрагмента
     * данными модели профиля
     *
     * @param agUser текущее состояние профиля
     */
    public void refreshUI(AgUser agUser) {
    }

    public boolean isFilledAndChanged(AgUser old, AgUser changed) {
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface ChangeListener {
        ChangeListener STUB = new ChangeListener() {
            @Override
            public void onChange(int fragmentId) {
            }
        };

        void onChange(int fragmentId);
    }
}
