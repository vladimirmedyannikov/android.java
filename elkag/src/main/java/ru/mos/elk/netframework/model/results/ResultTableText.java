package ru.mos.elk.netframework.model.results;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Александр Свиридов on 15.11.13.
 */
public class ResultTableText extends ResultImg {

    private String[] columns;

    public ResultTableText(String style) {
        super(ResultType.TABLE_TEXT, style);
    }

    @Override
    public void fill(JSONObject json) throws JSONException {
        super.fill(json);
        if(!json.isNull("img"))
            src = json.getString("img");
        JSONArray jsonCols = json.optJSONArray("columns");
        if(jsonCols!=null){
            columns = new String[jsonCols.length()];
            for(int i=0;i<columns.length;i++){
                columns[i]=jsonCols.getString(i);
            }
        }
    }

    public String[] getColumns(){
        return columns;
    }

}
