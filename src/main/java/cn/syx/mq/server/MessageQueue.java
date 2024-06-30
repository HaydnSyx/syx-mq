package cn.syx.mq.server;

import cn.syx.mq.model.SyxMessage;

import java.util.*;

public class MessageQueue {

    public static final Map<String, MessageQueue> queues = new HashMap<>();

    static {
        queues.putIfAbsent("cn.syx.test", new MessageQueue("cn.syx.test"));
    }

    private final Map<String, MessageSubscription> subscriptions = new HashMap<>();

    private final String topic;
    private SyxMessage<?>[] queue = new SyxMessage[1024 * 10];
    private int index = 0;

    public MessageQueue(String topic) {
        this.topic = topic;
    }

    public int send(SyxMessage<?> msg) {
        if (index >= queue.length) {
            return -1;
        }
        this.queue[index++] = msg;
        return index;
    }

    public SyxMessage<?> recv(int index) {
        if (index <= this.index) {
            return queue[index];
        }
        return null;
    }

    public void subscribe(MessageSubscription subscription) {
        String cid = subscription.getConsumerId();
        if (!subscriptions.containsKey(cid)) {
            subscriptions.put(cid, subscription);
        }
    }

    public void unsubscribe(MessageSubscription subscription) {
        String cid = subscription.getConsumerId();
        if (!subscriptions.containsKey(cid)) {
            subscriptions.remove(cid);
        }
    }

    public static void sub(MessageSubscription subscription) {
        MessageQueue queue = getQueueByTopic(subscription.getTopic());
        queue.subscribe(subscription);
    }

    public static void unsub(MessageSubscription subscription) {
        MessageQueue queue = queues.get(subscription.getTopic());
        if (Objects.isNull(queue)) {
            return;
        }
        queue.unsubscribe(subscription);
    }

    public static int send(String topic, String consumerId, SyxMessage<String> message) {
        MessageQueue queue = getQueueByTopic(topic);
        return queue.send(message);
    }

    public static SyxMessage<?> recv(String topic, String consumerId, int index) {
        MessageQueue queue = getQueueByTopic(topic);
        // 判断订阅关系
        getSubscriptionByCid(consumerId, queue);

        return queue.recv(index);
    }

    public static SyxMessage<?> recv(String topic, String consumerId) {
        MessageQueue queue = getQueueByTopic(topic);
        MessageSubscription subscription = getSubscriptionByCid(consumerId, queue);
        return queue.recv(subscription.getOffset());
    }

    public static int ack( String topic, String consumerId, int offset) {
        MessageQueue queue = getQueueByTopic(topic);
        MessageSubscription subscription = getSubscriptionByCid(consumerId, queue);

        if (offset > subscription.getOffset() && offset < queue.index) {
            subscription.setOffset(offset);
            return offset;
        }

        return -1;
    }


    private static MessageQueue getQueueByTopic(String topic) {
        MessageQueue queue = queues.get(topic);
        if (Objects.isNull(queue)) {
            throw new RuntimeException("topic not found");
        }
        return queue;
    }

    private static MessageSubscription getSubscriptionByCid(String consumerId, MessageQueue queue) {
        MessageSubscription subscription = queue.subscriptions.get(consumerId);
        if (Objects.isNull(subscription)) {
            throw new RuntimeException("consumer not found");
        }
        return subscription;
    }
}

