package ru.mos.polls.rxhttp.rxapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Объект для с пагинацией
 * @since 1.0
 */
public class Page {
    /**
     * количество элементов на странице {@link #size} по умолчанию
     */
    public static final int DEFAULT_SIZE = 20;
    /**
     * текущий номер страницы {@link #num} по умолчанию
     */
    public static final int DEFAULT_NUM = 1;

    /**
     * Количество элементов на странице {@link #size}<br/>
     * текущий номер страницы {@link #num}
     */
    @SerializedName("count_per_page")
    private int size;
    @SerializedName("page_number")
    private int num;

    public Page() {
        this(DEFAULT_SIZE, DEFAULT_NUM);
    }

    public Page(int size, int num) {
        this.size = size;
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 0) {
            size = 0;
        }
        this.size = size;
    }

    public void setNum(int num) {
        if (num < 0) {
            num = 0;
        }
        this.num = num;
    }

    /**
     * Сброс номера старницы и количества элементов на стронице
     * до значений по умолчанию {@link #DEFAULT_NUM}
     */
    public void reset() {
        num = DEFAULT_NUM;
    }

    /**
     * Увеличение номера последнего элемента на {@link #size}
     */
    public void increment() {
        ++num;
    }
}
