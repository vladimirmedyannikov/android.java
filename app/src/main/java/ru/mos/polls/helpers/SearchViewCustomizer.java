package ru.mos.polls.helpers;

import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.ButterKnife;
import ru.mos.polls.R;

/**
 * Используется для касмизации search view
 * Можно поменять иконки для вызова строки поиска,
 * иконку для сокрытия строки поиска, цвет текста поиска
 * и цвет подсказки строки поиска
 * <p/>
 * Задание настроек работает по принципу билдера
 * {@link #setCloseIcon(int)}
 * {@link #setFinderIcon(int)}
 * {@link #setColorTextSearch(int)}
 * {@link #setColorHintSearch(int)}
 * <p/>
 * Применение настроек происходит при вызове метода {@link #applay()}
 *
 * @since 1.9.2
 */
public class SearchViewCustomizer {
    private View searchView;

    private int iconFinder, iconClose;
    private int colorSearch, colorSearchHint;

    public SearchViewCustomizer(View searchView) {
        this.searchView = searchView;
    }

    public SearchViewCustomizer setFinderIcon(int resId) {
        iconFinder = resId;
        return this;
    }

    public SearchViewCustomizer setCloseIcon(int resId) {
        iconClose = resId;
        return this;
    }

    public SearchViewCustomizer setColorTextSearch(int colorId) {
        colorSearch = colorId;
        return this;
    }

    public SearchViewCustomizer setColorHintSearch(int colorId) {
        colorSearchHint = colorId;
        return this;
    }

    public void applay() {
        EditText input = ButterKnife.findById(searchView, android.support.v7.appcompat.R.id.search_src_text);
//                (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        if (input != null) {
            try {
                input.setTextColor(searchView.getContext().getResources().getColor(colorSearch));
            } catch (Resources.NotFoundException ignored) {
            }
            try {
                input.setHintTextColor(searchView.getContext().getResources().getColor(colorSearchHint));
            } catch (Resources.NotFoundException ignored) {
            }
        }

        ImageView searchIcon = ButterKnife.findById(searchView, R.id.search_button);
//        (ImageView) searchView.findViewById(R.id.search_button);
        if (searchIcon != null) {
            try {
                searchIcon.setImageResource(iconFinder);
            } catch (Resources.NotFoundException ignored) {
            }
        }

        ImageView closeButton = ButterKnife.findById(searchView, R.id.search_close_btn);
//                (ImageView) searchView.findViewById(R.id.search_close_btn);
        if (closeButton != null) {
            try {
                closeButton.setImageResource(iconClose);
            } catch (Resources.NotFoundException ignored) {
            }
        }
    }
}
