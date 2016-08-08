/**
 * 
 */
package ru.mos.elk.netframework.adapters;

import android.view.View;
import android.view.View.OnClickListener;

import ru.mos.elk.netframework.model.results.ResultButton;

/**
 * @author Александр Свиридов
 * @31.05.2013
 */
public class ButtonHolder extends TitleHolder<ResultButton>{

	private BtnClickListener internalListener;
	ButtonListener buttonListener;
	
	@Override
	public void findViews(View view) {
		super.findViews(view);
		internalListener = new BtnClickListener();
		tvTitle.setOnClickListener(internalListener);
	}
	 
	@Override
	public void fillView(int position, ResultButton item) {
		super.fillView(position, item);
		internalListener.setResultButton(item);
	}
	
	private class BtnClickListener implements OnClickListener{
		private ResultButton btn;
		
		public void setResultButton(ResultButton btn){
			this.btn = btn;
		}
		
		@Override
		public void onClick(View v) {
			if(buttonListener!=null)
				buttonListener.onClick(btn, -1);
		}
		
	}
}
