package com.konkuk.vecto.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.konkuk.vecto.security.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {
    // 사용자 아이디
    private String userId;

    // 로그인 유형 (vecto or kakao)
    private String provider;

    // 사용자 닉네임
    private String nickName;

    // 사용자 이메일
    // 카카오 유저는 이메일 x
    private String email;

    private String profileUrl;

    // 게시글 개수
    private Integer feedCount;

    // 유저가 작성한 게시글 Id list
    private List<Long> feedIdList;

    // 유저의 팔로워 수
    private Integer followerCount;

    // 유저의 팔로잉 수
    private Integer followingCount;


    @Builder
    public UserInfoResponse(User user, Integer feedCount, Integer followerCount, Integer followingCount, List<Long> feedIdList){
        this.userId = user.getUserId();
        this.provider = user.getProvider();
        this.nickName = user.getNickName();
        this.email = user.getEmail();
        this.profileUrl = user.getProfileImageUrl();
        this.feedCount = feedCount;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.feedIdList = feedIdList;
    }
}
