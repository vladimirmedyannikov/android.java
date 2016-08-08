package ru.mos.elk.netframework.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.mos.elk.R;
import ru.mos.elk.netframework.model.results.ResultTableText;

/**
 * @author Александр Свиридов on 15.11.13.
 */
public class TableTextHolder extends ImgHolder<ResultTableText> {

    public static final String VALUE = "VALUE";
    private ViewGroup vgColumns;

    @Override
    public void findViews(View view) {
        super.findViews(view);
        vgColumns = (ViewGroup) view.findViewById(R.id.viewGroupColumns);
    }

    @Override
    public void fillView(int position, ResultTableText item) {
        super.fillView(position,item);

        String[] columns = item.getColumns();
        int childrenCount = vgColumns.getChildCount();
        int j=0;
        for(int i=0;i<childrenCount & i<columns.length;i++){
            for(int k=j;k<childrenCount;k++){
                View tvColumn = vgColumns.getChildAt(k);
                if(VALUE.equals(tvColumn.getTag())) {
                    ((TextView)tvColumn).setText(columns[i]);
                    j=k+1;
                    break;
                }
            }

        }
    }
}
