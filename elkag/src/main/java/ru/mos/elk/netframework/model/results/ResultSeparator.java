package ru.mos.elk.netframework.model.results;


/**
 * @author Александр Свиридов
 *
 */
public class ResultSeparator extends ResultTitle {
	private static final long serialVersionUID = 3183086262277085334L;

	public ResultSeparator(String style) {
        super(ResultType.SEPARATOR, style);
    }

    protected ResultSeparator(ResultType type, String style) {
        super(type, style);
    }

    @Override
    public String toString() {
        return getTitle();
    }

}
