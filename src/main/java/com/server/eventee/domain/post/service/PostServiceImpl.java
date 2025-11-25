package com.server.eventee.domain.post.service;

import com.server.eventee.domain.event.excepiton.EventHandler;
import com.server.eventee.domain.event.excepiton.status.EventErrorStatus;
import com.server.eventee.domain.event.model.Event;
import com.server.eventee.domain.event.repository.EventRepository;
import com.server.eventee.domain.member.model.Member;
import com.server.eventee.domain.post.dto.PostRequest;
import com.server.eventee.domain.post.dto.PostResponse;
import com.server.eventee.domain.post.dto.VoteLogResponseDto;
import com.server.eventee.domain.post.model.Post;
import com.server.eventee.domain.post.model.PostType;
import com.server.eventee.domain.post.model.VoteLog;
import com.server.eventee.domain.post.repository.PostRepository;
import com.server.eventee.domain.post.repository.VoteLogRepository;
import com.server.eventee.domain.group.model.Group;
import com.server.eventee.domain.group.repository.GroupRepository;
import com.server.eventee.global.exception.BaseException;
import com.server.eventee.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final GroupRepository groupRepository;
    private final VoteLogRepository voteLogRepository;
    private final EventRepository eventRepository;

    private String normalizeContent(String content) {
        if (content == null) return null;
        if (content.contains(",")) return content.replace(",", "_");
        return content;
    }

    private String[] parseOptions(String voteContent) {
        if (voteContent == null) return new String[0];
        if (voteContent.contains(",")) return Arrays.stream(voteContent.split(","))
            .map(String::trim).toArray(String[]::new);
        return Arrays.stream(voteContent.split("_"))
            .map(String::trim).toArray(String[]::new);
    }

    @Transactional
    public Post makePost(PostRequest.PostDto request, Member member) {

        Group group = groupRepository.findGroupByGroupId(request.groupId())
            .orElseThrow(() -> new BaseException(ErrorCode.GROUP_NOT_FOUND));

        PostType postType = PostType.from(request.type());

        String normalizedVoteContent = normalizeContent(request.voteContent());

        Post post = Post.builder()
            .content(request.content())
            .type(postType)
            .group(group)
            .member(member)
            .voteTitle(request.voteTitle())
            .voteContent(normalizedVoteContent)
            .build();

        group.addPost(post);
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(long id) {
        Post post = loadPostById(id);
        Group group = post.getGroup();

        group.deletePost(post);
        postRepository.delete(post);
        groupRepository.save(group);
    }

    @Transactional
    public PostResponse.PostDto updatePost(PostRequest.PostDto request, Member member, Long postId) {

        Post post = loadPostById(postId);

        String normalizedType = request.type() != null
            ? request.type().trim().toUpperCase()
            : post.getPostType().name();

        String voteTitle = normalizedType.equals("VOTE") ? request.voteTitle() : null;

        String voteContent = normalizedType.equals("VOTE")
            ? normalizeContent(request.voteContent())
            : null;

        PostRequest.PostDto normalizedRequest = new PostRequest.PostDto(
            request.groupId(),
            normalizedType,
            request.content(),
            voteTitle,
            voteContent
        );

        post.updatePost(normalizedRequest);
        Post saved = postRepository.save(post);

        return PostResponse.PostDto.from(saved, member);
    }

    private Post loadPostById(long id) {
        return postRepository.findPostByPostId(id)
            .orElseThrow(() -> new BaseException(ErrorCode.POST_NOT_FOUND));
    }

    public PostResponse.PostListByGroupDto getPostByEvent(long eventId, Member member) {
        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
            .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

        List<Group> groups = event.getGroups();
        log.info("groups:{}", groups.toString());

        return PostResponse.PostListByGroupDto.from(groups, member);
    }

    public VoteLogResponseDto vote(PostRequest.VoteDto request, Member member) {

        Post post = loadPostById(request.postId());

        if (!post.getPostType().equals(PostType.VOTE)) {
            throw new BaseException(ErrorCode.POST_TYPE_NOT_VOTE);
        }

        Optional<VoteLog> checkLog = voteLogRepository.findVoteLogByMemberAndPost(member, post);
        if (checkLog.isPresent()) throw new BaseException(ErrorCode.VOTE_ALREADY_DO);

        String[] options = parseOptions(post.getVoteContent());

        int num = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(request.voteText())) num = i + 1;
        }

        VoteLog log = VoteLog.builder()
            .post(post)
            .member(member)
            .voteNum(num)
            .word(request.voteText())
            .build();

        post.addVoteLog(log);

        voteLogRepository.save(log);
        postRepository.save(post);

        return VoteLogResponseDto.from(post.getVoteLogs(), member);
    }

    @Transactional
    public void adminPost(PostRequest.AdminPostDto request, Member member) {

        List<Long> groupIds = Arrays.stream(request.groupNums().split("_"))
            .map(Long::parseLong)
            .toList();

        List<Group> groups = groupRepository.findByGroupIdIn(groupIds);

        PostType postType = PostType.from(request.type());

        String normalizedVoteContent = normalizeContent(request.voteContent());

        List<Post> posts = new ArrayList<>();

        groups.forEach(g -> {
            Post post = Post.builder()
                .content(request.content())
                .type(postType)
                .group(g)
                .member(member)
                .voteTitle(request.voteTitle())
                .voteContent(normalizedVoteContent)
                .build();
            posts.add(post);
            g.addPost(post);
        });

        postRepository.saveAll(posts);
    }
}
