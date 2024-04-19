package com.glacier.followingservice;

public enum FollowSqlStatements {

    GET_FOLLOWERS("""
            SELECT FOLLOWER.USER_ID FROM\s
            FOLLOWS F, USERS FOLLOWER, USERS FOLLOWED\s
            WHERE FOLLOWER.USER_ID = F.FOLLOWER_ID
            AND FOLLOWED.USER_ID = F.FOLLOWED_ID
            AND FOLLOWED.USER_NAME = ?"""),
    GET_USER_INFO_BY_ID("SELECT USER_ID, USER_NAME FROM USERS WHERE USER_ID = ?"),
    GET_USER_ID_BY_USER_NAME("SELECT USER_ID FROM USERS WHERE USER_NAME = ?"),
    USER_ID("user_id"), USER_NAME("user_name");

    private final String sqlStatement;

    FollowSqlStatements(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }
    public String statement() {
        return sqlStatement;
    }
}
