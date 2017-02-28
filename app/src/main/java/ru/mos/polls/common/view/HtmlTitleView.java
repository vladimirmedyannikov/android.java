package ru.mos.polls.common.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.elk.ElkTextUtils;
import ru.mos.polls.R;
import ru.mos.polls.helpers.AnimationHelper;
import ru.mos.polls.innovation.model.Innovation;

/**
 * Компонент для отображения информации о вопросе: краткое, полное описание
 * Исопльзуется на экранах:
 * списка вопросов голосования {@link ru.mos.polls.survey.SurveySummaryFragment}
 * деталей голосования {@link ru.mos.polls.survey.SurveyFragment}
 * деталей городской новинки {@link ru.mos.polls.innovation.gui.activity.InnovationActivity}
 *
 * @since 1.8
 */
public class HtmlTitleView extends LinearLayout {
    public static int SIMPLE = 0;
    public static int EXPAND_COLLAPSE = 1;

    private WebView shortContainer, fullContainer;
    protected TextView title;
//    private ImageView detailsExpand, detailsCollapse;
    private View /*detailContainer,*/ divider;
    private LinearLayout contentContainer;
    private RelativeLayout buttonContainer;
    private TextView action;

    private String moreValue, lessValue;

    private int contentChangingType = SIMPLE;

    private StateListener stateListener = StateListener.STUB;


    public HtmlTitleView(Context context) {
        super(context);
        init();
    }

    public HtmlTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HtmlTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutParams(attrs);
        init();
    }

    private void setLayoutParams(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SurveyTitleView,
                0, 0);

        try {
            contentChangingType = a.getInt(R.styleable.SurveyTitleView_contentChangingType, SIMPLE);
        } finally {
            a.recycle();
        }
    }


    public void setStateListener(StateListener stateListener) {
        if (stateListener == null) {
            stateListener = StateListener.STUB;
        }
        this.stateListener = stateListener;
    }

    public void display(Innovation innovation) {
        if (innovation != null) {
            prepare();
            display(innovation.getTitle(),
                    innovation.getTextShortHtml(),
                    innovation.getTextFullHtml());
        }
    }

    private void prepare() {
        shortContainer.setVisibility(GONE);
        fullContainer.setVisibility(GONE);
        action.setText(getContext().getString(R.string.title_more));
    }

    protected void onExpanded() {
    }

    protected void onCollapsed() {
    }

    /**
     * Алгоритм отображения короткого и длинного текста html
     * 1. При загрузке экрана Голосование в заголовок выводится text_short_html в бразуерном компоненте, если text_short_html пустой, то выводим по старинке поле title не в браузерном компоненте
     * 2. При нажатии на кнопку подробнее у нас раскрываетс полное описание и в тот же браузерный компонент который используется в заголовке мы подставляем text_full_html без конкатинации с text_short_html или Title
     * 3. Если при нажатии на подробнее text_short_html был пустым и в первом пункте вывели Title не в браузерном компоненте, то скрываем контрол с Title и уже на это место ставим браузерный компонент в который выводим text_full_html
     *
     * @param titleValue
     * @param shortValue
     * @param fullValue
     */
    protected final void display(String titleValue, String shortValue, String fullValue) {
        final Algorithm algorithm;
        if (ElkTextUtils.isEmpty(shortValue)) {
            algorithm = new NoShortTextAlgorithm(titleValue, shortValue, fullValue);
        } else {
            algorithm = new HasShortTextAlgorithm(titleValue, shortValue, fullValue);
        }

        action.setVisibility(VISIBLE);
        if (ElkTextUtils.isEmpty(fullValue)) {
            action.setVisibility(GONE);
            divider.setVisibility(View.VISIBLE);
            removePaddingFromContent();
        }

        View.OnClickListener onActionClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMoreAction()) {
                    if (contentChangingType == EXPAND_COLLAPSE) {
                        AnimationHelper.collapse(algorithm.getFullContainer(), new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                algorithm.displayCollapsed();
                                AnimationHelper.expand(algorithm.getShortContainer(), new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        stateListener.onCollapse();
                                        onCollapsed();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                        });
                    } else {
                        algorithm.displayCollapsed();
                        stateListener.onCollapse();
                        onCollapsed();
                    }
                } else {
                    if (contentChangingType == EXPAND_COLLAPSE) {
                        AnimationHelper.collapse(algorithm.getShortContainer(), new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                algorithm.displayExpended();
                                AnimationHelper.expand(algorithm.getFullContainer(), new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        stateListener.onExpand();
                                        onExpanded();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                        });
                    } else {
                        algorithm.displayExpended();
                        stateListener.onExpand();
                        onExpanded();
                    }

                }
            }
        };
        action.setOnClickListener(onActionClick);
        algorithm.init();
        algorithm.displayCollapsed();
    }

    private abstract class Algorithm {

        protected final String titleValue;
        protected final String shortValue;
        protected final String fullValue;

        protected Algorithm(String titleValue, String shortValue, String fullValue) {
            this.titleValue = titleValue;
            this.shortValue = shortValue;
            this.fullValue = fullValue;
        }

        public abstract void init();

        @CallSuper
        protected void displayCollapsed() {
            action.setText(moreValue);
        }

        @CallSuper
        protected void displayExpended() {
            action.setText(lessValue);
        }

        public abstract View getShortContainer();

        public abstract View getFullContainer();
    }

    private class HasShortTextAlgorithm extends Algorithm {

        private HasShortTextAlgorithm(String titleValue, String shortValue, String fullValue) {
            super(titleValue, shortValue, fullValue);
        }

        @Override
        public void init() {
            title.setVisibility(View.GONE);
            shortContainer.loadDataWithBaseURL(null, shortValue, "text/html", "utf-8", null);
            fullContainer.loadDataWithBaseURL(null, fullValue, "text/html", "utf-8", null);
        }

        @Override
        protected void displayCollapsed() {
            super.displayCollapsed();
            shortContainer.setVisibility(View.VISIBLE);
            fullContainer.setVisibility(View.GONE);
        }

        @Override
        protected void displayExpended() {
            super.displayExpended();
            shortContainer.setVisibility(View.GONE);
            fullContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public View getShortContainer() {
            return shortContainer;
        }

        @Override
        public View getFullContainer() {
            return fullContainer;
        }

    }

    private class NoShortTextAlgorithm extends Algorithm {

        private NoShortTextAlgorithm(String titleValue, String shortValue, String fullValue) {
            super(titleValue, shortValue, fullValue);
        }

        @Override
        public void init() {
            shortContainer.setVisibility(View.GONE);
            title.setText(titleValue);
            fullContainer.loadDataWithBaseURL(null, fullValue, "text/html", "utf-8", null);
        }

        @Override
        protected void displayCollapsed() {
            super.displayCollapsed();
            title.setVisibility(View.VISIBLE);
            fullContainer.setVisibility(View.GONE);
        }

        @Override
        protected void displayExpended() {
            super.displayExpended();
            title.setVisibility(View.GONE);
            fullContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public View getShortContainer() {
            return title;
        }

        @Override
        public View getFullContainer() {
            return fullContainer;
        }

    }

    private void init() {
        setOrientation(VERTICAL);
        removeAllViews();
        View v = getView();
        addView(v);
    }

    @LayoutRes
    protected int getLayoutId() {
        return R.layout.layout_innovation_title;
    }

    protected View getView() {
        View result = View.inflate(getContext(), getLayoutId(), null);
        buttonContainer = ButterKnife.findById(result,R.id.buttonContainer);
        /**
         * Отображение данных о вопросе или голосовании
         */
        title =ButterKnife.findById(result,R.id.title);
        fullContainer = ButterKnife.findById(result,R.id.fullContainer);
        fullContainer.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        fullContainer.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        fullContainer.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        /**
         * Задает прозрачный фон для webview, но при отображении контента
         * webview промаргивает черным цветом, поэтому временно отключил
         */

        shortContainer = ButterKnife.findById(result,R.id.shortContainer);
        shortContainer.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        shortContainer.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        shortContainer.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        /**
         * Задает прозрачный фон для webview, но при отображении контента
         * webview промаргивает черным цветом, поэтому временно отключил
         */

        /**
         * Все что относится к  кнопке "Подробнее/свернуть"
         */
        divider = ButterKnife.findById(result,R.id.divider);
        contentContainer = ButterKnife.findById(result,R.id.contentContainer);
        action = ButterKnife.findById(result, R.id.action);

        moreValue = getContext().getString(R.string.title_more);
        lessValue = getContext().getString(R.string.title_less);

        return result;
    }

    private boolean isMoreAction() {
        return moreValue.equalsIgnoreCase(action.getText().toString());
    }

    private void removePaddingFromContent() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentContainer.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        contentContainer.setLayoutParams(params);
    }

    public interface StateListener {
        StateListener STUB = new StateListener() {
            @Override
            public void onExpand() {
            }

            @Override
            public void onCollapse() {
            }
        };

        void onExpand();

        void onCollapse();
    }

}
