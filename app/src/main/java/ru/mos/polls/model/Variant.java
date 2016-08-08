package ru.mos.polls.model;

import android.text.InputType;


public class Variant {

    final int id;
    final VariantKind kind;
    private String text;
    private String rightText;
    private InputType inputType;
    private String min;
    private String max;

    public Variant(int id, VariantKind kind, String text) {
        this.id = id;
        this.kind = kind;
        this.text = text;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public String getText() {
        return text;
    }

    public void setLeftText(String rightText) {
        this.rightText = rightText;
    }

    public String getLeftText() {
        return text;
    }
}

