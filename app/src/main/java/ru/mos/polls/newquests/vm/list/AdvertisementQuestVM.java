package ru.mos.polls.newquests.vm.list;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.QuestHtmlBannerBinding;
import ru.mos.polls.newquests.adapter.QuestsItemAdapter;
import ru.mos.polls.newquests.model.quest.AdvertisementQuest;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class AdvertisementQuestVM extends RecyclerBaseViewModel<AdvertisementQuest, QuestHtmlBannerBinding> {
    public AdvertisementQuestVM(AdvertisementQuest model, QuestHtmlBannerBinding viewDataBinding) {
        super(model, viewDataBinding);
    }

    public AdvertisementQuestVM(AdvertisementQuest model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.quest_html_banner;
    }

    @Override
    public int getViewType() {
        return QuestsItemAdapter.BANNER;
    }

    @Override
    public void onBind(QuestHtmlBannerBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        final WebView webView = viewDataBinding.webView;
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        final View front = viewDataBinding.front;
        final View back = ButterKnife.findById(viewDataBinding.getRoot(), R.id.back);//Binding не видит вьюху, которая include
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
        webView.loadDataWithBaseURL(null, model.getHtml(), null, "utf-8", null);

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
                            // TODO: 07.12.17 переделать на бродкаст
//                            UrlSchemeController.start(context, model.getUrlScheme());
//                            QuestsApiController.hide((BaseActivity) context, ru.mos.polls.quests.quest.AdvertisementQuest.this, null);
                        }
                        break;

                }
                return false;
            }
        });
    }
}
