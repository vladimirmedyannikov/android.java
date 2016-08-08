/**
 * 
 */
package ru.mos.elk.netframework.adapters;

/**
 * @author Александр Свиридов
 * @31.05.2013
 */
public interface ViewInterceptor<T> {
	
	/**@param element 
	 * @return - true if action was intercepted, false otherwise
     * method is invoked before adding element to adapter*/
	public boolean onAdd(T element, int position);

    /**
     * method invoked just before element is removed from page.*/
    public void onClean();

    /**
     * method is invoked every time the element is shown*/
//    public View onShow(T element, ViewGroup gr, int position);
}
