package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User row);

    int insertSelective(User row);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User row);

    int updateByPrimaryKey(User row);

    int checkUsername(String username);

    int checkEmail(String email);

    User selectLogin(@Param("username") String username, @Param("password") String password);
    String selectQuestionByUsername(String username);
    int checkAnswer(@Param("username")String username, @Param("question")String question, @Param("answer")String answer);
    int updatePasswordByUsername(@Param("username")String username, @Param("passwordNew")String passwordNew);
    int checkPassword(@Param("password")String password, @Param("userId")Integer userId);
    int checkEmailByUserID(@Param("email") String email, @Param("userId") Integer userId);
}