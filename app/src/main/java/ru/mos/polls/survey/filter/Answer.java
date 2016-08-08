package ru.mos.polls.survey.filter;

import java.io.Serializable;

public class Answer implements Serializable {

    private final long questionId;
    private final String variantBackId;

    public Answer(long questionId, String variantBackId) {
        this.questionId = questionId;
        this.variantBackId = variantBackId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getVariantBackId() {
        return variantBackId;
    }

    @Override
    public String toString() {
        return "Answer questionId=" + questionId + " " + " variantId = " + variantBackId;
    }
}
