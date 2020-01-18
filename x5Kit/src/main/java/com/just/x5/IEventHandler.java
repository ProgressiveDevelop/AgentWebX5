package com.just.x5;

import android.view.KeyEvent;

/**
 * 事件接口
 */

public interface IEventHandler {
    /**
     * 按键事件
     *
     * @param keyCode 按键
     * @param event   事件
     * @return 是否处理
     */
    boolean onKeyDown(int keyCode, KeyEvent event);

    //返回事件
    boolean back();
}
