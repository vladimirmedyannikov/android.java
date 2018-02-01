package ru.mos.polls.mainbanner;

/**
 * Created by Trunks on 19.01.2018.
 */

public class BannerItem {
    int image;
    String title;
    long value;

    public BannerItem(int image, String title, long value) {
        this.image = image;
        this.title = title;
        this.value = value;
    }
}
