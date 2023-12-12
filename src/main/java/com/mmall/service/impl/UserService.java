package com.mmall.service.impl;

import com.github.pagehelper.StringUtil;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if(user == null){
            return ServerResponse.createByErrorMsg("Wrong password");
        }

        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("Successfully Login!", user);
    }

    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.String_EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("Register failed");
        }
        return ServerResponse.createBySuccessMsg("Register succeeded!");
    }

    public ServerResponse<String> checkValid(String str, String type){
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount == 0){
                    return ServerResponse.createByErrorMsg("The username already exists.");
                }
            }
            if(Const.String_EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount == 0){
                    return ServerResponse.createByErrorMsg("The email already exists.");
                }
            }
        }else{
            return ServerResponse.createByErrorMsg("Wrong params");
        }
        return ServerResponse.createBySuccessMsg("The info is valid");
    }

    public ServerResponse<String> selectQuestion(String username){
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMsg("The user doesn't exist!");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMsg("Question is empty!");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMsg("Wrong answer!");
    }

    public ServerResponse<String>  forgetResetPassword(String username, String passwordNew, String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMsg("Wrong param, token is needed!");
        }
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMsg("The user doesn't exist!");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMsg("invalid token!");
        }

        if(StringUtils.equals(forgetToken, token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCnt = userMapper.updatePasswordByUsername(username, md5Password);
            if(rowCnt > 0){
                return ServerResponse.createBySuccessMsg("Successfully updated password!");
            }
        }else {
            return ServerResponse.createByErrorMsg("Wrong token, please try again!");
        }
        return ServerResponse.createByErrorMsg("Password update failed!");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMsg("The old password is wrong!");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccessMsg("Password updated!");
        }
        return ServerResponse.createByErrorMsg("Password update failed!");
    }

    public ServerResponse<User> updateInformation(User user){
        //cannot update username
        //check email
        int resultCount = userMapper.checkEmailByUserID(user.getEmail(), user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMsg("Email is already used by another user!");
        }
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPhone(user.getPhone());
        updatedUser.setQuestion(user.getQuestion());
        updatedUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updatedUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("Update info succeeded!", updatedUser);
        }
        return ServerResponse.createByErrorMsg("Update info failed!");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMsg("current user not found");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}
