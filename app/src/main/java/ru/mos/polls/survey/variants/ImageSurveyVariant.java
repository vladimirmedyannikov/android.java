package ru.mos.polls.survey.variants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.IntRange;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.survey.StatusProcessor;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.variants.image.FullImageActivity;

/**
 * Отображает картинку
 */
public class ImageSurveyVariant extends SurveyVariant {

    private final String text;
    private final String[] src;
    private TextView tv;

    public ImageSurveyVariant(String backId, long innerId, @IntRange(from = PERCENT_NOT_SET, to = 100) int percent, int voters, String text, String[] src) {
        super(backId, innerId, percent, voters);
        this.text = text;
        this.src = src;
    }

    public String getText() {
        return text;
    }

    @Override
    protected View onGetView(final Context context, StatusProcessor statusProcessor) {
        View v = View.inflate(context, R.layout.survey_variant_image, null);
        statusProcessor.process(v);
        tv = (TextView) v.findViewById(R.id.text);
        statusProcessor.process(tv);
        tv.setText(text);
        statusProcessor.processChecked(tv, this);

        final String url = src[0];
        final ImageView imageView = (ImageView) v.findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullImageActivity(context, url);
                getListener().performParentClick();
            }
        });
        ImageLoader imageLoader = AGApplication.getImageLoader();
        imageLoader.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                imageView.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });
        return v;
    }

    protected void showFullImageActivity(Context context, String url) {
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra(FullImageActivity.EXTRA_URL, url);
        context.startActivity(intent);
    }

    @Override
    public void onClick(Activity context, Fragment fragment, boolean checked) {
        statusProcessor.processChecked(tv, this);
        getListener().onClicked();
    }

    @Override
    public void verify() throws VerificationException {
        //Ничего не делает, так как это вариант опроса всегда корректен
    }

    @Override
    protected void processAnswerJson(JSONObject jsonObject) {
        //ничего не делает
    }

    @Override
    public void loadAnswerJson(JSONObject answerJsonObject) {
        //ничего не делает
    }

    @Override
    public boolean onActivityResultOk(Intent data) {
        //не предусмотрено для данного класса
        return false;
    }

    @Override
    public boolean onActivityResultCancel(Intent data) {
        //не предусмотрено для данного класса
        return false;
    }

    @Override
    public String toString() {
        return "image " + text + " " + isChecked();
    }

    public static class Factory extends SurveyVariant.Factory {

        @Override
        protected SurveyVariant onCreate(JSONObject jsonObject, long surveyId, long questionId, String variantId, long innerVariantId, int percent, int voters) {
            final String text = jsonObject.optString("text");
            JSONArray srcJsonArray = jsonObject.optJSONArray("src");
            String[] srcs = new String[srcJsonArray.length()];
            for (int i = 0; i < srcJsonArray.length(); i++) {
                String src = srcJsonArray.optString(i);
                srcs[i] = src;
            }
            final SurveyVariant result = new ImageSurveyVariant(variantId, innerVariantId, percent, voters, text, srcs);
            return result;
        }
    }

}
