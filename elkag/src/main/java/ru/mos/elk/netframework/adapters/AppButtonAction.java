package ru.mos.elk.netframework.adapters;

import ru.mos.elk.actionmode.ActionDescription;
import ru.mos.elk.netframework.model.results.ResultAppButton;

/**
 * @author by Alex on 05.10.13.
 */
public interface AppButtonAction {

    public ActionDescription makeAction(ResultAppButton appButton);
}
