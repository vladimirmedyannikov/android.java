package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 *
 */
public class ResultLink extends ResultImg {
	private static final long serialVersionUID = 5327765797956948304L;

	String linkId;

    public ResultLink(String style) {
        super(ResultType.LINK, style);
    }
    
    public ResultLink(ResultType type, String style) {
        super(type, style);
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    @Override
    public void fill(JSONObject json) throws JSONException {
        super.fill(json);
        linkId = json.optString("link_id");
    }

}
