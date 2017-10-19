package ru.mos.polls.base.view;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.mos.polls.R;
import ru.mos.polls.base.view.model.DictionaryItem;

/**
 * View для отображения списков, как из справочников, так и заранее созданных
 */
public class DictionaryView extends LinearLayout {

    public static final int FIRST_NOTHING_ELEMENT = 0;

    private TextView textView;
    private TextView firstItem;
    private int parentId;
    private int selected;
    private boolean addFirstNothingElement = false;
    private ArrayList<DictionaryItem> listDictionary;
    private boolean nonSelectFirstNothingElement = true;
    private boolean firstSelectedElementIsSetHintFirstNothingElement = false;
    private String titleFirstNothingElement;

    private OnDictionarySelectedListener onDictionarySelectedListener;
    private OnLoadCompleteListener onLoadCompleteListener;


    public DictionaryView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public DictionaryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    public DictionaryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DictionaryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init();
        }
    }

    /**
     * метод задает имя первого элемента в списке (если указан параметр addFirstNothingElement)
     *
     * @param title
     */
    public void setTitleFirstNothingElement(String title) {
        titleFirstNothingElement = title;
    }

    /**
     * устанавливает слушателя на выборщика элементов
     *
     * @param listener
     */
    public void setOnDictionarySelectedListener(OnDictionarySelectedListener listener) {
        onDictionarySelectedListener = listener;
    }

    public void setFirstSelectedElementIsSetHintFirstNothingElement(boolean b) {
        firstSelectedElementIsSetHintFirstNothingElement = b;
    }

    /**
     * устанавливает слушателя на загрузку справочников
     *
     * @param listener
     */
    public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
        onLoadCompleteListener = listener;
    }

    /**
     * обновление выбранного элемента
     */
    public void refreshUi() {
        if (selected < listDictionary.size() && selected >= 0) {
            firstItem.setTextColor(getResources().getColor(R.color.black_light));
            firstItem.setText(listDictionary.get(selected).getTitle());
            if (firstSelectedElementIsSetHintFirstNothingElement && !TextUtils.isEmpty(titleFirstNothingElement)) {
                if (listDictionary.get(selected).getId() == 1) {
                    firstItem.setTextColor(getResources().getColor(R.color.text_hint));
                    firstItem.setText(titleFirstNothingElement);
                }
            }
        }
    }

    /**
     * устанавливает список элементов для отображения
     *
     * @param list
     */
    public void setData(ArrayList<DictionaryItem> list) {
        listDictionary.clear();
        if (addFirstNothingElement) {
            listDictionary.add(new DictionaryItem(FIRST_NOTHING_ELEMENT, titleFirstNothingElement));
        }
        listDictionary.addAll(list);
        refreshUi();
    }

    /**
     * @param addFirstNothingElement добавлять или нет элемент пустышку в начало списка
     */
    public void setAddFirstNothingElement(boolean addFirstNothingElement) {
        this.addFirstNothingElement = addFirstNothingElement;
    }

    /**
     * @param nonSelectFirstNothingElement будет ли первый элемент в выпадающем с писке
     */
    public void setNonSelectFirstNothingElement(boolean nonSelectFirstNothingElement) {
        setAddFirstNothingElement(nonSelectFirstNothingElement || getAddFirstNothingElement());
        this.nonSelectFirstNothingElement = nonSelectFirstNothingElement;
    }

    /**
     * устанавливается выбранный элемент из списка по его ID
     *
     * @param id
     */
    public void setSelected(int id) {
        selected = 0;
        for (int i = 0; i < listDictionary.size(); i++) {
            if (listDictionary.get(i).getId() == id) {
                selected = i;
                break;
            }
        }
        refreshUi();
    }

    /**
     * установка названия списка
     *
     * @param title
     */
    public void setTitle(String title) {
        textView.setText(title);
    }

    /**
     * установка видимости заголовка списка
     */
    public void setTitleVisible() {
        textView.setVisibility(VISIBLE);
    }

    /**
     * установка невидимости заголовка списка
     */
    public void setTitleInvisible() {
        textView.setVisibility(INVISIBLE);
    }

    /**
     * убрать заголовок списка
     */
    public void setTitleGone() {
        textView.setVisibility(GONE);
    }

    public boolean getAddFirstNothingElement() {
        return addFirstNothingElement;
    }

    /**
     * @return выбранный в данный момент элемент
     */
    public DictionaryItem getSelected() {
        return listDictionary.get(selected);
    }

    private void init() {
        removeAllViews();
        View view = View.inflate(getContext(), R.layout.layout_dictionary_view, (ViewGroup) getParent());
        textView = (TextView) view.findViewById(R.id.title);
        firstItem = (TextView) view.findViewById(R.id.first_item);
        listDictionary = new ArrayList<>();
        selected = FIRST_NOTHING_ELEMENT;
        addFirstNothingElement = false;
        firstItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final DictionaryAdapter arrayAdapter = getAdapter(getContext(), listDictionary, nonSelectFirstNothingElement);
                builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                                setSelected(arrayAdapter.getItem(which).getId());
                                if (onDictionarySelectedListener != null) {
                                    onDictionarySelectedListener.selected(arrayAdapter.getItem(which));
                                }
                                dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (onDictionarySelectedListener != null) {
                            onDictionarySelectedListener.nothingSelected();
                        }
                        refreshUi();
                    }
                });
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (onDictionarySelectedListener != null) {
                            onDictionarySelectedListener.nothingSelected();
                        }
                        refreshUi();
                    }
                });
                alertDialog.show();
            }
        });

        addView(view);
    }

    private DictionaryAdapter getAdapter(Context context, ArrayList<DictionaryItem> listDictionary, boolean nonSelectedFirstItem) {
        DictionaryAdapter result = new DictionaryAdapter(context, listDictionary, nonSelectedFirstItem);
        result.setDropDownViewResource(R.layout.dictionary_item_dropdown);
        return result;
    }

    private ArrayList<DictionaryItem> getFixedArray(ArrayList<DictionaryItem> objects, boolean nonSelectedFirstItem) {
        ArrayList <DictionaryItem> list = new ArrayList<>();
        list.addAll(objects);
        if (nonSelectedFirstItem) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getTitle().equals(titleFirstNothingElement)) {
                    list.remove(i);
                    break;
                }
            }
        }
        return list;
    }

    private class DictionaryAdapter extends ArrayAdapter<DictionaryItem> {

        private DictionaryAdapter(Context context, ArrayList<DictionaryItem> objects, boolean nonSelectedFirstItem) {
            super(context, -1, getFixedArray(objects, nonSelectedFirstItem));
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.dictionery_item, null);
            }
            displayDictionary(convertView, getItem(position));
            return convertView;
        }

        private void displayDictionary(View v, DictionaryItem dictionaryItem) {
            TextView title = (TextView) v.findViewById(android.R.id.text1);
            title.setText(dictionaryItem.getTitle());
        }
    }

    public interface OnDictionarySelectedListener {
        void selected(DictionaryItem dictionaryItem);

        void nothingSelected();
    }

    public interface OnLoadCompleteListener {
        void complete();

        void loadError();
    }
}