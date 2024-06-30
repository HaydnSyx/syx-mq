package cn.syx.mq.core;

import cn.syx.mq.model.SyxMessage;

public interface SyxMqListener<T> {

    void onMessage(SyxMessage<T> msg);
}
