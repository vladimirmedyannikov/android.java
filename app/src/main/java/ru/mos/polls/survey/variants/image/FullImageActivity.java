package ru.mos.polls.survey.variants.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley2.RequestQueue;
import com.android.volley2.VolleyError;
import com.android.volley2.toolbox.ImageLoader;

import ru.mos.elk.netframework.utils.BitmapLruCache;
import ru.mos.polls.AbstractActivity;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.BitmapHelper;
import ru.mos.polls.helpers.TitleHelper;

public class FullImageActivity extends ToolbarAbstractActivity {

    public static final String EXTRA_URL = "url";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);
        TitleHelper.setTitle(this, getString(R.string.title_view_image));
        imageView = (ImageView) findViewById(R.id.image);
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(url)) {
            finish();
        } else {
            /**
             * Исопльзуем общую очередь зарпосов
             */
            RequestQueue queue = AbstractActivity.RequestQueueManager.getInstance(this).getQueue();
            /**
             * Используем общий кеш для картнок
             */
            BitmapLruCache cache = BitmapHelper.BitmapLruCacheManager.getInstance(5000).getCache();

            ImageLoader imageLoader = new ImageLoader(queue, cache);
            imageLoader.get(url, new ImageLoader.ImageListener() {

                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap btmp = imageContainer.getBitmap();
                    imageView.setImageBitmap(btmp);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    /**
                     * Иногда все ж суда попадаем, не выводим сообщение
                     */
                }

            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
