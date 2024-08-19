package com.wanli;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wanli.entity.User;
import com.wanli.mapper.UserMapper;
import com.wanli.service.UserService;
import com.wanli.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class MallOnlineSystemApplicationTests {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testAuthString(){
        System.out.println(userService.getUserAuthorityInfo(2l));
    }

    @Test
    void testJwt() {
//        System.out.println(jwtUtil.createToken("username"));
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTcxNzM5NTYyOSwiZXhwIjoxNzE4MDAwNDI5fQ.kYkPjKrfPfpbsOb0wWVWHEPzezdrep4iO8fGB8EkwhQ";
        System.out.println(jwtUtil.getClaimsToken(token));
    }

    @Test
    void testPwdEncoder() {
        //原始密码
        //$2a$10$9240PDVtU6aJcHzt1t3bSedfSr6k02eVhlieiqyCB/LPmNC.Jo81C
        //$2a$10$taxT.qiV9.HxnGnUs6Tf0ezuUhcHIWw8/wNOqq3GBtHydhsdQjFrW
        String pwd = "123123";
        String encode = bCryptPasswordEncoder.encode(pwd);
        System.out.println(encode);

        //输入密码
        String inputPwd = "123321";
        boolean b = bCryptPasswordEncoder.matches(inputPwd, encode);  //使用该方法尽心密码比对
        System.out.println(b);
    }

    @Test
    void contextLoads() {
        //QueryWrapper 相当于 SQL 语句中的 where
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //eq():= 等于；ne():<> 不等于；gt():> 大于；ge(): 大于等于 >=；lt():< 小于； le:<= 小于等于
//        queryWrapper.between("sal",10,100);
//        queryWrapper.eq("username","suzuki");
        queryWrapper.eq("statu", 1);
        queryWrapper.like("username", "s");
        queryWrapper.isNotNull("last_login");


        User user = userMapper.selectById(45);
        System.out.println(user);

        List<User> users = userMapper.selectList(queryWrapper);
        System.out.println(users);

        Integer count = userMapper.selectCount(null);
        System.out.println(count);
    }

    @Test
    public void select() {
        User user = userService.getById(45);
        System.out.println(user);

        User user1 = userService.getOne(new QueryWrapper<User>().eq("id", 52));
        System.out.println(user1);

        List<User> list = userService.list();

        List<User> list1 = userService.list(new QueryWrapper<User>().like("username", "万里"));
        System.out.println(list1.size());

        List<User> list2 = userService.listByIds(Arrays.asList(49, 50, 51));
        System.out.println(list2);

        List<User> list3 = userService.query().ge("id", 2).like("username", "万里").list();
        System.out.println(list3);
    }

    @Test
    public void serviceUpdate() {
        userService.update()
                .set("username", "仇富者联盟")
                .set("updated", LocalDateTime.now())
                .eq("id", 54).update();
    }

    @Test
    public void serviceDelete() {
//        userService.removeById(52);
        userService.remove(new QueryWrapper<User>().eq("id", 51));
        userService.removeByIds(Arrays.asList(50, 51));
    }

    @Test
    void insert() {
        //插入数据，构建实体对象
        User user = new User();
        user.setUsername("万里1");
        user.setPassword("123");
        user.setAvatar("https://wanligo-online.oss-cn-hangzhou.aliyuncs.com/2024/01/02ebad2edf61a4469e90d8c83da186f7d0202161722474684943.jpg");
        user.setEmail("222@qq.com");
        user.setTel("12345678910");
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
        user.setStatu(1);

        userMapper.insert(user);
    }

    @Test
    void save() {
        User user = new User();
        user.setUsername("万里2");
        user.setPassword("123");
        user.setAvatar("https://wanligo-online.oss-cn-hangzhou.aliyuncs.com/2024/01/02ebad2edf61a4469e90d8c83da186f7d0202161722474684943.jpg");
        user.setEmail("222@qq.com");
        user.setTel("12345678910");
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
        user.setStatu(1);

        User user2 = new User();
        user2.setUsername("万里3");
        user2.setPassword("123");
        user2.setAvatar("https://wanligo-online.oss-cn-hangzhou.aliyuncs.com/2024/01/02ebad2edf61a4469e90d8c83da186f7d0202161722474684943.jpg");
        user2.setEmail("222@qq.com");
        user2.setTel("12345678910");
        user2.setCreated(LocalDateTime.now());
        user2.setUpdated(LocalDateTime.now());
        user2.setStatu(1);

        List<User> list = new ArrayList<>();
        list.add(user2);
        list.add(user);

        //批量插入
        userService.saveBatch(list);
    }

    @Test
    void updated() {
        User user = userMapper.selectById(45);
        user.setUsername("张三");
        userMapper.updateById(user);

//        QueryWrapper<User> qw = new QueryWrapper<>();
//        qw.eq("username","李四");
//        userMapper.update(user,qw);
    }

    @Test
    void delete() {
//        userMapper.deleteById(46);
        userMapper.deleteBatchIds(Arrays.asList(47, 50));
    }

}
