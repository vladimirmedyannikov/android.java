/**
 * 
 */
package ru.mos.elk.netframework.adapters;

import ru.mos.elk.netframework.model.results.ResultButton;

/**
 * @author Александр Свиридов
 * @31.05.2013
 */
public interface ButtonListener {
	
	public void onClick(ResultButton button, int position);
}
