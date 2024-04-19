package com.glacier.followingservice.entity;

import java.sql.Timestamp;

public record Follows(
        Integer follower_id,
        Integer followed_id,
        Timestamp follow_date
    ) {
}
