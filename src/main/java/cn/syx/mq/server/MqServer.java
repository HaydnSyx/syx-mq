package cn.syx.mq.server;

import cn.syx.mq.model.Result;
import cn.syx.mq.model.SyxMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/syxmq")
public class MqServer {

    @PostMapping("/send")
    public Result<?> send(@RequestParam("t") String topic,
                               @RequestBody SyxMessage<?> message) {
        int result = MessageQueue.send(topic, message);
        return Result.ok(result + "");
    }

    @GetMapping("/recv")
    public Result<SyxMessage<?>> recv(@RequestParam("t") String topic,
                                           @RequestParam("cid") String consumerId) {
        SyxMessage<?> message = MessageQueue.recv(topic, consumerId);
        return Result.msg(message);
    }

    @GetMapping("/batch_recv")
    public Result<List<SyxMessage<?>>> batch_recv(@RequestParam("t") String topic,
                                            @RequestParam("cid") String consumerId,
                                            @RequestParam(value = "size", required = false, defaultValue = "1000") int size) {
        return Result.msg(MessageQueue.batchRecv(topic, consumerId, size));
    }

    @GetMapping("/ack")
    public Result<String> ack(@RequestParam("t") String topic,
                              @RequestParam("cid") String consumerId,
                              @RequestParam("offset") int offset) {
        int ack = MessageQueue.ack(topic, consumerId, offset);
        return Result.ok(ack + "");
    }

    @GetMapping("/sub")
    public Result<String> subscribe(@RequestParam("t") String topic,
                                    @RequestParam("cid") String consumerId) {
        MessageQueue.sub(new MessageSubscription(topic, consumerId, -1));
        return Result.ok();
    }

    @GetMapping("/unsub")
    public Result<String> unsubscribe(@RequestParam("t") String topic,
                                      @RequestParam("cid") String consumerId) {
        MessageQueue.unsub(new MessageSubscription(topic, consumerId, -1));
        return Result.ok();
    }
}
