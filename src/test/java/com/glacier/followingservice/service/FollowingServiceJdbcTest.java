package com.glacier.followingservice.service;

import com.glacier.followingservice.FollowMessages;
import com.glacier.followingservice.entity.User;
import oracle.jdbc.datasource.impl.OracleDataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("test-containers-flyway")
class FollowingServiceJdbcTest {

    private static FollowingServiceJdbc followingService;

    @Container
    @ServiceConnection
    private static final OracleContainer oracleContainer = new OracleContainer(
            DockerImageName.parse("gvenzl/oracle-free:slim-faststart"))
            .withDatabaseName("pdb1")
            .withUsername("test_user")
            .withPassword(("123"));

    @BeforeAll
    public static void init() throws Exception {
        var ods = new OracleDataSource();
        ods.setURL(oracleContainer.getJdbcUrl());
        ods.setUser(oracleContainer.getUsername());
        ods.setPassword(oracleContainer.getPassword());

        var conn = ods.getConnection();
        conn.setAutoCommit(false);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(ods);

        followingService = new FollowingServiceJdbc(jdbcTemplate);
    }

    @Test
    void unfollow_success() {
        try {
            followingService.unfollow("USER2", "USER1");
        } catch(SQLException e) {
            fail("An sql error has occurred!", e);
        }
    }

    @Test
    void unfollow_failure() {
        try {
            followingService.unfollow("USER1", "USER2");
            fail("Expected IllegalStateException was not thrown");
        } catch(SQLException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(FollowMessages.UNFOLLOW_NO_USERS_ERROR.getMessage());
        }
    }

    @Test
    void follow_success() {
        try {
            followingService.follow("USER1", "USER2");
        } catch (SQLException e) {
            fail("An sql error has occurred!", e);
        }
    }

    @Test
    void follow_failure() {
        try {
            followingService.follow("USER2", "USER1");
        } catch (SQLException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(FollowMessages.FOLLOW_USERS_ALREADY_FOLLOWING.getMessage());
        }
    }

    @Test
    void getFollowers_success() {
        List<User> userList = followingService.getFollowers("USER1");
        Assertions.assertThat(userList.isEmpty()).isEqualTo(false);
        Assertions.assertThat(userList.getFirst().user_name()).isEqualTo("USER2");
    }

    @Test
    void getFollowers_failure() {
        List<User> userList = followingService.getFollowers("USER2");
        Assertions.assertThat(userList.isEmpty()).isEqualTo(true);
    }
    
    @Test
    void followWithBadData() {
        try {
            followingService.follow("dfgdfg", "dsgsdf");
        } catch (SQLException e) {
            fail("An sql error has occurred!", e);
        } catch (NoSuchElementException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(FollowMessages.NO_USERS_FOUND_WITH_NAME.getMessage());
        }
    }

    @Test
    void getFollowersWithBadData() {
        List<User> followers = followingService.getFollowers("dsfsdf");
        Assertions.assertThat(followers.isEmpty()).isEqualTo(true);
    }
}