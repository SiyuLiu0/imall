package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }

    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId, status);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductList(pageNum, pageSize);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(), "Please login first!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMsg("Updating is not allowed, admin login is needed!");
        }
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Const.CUREENT_USER);
        if(user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", false);
            return resultMap;
        }

        //use simditor
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success", false);
                resultMap.put("msg", "no right to do this!");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            resultMap.put("success", true);
            resultMap.put("msg", "Uploaded!");
            fileMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success", false);
            resultMap.put("msg", "no right to do this!");
            return resultMap;
        }
    }
}
