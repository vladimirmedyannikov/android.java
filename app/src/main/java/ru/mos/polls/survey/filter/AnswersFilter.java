package ru.mos.polls.survey.filter;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.filter.conditions.Condition;

public abstract class AnswersFilter extends ConditionFilter {

    private final Answer[] answers;

    public AnswersFilter(long id, Answer[] answers, Condition condition) {
        super(id, condition);
        if (answers == null) {
            throw new NullPointerException("answers");
        }
        this.answers = answers;
    }

    @Override
    public boolean isSuitable(Survey survey) {
        getCondition().reset();
        for (Answer answer : answers) {
            long questionId = answer.getQuestionId();
            String variantId = answer.getVariantBackId();
            String[] variantsIds = survey.getQuestion(questionId).getCheckedBackIds();
            final boolean contains = onContains(variantId, variantsIds);
            getCondition().add(contains);
        }
        return getCondition().get();
    }

    protected abstract boolean onContains(String variantId, String[] variantsIds);

    public static abstract class Factory extends ConditionFilter.Factory {

        private static Answer[] getAnswers(JSONArray answersJsonArray) {
            final Answer[] result;
            if (answersJsonArray == null) {
                result = null;
            } else {
                result = new Answer[answersJsonArray.length()];
                for (int i = 0; i < answersJsonArray.length(); i++) {
                    JSONObject answerJsonObject = answersJsonArray.optJSONObject(i);
                    long questionId = answerJsonObject.optLong("question_id");
                    String variantId = answerJsonObject.optString("variant_id");
                    Answer answer = new Answer(questionId, variantId);
                    result[i] = answer;
                }
            }
            return result;
        }

        @Override
        protected Filter onCreate(long filterId, JSONObject jsonObject, Condition condition) {
            JSONArray answersJsonArray = jsonObject.optJSONArray("answers");
            Answer[] answers = getAnswers(answersJsonArray);
            return onCreate(filterId, answers, condition);
        }

        protected abstract Filter onCreate(long filterId, Answer[] answers, Condition condition);

    }

}
