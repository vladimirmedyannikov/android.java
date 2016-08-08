package ru.mos.polls.survey.questions;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Один вопрос из опросника.
 */
public abstract class ListViewSurveyQuestion extends ListSurveyQuestion {

    private transient ListView listView;

    public ListViewSurveyQuestion(long id, Text question, String hint, List<SurveyVariant> variantsList, long filterId, int votersCount, int votersVariantsCount) {
        super(id, question, hint, variantsList, filterId, votersCount, votersVariantsCount);
    }

    @Override
    protected View inflateListView(Context context, QuestionAdapter adapter) {
        View v = View.inflate(context, R.layout.survey_question_container_listview, null);
        ButterKnife.bind(this, v);
        listView = ButterKnife.findById(v, R.id.list);
        processListView(listView);
        return v;
    }

    @Override
    protected void postprocessListView(ListView listView) {
        for (int i = 0; i < getVariantsList().size(); i++) {
            SurveyVariant variant = getVariantsList().get(i);
            boolean b = variant.isChecked();
            listView.setItemChecked(i + listView.getHeaderViewsCount(), b);
        }
    }

    @Override
    public ListView getListView() {
        return listView;
    }

    @Override
    protected void verifyItems() throws VerificationException {
        ListView lv = getListView();
        if (lv != null) {
            SparseBooleanArray checkedItemPositions = lv.getCheckedItemPositions();
            for (int i = 0; i < checkedItemPositions.size(); i++) {
                boolean checked = checkedItemPositions.valueAt(i);
                if (checked) {
                    int checkedKey = checkedItemPositions.keyAt(i);
                    if (checkedKey < lv.getHeaderViewsCount()) {
                        break;
                    }
                    int key = checkedKey - lv.getHeaderViewsCount();
                    SurveyVariant variant = getVariantsList().get(key);
                    variant.verify();
                }
            }
        }
    }

    @Override
    public String[] getCheckedBackIds() {
        List<String> list = new ArrayList<String>();
        for (SurveyVariant variant : getVariantsList()) {
            if (variant.isChecked()) {
                list.add(variant.getBackId());
            }
        }
        String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    @Override
    public long[] getCheckedInnerIds() {
        List<Long> list = new ArrayList<Long>();
        for (SurveyVariant variant : getVariantsList()) {
            if (variant.isChecked()) {
                list.add(variant.getInnerId());
            }
        }
        long[] result = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    protected abstract void processListView(ListView listView);

}
