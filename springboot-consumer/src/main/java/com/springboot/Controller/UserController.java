package com.springboot.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.springboot.model.User;
import com.springboot.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaokuli
 * @date 2018/12/16 - 23:24
 */
@Controller
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/index")
    public String index(Model model,
            @RequestParam(value="curPage",required = false)Integer curPage){

        //每页显示10条
        int pageSize = 5;
        if(curPage==null || curPage<0){
            curPage = 1;
        }

        //总数
        int totalRows = userService.getUserByTotal();
        //计算分页
        int totalPages = totalRows / pageSize;

        //有可能有余数
        int leaf = totalRows % pageSize;
        if(leaf>0){
            totalPages = totalPages + 1 ;
        }
        if(curPage>totalPages){
            curPage=totalPages;
        }

        //计算查询的开始行
        int startRow = (curPage-1) * pageSize;
        Map<String,Object> parammap = new HashMap<>();
        parammap.put("startRow",startRow);
        parammap.put("pageSize",pageSize);
        List<User> userList = userService.getUserByPage(parammap);
        model.addAttribute("userList",userList);
        model.addAttribute("curPage",curPage);
        model.addAttribute("totalPages",totalPages);
        //跳转到模板页
        return "index";
    }

    @RequestMapping("/user/toAddUser")
    public String toAddUser(){
        return "addUser";
    }

    @RequestMapping("/user/addUser")
    public String addUser(User user){
        if(user.getId() == null){
            userService.addUser(user);
        }else{
            userService.updateUser(user);
        }
        return "redirect:/index";
    }

    @RequestMapping("/user/toUpdateUser")
    public String toUpdateUser(Integer id, Model model){
        User user = userService.getUserById(id);
        model.addAttribute("user",user);
        return "addUser";
    }

    @RequestMapping("/user/deleteUser")
    public String deleteUser(@RequestParam("id")Integer id){
        userService.deleteUser(id);
        return "redirect:/index";
    }


}
