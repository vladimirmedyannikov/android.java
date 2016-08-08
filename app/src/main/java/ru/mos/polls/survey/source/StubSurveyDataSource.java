package ru.mos.polls.survey.source;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.mos.polls.R;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.parsers.SurveyFactory;

public class StubSurveyDataSource implements SurveyDataSource {

    private Context context;

    public StubSurveyDataSource(Context c) {
        context = c;
    }

    @Override
    public void load(long surveyId, final boolean isHearing, final LoadListener listener) {
        Survey result = null;

        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.survey_bad)));
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject json = new JSONObject(sb.toString());
            result = SurveyFactory.fromJson(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Survey, Void, Survey>() {

            @Override
            protected Survey doInBackground(Survey... params) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return params[0];
            }

            @Override
            protected void onPostExecute(Survey aVoid) {
                super.onPostExecute(aVoid);
                listener.onLoaded(aVoid);
            }
        }.execute(result);

    }

    @Override
    public void save(Survey survey, SaveListener listener, boolean isInterrupted) {
        listener.onSaved(0, 0, null);
    }

}
