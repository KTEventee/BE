package com.server.eventee.domain.member.exception;

import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.BaseCode;


public class MemberHandler extends BaseException {

  public MemberHandler(BaseCode code) {
    super(code);
  }
}