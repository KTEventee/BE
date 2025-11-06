package com.server.eventee.global.exception.codes;


import com.server.eventee.global.exception.codes.reason.Reason;

public interface BaseCode {
    public Reason.ReasonDto getReasonHttpStatus();
}
