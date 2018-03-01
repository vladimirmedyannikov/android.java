package ru.mos.polls.profile.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import ru.mos.polls.R;


public class BirthDateParser implements Parcelable {

    private String[] months;
    private String pattern;

    public BirthDateParser(Context context){
        months = context.getResources().getStringArray(R.array.elk_birthdate_months);
        pattern = context.getString(R.string.elk_birthdate_pattern);
    }

    public String parseText(String text){
        String[] elements = text.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        if(elements[0].length()==1)
            stringBuilder.append('0');
        stringBuilder.append(elements[0]);
        stringBuilder.append('.');
        for(int i=0;i<months.length;i++)
            if(months.equals(elements[1])){
                if(i<10)
                    stringBuilder.append('0');
                stringBuilder.append(i);
                stringBuilder.append('.');
                break;
            }
        stringBuilder.append(elements[2]);
        return stringBuilder.toString();
    }

    public String format(String date){
        if(date.length()<5) return date;
        String[] elem = date.split("\\.");
        int month = Integer.parseInt(elem[1]);
        return String.format(pattern, elem[0], months[month - 1], elem[2]);
    }

    public String make(int day, int month, int year) {
        StringBuilder stringBuilder = new StringBuilder();
        if(day<10)
            stringBuilder.append('0');
        stringBuilder.append(day);
        stringBuilder.append('.');
        if(month<10)
            stringBuilder.append('0');
        stringBuilder.append(month);
        stringBuilder.append('.');
        stringBuilder.append(year);
        return stringBuilder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(months);
        dest.writeString(pattern);
    }

    protected BirthDateParser(Parcel source) {
        months = source.createStringArray();
        pattern = source.readString();
    }

    public static final Parcelable.Creator<BirthDateParser> CREATOR = new Parcelable.Creator<BirthDateParser>() {

        @Override
        public BirthDateParser createFromParcel(Parcel source) {
            return new BirthDateParser(source);
        }

        @Override
        public BirthDateParser[] newArray(int size) {
            return new BirthDateParser[size];
        }

    };
}
