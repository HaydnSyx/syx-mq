package cn.syx.mq.model;

import lombok.Builder;

@Builder
public class Result<T> {
    private int code;
    private String message;
    private T data;


    public static Result<String> ok() {
        return Result.<String>builder()
                .code(1)
                .message("success")
                .data("OK")
                .build();
    }

    public static Result<String> ok(String data) {
        return Result.<String>builder()
                .code(1)
                .message("success")
                .data(data)
                .build();
    }

    public static Result<SyxMessage<String>> msg(String msg) {
        return Result.<SyxMessage<String>>builder()
                .code(1)
                .message("success")
                .data(SyxMessage.of(msg))
                .build();
    }

    public static Result<SyxMessage<?>> msg(SyxMessage<?> message) {
        return Result.<SyxMessage<?>>builder()
                .code(1)
                .message("success")
                .data(message)
                .build();
    }
}
