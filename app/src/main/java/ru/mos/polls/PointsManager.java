package ru.mos.polls;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;


import ru.mos.elk.Constants;
import ru.mos.polls.db.PollsData;
import ru.mos.polls.db.PollsProvider;
import ru.mos.polls.model.PointHistory;
import ru.mos.polls.model.UserPoints;


public abstract class PointsManager {

    public static final String PREFS = "point_history_prefs";

    public static final String PREF_STATUS = "pref_state";
    public static final String PREF_BURNT_POINTS = "pref_burnt_points";
    public static final String PREF_SPENT_POINTS = "pref_spent_points";
    public static final String PREF_ALL_POINTS = "pref_all_points";
    public static final String PREF_CURRENT_POINTS = "pref_current_points";

    public static final String EXPIRE_HISTORY_TIME = "point_history_expire_time";
    public static final String EXPIRE_USER_POINTS_TIME = "user_points_time";

    private static boolean isHistoryLoad = false;
    private static boolean isUserPointsLoad = false;

//    /**
//     * обновляем опросы с сервера
//     */
//    public static synchronized void refreshPointHistory(final BaseActivity activity, final Response.Listener<Object> listener, final Response.ErrorListener errorListener) {
//        if (isHistoryLoad) return;
//        isHistoryLoad = true;
//
//        final ProgressDialog pd = new ProgressDialog(activity);
//        pd.setCancelable(false);
//        pd.show();
//
//        activity.setSupportProgressBarIndeterminateVisibility(true);
//        Response.Listener<PointHistory[]> mainListener = new Response.Listener<PointHistory[]>() {
//            @Override
//            public void onResponse(PointHistory[] pointHistories) {
//                isHistoryLoad = false;
//                activity.setSupportProgressBarIndeterminateVisibility(false);
//                ContentResolver cr = activity.getContentResolver();
//                cr.delete(PollsProvider.getContentUri(PollsData.TPointHistory.URI_CONTENT), null, null);
//
//                final Uri insertUri = PollsProvider.getContentUri(PollsData.TPointHistory.TABLE_NAME);
//                ContentValues cv;
//                for (PointHistory pointHistory : pointHistories) {
//                    cv = new ContentValues();
//                    cv.put(TPointHistory.TITLE, pointHistory.getTitle());
//                    cv.put(TPointHistory.WRITE_OFF_DATE, pointHistory.getWriteOffDate());
//                    cv.put(TPointHistory.POINTS, pointHistory.getPoints());
//                    cv.put(TPointHistory.REFILL, pointHistory.isRefill() ? 1 : 0);
//                    cv.put(TPointHistory.ACTION, pointHistory.getAction().toString());
//                    cr.insert(insertUri, cv);
//                }
//                SharedPreferences prefs = activity.getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
//                prefs.edit().putLong(EXPIRE_HISTORY_TIME, System.currentTimeMillis() + 20L * Constants.MINUTE).commit();
//
//                if (pd != null) {
//                    pd.dismiss();
//                }
//
//                if (listener != null)
//                    listener.onResponse(pointHistories);
//            }
//        };
//        Response.ErrorListener errListener = new StandartErrorListener(activity, R.string.cant_load_point_history) {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                super.onErrorResponse(error);
//                isHistoryLoad = false;
//
//                if (pd != null) {
//                    pd.dismiss();
//                }
//
//                if (errorListener != null)
//                    errorListener.onErrorResponse(error);
//            }
//        };
//        activity.addRequest(new PointHistoryListRequest(activity, API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.GET_HISTORY)), null, mainListener, errListener));
//    }

//    public static synchronized void refreshUserPoints(final BaseActivity activity, final Response.Listener<Void> outListener) {
//        isUserPointsLoad = true;
//        activity.setSupportProgressBarIndeterminateVisibility(true);
//        Response.Listener<Void> listener = new Response.Listener<Void>() {
//            @Override
//            public void onResponse(Void v) {
//                isUserPointsLoad = false;
//                activity.setSupportProgressBarIndeterminateVisibility(false);
//
//                if (outListener != null)
//                    outListener.onResponse(v);
//            }
//        };
//        Response.ErrorListener errListener = new StandartErrorListener(activity, R.string.cant_load_user_points) {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                super.onErrorResponse(error);
//                isUserPointsLoad = false;
//            }
//        };
//        activity.addRequest(new PointsRequest<Void>(activity, API.getURL(UrlManager.url(UrlManager.Controller.POLL, UrlManager.Methods.GET_POINTS)), null, listener, errListener));
//    }

    public static void storePoints(Context context, UserPoints userPoints) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_BURNT_POINTS, userPoints.getBurntPoints());
        editor.putInt(PREF_SPENT_POINTS, userPoints.getSpentPoints());
        editor.putInt(PREF_ALL_POINTS, userPoints.getAllPoints());
        editor.putInt(PREF_CURRENT_POINTS, userPoints.getCurrentPoints());
        editor.putLong(EXPIRE_USER_POINTS_TIME, System.currentTimeMillis() + 20L * Constants.MINUTE);
        editor.putString(PREF_STATUS, userPoints.getStatus()).commit();
    }

    public static String getStatus(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(PREF_STATUS, "");
    }

    public static int getPoints(Context context, PointHistory.Action action) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
        int result = prefs.getInt(PREF_CURRENT_POINTS, 0);
        if (action != null) {
            if ("debit".equalsIgnoreCase((action.toString()))) {
                result = prefs.getInt(PREF_ALL_POINTS, 0);
            } else if ("credit".equalsIgnoreCase(action.toString())) {
                result = prefs.getInt(PREF_SPENT_POINTS, 0);
            } else if ("blocked".equalsIgnoreCase(action.toString())) {
                result = prefs.getInt(PREF_BURNT_POINTS, 0);
            }
        }
        return result;
    }

    public static boolean isExpired(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
        long expired = prefs.getLong(PointsManager.EXPIRE_HISTORY_TIME, -1L);
        return expired < System.currentTimeMillis();
    }

    public static void clearPointsAndHistory(Context context) {
        ContentResolver cr = context.getContentResolver();
        cr.delete(PollsProvider.getContentUri(PollsData.TPointHistory.URI_CONTENT), null, null);

        context.getSharedPreferences(PREFS, Activity.MODE_PRIVATE).edit().clear().commit();
    }

    public static String getPointUnitString(Context context, long count) {
        final String countStr = String.valueOf(count);
        if (countStr.length() >= 2) {
            final int endNum = Integer.parseInt(countStr.substring(countStr.length() - 2));
            if (endNum > 10 && endNum < 15) return context.getString(R.string.point_var_2);
        }
        if (countStr.endsWith("1")) return context.getString(R.string.point_var_1);
        if (countStr.endsWith("2") || countStr.endsWith("3") || countStr.endsWith("4"))
            return context.getString(R.string.point_var_3);
        return context.getString(R.string.point_var_2);
    }

    public static String getSuitableString(Context context, int stringArrayResId, int count) {
        int ending = getNumEnding(count);
        String[] arr = context.getResources().getStringArray(stringArrayResId);
        String result = arr[ending];
        return result;
    }

    public static String getMessage(Context context, int price, int currentPoints) {
        if (price == 0) return context.getString(R.string.novelty_result_send_share);
        String suitableString1 = PointsManager.getSuitableString(context, R.array.survey_done_message_1, price);
        String message1 = String.format(suitableString1, price);
        String suitableString2 = PointsManager.getSuitableString(context, R.array.survey_done_message_2, currentPoints);
        String message2 = String.format(suitableString2, currentPoints);
        return message1 + message2;
    }

    public static String getMessageForPoll(Context context, int price, int currentPoints) {
        String message3 = context.getString(R.string.survey_dialog_for_share_message);
        return getMessage(context, price, currentPoints) + "\n" + message3;
    }

    /**
     * @return 0 - один балл, яблоко
     * 1 - два балла, яблока
     * 2 - пять баллов, яблок
     */
    private static int getNumEnding(int number) {
        final int result;
        number = number % 100;
        if (number >= 11 && number <= 19) {
            result = 2;
        } else {
            final int i = number % 10;
            switch (i) {
                case 1:
                    result = 0;
                    break;
                case 2:
                case 3:
                case 4:
                    result = 1;
                    break;
                default:
                    result = 2;
            }
        }
        return result;
    }


}
