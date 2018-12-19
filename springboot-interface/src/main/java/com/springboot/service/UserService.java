package com.springboot.service;

import com.springboot.model.User;

import java.util.List;
import java.util.Map;

/**
 * @author xiaokuli
 * @date 2018/12/16 - 22:17
 */
public interface UserService {

    List<User> getUserByPage(Map<String,Object> parammap);

    int getUserByTotal();

    int addUser(User user);

    int updateUser(User user);

    int deleteUser(Integer id);

    User getUserById(Integer id);

}
