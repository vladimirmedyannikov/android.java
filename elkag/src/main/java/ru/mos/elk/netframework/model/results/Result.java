package ru.mos.elk.netframework.model.results;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author Александр Свиридов
 *
 */
public abstract class Result implements Serializable {
	private static final long serialVersionUID = -5274194739795417816L;
	
	private final ResultType type;
	private final String style;

    public Result(ResultType type, String style) {
    	this.type = type;
        this.style = style;
    }

    public ResultType getType() {
        return type;
    }

    /** fills this object from json*/
    public abstract void fill(JSONObject json) throws JSONException;

    @Override
    public String toString() {
        return type.toString();
    }

	public String getStyle() {
		return style;
	}

}
