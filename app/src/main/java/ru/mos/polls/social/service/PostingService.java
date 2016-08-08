package ru.mos.polls.social.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.manager.SocialManager;
import ru.mos.polls.social.model.SocialPostValue;

/**
 * Сервис для выполнения постинга в социальной сети
 */
public class PostingService extends Service {
    private static final String EXTRA_POST_VALUE = "extra_post_value";

    public static void start(Context context, SocialPostValue socialPostValue) {
        Intent intent = new Intent(context, PostingService.class);
        intent.putExtra(EXTRA_POST_VALUE, socialPostValue);
        context.startService(intent);
    }

    /**
     * Список объетов, которые нужно запостить
     */
    private List<SocialPostValue> queue;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue = new ArrayList<SocialPostValue>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            executePostingTask(intent);
        } catch (Exception ignored) {
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void executePostingTask(Intent intent) {
        final SocialPostValue socialPostValue
                = (SocialPostValue) intent.getSerializableExtra(EXTRA_POST_VALUE);
        queue.add(socialPostValue);
        new PostingTask(socialPostValue).execute();
    }

    private class PostingTask extends AsyncTask<Void, Void, Exception> {
        private final SocialPostValue socialPostValue;

        PostingTask(SocialPostValue socialPostValue) {
            this.socialPostValue = socialPostValue;
        }

        @Override
        protected Exception doInBackground(Void... params) {
            Exception result = null;
            try {
                SocialManager.post(PostingService.this, socialPostValue);
            } catch (Exception e) {
                result = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Exception result) {
            SocialUIController
                    .sendPostingResult(PostingService.this, socialPostValue, result);
            /**
             * удаление данныХ, которые были заполщены из очереди
             */
            if (queue != null && queue.size() > 0) {
                queue.remove(socialPostValue);
                /**
                 * если данных для постинга больше нет в очереди,
                 * то останавливаем сервис
                 */
                if (queue.size() == 0) {
                    stopSelf();
                }
            }
        }
    }
}
