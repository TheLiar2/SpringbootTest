package com.springboot.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.springboot.mapper.UserMapper;
import com.springboot.model.User;
import com.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author xiaokuli
 * @date 2018/12/16 - 22:20
 */
@Component//spring的注解
@Service //dubbo的注解
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public List<User> getUserByPage(Map<String, Object> parammap) {
        return userMapper.selectUserByPage(parammap);
    }

    @Override
    public int getUserByTotal() {
        /*redis缓存总记录数*/
        /*设置key的序列化，采用字符串方式，可读性更好*/
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        /*先从缓存中找*/
        Integer totalRows = (Integer) redisTemplate.opsForValue().get("totalRows");
        /*解决缓存穿透问题*/
        if(totalRows == null){
            synchronized (this){
                totalRows = (Integer) redisTemplate.opsForValue().get("totalRows");
                if(totalRows==null){
                    totalRows =  userMapper.selectUserByTotal();
                    redisTemplate.opsForValue().set("totalRows",totalRows);
                }

            }
        }
        return totalRows;
    }

    @Override
    public int addUser(User user) {
        /*设置key的序列化，采用字符串方式，可读性更好*/
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        int count = userMapper.insert(user);

        if(count>0){
            //更新缓存总记录数
            int totalRows =  userMapper.selectUserByTotal();
            redisTemplate.opsForValue().set("totalRows",totalRows);
        }
        return count;
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public int deleteUser(Integer id) {
        /*设置key的序列化，采用字符串方式，可读性更好*/
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        int count = userMapper.deleteByPrimaryKey(id);
        if(count >0){
            //更新缓存总记录数
            int totalRows =  userMapper.selectUserByTotal();
            redisTemplate.opsForValue().set("totalRows",totalRows);
        }
        return count;
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
