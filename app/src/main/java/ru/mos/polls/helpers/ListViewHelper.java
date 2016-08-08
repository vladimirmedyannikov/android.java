package ru.mos.polls.helpers;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;


public class ListViewHelper {
    public static final String SCROLLABLE_STATE = "scrollable_state";
    public static final String INDEX = "index";
    public static final String TOP = "top";

    private static String saveTag;
    private static int index;
    private static int top;

    /**
     * Сохраняем  состояние сколлинга списка
     * Метод рекомендуется вызвать onStop()
     *
     * @param tag      - параметр, зарактеризующий, для какого экрана сохраняем список
     * @param listView - список, состояние которого сохраняем
     */
    public static void saveScrollableState(String tag, ListView listView) {
        if (!TextUtils.isEmpty(tag)) {
            getListViewParams(listView);
            saveTag = tag;
        }
    }

    /**
     * Восстонавливаем состояние склоллинга списка по возвращению на экран
     * Метод рекомендуется вызвать в onResume()
     *
     * @param tag      - параметр, зарактеризующий, для какого экрана восстанавливаем список
     * @param listView - список, состояние скроллинга которого восстанавливаем
     */
    public static void restoreScrollableState(String tag, ListView listView) {
        if (listView != null
                && !TextUtils.isEmpty(tag)
                && !TextUtils.isEmpty(saveTag)
                && saveTag.equalsIgnoreCase(tag)) {
            listView.setSelectionFromTop(index, top);
        }
    }

    /**
     * Метод сброса параметров состояния сколла списка
     * Рекомендуется вызвать при разрушении экрана или при создании экрана
     */
    public static void clearScrollableState() {
        index = 0;
        top = 0;
    }

    /**
     * Вызывать в onSavedInstanceState
     *
     * @param savedInstanceState - параметр из метода onSavedInstance
     * @param listView           - список, состояние скроллинга которого восстанавливаем
     */
    public static void saveScrollableState(Bundle savedInstanceState, ListView listView) {
        if (savedInstanceState != null) {
            getListViewParams(listView);
            savedInstanceState.putInt(INDEX, index);
            savedInstanceState.putInt(TOP, top);
        }
    }

    /**
     * Вызывать из onViewRestoredState для фрагмента и onRestoreInstanceState для активити
     *
     * @param savedInstanceState
     * @param listView
     */
    public static void restoreScrollableState(Bundle savedInstanceState, ListView listView) {
        if (savedInstanceState != null && listView != null) {
            index = savedInstanceState.getInt(INDEX);
            top = savedInstanceState.getInt(TOP);
            listView.setSelectionFromTop(index, top);
        }
    }

    private static void getListViewParams(ListView listView) {
        if (listView != null) {
            index = listView.getFirstVisiblePosition();
            View firstView = listView.getChildAt(0);
            top = (firstView == null) ? 0 : firstView.getTop();
        }
    }
}
