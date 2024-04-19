package com.glacier.followingservice;

public enum FollowMessages {
    UNFOLLOW_NO_USERS_ERROR("There is no following between specified users."),
    FOLLOW_USERS_ALREADY_FOLLOWING("Users are already followers. Or user_id is equal."),
    NO_USERS_FOUND_WITH_NAME("No user found with specified userName.");

    private final String message;

    FollowMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
