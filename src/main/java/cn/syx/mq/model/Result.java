package cn.syx.mq.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public static Result<List<SyxMessage<?>>> msg(List<SyxMessage<?>> messages) {
        return Result.<List<SyxMessage<?>>>builder()
                .code(1)
                .message("success")
                .data(messages)
                .build();
    }
}
