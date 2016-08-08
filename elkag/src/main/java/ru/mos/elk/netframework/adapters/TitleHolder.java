/**
 * 
 */
package ru.mos.elk.netframework.adapters;

import android.view.View;
import android.widget.TextView;

import ru.mos.elk.R;
import ru.mos.elk.netframework.model.results.ResultTitle;

/**
 * @author Александр Свиридов
 *
 */
public class TitleHolder<T extends ResultTitle> implements Holder<T>{
	
	TextView tvTitle;
	
	@Override
	public void findViews(View view) {
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
	}

	@Override
	public void fillView(int position, T item) {
		tvTitle.setText(item.getTitle());		
	}

}

