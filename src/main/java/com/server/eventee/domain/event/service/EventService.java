package com.server.eventee.domain.event.service;

import com.server.eventee.domain.event.dto.EventRequest;
import com.server.eventee.domain.event.dto.EventResponse;
import com.server.eventee.domain.member.model.Member;

public interface EventService {

  EventResponse.CreateResponse createEvent(Member member, EventRequest.CreateRequest request);

  EventResponse.JoinResponse joinEvent(Member member, EventRequest.JoinRequest inviteCode);
}
