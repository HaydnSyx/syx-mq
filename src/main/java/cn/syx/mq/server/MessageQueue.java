package cn.syx.mq.server;

import cn.syx.mq.model.SyxMessage;
import cn.syx.mq.store.Indexer;
import cn.syx.mq.store.Store;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class MessageQueue {

    public static final Map<String, MessageQueue> queues = new HashMap<>();

    static {
        queues.putIfAbsent("cn.syx.test", new MessageQueue("cn.syx.test"));
    }

    private final Map<String, MessageSubscription> subscriptions = new HashMap<>();

    private final String topic;
    //    private SyxMessage<?>[] queue = new SyxMessage[1024 * 10];
    private final Store store;
//    private int index = 0;

    public MessageQueue(String topic) {
        this.topic = topic;
        this.store = new Store(topic);
        this.store.init();
    }

    public int send(SyxMessage<?> msg) {
        /*if (index >= queue.length) {
            return -1;
        }*/

        // 保存offset到头信息中
        int offset = store.pos();
        msg.wrapperOffset(offset);
        store.write(msg);
        return offset;
    }

    public SyxMessage<?> recv(int offset) {
        return store.read(offset);
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
        log.info("===> message_queue sub topic:{}, consumerId:{}", subscription.getTopic(), subscription.getConsumerId());
        MessageQueue queue = getQueueByTopic(subscription.getTopic());
        queue.subscribe(subscription);
    }

    public static void unsub(MessageSubscription subscription) {
        log.info("===> message_queue unsub topic:{}, consumerId:{}", subscription.getTopic(), subscription.getConsumerId());
        MessageQueue queue = queues.get(subscription.getTopic());
        if (Objects.isNull(queue)) {
            return;
        }
        queue.unsubscribe(subscription);
    }

    public static int send(String topic, SyxMessage<?> message) {
        MessageQueue queue = getQueueByTopic(topic);
        int send = queue.send(message);
        log.info("===> message_queue send topic:{}, message:{}, send:{}", topic, JSON.toJSONString(message), send);
        return send;
    }

    public static SyxMessage<?> recv(String topic, String consumerId, int index) {
        MessageQueue queue = getQueueByTopic(topic);
        // 判断订阅关系
        getSubscriptionByCid(consumerId, queue);

        return queue.recv(index);
    }

    public static SyxMessage<?> recv(String topic, String consumerId) {
        log.info("===> message_queue recv topic:{}, consumerId:{}", topic, consumerId);
        MessageQueue queue = getQueueByTopic(topic);
        MessageSubscription subscription = getSubscriptionByCid(consumerId, queue);
        // 取下一个位置
        int offset = subscription.getOffset();
        int next_offset = 0;
        if (offset > -1) {
            Indexer.Entry entry = Indexer.getEntry(topic, offset);
            assert entry != null;
            next_offset = offset + entry.getLength();
        }
        return queue.recv(next_offset);
    }

    public static List<SyxMessage<?>> batchRecv(String topic, String consumerId, int size) {
        log.info("===> message_queue batchRecv topic:{}, consumerId:{}, size:{}", topic, consumerId, size);
        MessageQueue queue = getQueueByTopic(topic);
        MessageSubscription subscription = getSubscriptionByCid(consumerId, queue);
        // 取下一个位置
        int offset = subscription.getOffset();
        List<SyxMessage<?>> messages = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            SyxMessage<?> message = queue.recv(offset + i);
            if (Objects.isNull(message)) {
                break;
            }
            messages.add(message);
        }

        return messages;
    }

    public static int ack(String topic, String consumerId, int offset) {
        MessageQueue queue = getQueueByTopic(topic);
        MessageSubscription subscription = getSubscriptionByCid(consumerId, queue);

        log.info("===> message_queue ack topic:{}, consumerId:{}, offset:{}", topic, consumerId, offset);
        if (offset > subscription.getOffset() && offset < Store.LEN) {
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

