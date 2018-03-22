package ru.mos.polls.survey.summary;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.experts.DetailsExpert;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.ui.SurveyFragment;

/**
 * Кастомынй компонент для отображения preview мнений экспертов на экранах
 * списка вопросов голосований {@link ru.mos.polls.survey.SurveySummaryFragment}
 * и на экране вопроса голосования {@link SurveyFragment}<br/>
 * Данные по мнению эксперта описываются моделью {@link DetailsExpert}<br/>
 * Для отображения preview мнения эксперта использовать методу {@link #display(BaseActivity, Survey)}
 * и {@link #display(List)}
 *
 * @since 1.8
 */
public class ExpertsView extends LinearLayout {
    private LinearLayout container;
    private Callback callback = Callback.STUB;

    public ExpertsView(Context context) {
        super(context);
        init();
    }

    public ExpertsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ExpertsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setCallback(Callback callback) {
        if (callback == null) {
            callback = Callback.STUB;
        }
        this.callback = callback;
    }

    public void display(SurveyQuestion surveyQuestion) {
        if (surveyQuestion != null) {
            List<DetailsExpert> detailsExperts = surveyQuestion.getDetailsExperts();
            display(detailsExperts);
        }
    }

    public void display(BaseActivity activity, Survey survey) {
        List<DetailsExpert> detailsExperts = survey.getDetailsExperts();
        display(detailsExperts);
    }

    public void display(List<DetailsExpert> detailsExperts) {
        container.removeAllViews();
        if (detailsExperts != null) {
            if (detailsExperts.size() > 1) {
                /**
                 * несколько мнений экспертов
                 */
                setVisibility(VISIBLE);
                for (DetailsExpert detailsExpert : detailsExperts) {
                    View view = getExpertView(detailsExpert);
                    container.addView(view);
                }
            } else if (detailsExperts.size() == 1) {
                /**
                 * одно мнение эксперта
                 */
                setVisibility(VISIBLE);
                View view = getOnlyOneExpertView(detailsExperts.get(0));
                container.addView(view);
            }
        } else {
            setVisibility(GONE);
        }
    }

    /**
     * Отображаем аватар эксперта н случай, если экспертов много
     *
     * @param detailsExpert
     * @return
     */
    private View getExpertView(final DetailsExpert detailsExpert) {
        final View result = View.inflate(getContext(), R.layout.layout_expert, null);
        final ImageView imageView = ButterKnife.findById(result, R.id.expertAvatar);
        final ProgressBar loadingProgress = ButterKnife.findById(result, R.id.loadingProgress);
        loadAvatar(getContext(), imageView, loadingProgress, detailsExpert);
        imageView.setOnClickListener(expertClickListener(detailsExpert));
        return result;
    }

    /**
     * На случай если эксперт один
     *
     * @param detailsExpert
     * @return
     */
    private View getOnlyOneExpertView(final DetailsExpert detailsExpert) {
        final View result = View.inflate(getContext(), R.layout.layout_one_expert, null);
        displayExpertFio(result, detailsExpert);
        displayExpertDescription(result, detailsExpert);
        final ImageView imageView = ButterKnife.findById(result, R.id.expertAvatar);
        final ProgressBar loadingProgress = ButterKnife.findById(result, R.id.loadingProgress);
        loadAvatar(getContext(), imageView, loadingProgress, detailsExpert);
        result.setOnClickListener(expertClickListener(detailsExpert));
        return result;
    }

    @NonNull
    private OnClickListener expertClickListener(final DetailsExpert detailsExpert) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onChooseExpert(detailsExpert);
            }
        };
    }

    private void displayExpertFio(View view, DetailsExpert detailsExpert) {
        final TextView expertFio = ButterKnife.findById(view, R.id.expertFio);
        expertFio.setText(detailsExpert.getTitle());
    }

    private void displayExpertDescription(View view, DetailsExpert detailsExpert) {
        final TextView expertDescription = ButterKnife.findById(view, R.id.expertDescription);
        expertDescription.setText(detailsExpert.getDescription());
    }

    public static void loadAvatar(final Context context,
                                  final ImageView imageView,
                                  final ProgressBar progressBar,
                                  final DetailsExpert detailsExpert) {
        imageView.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        ImageLoader imageLoader1 = AGApplication.getImageLoader();
        imageLoader1.loadImage(detailsExpert.getImgUrl(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                progressBar.setVisibility(GONE);
                imageView.setVisibility(GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(VISIBLE);
                }
                progressBar.setVisibility(GONE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });

    }

    private void init() {
        setVisibility(GONE);
        /**
         * Инициализация и настройка корневого view group
         */
        View view = View.inflate(getContext(), R.layout.layout_expert_view, null);
        container = ButterKnife.findById(view, R.id.container);
        HorizontalScrollView scrollView = ButterKnife.findById(view, R.id.scrollView);
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onChooseExpert(new DetailsExpert(null));
            }
        });
        scrollView.setHorizontalScrollBarEnabled(false);
        addView(view);
    }

    public interface Callback {
        Callback STUB = new Callback() {
            @Override
            public void onChooseExpert(DetailsExpert detailsExpert) {
            }
        };

        void onChooseExpert(DetailsExpert detailsExpert);
    }
}
