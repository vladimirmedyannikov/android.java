package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 *
 */
public class ResultTitle extends Result {
	private static final long serialVersionUID = -674947504720556795L;
	
	private String title;

    public ResultTitle(String style) {
        super(ResultType.TITLE, style);
    }

    protected ResultTitle(ResultType type, String style) {
        super(type, style);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void fill(JSONObject json)  throws JSONException{
        title = json.optString("title");
    }
    
    @Override
    public String toString() {
        return title;
    }

}
