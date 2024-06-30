package cn.syx.mq.server;

import cn.syx.mq.model.Result;
import cn.syx.mq.model.SyxMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/syxmq")
public class MqServer {

    @RequestMapping("/send")
    public Result<String> send(@RequestParam("t") String topic,
                               @RequestParam("cid") String consumerId,
                               @RequestBody SyxMessage<String> message) {
        int result = MessageQueue.send(topic, consumerId, message);
        return Result.ok(result + "");
    }

    @RequestMapping("/recv")
    public Result<SyxMessage<?>> recv(@RequestParam("t") String topic,
                                           @RequestParam("cid") String consumerId) {
        SyxMessage<?> message = MessageQueue.recv(topic, consumerId);
        return Result.msg(message);
    }

    @RequestMapping("/ack")
    public Result<String> ack(@RequestParam("t") String topic,
                              @RequestParam("cid") String consumerId,
                              @RequestParam("offset") int offset) {
        int ack = MessageQueue.ack(topic, consumerId, offset);
        return Result.ok(ack + "");
    }

    @RequestMapping("/sub")
    public Result<String> subscribe(@RequestParam("t") String topic,
                                    @RequestParam("cid") String consumerId) {
        MessageQueue.sub(new MessageSubscription(topic, consumerId, -1));
        return Result.ok();
    }

    @RequestMapping("/unsub")
    public Result<String> unsubscribe(@RequestParam("t") String topic,
                                      @RequestParam("cid") String consumerId) {
        MessageQueue.unsub(new MessageSubscription(topic, consumerId, -1));
        return Result.ok();
    }
}
