package ru.mos.polls.helpers;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Многие не любят фрагменты (а зря!), да, у фрагментов
 * "бывает случается" неожиданно нестандартное поведение
 * Данный класс предназначен для того, чтобы исключать подобное "нестандартное поведение"
 *
 * @since 1.9.2
 */
public class FragmentHelper {

    /**
     * Если вызвать фрагмент вложен в другой фрагмент, и вложенного фрагмента вызвать startActivityForResult(),
     * то ответ в onActivityResult для первого фрагмента не придет, соотвественно,
     * необходимо вызывать fragment.getParentFragment().startActivityForResult()
     * Тогда в родительский фрагмент придет ответ и внутри родительского onActivityResult()  необходимо
     * вызвать этот метод, чтобы пробросить ответ в onActivityResult дочернего фрагмента
     *
     * @param parentFragment родительский фрагмент
     * @param requestCode    код запроса
     * @param resultCode     код ответа
     * @param data           данные
     * @see <a href="http://stackoverflow.com/questions/13580075/onactivityresult-not-called-in-new-nested-fragment-api">
     * Костыль</a>
     */
    public static void onActivityResult(Fragment parentFragment, int requestCode, int resultCode, Intent data) {
        List<Fragment> fragments = parentFragment.getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * Поиск родительского фрагмента
     *
     * @param fragment текущий фрагмент
     * @return родительский фрагмент
     */
    public static Fragment getParentFragment(Fragment fragment) {
        Fragment result = fragment;
        if (fragment.getParentFragment() != null) {
            result = getParentFragment(fragment.getParentFragment());
        }
        return result;
    }
}
