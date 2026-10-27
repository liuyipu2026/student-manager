package com.example.student_web.common;

public class Result {
    private int code;
    private String message;
    private Object data;

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }

    public static Result success(String message, Object data) {
        return new Result(200, message, data);
    }

    public static Result error(int code, String message) {
        return new Result(code, message, null);
    }

    public static Result error(String message) {
        return new Result(500, message, null);
    }

    // getter 和 setter（必须要有，否则 Jackson 转 JSON 会失败）
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}