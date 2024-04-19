package com.glacier.followingservice.controller;

import com.glacier.followingservice.common.ApiResponse;
import com.glacier.followingservice.entity.User;
import com.glacier.followingservice.service.FollowingServiceJdbc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class FollowingController {
    private final FollowingServiceJdbc service;

    @GetMapping("/followers")
    public List<User> getFollowers(@RequestParam String username) {
        return service.getFollowers(username);
    }

    @GetMapping("/unfollow")
    public ResponseEntity<ApiResponse> unfollow(
            @RequestParam String follower_username,
            @RequestParam String followed_username
    ) {
        try {
            service.unfollow(follower_username, followed_username);
            return new ResponseEntity<>(new ApiResponse(true, "Unfollow was made successfully!"), HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(new ApiResponse(false, "User :" + follower_username + ": doesn't following :" + followed_username + ":"), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(new ApiResponse(false, "There is no user with specified user_name"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/follow")
    public ResponseEntity<ApiResponse> follow(
            @RequestParam String follower_username,
            @RequestParam String followed_username
    ) {
        try {
            service.follow(follower_username, followed_username);
            return new ResponseEntity<>(new ApiResponse(true, "Follow was made successfully!"), HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(new ApiResponse(false, "User :" + follower_username + ": already follow :" + followed_username + ":"), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(new ApiResponse(false, "There is no user with specified user_name"), HttpStatus.BAD_REQUEST);
        }
    }
}
