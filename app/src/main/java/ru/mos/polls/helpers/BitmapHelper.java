package ru.mos.polls.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.widget.ImageView;

import ru.mos.elk.netframework.utils.BitmapLruCache;


public abstract class BitmapHelper {
    /**
     * Асинхронный метод получения круглой картинки
     *
     * @param view     view для отображения картинки
     * @param bitmap   исходная картинка
     * @param listener callback для обрабтки начала и окончания процесса закругления картинки
     */
    public static void getCircleImage(final ImageView view, final Bitmap bitmap, final RoundedTaskListener listener) {
        if (bitmap != null) {
            int radius = bitmap.getWidth() / 2;
            if (bitmap.getHeight() <= bitmap.getWidth()) {
                radius = bitmap.getHeight() / 2;
            }
            getRoundedImage(view, bitmap, radius, listener);
        }
    }

    /**
     * Асинхронный метод скругления bitmap
     *
     * @param view         view для отображения картинки
     * @param bitmap       исходная картинка
     * @param radiusCorner радиус закругления углов картнки
     * @param listener     callback для обрабтки начала и окончания процесса закругления картинки
     */
    public static void getRoundedImage(ImageView view, Bitmap bitmap, int radiusCorner, RoundedTaskListener listener) {
        if (bitmap != null && view != null && radiusCorner > 0) {
            RoundedImageTask roundedImageTask = new RoundedImageTask(view, bitmap, radiusCorner, listener);
            roundedImageTask.execute();
        }
    }

    /**
     * Поток для скругления bitmap
     */
    private static class RoundedImageTask extends AsyncTask<Void, Void, Bitmap> {
        private Bitmap bitmap;
        private ImageView imageView;
        private int radiusCorner;
        private RoundedTaskListener listener;

        RoundedImageTask(ImageView imageView, Bitmap bitmap, int radiusCorner, RoundedTaskListener listener) {
            this.imageView = imageView;
            this.bitmap = bitmap;
            this.radiusCorner = radiusCorner;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            if (listener != null) {
                listener.onPreExecute();
            }
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap result = null;
            try {
                result = roundCorners(bitmap, imageView, radiusCorner);
            } catch (Exception ignored) {

            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (listener != null) {
                listener.onComplete(bitmap);
            }
        }

        public Bitmap roundCorners(Bitmap bitmap, ImageView imageView, int roundPixels) {
            Bitmap roundBitmap;

            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            int vw = imageView.getWidth();
            int vh = imageView.getHeight();
            if (vw <= 0) vw = bw;
            if (vh <= 0) vh = bh;

            int width, height;
            Rect srcRect;
            Rect destRect;
            switch (imageView.getScaleType()) {
                case CENTER_INSIDE:
                    float vRation = (float) vw / vh;
                    float bRation = (float) bw / bh;
                    int destWidth;
                    int destHeight;
                    if (vRation > bRation) {
                        destHeight = Math.min(vh, bh);
                        destWidth = (int) (bw / ((float) bh / destHeight));
                    } else {
                        destWidth = Math.min(vw, bw);
                        destHeight = (int) (bh / ((float) bw / destWidth));
                    }
                    int x = (vw - destWidth) / 2;
                    int y = (vh - destHeight) / 2;
                    srcRect = new Rect(0, 0, bw, bh);
                    destRect = new Rect(x, y, x + destWidth, y + destHeight);
                    width = vw;
                    height = vh;
                    break;
                case FIT_CENTER:
                case FIT_START:
                case FIT_END:
                default:
                    vRation = (float) vw / vh;
                    bRation = (float) bw / bh;
                    if (vRation > bRation) {
                        width = (int) (bw / ((float) bh / vh));
                        height = vh;
                    } else {
                        width = vw;
                        height = (int) (bh / ((float) bw / vw));
                    }
                    srcRect = new Rect(0, 0, bw, bh);
                    destRect = new Rect(0, 0, width, height);
                    break;
                case CENTER_CROP:
                    vRation = (float) vw / vh;
                    bRation = (float) bw / bh;
                    int srcWidth;
                    int srcHeight;
                    if (vRation > bRation) {
                        srcWidth = bw;
                        srcHeight = (int) (vh * ((float) bw / vw));
                        x = 0;
                        y = (bh - srcHeight) / 2;
                    } else {
                        srcWidth = (int) (vw * ((float) bh / vh));
                        srcHeight = bh;
                        x = (bw - srcWidth) / 2;
                        y = 0;
                    }
                    width = srcWidth;// Math.min(vw, bw);
                    height = srcHeight;//Math.min(vh, bh);
                    srcRect = new Rect(x, y, x + srcWidth, y + srcHeight);
                    destRect = new Rect(0, 0, width, height);
                    break;
                case FIT_XY:
                    width = vw;
                    height = vh;
                    srcRect = new Rect(0, 0, bw, bh);
                    destRect = new Rect(0, 0, width, height);
                    break;
                case CENTER:
                case MATRIX:
                    width = Math.min(vw, bw);
                    height = Math.min(vh, bh);
                    x = (bw - width) / 2;
                    y = (bh - height) / 2;
                    srcRect = new Rect(x, y, x + width, y + height);
                    destRect = new Rect(0, 0, width, height);
                    break;
            }

            try {
                roundBitmap = getRoundedCornerBitmap(bitmap, roundPixels, srcRect, destRect, width, height);
            } catch (OutOfMemoryError e) {
                roundBitmap = bitmap;
            }

            return roundBitmap;
        }

        private Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixels, Rect srcRect, Rect destRect, int width, int height) {
            Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            if (output != null) {
                Canvas canvas = new Canvas(output);
                final Paint paint = new Paint();
                final RectF destRectF = new RectF(destRect);
                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(0xFF000000);
                canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, srcRect, destRectF, paint);
            }
            return output;
        }
    }

    /**
     * Один объект для кеша картинок
     */
    public static class BitmapLruCacheManager {
        public static BitmapLruCacheManager instance;

        private BitmapLruCache cache;

        private BitmapLruCacheManager() {
            if (cache == null) {
                cache = new BitmapLruCache();
            }
        }

        private BitmapLruCacheManager(int sizeInKiloBytes) {
            if (cache == null) {
                cache = new BitmapLruCache(sizeInKiloBytes);
            }
        }

        public static synchronized BitmapLruCacheManager getInstance() {
            if (instance == null) {
                instance = new BitmapLruCacheManager();
            }
            return instance;
        }

        public static synchronized BitmapLruCacheManager getInstance(int sizeInKiloBytes) {
            if (instance == null) {
                instance = new BitmapLruCacheManager(sizeInKiloBytes);
            }
            return instance;
        }

        public BitmapLruCache getCache() {
            return cache;
        }

        public void clearCache() {
            if (cache != null) {
                cache.evictAll();
            }
        }
    }

    public interface RoundedTaskListener {
        void onPreExecute();

        void onComplete(Bitmap roundedBitmap);
    }
}
