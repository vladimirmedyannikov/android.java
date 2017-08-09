package ru.mos.polls.base.component;

public interface ComponentHolder {
    <T extends UIComponent> T getComponent(Class<T> componentClass);
}
