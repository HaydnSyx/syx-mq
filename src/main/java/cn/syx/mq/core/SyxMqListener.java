package cn.syx.mq.core;

public interface SyxMqListener<T> {

    void onMessage(SyxMessage<T> msg);
}
