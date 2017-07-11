package ru.mos.polls.newprofile.base.databindingadapters;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Trunks on 23.06.2017.
 */

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
