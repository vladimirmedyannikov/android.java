package ru.mos.polls.poll.model;

import ru.mos.polls.R;


public enum Kind {
    STANDART("standart", "", android.R.color.transparent),
    HEARING_PREVIEW("hearing_preview", "публичное слушание", R.color.public_poll),
    HEARING("hearing", "публичное слушание", R.color.greenText),
    SPECIAL("special", "специальное голосование", R.color.special_poll);

    public String kind;
    public String label;
    public int color;

    public static Kind parse(String kind) {
        Kind result = null;
        if (STANDART.getKind().equalsIgnoreCase(kind)) {
            result = STANDART;
        } else if (HEARING_PREVIEW.getKind().equalsIgnoreCase(kind)) {
            result = HEARING_PREVIEW;
        } else if (HEARING.getKind().equalsIgnoreCase(kind)) {
            result = HEARING;
        } else if (SPECIAL.getKind().equalsIgnoreCase(kind)) {
            result = SPECIAL;
        }
        return result;
    }

    Kind(String kind, String label, int color) {
        this.kind = kind;
        this.label = label;
        this.color = color;
    }

    public boolean isStandart() {
        return STANDART.getKind().equalsIgnoreCase(kind);
    }

    public boolean isHearing() {
        return HEARING.getKind().equalsIgnoreCase(kind);
    }

    public static boolean isHearing(String kind) {
        return kind.equalsIgnoreCase(HEARING.kind);
    }

    public boolean isHearingPreview() {
        return HEARING_PREVIEW.getKind().equalsIgnoreCase(kind);
    }
    public static boolean isHearingPreview(String kind) {
        return HEARING_PREVIEW.getKind().equalsIgnoreCase(kind);
    }
    public boolean isSpecial() {
        return SPECIAL.getKind().equalsIgnoreCase(kind);
    }
    public static boolean isSpecial(String kind) {
        return SPECIAL.getKind().equalsIgnoreCase(kind);
    }
    public int getColor() {
        return color;
    }

    public String getLabel() {
        return label;
    }

    public String getKind() {
        return kind;
    }

    @Override
    public String toString() {
        return kind;
    }
}
