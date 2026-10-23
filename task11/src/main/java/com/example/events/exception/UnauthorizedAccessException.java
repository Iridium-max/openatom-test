package com.example.events.exception;
public class UnauthorizedAccessException extends BusinessException {
    public UnauthorizedAccessException(){ super(403, "无权操作其他用户发起的活动"); }
}
