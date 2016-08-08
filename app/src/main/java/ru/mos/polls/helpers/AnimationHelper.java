package ru.mos.polls.helpers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import ru.mos.polls.R;


public class AnimationHelper {
    public static void expand(View view) {
        expand(view, null);
    }

    public static void expand(final View view, Animation.AnimationListener listener) {
        view.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setInterpolator(new DecelerateInterpolator());
        // 1dp/ms
        animation.setDuration((long) (targetHeight / view.getContext().getResources().getDisplayMetrics().density) * 2);
        if (listener != null) {
            animation.setAnimationListener(listener);
        }
        view.startAnimation(animation);
    }

    public static void collapse(View view) {
        collapse(view, null);
    }

    public static void collapse(final View view, Animation.AnimationListener listener) {
        final int initialHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setInterpolator(new DecelerateInterpolator());
        // 1dp/ms
        animation.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density) * 2);
        if (listener != null) {
            animation.setAnimationListener(listener);
        }
        view.startAnimation(animation);
    }

    public static void fadeInAnimate(View view, Animation.AnimationListener listener) {
        fadeInAnimate(view, 250, listener);
    }

    public static void fadeOutAnimate(View view, Animation.AnimationListener listener) {
        fadeOutAnimate(view, 250, listener);
    }

    public static void fadeInAnimate(View view, int durationMillis, Animation.AnimationListener listener) {
        if (view != null) {
            AlphaAnimation animation = new AlphaAnimation(0, 1);
            animation.setDuration(durationMillis);
            if (listener != null) {
                animation.setAnimationListener(listener);
            }
            animation.setInterpolator(new DecelerateInterpolator());
            view.startAnimation(animation);
        }
    }

    public static void fadeOutAnimate(View view, int durationMillis, Animation.AnimationListener listener) {
        if (view != null) {
            AlphaAnimation animation = new AlphaAnimation(1, 0);
            animation.setDuration(durationMillis);
            if (listener != null) {
                animation.setAnimationListener(listener);
            }
            animation.setInterpolator(new DecelerateInterpolator());
            view.startAnimation(animation);
        }
    }

    public static void rotateToDownArrowDetails(Context context, final ImageView view) {
        RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_to_down_details_label);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setImageResource(R.drawable.rotate_icon);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(rotate);
    }

    public static void rotateToUpArrowDetails(Context context, final ImageView view) {
        RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_to_up_details_label);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setImageResource(R.drawable.rotate_icon_up);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(rotate);
    }
}
