package ru.mos.polls.survey.questions;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Опрос только с одним элементом.
 */
public class SimpleSurveyQuestion extends SurveyQuestion {

    public SimpleSurveyQuestion(long id, Text question, String hint, List<SurveyVariant> variantsList, long filterId, int votersCount, int votersVariantsCount) {
        super(id, question, hint, variantsList, filterId, votersCount, votersVariantsCount);
    }

    @Override
    protected View onGetView(final Activity context, final Fragment fragment, final StatusProcessor statusProcessor, ViewFactory viewFactory) {
        final ViewGroup result = (ViewGroup) View.inflate(context, R.layout.survey_question_container_simple, null);
        ViewGroup container = ButterKnife.findById(result, R.id.container);
        final SurveyVariant variant = refreshView(context, fragment, statusProcessor, container, viewFactory);

        return result;
    }

    private SurveyVariant refreshView(final Activity context, final Fragment fragment, final StatusProcessor statusProcessor, final ViewGroup result, final ViewFactory viewFactory) {
        result.removeAllViews();
        final SurveyVariant variant = getVariantsList().get(0);
        View v = variant.getView(context, statusProcessor);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = statusProcessor.isEnabled();
                if (enabled) {
                    variant.setChecked(true); //один раз нажали - считаем что отмечен. дальше только проверка корреткности значения
                    variant.setListener(new SurveyVariant.Listener() {

                        @Override
                        public void onClicked() {
                            getListener().onAfterClick(variant);
                        }

                        @Override
                        public void onCommit() {
                            refreshView(context, fragment, statusProcessor, result, viewFactory);
                            getListener().onCommmit(variant);
                            getListener().onAfterClick(variant);
                        }

                        @Override
                        public void onCancel() {
                            getListener().onCancel(variant);
                            getListener().onAfterClick(variant);
                        }

                        @Override
                        public void performParentClick() {
                            result.performClick();
                        }

                        @Override
                        public void refreshSurvey() {
                            getListener().refreshSurvey();
                        }

                    });
                    getListener().onBeforeClick(variant);
                    variant.onClick(context, fragment, true);
                }
            }
        });
        result.addView(v);
        if (viewFactory != null) {
            View headerView = viewFactory.getHeaderView(context);
            if (headerView != null) {
                result.addView(headerView, 0);
            }
            View footerView = viewFactory.getFooterView(context);
            if (footerView != null) {
                result.addView(footerView);
            }
        }
        return variant;
    }

    @Override
    protected void verifyCheckedCount() throws VerificationException {
        //ничего не делает
    }

    @Override
    protected void verifyItems() throws VerificationException {
        for (SurveyVariant variant : getVariantsList()) {
            variant.verify();
        }
    }

    @Override
    public String[] getCheckedBackIds() {
        //всегда только один элемент - он считается всегда отмеченным
        return new String[]{
                getVariantsList().get(0).getBackId()
        };
    }

    @Override
    public long[] getCheckedInnerIds() {
        //всегда только один элемент - он считается всегда отмеченным
        return new long[]{
                getVariantsList().get(0).getInnerId()
        };
    }

}
