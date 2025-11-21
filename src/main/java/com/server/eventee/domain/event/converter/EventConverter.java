package com.server.eventee.domain.event.converter;

import com.server.eventee.domain.event.dto.EventResponse;
import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.model.MemberEvent;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.model.Post;
import com.server.eventee.domain.post.model.PostType;
import com.server.eventee.domain.post.model.VoteLog;
import com.server.eventee.domain.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EventConverter {

  private static final String DEFAULT_EVENT_THUMBNAIL =
      "https://eventee-bucket.s3.ap-northeast-2.amazonaws.com/event/defaultEventImage.png";

  private static final String DEFAULT_GROUP_IMAGE =
      "https://eventee-bucket.s3.ap-northeast-2.amazonaws.com/group/defaultGroupImage.png";

  public Event toEvent(String inviteCode, String title, String description,
      String password, LocalDateTime startAt, LocalDateTime endAt, Integer teamCount) {

    return Event.builder()
        .title(title)
        .description(description)
        .password(password)
        .startAt(startAt)
        .endAt(endAt)
        .inviteCode(inviteCode)
        .teamCount(teamCount)
        .status("OPEN")
        .thumbnailUrl(DEFAULT_EVENT_THUMBNAIL)
        .build();
  }

  public MemberEvent toHostRelation(Member member, Event event) {
    return MemberEvent.builder()
        .member(member)
        .event(event)
        .role(MemberEvent.MemberEventRole.HOST)
        .nickname(event.getTitle() + "관리자")
        .build();
  }

  public Group toGroup(int groupNo, Member leader, Event event) {
    return Group.builder()
        .groupName("팀 " + groupNo + "조")
        .groupDescription("자동 생성된 그룹입니다.")
        .groupImg(DEFAULT_GROUP_IMAGE)
        .groupNo(groupNo)
        .groupLeader(leader.getNickname())
        .event(event)
        .build();
  }

  public EventResponse.CreateResponse toCreateResponse(Event event, Member member) {
    String inviteUrl = "https://eventee.site/invite/" + event.getInviteCode();

    return EventResponse.CreateResponse.builder()
        .eventId(event.getId())
        .title(event.getTitle())
        .inviteCode(event.getInviteCode())
        .inviteUrl(inviteUrl)
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .createdAt(event.getCreatedAt())
        .creator(
            EventResponse.CreateResponse.CreatorInfo.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .build()
        )
        .build();
  }

  public EventResponse.EventWithGroupsResponse toEventWithGroupsResponse(Event event, List<Group> groups) {

    List<EventResponse.EventWithGroupsResponse.GroupSummary> groupDtos =
        groups.stream()
            .map(g -> EventResponse.EventWithGroupsResponse.GroupSummary.builder()
                .groupId(g.getGroupId())
                .groupName(g.getGroupName())
                .groupDescription(g.getGroupDescription())
                .groupImg(g.getGroupImg())
                .groupNo(g.getGroupNo())
                .groupLeader(g.getGroupLeader())
                .build())
            .toList();

    return EventResponse.EventWithGroupsResponse.builder()
        .eventId(event.getId())
        .eventTitle(event.getTitle())
        .eventDescription(event.getDescription())
        .thumbnailUrl(event.getThumbnailUrl())
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .teamCount(event.getTeamCount())
        .groups(groupDtos)
        .build();
  }

  public EventResponse.GroupPostsResponse toGroupPostsResponse(Group group, List<Post> posts, Member member) {

    List<EventResponse.GroupPostsResponse.PostInfo> postInfos =
        posts.stream()
            .map(post -> convertPostToDto(post, member))
            .toList();

    return EventResponse.GroupPostsResponse.builder()
        .groupId(group.getGroupId())
        .groupName(group.getGroupName())
        .posts(postInfos)
        .build();
  }

  private EventResponse.GroupPostsResponse.PostInfo convertPostToDto(Post post, Member currentUser) {

    boolean isMinePost = post.getMember().getId().equals(currentUser.getId());

    List<EventResponse.GroupPostsResponse.CommentInfo> comments =
        convertComments(post.getComments(), currentUser);

    String pollQuestion = null;
    List<EventResponse.GroupPostsResponse.VoteOptionInfo> pollOptions = null;
    Integer userVote = null;

    if (post.getPostType() == PostType.VOTE) {

      pollQuestion = post.getVoteTitle();

      String[] options = post.getVoteContent() != null
          ? post.getVoteContent().split("_")
          : new String[0];

      List<VoteLog> logs = post.getVoteLogs();
      int totalVotes = logs.size();

      pollOptions = new ArrayList<>();

      for (int i = 0; i < options.length; i++) {
        int optionNo = i + 1;
        String text = options[i];

        int votes = (int) logs.stream()
            .filter(v -> v.getVoteNum() == optionNo)
            .count();

        int percent = totalVotes > 0 ? (votes * 100 / totalVotes) : 0;

        boolean isMine = logs.stream()
            .anyMatch(v -> v.getMember().getId().equals(currentUser.getId())
                && v.getVoteNum() == optionNo);

        pollOptions.add(
            EventResponse.GroupPostsResponse.VoteOptionInfo.builder()
                .optionNo(optionNo)
                .text(text)
                .votes(votes)
                .percent(percent)
                .isMine(isMine)
                .build()
        );
      }

      userVote = logs.stream()
          .filter(v -> v.getMember().getId().equals(currentUser.getId()))
          .map(VoteLog::getVoteNum)
          .findFirst()
          .orElse(null);
    }

    return EventResponse.GroupPostsResponse.PostInfo.builder()
        .postId(post.getPostId())
        .author(post.getMember().getNickname())
        .content(post.getContent())
        .type(post.getPostType().type.toLowerCase())
        .createdAt(post.getCreatedAt())
        .comments(comments)
        .pollQuestion(pollQuestion)
        .pollOptions(pollOptions)
        .userVote(userVote)
        .isMine(isMinePost)
        .build();
  }

  private List<EventResponse.GroupPostsResponse.CommentInfo> convertComments(
      List<Comment> comments,
      Member currentUser
  ) {
    return comments.stream()
        .map(c -> EventResponse.GroupPostsResponse.CommentInfo.builder()
            .commentId(c.getCommentId())
            .content(c.getContent())
            .writerNickname(c.getMember().getNickname())
            .writerProfileUrl(c.getMember().getProfileImageUrl())
            .createdAt(c.getCreatedAt())
            .isMine(c.getMember().getId().equals(currentUser.getId()))
            .build())
        .toList();
  }

  public EventResponse.JoinResponse toJoinResponse(Event event, MemberEvent memberEvent) {

    List<EventResponse.JoinResponse.GroupInfo> groupInfos =
        event.getGroups().stream()
            .map(group -> EventResponse.JoinResponse.GroupInfo.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .groupDescription(group.getGroupDescription())
                .groupImg(group.getGroupImg())
                .groupNo(group.getGroupNo())
                .groupLeader(group.getGroupLeader())
                .build())
            .toList();

    return EventResponse.JoinResponse.builder()
        .eventId(event.getId())
        .title(event.getTitle())
        .description(event.getDescription())
        .thumbnailUrl(event.getThumbnailUrl())
        .teamCount(event.getTeamCount())
        .role(memberEvent.getRole().name())
        .nickname(memberEvent.getNickname())
        .groups(groupInfos)
        .build();
  }
}
