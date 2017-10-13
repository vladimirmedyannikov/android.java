package ru.mos.polls.sourcesvoting.model;

/**
 * Created by Trunks on 13.10.2017.
 */

public class SourcesVoting extends SourcesVotingSet {

    /**
     * id : 1
     * title : Городские голосования
     * enable : true
     * editable : false
     */


    private String title;

    private boolean editable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
