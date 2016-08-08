package ru.mos.polls.survey.summary;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mos.polls.R;

/**
 * Кастомный компанент для отображения прогресса прохождения голосования
 */
public class ProgressView extends HorizontalScrollView {
    /**
     * корневой элемент, содержащий прогресс для каждого объекта
     */
    private LinearLayout progressContainer;

    private Drawable progressDrawable, emptyDrawable, markerDrawable;
    private int marker = R.drawable.circle_fill_selected;
    private int empty = R.drawable.circle_none_selected;
    private Map<Object, ImageView> modelToView = new HashMap<Object, ImageView>();

    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(attrs);
        init();
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutParams(attrs);
        init();
    }

    private void setLayoutParams(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProgressView,
                0, 0);

        try {
            progressDrawable = a.getDrawable(R.styleable.ProgressView_drawableProgress);
            emptyDrawable = a.getDrawable(R.styleable.ProgressView_drawableEmpty);
            markerDrawable = a.getDrawable(R.styleable.ProgressView_drawableMarker);
        } finally {
            a.recycle();
        }

    }

    private void init() {
        /**
         * настраиваем horizontal scroll view
         */
        setFillViewport(true);
        setSmoothScrollingEnabled(true);
        /**
         * прячем линию прогресса скролинга
         */
        setHorizontalScrollBarEnabled(false);
        /**
         * инициализация корневого родительского элемента
         */
        progressContainer = new LinearLayout(getContext());
        progressContainer.setOrientation(LinearLayout.HORIZONTAL);
        progressContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        progressContainer.setGravity(Gravity.CENTER);
        addView(progressContainer);
    }

    public void setProgressDrawable(Drawable progressDrawable) {
        this.progressDrawable = progressDrawable;
    }

    public void setEmptyDrawable(Drawable emptyDrawable) {
        this.emptyDrawable = emptyDrawable;
    }

    /**
     * Задаем модель для отображения пргресса
     *
     * @param model список элементов, каждому элементу из списка будет соотвествовать view, отображающий его прогресс
     */
    public void setModel(List<Object> model) {
        progressContainer.removeAllViews();
        for (Object object : model) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
            progressContainer.addView(imageView);
            modelToView.put(object, imageView);
        }
    }

    /**
     * Обновление прогрессов для каждого элемента
     *
     * @param progressable интерфейс, используемый для определения прогресса элемента
     */
    public void invalidate(Progressable progressable) {
        for (Map.Entry<Object, ImageView> entry : modelToView.entrySet()) {
            Object model = entry.getKey();
            ImageView view = entry.getValue();
            Drawable drawable = emptyDrawable;
            if (progressable.hasProgress(model)) {
                drawable = progressDrawable;
            }
            view.setImageDrawable(drawable);
        }
    }

    /**
     * Обновление состояния прогрессов для каждого элемента
     *
     * @param model      список объектов для прогресса
     * @param answerable интерфейс, используемый для определения прогресса каждого элемента
     */
    public void invalidate(List<Object> model, Progressable answerable) {
        setModel(model);
        invalidate(answerable);
    }

    public void setMarkerToObject(int index, Progressable progressable, int markerResourceId) {
        markerDrawable = getContext().getResources().getDrawable(markerResourceId);
        setMarkerToObject(index, progressable);
    }
    public void setMarkerToObject(int index, Progressable progressable, boolean markerResourceId) {
        int currentMarker = empty;
        if (markerResourceId) currentMarker = marker;
        markerDrawable = getContext().getResources().getDrawable(currentMarker);
        setMarkerToObject(index, progressable);
    }
    /**
     * Установка текущего объекта в центре экрана
     * (в центр экрана устанавливается объект, если все объект не омещаются на экране)
     *
     * @param index
     */
    public void setMarkerToObject(final int index, Progressable progressable) {
        /**
         * отрисовываем текущий прогресс по кажому объекту модели
         */
        invalidate(progressable);
        post(new Runnable() {
            @Override
            public void run() {
                /**
                 * получаем текущий view по идексу объекта
                 */
                ImageView view = (ImageView) progressContainer.getChildAt(index);
                /**
                 * устанавливаем маркер для текущего объекта модели
                 */
                if (view != null && markerDrawable != null) {
                    view.setImageDrawable(markerDrawable);
                    /**
                     * скролим вопрос, если его не видно
                     */
                    int screenWidth = ((Activity) getContext()).getWindowManager()
                            .getDefaultDisplay().getWidth();

                    int scrollX = (view.getLeft() - (screenWidth / 2))
                            + (view.getWidth() / 2);
                    smoothScrollTo(scrollX, 0);
                }
            }
        });

    }

    /**
     * Интерфес определения состояния объекта для отображения прогресса
     */
    public interface Progressable {
        boolean hasProgress(Object object);
    }
}
