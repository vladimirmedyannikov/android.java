/**
 * 
 */
package ru.mos.elk.netframework.adapters;

/**
 * @author Александр Свиридов
 * @30.05.2013<p/>
 * 
 * intercepts event of action with element.
 */
public interface ActionInterceptor<T> {

	/**@param element
     * @param position - position of clicked element
	 * @return - true if action was intercepted, false otherwise*/
	public boolean onAct(T element, int position);
}
