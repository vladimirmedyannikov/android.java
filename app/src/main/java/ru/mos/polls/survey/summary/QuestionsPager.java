package ru.mos.polls.survey.summary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.mos.polls.R;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.questions.SurveyQuestion;

public class QuestionsPager extends FrameLayout {

    private static final int MAX_ELEMENTS_COUNT = 10;

    private ViewGroup container;
    private Map<Integer, Boolean> itemsActive = new HashMap<Integer, Boolean>();
    private Listener listener = Listener.STUB;
    private Survey survey;

    public QuestionsPager(Context context) {
        super(context);
        init();
    }

    public QuestionsPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuestionsPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setListener(Listener listener) {
        if (listener == null) {
            this.listener = Listener.STUB;
        } else {
            this.listener = listener;
        }
    }

    private void init() {
        removeAllViews();
        container = (ViewGroup) View.inflate(getContext(), R.layout.survey_questions_pager, null);
        addView(container);
    }

    private View addTextView(String text, boolean active) {
        final TextView tv;
        if (active) {
            tv = (TextView) View.inflate(getContext(), R.layout.survey_questions_pager_item_active, null);
        } else {
            tv = (TextView) View.inflate(getContext(), R.layout.survey_questions_pager_item_inactive, null);
        }
        tv.setText(text);
        container.addView(tv);
        return tv;
    }

    private View addDelimiter() {
        final View v;
        v = View.inflate(getContext(), R.layout.survey_questions_pager_item_delimiter, null);
        container.addView(v);
        return v;
    }

    private void setQuestionsCount(int count) {
        itemsActive.clear();
        for (int i = 0; i < count; i++) {
            itemsActive.put(i, true);
        }
    }

    private void recreateTextViews() {
        container.removeAllViews();
        int count = itemsActive.size();
        int dif = MAX_ELEMENTS_COUNT / 2;
        boolean shouldCut = MAX_ELEMENTS_COUNT < count;
        boolean hasPoints = false;
        boolean needDelimiter = false;
        for (int i = 0; i < itemsActive.size(); i++) {
            final int index = i;
            if (needDelimiter) {
                addDelimiter();
            }
            boolean active = itemsActive.get(i);
            if (shouldCut) {
                if (i < dif || i >= count - dif) {
                    View v = addTextView(Integer.toString(i + 1), active);
                    v.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onClick(index);
                        }
                    });
                    if (i == dif - 1) {
                        needDelimiter = false;
                    } else {
                        needDelimiter = true;
                    }
                } else {
                    if (!hasPoints) {
                        addTextView("...", false);
                        hasPoints = true;
                    }
                    needDelimiter = false;
                }
            } else {
                View v = addTextView(Integer.toString(i + 1), active);
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(index);
                    }
                });
                needDelimiter = true;
            }
        }
    }

    private void setQuestionEnables(int i, boolean b) {
        itemsActive.put(i, b);
    }

    public void display(Survey survey) {
        this.survey = survey;
        List<SurveyQuestion> questionsList = survey.getFilteredQuestionList();
        int size = questionsList.size();
        setQuestionsCount(size);
        Iterator<SurveyQuestion> iterator = questionsList.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            SurveyQuestion question = iterator.next();
            setQuestionEnables(i, question.isPassed());
            i++;
        }
        recreateTextViews();
    }

    public interface Listener {

        public static final Listener STUB = new Listener() {
            @Override
            public void onClick(int index) {
            }
        };

        void onClick(int index);

    }

}
