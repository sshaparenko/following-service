package com.glacier.followingservice.service;

import com.glacier.followingservice.FollowSqlStatements;
import com.glacier.followingservice.entity.User;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
@Transactional
public class FollowingServiceJdbc implements FollowingService {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(FollowingServiceJdbc.class);

    public FollowingServiceJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    RowMapper<Integer> userIdRowMapper = (resultSet, rowNumber) -> resultSet.getInt(FollowSqlStatements.USER_ID.statement());

    RowMapper<User> userRowMapper = (resultSet, rowNumber) -> new User(
            resultSet.getInt(FollowSqlStatements.USER_ID.statement()),
            resultSet.getString(FollowSqlStatements.USER_NAME.statement())
    );

    @Override
    public void unfollow(String user_name, String followed_name) throws SQLException, NoSuchElementException {
        Map<String, Integer> idMap = new HashMap<>();

        Integer[] idArray = getIDsFromUsername(user_name, followed_name);

        idMap.put("p_follower_id", idArray[0]);
        idMap.put("p_followed_id", idArray[1]);
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("FOLLOW_PKG")
                .withProcedureName("UNFOLLOW");
        jdbcCall.addDeclaredParameter(new SqlParameter("p_follower_id", OracleTypes.NUMBER));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_followed_id", OracleTypes.NUMBER));

        try {
            jdbcCall.execute(idMap);
        } catch (UncategorizedSQLException e) {
            log.warn("There is no following between specified users with id {} : {}", idArray[0], idArray[1]);
            throw new SQLException("There is no following between specified users.", e);
        }
    }

    /*
     * This function return an array if id for users with specified usernames
     * */
    private Integer[] getIDsFromUsername(String follower_name, String followed_name) throws NoSuchElementException {
        var statement = FollowSqlStatements.GET_USER_ID_BY_USER_NAME.statement();
        try {
            Integer follower_id = jdbcTemplate.query(statement, userIdRowMapper, follower_name).getFirst();
            Integer followed_id = jdbcTemplate.query(statement, userIdRowMapper, followed_name).getFirst();
            return new Integer[]{follower_id, followed_id};
        } catch (NoSuchElementException e) {
            log.warn("No user found with specified userName {} and {}", follower_name, followed_name);
            throw new NoSuchElementException("No user found with specified userName.", e);
        }
    }

    @Override
    public void follow(String user_name, String followed_name) throws SQLException, NoSuchElementException {
        Map<String, Integer> idMap = new HashMap<>();

        Integer[] idArray = getIDsFromUsername(user_name, followed_name);
        idMap.put("p_follower_id", idArray[0]);
        idMap.put("p_followed_id", idArray[1]);

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("FOLLOW_PKG")
                .withProcedureName("FOLLOW");
        jdbcCall.addDeclaredParameter(new SqlParameter("p_follower_id", OracleTypes.NUMBER));
        jdbcCall.addDeclaredParameter(new SqlParameter("p_followed_id", OracleTypes.NUMBER));

        try {
            jdbcCall.execute(idMap);
        } catch (UncategorizedSQLException e) {
            log.warn("User with id {} already following user with id {}. Or user_id is equal.", idArray[0], idArray[1]);
            throw new SQLException("Users are already followers. Or user_id is equal.", e);
        }
    }

    /*
     * This method return USER_ID and USER_NAME of the FOLLOWERS
     * */
    @Override
    public List<User> getFollowers(String user_name) {
        //querying list of followers IDs
        List<Integer> userIdList = getFollowersIDs(user_name);
        //send list of followers IDs to get their ID + USER_NAME
        return getUsersFromIDs(userIdList);
    }

    /*
     * This method return List of FOLLOWERS IDs
     * */
    private List<Integer> getFollowersIDs(String user_name) {
        log.info("Getting the followers IDs...");
        var getFollowersStatement = FollowSqlStatements.GET_FOLLOWERS.statement();
        return jdbcTemplate.query(getFollowersStatement, userIdRowMapper, user_name);
    }

    /*
     * This method return a list of USERS that were queried by their IDs
     * */
    private List<User> getUsersFromIDs(List<Integer> userIdList) {
        log.info("Getting user info from IDs...");
        List<User> resultList = new ArrayList<>();

        var getUsersStatement = FollowSqlStatements.GET_USER_INFO_BY_ID.statement();

        userIdList.forEach(el -> {
            User user = jdbcTemplate.query(getUsersStatement, userRowMapper, el).getFirst();
            resultList.add(user);
        });
        log.info("User info was collected successfully!");
        return resultList;
    }

    /*
     * This method return list of Users that you FOLLOW
     * */
    @Override
    public List<User> getFollowed(String user_name) {
        return List.of();
    }
}
