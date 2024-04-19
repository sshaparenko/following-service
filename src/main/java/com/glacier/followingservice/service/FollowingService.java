package com.glacier.followingservice.service;

import com.glacier.followingservice.entity.User;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public interface FollowingService {
    void unfollow(String user_name, String followed_name) throws SQLException, NoSuchElementException;
    void follow(String user_name, String followed_name) throws SQLException, NoSuchElementException;
    List<User> getFollowers(String user_name);
    List<User> getFollowed(String user_name);
}
