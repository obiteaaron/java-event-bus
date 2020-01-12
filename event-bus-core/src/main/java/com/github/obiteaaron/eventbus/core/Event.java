package com.github.obiteaaron.eventbus.core;

/**
 * 发生的事件
 *
 * @author ObiteAaron
 * @since 0.1
*/
public interface Event {
    /**
     * 事件产生时会有相应的对象需要传递到事件监听者
     *
     * @return 返回事件带的对象
     */
    Object getEventObject();
}
