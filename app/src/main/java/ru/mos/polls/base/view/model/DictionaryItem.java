package ru.mos.polls.base.view.model;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;


public class DictionaryItem implements Serializable{
    public static final String NON_SELECT = "Не установлено";

    public static DictionaryItem getNothingSelectedElement (Context context){
        DictionaryItem dictionaryItem = new DictionaryItem();
        dictionaryItem.setId(-1);
        dictionaryItem.setTitle("Не установлено");
        return dictionaryItem;
    }

    public static DictionaryItem getNothingSelectedElement (Context context, String title){
        DictionaryItem dictionaryItem = new DictionaryItem();
        dictionaryItem.setId(-1);
        dictionaryItem.setTitle(!TextUtils.isEmpty(title) ? title : NON_SELECT);
        return dictionaryItem;
    }

    private String title;
    private int id;

    public DictionaryItem(){}

    public DictionaryItem(int id, String title){
        this.id = id;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return title;
    }

}
