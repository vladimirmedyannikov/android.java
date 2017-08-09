package ru.mos.polls.base.component;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public final class UIComponentHolder implements ComponentHolder {
    private final List<UIComponent> components;

    private UIComponentHolder(List<UIComponent> components) {
        this.components = components;
    }

    public void onViewCreated(Context context, View view) {
        for (UIComponent component : components) {
            component.onViewCreated(context, view);
        }
    }

    public void onDestroyView() {
        for (UIComponent component : components) {
            component.onDestroyView();
        }
    }

    public <T extends UIComponent> T getComponent(Class<T> componentClass) {
        for (UIComponent component : components) {
            if (componentClass.isInstance(component)) {
                return (T) component;
            } else {
                UIComponent subComponent = component.getComponent(componentClass);
                if (subComponent != null) {
                    return (T) subComponent;
                }
            }
        }
        return null;
    }


    public static final class Builder {
        private final List<UIComponent> components = new ArrayList<>();

        public Builder with(UIComponent component) {
            components.add(component);
            return this;
        }

        public UIComponentHolder build() {
            return new UIComponentHolder(components);
        }
    }

}
