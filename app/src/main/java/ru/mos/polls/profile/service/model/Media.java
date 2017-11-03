package ru.mos.polls.profile.service.model;

/**
 * Created by Trunks on 13.07.2017.
 */

public class Media {
    String extension;
    String base64;

    public Media(String extension, String base64) {
        this.extension = extension;
        this.base64 = base64;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
