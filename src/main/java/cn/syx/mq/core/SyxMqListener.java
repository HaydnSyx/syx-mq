package cn.syx.mq.core;

import cn.syx.mq.model.SyxMessage;

public interface SyxMqListener {

    void onMessage(SyxMessage<?> msg);
}
