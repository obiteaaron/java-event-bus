package com.github.obiteaaron.eventbus.core;

/**
 * 事件监听器，当发生事件时会调用
 *
 * @author ObiteAaron
 * @since 0.1
*/
public interface EventListener<E extends Event> {

    void onEvent(E event);
}
