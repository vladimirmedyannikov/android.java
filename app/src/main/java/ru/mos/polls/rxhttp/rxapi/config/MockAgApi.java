package ru.mos.polls.rxhttp.rxapi.config;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.reactivex.Observable;
import ru.mos.polls.rxhttp.rxapi.model.base.GeneralResponse;



public abstract class MockAgApi implements AgApi {
    private final Context context;

    private MockAgApi(Context context) {
        this.context = context;
    }

//    @Override
//    public Observable<Response<NoveltySelect>> noveltySelect(@Body Map<String, Object> fields) {
//        return asset(context,
//                "<add_filename_mock_test.json>",
//                NoveltySelect.class);
//    }
//

    private <T> Observable<GeneralResponse<T>> asset(Context context, String fileFromAssets, Class<T> clazz) {
        Observable<GeneralResponse<T>> result = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileFromAssets)));
            GeneralResponse<T> response = new GeneralResponse<>();
            response.setResult(new Gson().fromJson(reader, clazz));
            result = Observable.just(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
