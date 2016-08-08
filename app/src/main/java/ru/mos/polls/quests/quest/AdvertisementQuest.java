package ru.mos.polls.quests.quest;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONObject;

import butterknife.ButterKnife;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.quests.QuestsFragment;
import ru.mos.polls.quests.controller.QuestsApiController;

/**
 * Отображение баннеров в главной ленте
 *
 * @since 1.9
 */
public class AdvertisementQuest extends BackQuest {
    public static final String TYPE = "advertisement";
    public static final String TEXT_HTML = "text_html";
    private String html;

    public AdvertisementQuest(long innerId, JSONObject jsonObject) {
        super(innerId, jsonObject);
        html = jsonObject.optString(TEXT_HTML);
    }

    @Override
    public View inflate(final Context context, View convertView) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.quest_html_banner, null);
        }

        final WebView webView = ButterKnife.findById(convertView, R.id.webView);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        final View front = ButterKnife.findById(convertView, R.id.front);
        final View back = ButterKnife.findById(convertView, R.id.back);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                front.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                front.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
            }
        });
        webView.loadDataWithBaseURL(null, html, null, "utf-8", null);

        /**
         * Невероятный кастыль обработки клика
         * WebView в ListView - оч плохая идея, сам список тормозит!
         * плюс он не кликабельный! поэтому пришлось реализовать обработку клика
         * через onTouch
         */
        webView.setOnTouchListener(new View.OnTouchListener() {
            boolean isClick = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isClick = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isClick = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isClick) {
                            UrlSchemeController.start(context, getUrlScheme());
                            QuestsApiController.hide((BaseActivity) context, AdvertisementQuest.this, null);
                        }
                        break;

                }
                return false;
            }
        });
        return convertView;
    }

    @Override
    public void onClick(Context context, QuestsFragment.Listener listener) {
        super.onClick(context, listener);
    }
}
