package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0")int parentId) {
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }
        ///check whether admin
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.addCategory(categoryName, parentId);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }

    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName){
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value="categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.getChildrenParallerlCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value="categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }
    }
}
