package com.mmall.service.impl;

import com.github.pagehelper.StringUtil;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("The username doesn't exist.");
        }

        //login with MD5

        User user = userMapper.selectLogin(username, password);
        if(user == null){
            return ServerResponse.createByErrorMsg("Wrong password");
        }

        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("Successfully Login!", user);
    }
}
