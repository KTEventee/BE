package com.server.eventee.domain.event.excepiton;

import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.BaseCode;

public class EventHandler extends BaseException{
  public EventHandler(BaseCode code) {
    super(code);
  }

}