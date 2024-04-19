CREATE OR REPLACE PACKAGE follow_pkg IS
  FUNCTION getFollowedCount(p_user_id NUMBER) RETURN PLS_INTEGER;
  FUNCTION getFollowersCount(p_user_id NUMBER) RETURN PLS_INTEGER;
  PROCEDURE unfollow(p_follower_id IN NUMBER, p_followed_id IN NUMBER);
  PROCEDURE follow(p_follower_id IN NUMBER, p_followed_id IN NUMBER);
END follow_pkg;
/
CREATE OR REPLACE PACKAGE BODY follow_pkg IS
  FUNCTION getFollowedCount(p_user_id NUMBER) RETURN PLS_INTEGER IS
    l_count PLS_INTEGER := 0;
  BEGIN
    SELECT COUNT(*) INTO l_count FROM FOLLOWS
      WHERE follower_id = p_user_id;

    RETURN l_count;
  END;

  FUNCTION getFollowersCount(p_user_id NUMBER) RETURN PLS_INTEGER IS
    l_count NUMBER := 0;
  BEGIN
    SELECT COUNT(*) INTO l_count FROM FOLLOWS
      WHERE followed_id = p_user_id;

    RETURN l_count;
  END;

  PROCEDURE unfollow(p_follower_id IN NUMBER, p_followed_id IN NUMBER) IS
  BEGIN

    DELETE FROM FOLLOWS
      WHERE follower_id = p_follower_id AND followed_id = p_followed_id;

    IF SQL%rowcount = 0 THEN
      raise_application_error(-20001, 'Unfollow was not made due to abscence of following relation or irrelevant data');
    END IF;
  END;

  PROCEDURE follow(p_follower_id IN NUMBER, p_followed_id IN NUMBER) IS
  BEGIN
    INSERT INTO FOLLOWS VALUES (p_follower_id, p_followed_id, systimestamp);
  EXCEPTION
    WHEN dup_val_on_index THEN
      raise_application_error(-20000, 'User with id:' || p_follower_id ||
      ' already follows user with id:' || p_followed_id);
  END;
END follow_pkg;
/