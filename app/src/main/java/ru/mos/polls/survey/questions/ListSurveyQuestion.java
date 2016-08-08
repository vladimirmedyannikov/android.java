package ru.mos.polls.survey.questions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.variants.InputSurveyVariant;
import ru.mos.polls.survey.variants.IntervalSurveyVariant;
import ru.mos.polls.survey.variants.SurveyVariant;
import ru.mos.polls.survey.variants.TextSurveyVariant;

/**
 * Вопрос, подразумевающий какие-то манипуляции со списком.
 */
public abstract class ListSurveyQuestion extends SurveyQuestion {

    public ListSurveyQuestion(long id, Text question, String hint, List<SurveyVariant> variantsList, long filterId, int votersCount, int votersVariantsCount) {
        super(id, question, hint, variantsList, filterId, votersCount, votersVariantsCount);
    }

    protected abstract ListView getListView();

    @Override
    protected View onGetView(final Activity context, final Fragment fragment, final StatusProcessor statusProcessor, ViewFactory viewFactory) {
        final QuestionAdapter adapter = new QuestionAdapter(context, statusProcessor);
        View v = inflateListView(context, adapter);
        ButterKnife.bind(context);
        if (viewFactory != null) {
            View headerView = viewFactory.getHeaderView(context);
            if (headerView != null) {
                getListView().addHeaderView(headerView, null, false);
            }
            View footerView = viewFactory.getFooterView(context);
            if (footerView != null) {
                getListView().addFooterView(footerView);
            }
        }

        getListView().setAdapter(adapter);
        postprocessListView(getListView());
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int pos, final long id) {
                if (pos < getListView().getHeaderViewsCount()) {
                    return;
                }
                final int position = pos - getListView().getHeaderViewsCount();
                boolean enabled = statusProcessor.isEnabled();
                if (enabled) {
                    long[] checkedItemIds = getListView().getCheckedItemIds();
                    for (SurveyVariant variant : getVariantsList()) {
                        boolean found = false;
                        for (int i = 0; i < checkedItemIds.length; i++) {
                            long checkedItemId = checkedItemIds[i];
                            if (variant.getInnerId() == checkedItemId) {
                                found = true;
                                break;
                            }
                        }
                        variant.setChecked(found);
                    }
                    boolean checked = getListView().isItemChecked(pos);
                    final SurveyVariant surveyVariant = getVariantsList().get(position);
                    surveyVariant.setListener(new SurveyVariant.Listener() {

                        @Override
                        public void onCommit() {
                            adapter.notifyDataSetChanged();
                            getListener().onCommmit(surveyVariant);
                            getListener().onAfterClick(surveyVariant);
                        }

                        @Override
                        public void onCancel() {
                            adapter.notifyDataSetChanged();
                            getListener().onCancel(surveyVariant);
                            getListener().onAfterClick(surveyVariant);
                        }

                        @Override
                        public void performParentClick() {
                            getListView().performItemClick(view, position, id);
                        }

                        @Override
                        public void refreshSurvey() {
                            getListener().refreshSurvey();
                        }

                        @Override
                        public void onClicked() {
                            adapter.notifyDataSetChanged();
                            getListener().onAfterClick(surveyVariant);
                        }

                    });
                    adapter.notifyDataSetChanged();
                    getListener().onBeforeClick(surveyVariant);
                    surveyVariant.onClick(context, fragment, checked);

                }
            }
        });
        return v;
    }

    protected abstract View inflateListView(Context context, QuestionAdapter adapter);

    protected void postprocessListView(ListView listView) {
    }

    protected abstract View getListItemView(Context context, View convertView, SurveyVariant item, StatusProcessor statusProcessor);

    public int getIndexOfScrollableView() {
        int index = 0;
        if (getListView() != null) {
            index = getListView().getFirstVisiblePosition();
        }
        return index;
    }

    public int getViewTopOfFirstItem() {
        int top = 0;
        if (getListView() != null) {
            View v = getListView().getChildAt(0);
            top = v == null ? 0 : v.getTop();
        }
        return top;
    }

    public void setScrollableState(int index, int top) {
        getListView().setSelectionFromTop(index, top);
    }

    protected class QuestionAdapter extends BaseAdapter {

        private Map<Class, Integer> viewTypes = new HashMap<Class, Integer>();
        private final int TYPE_TEXT = 0;
        private final int TYPE_INPUT = 1;
        private final int TYPE_INTERVAL = 2;

        private final Context context;
        private final StatusProcessor statusProcessor;

        public QuestionAdapter(Context c, StatusProcessor statusProcessor) {
            context = c;
            this.statusProcessor = statusProcessor;
            viewTypes.put(TextSurveyVariant.class, TYPE_TEXT);
            viewTypes.put(InputSurveyVariant.class, TYPE_INPUT);
            viewTypes.put(IntervalSurveyVariant.class, TYPE_INTERVAL);
        }

        @Override
        public boolean isEnabled(int position) {
            return statusProcessor.isEnabled();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getViewTypeCount() {
            return viewTypes.size();
        }

        @Override
        public int getItemViewType(int position) {
            SurveyVariant item = getItem(position);
            final int result;
            final Class cls = ((Object) item).getClass();
            if (viewTypes.containsKey(cls)) {
                result = viewTypes.get(cls);
            } else {
                result = TYPE_TEXT;
            }
            return result;
        }

        @Override
        public int getCount() {
            return getVariantsList().size();
        }

        @Override
        public SurveyVariant getItem(int position) {
            return getVariantsList().get(position);
        }

        @Override
        public long getItemId(int position) {
            SurveyVariant var = getItem(position);
            return var.getInnerId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SurveyVariant item = getItem(position);
            convertView = getListItemView(context, convertView, item, statusProcessor);
            statusProcessor.process(convertView);
            processVotersCount(convertView, item);
            return convertView;
        }

        private void processVotersCount(View convertView, SurveyVariant item) {
            TextView votersPercentTextView = ButterKnife.findById(convertView, R.id.votersPercent);
            String s = context.getString(R.string.survey_variant_voters_percent);
            votersPercentTextView.setText(String.format(Locale.getDefault(), s, item.getVoters(), item.getPercent()));
            if (item.isChecked()) {
                votersPercentTextView.setTypeface(null, Typeface.BOLD);
            } else {
                votersPercentTextView.setTypeface(null, Typeface.NORMAL);
            }
            statusProcessor.processVotersPercents(votersPercentTextView);
        }

    }

}
