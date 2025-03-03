package net.blueshell.api.base;

import net.blueshell.api.common.enums.EventType;
import org.springframework.context.ApplicationEvent;

public abstract class BaseEvent<T> extends ApplicationEvent {
    private final EventType type;

    public BaseEvent(T source, EventType type) {
        super(source);
        this.type = type;
    }
}
