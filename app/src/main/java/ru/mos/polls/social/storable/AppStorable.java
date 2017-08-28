package ru.mos.polls.social.storable;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.mos.polls.social.model.AppSocial;
import ru.mos.social.controller.ValidatorController;
import ru.mos.social.manager.BaseSharedPreferencesStorableManager;
import ru.mos.social.model.Token;

import static ru.mos.social.controller.SocialController.isDbg;

/**
 * Created by matek3022 on 28.08.17.
 */

public class AppStorable extends BaseSharedPreferencesStorableManager {
    private ValidatorController validatorController;

    public AppStorable(Context context) {
        super(context);
        validatorController = new ValidatorController();
    }

    @Override
    public AppSocial get(int id) {
        validatorController.checkOrThrowException(id);
        AppSocial appSocial;
        String saved = prefs.getString(String.valueOf(id), "");
        try {
            JSONObject socialJson = new JSONObject(saved);
            appSocial = new AppSocial(socialJson);
        } catch (JSONException e) {
            appSocial = new AppSocial(id, AppSocial.getSocialName(id), 0, new Token());
            if (isDbg()) e.printStackTrace();
        }
        return appSocial;
    }

    @Override
    public List<AppSocial> getAll() {
        List<AppSocial> result = new ArrayList<>();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (isDbg()) Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            try {
                JSONObject socialJson = new JSONObject(entry.getValue().toString());
                AppSocial social = new AppSocial(socialJson);

                /**
                 * т.к. в OpenCity нет (и пока не планируется) Google+
                 * и при его вытаскивании из списка подтирается (т.к. реализованы только 4 места)
                 * какая - нибудь другая соц сеть
                 */
                if (social.getId()!= AppSocial.ID_GP) {
                    result.add(social);
                }
            } catch (JSONException e) {
                if (isDbg()) e.printStackTrace();
            }
        }
        return result;
    }
}
