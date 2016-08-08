/**
 * 
 */
package ru.mos.elk.netframework.adapters;

import android.view.View;

import ru.mos.elk.netframework.model.results.Result;

/**
 * @author Александр Свиридов
 *
 */
public interface Holder<T extends Result> {
	
	public void findViews(View view);
	
    /** fills existing View object for adapter.*/
    public void fillView(int position, T item);

}
