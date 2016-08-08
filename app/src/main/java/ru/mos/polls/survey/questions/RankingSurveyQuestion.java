package ru.mos.polls.survey.questions;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobeta.android.dslv.DragSortListView;

import java.util.Collections;
import java.util.List;

import ru.mos.polls.R;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.SurveyVariant;

@Deprecated
/**
 * Вопрос, подразумевающий сортировку множества ответов.
 *
 * Исклёчн из требований. Но лучше оставить, так как требования переменчивы как ветер на море.
 */
public class RankingSurveyQuestion extends ListSurveyQuestion {

    private DragSortListView listView;

    public RankingSurveyQuestion(long id, Text question, String hint, List<SurveyVariant> variantsList, long filterId, int votersCount, int votersVariantsCount) {
        super(id, question, hint, variantsList, filterId, votersCount, votersVariantsCount);
    }

    @Override
    protected ListView getListView() {
        return listView;
    }

    @Override
    protected View inflateListView(final Context context, final QuestionAdapter adapter) {
        View v = View.inflate(context, R.layout.survey_question_container_sortlistview, null);
        listView = (DragSortListView) v.findViewById(R.id.list);
        listView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                Collections.swap(getVariantsList(), from, to);
                adapter.notifyDataSetChanged();
            }
        });
        return v;
    }

    @Override
    protected View getListItemView(Context context, View convertView, SurveyVariant item, StatusProcessor statusProcessor) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.survey_question_ranking, null);
        }
        ViewGroup container = (ViewGroup) convertView.findViewById(R.id.container);
        container.removeAllViews();
        View innerView = item.getView(context, statusProcessor);
        container.addView(innerView);
        return convertView;
    }

    @Override
    protected void verifyCheckedCount() throws VerificationException {
        //ничего не делает - для этого вопроса не имеет смысла выбор элемента
    }

    @Override
    protected void verifyItems() throws VerificationException {
        for (SurveyVariant variant : getVariantsList()) {
            variant.verify();
        }
    }

    @Override
    public String[] getCheckedBackIds() {
        return new String[0]; //TODO сюда выбранные элементы, вероятно это все элементы
    }

    @Override
    public long[] getCheckedInnerIds() {
        return new long[0]; //TODO сюда выбранные элементы, вероятно это все элементы
    }

}
