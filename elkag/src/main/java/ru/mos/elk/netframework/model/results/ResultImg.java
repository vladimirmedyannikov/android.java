package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов
 *
 */
@Deprecated
public class ResultImg extends ResultTitle {
	private static final long serialVersionUID = 4739393432304562437L;
	
	String src;
	
    public ResultImg(String style) {
        super(ResultType.IMG, style);
    }
    
    protected ResultImg(ResultType type, String style){
    	super(type,style);
    }
    
    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public void fill(JSONObject json) throws JSONException {
        super.fill(json);
        if(!json.isNull("src"))
            src = json.optString("src");
    }
}
