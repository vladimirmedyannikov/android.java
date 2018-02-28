package ru.mos.polls.base.databindingadapters;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class DataBindingAdapters {

    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }

    @BindingAdapter("imageSrc")
    public static void setImageResource(ImageView view, int resource) {
        view.setImageResource(resource);
    }
}
