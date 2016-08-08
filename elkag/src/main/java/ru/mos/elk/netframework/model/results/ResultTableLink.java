package ru.mos.elk.netframework.model.results;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов on 15.11.13.
 */
public class ResultTableLink extends ResultLink {

    private String[] columns;
    private boolean isNeedHideTask;
    private long id;

    public ResultTableLink(String style) {
        super(ResultType.TABLE_LINK, style);
    }

    @Override
    public void fill(JSONObject json) throws JSONException {
        super.fill(json);

        linkId = json.optString("link_url");
        if(!json.isNull("img"))
            src = json.optString("img");
        JSONArray jsonCols = json.optJSONArray("columns");
        if(jsonCols!=null){
            columns = new String[jsonCols.length()];
            for(int i=0;i<columns.length;i++){
                columns[i]=jsonCols.getString(i);
            }
        }
        if(!json.isNull("need_hide_task")) {
            isNeedHideTask = json.optBoolean("need_hide_task");
        }
        if(!json.isNull("id")) {
            id = json.optLong("id");
        }
    }

    public String[] getColumns(){
        return columns;
    }

    public boolean isNeedHideTask() {
        return isNeedHideTask;
    }

    public long getId() {
        return id;
    }
}
