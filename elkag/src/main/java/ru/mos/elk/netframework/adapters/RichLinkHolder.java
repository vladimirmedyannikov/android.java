package ru.mos.elk.netframework.adapters;

import android.view.View;
import android.widget.TextView;

import ru.mos.elk.R;
import ru.mos.elk.netframework.model.results.ResultRichLink;

/**
 * @author Александр Свиридов on 23.12.13.
 */
public class RichLinkHolder<T extends ResultRichLink> extends ImgHolder<T> {

    private TextView tvBody;

    @Override
    public void findViews(View view) {
        super.findViews(view);
        tvBody = (TextView) view.findViewById(R.id.tvBody);
    }

    /* (non-Javadoc)
     * @see ru.mos.elk.netframework.adapters.TitleHolder#fillView(int, ru.mos.elk.netframework.model.results.ResultTitle)
     */
    @Override
    public void fillView(int position, T item) {
        super.fillView(position, item);
        tvBody.setText(item.getBody());
    }

}
