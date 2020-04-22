package com.mall.controller.backend;

import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.ICategoryService;
import com.mall.service.IUserService;
import com.mall.util.CookieUtil;
import com.mall.util.JsonUtil;
import com.mall.util.RedisShardedPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest httpServletRequest, String categoryName, @RequestParam(value = "parentId", defaultValue = "0")int parentId){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMassage("用户未登录，无法获取当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMassage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录请登录");
        }
        //校验是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            //增加我们处理分类的逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        }else {
            return ServerResponse.createByErrorMassage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpServletRequest httpServletRequest, Integer categoryId, String categoryName){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMassage("用户未登录，无法获取当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMassage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录请登录");
        }
        //校验是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //更新categoryName
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        }else {
            return ServerResponse.createByErrorMassage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpServletRequest httpServletRequest, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMassage("用户未登录，无法获取当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMassage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录请登录");
        }
        //校验是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息，并且不递归，保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMassage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest httpServletRequest, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMassage("用户未登录，无法获取当前用户信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null){
            return ServerResponse.createByErrorCodeMassage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录请登录");
        }
        //校验是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //查询当前节点的id和递归子节点的id
            return iCategoryService.selectCategoryAndChildrenId(categoryId);
        }else {
            return ServerResponse.createByErrorMassage("无权限操作，需要管理员权限");
        }
    }
}
