package ru.mos.polls.survey.hearing.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.common.model.Position;

/**
 * Структура данных, описывающая экспозицию публичного слушания
 *
 * @since 2.0
 */
public class Exposition implements Serializable {
    public static List<Exposition> from(JSONArray jsonArray) {
        List<Exposition> result = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                result.add(new Exposition(jsonArray.optJSONObject(i)));
            }
        }
        return result;
    }

    private long id;
    private long startDate, endDate;
    private String workingHours;
    private String referenceUrl;
    private List<String> contactsEmail;
    private List<String> contactsPhones;
    private String address;
    private String addressDistrict;
    private Position position;

    public Exposition(JSONObject json) {
        if (json != null) {
            id = json.optLong("id");
            startDate = json.optLong("start_date") * 1000;
            endDate = json.optLong("end_date") * 1000;
            workingHours = json.optString("working_hours");
            referenceUrl = json.optString("reference_url");
            contactsPhones = asList(json.optJSONArray("contact_phones"));
            contactsEmail = asList(json.optJSONArray("contact_emails"));
            address = json.optString("address");
            addressDistrict = json.optString("address_district_committie");
            position = new Position(json.optJSONObject("position"));
        }
    }

    public long getId() {
        return id;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressDistrict() {
        return addressDistrict;
    }

    public Position getPosition() {
        return position;
    }

    public List<String> getContactsEmail() {
        return contactsEmail;
    }

    public List<String> getContactsPhones() {
        return contactsPhones;
    }

    public static void callDial(Context context, String phone) {
        Intent calling = new Intent(Intent.ACTION_DIAL);
        calling.setData(Uri.parse(String.format("tel:%s", phone)));
        try {
            context.startActivity(calling);
        } catch (Exception ignored) {
        }
    }

    private List<String> asList(JSONArray array) {
        List<String> result = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); ++i) {
                result.add(array.optString(i));
            }
        }
        return result;
    }
}
