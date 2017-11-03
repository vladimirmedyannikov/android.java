package ru.mos.polls.innovations.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.innovations.oldmodel.InnovationActiviti;
import ru.mos.polls.innovations.oldmodel.Rating;

/**
 * Исопльзуется на экране детальной информации о городской новинке {@link ru.mos.polls.innovations.ui.activity.InnovationActivity}
 * Компонент может быть настроен через xml разметку. Можно задавать:
 * 1) цвет каждой горизонтальной диаграммы
 * 2) высоту и ширину диаганальной диаграммы
 *
 * @since 1.9
 */
public class ChartsView extends LinearLayout {
    public static final int DEFAULT_MAX_WIDTH_DP_BARS = 70;
    public static final int DEFAULT_HEIGHT_DP_BAR = 35;

    @BindView(R.id.fullRating) TextView fullRating;
    @BindView(R.id.fullCount) TextView fullCounts;
    @BindViews({R.id.countOneMark, R.id.countTwoMark, R.id.countThreeMark, R.id.countFourMark, R.id.countFiveMark})
    List<TextView> countMark;
    @BindViews({R.id.barForOne, R.id.barForTwo, R.id.barForThree, R.id.barForFour, R.id.barForFive})
    List<LinearLayout> linearForBar;
    @BindViews({R.id.barOne, R.id.barTwo, R.id.barThree, R.id.barFour, R.id.barFive})
    List<LinearLayout> bars;
    private float scale;
    /**
     * цвета для прогресс баров: 0 - первый прогресс бар и т.д.
     */
    private int[] colors;

    /**
     * ширина и высота прогресс баров
     */
    private int barWidth;
    private int barHeight;
    /**
     * максимально допустимая ширина диаграммы
     */
    private int maxBarWidth;

    public ChartsView(Context context) {
        super(context);
        init();
    }

    public ChartsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setLayoutParams(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ChartsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        setLayoutParams(attrs);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    private void setLayoutParams(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ChartsView,
                0, 0);
        try {
            barHeight = a.getInt(R.styleable.ChartsView_bar_height, DEFAULT_HEIGHT_DP_BAR);
            barWidth = a.getInt(R.styleable.ChartsView_bar_width, DEFAULT_MAX_WIDTH_DP_BARS);
            barWidth = (int) (barWidth * scale);
            setBarColor(0, R.styleable.ChartsView_color_bar_one, a);
            setBarColor(1, R.styleable.ChartsView_color_bar_two, a);
            setBarColor(2, R.styleable.ChartsView_color_bar_three, a);
            setBarColor(3, R.styleable.ChartsView_color_bar_four, a);
            setBarColor(4, R.styleable.ChartsView_color_bar_five, a);
        } finally {
            a.recycle();
        }

    }

    private void setBarColor(int index, int styleable, TypedArray a) {
        int color = a.getColor(styleable, -1);
        if (color != -1) {
            colors[index] = color;
        }
    }

    public void display(InnovationActiviti innovationActiviti) {
        display(innovationActiviti.getRating());
    }

    public void display(Rating rating) {
        maxBarWidth = getMaxBarWidth();
        if (rating != null) {
            displayFull(rating);
            if (hasCounts(rating)) {
                displayMarksBars(rating);
                displayMarksCounts(rating);
            }
        }
    }

    private boolean hasCounts(Rating rating) {
        return rating.getCounts() != null && rating.getCounts().length == 5;
    }

    private void displayFull(Rating rating) {
        fullRating.setText(String.format("%.1f", rating.getFullRating()));
        fullCounts.setText(rating.getFormattedFullCount(getContext()));
    }

    private void displayMarksCounts(Rating rating) {
        int i = countMark.size() - 1;
        for (TextView mark : countMark) {
            mark.setText(String.valueOf(rating.getCounts()[i]));
            i--;
        }
    }

    private void displayMarksBars(Rating rating) {
        int i = linearForBar.size() - 1;
        for (LinearLayout linearBar : linearForBar) {
            setBarWidth(linearBar, rating.getCounts()[i], rating);
            setBarColor(linearBar, i);
            i--;
        }
        for (LinearLayout bar : bars) {
            setBarWidth(bar, rating.getMaxCount(), rating);
        }
    }

    private void setBarColor(View bar, int colorIndex) {
        bar.setBackgroundColor(colors[colorIndex]);
    }

    private void setBarWidth(View bar, int count, Rating rating) {
        setBarWidth(bar, rating.getMaxCount(), count);
    }

    private void setBarWidth(View bar, int fullCount, int count) {
        int width = calculateWidth(fullCount, count);
        RelativeLayout.LayoutParams layoutParams
                = new RelativeLayout.LayoutParams(width, (int) (barHeight * scale));
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        bar.setLayoutParams(layoutParams);
    }

    /**
     * Рассчет ширины горизонтальной зиаграммы
     * 1) Максимальная ширина диаграммы оценки - это максимальное количество
     * пикселей для оценки с наибольшим количеством голосов;
     * 2) Максимальная возможная ширина {@link #barWidth} задается в настройках в xml,
     * она может превышать ширину экрана, поэтому, при расчете {@link #barWidth} учитываем ограничение
     * {@link #maxBarWidth}, см {@link #getMaxBarWidth()}
     * 2) если рассчетная ширина диаграммы для оценки оказалось меньше 1 пикселя,
     * то рисуем 1 пиксель для данной оценки, иначе линии не будет видно;
     * 3) если количество голосов нуль, то рисуем полоску в 1 пиксель
     *
     * @param fullCount - количество голосов, максимальное для одной из оценок 1, 2, 3, 4, 5
     * @param count     количество голосов для данной оценки
     * @return
     */
    private int calculateWidth(int fullCount, int count) {
        if (barWidth > maxBarWidth) {
            barWidth = maxBarWidth;
        }
        int width = (int) (barWidth * ((double) count / fullCount));
        if (width <= 0) {
            width = (int) scale + 1;
        } else {
            width = (int) (width * scale);
        }

        return width;
    }

    private void init() {
        colors = new int[]{
                getResources().getColor(R.color.rating_orange),
                getResources().getColor(R.color.rating_orange),
                getResources().getColor(R.color.rating_orange),
                getResources().getColor(R.color.rating_orange),
                getResources().getColor(R.color.rating_orange)
        };
        setOrientation(VERTICAL);
        removeAllViews();
        View v = getView();
        addView(v);
        scale = getResources().getDisplayMetrics().density;
    }

    /**
     * Рассчет максимально возможной длины диаграммы
     * Диаграмма не должна превышать 1/5 ширины экрана
     *
     * @return максимальная ширина диаграммы
     */
    private int getMaxBarWidth() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / (displayMetrics.density * 2));
    }

    private View getView() {
        View result = View.inflate(getContext(), R.layout.layout_charts_view, null);
        return result;
    }
}
