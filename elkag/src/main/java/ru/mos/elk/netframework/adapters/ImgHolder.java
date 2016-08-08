/**
 * 
 */
package ru.mos.elk.netframework.adapters;

import android.view.View;

import com.android.volley2.toolbox.NetworkImageView;

import ru.mos.elk.R;
import ru.mos.elk.netframework.model.results.ResultTitle;

/**
 * @author Александр Свиридов
 * @30.05.2013
 */
public class ImgHolder<T extends ResultTitle> extends TitleHolder<T>{
	
	NetworkImageView imgView;
	
	@Override
	public void findViews(View view) {
		super.findViews(view);
		imgView = (NetworkImageView) view.findViewById(R.id.image);
	}
	
}
