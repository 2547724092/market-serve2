package com.wanli.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.code.kaptcha.Producer;
import com.wanli.common.BaseController;
import com.wanli.common.Result;
import com.wanli.config.Const;
import com.wanli.entity.User;
import com.wanli.service.UserService;
import com.wanli.util.OSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Slf4j
public class IndexController extends BaseController {
    @Autowired
    private Producer producer;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private OSSUtil ossUtil;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file")MultipartFile file){   //上传的文件必须叫 file
        String imgPath = ossUtil.uploadOneFile(file);
        if(imgPath == null){
            return Result.fail("文件上传失败");
        }
        return Result.success(imgPath);
    }

    @GetMapping("/userinfo")
    public Result info(Principal principal) {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        return Result.success(user);
    }

    @GetMapping("/captcha")
    public Result captcha() throws IOException {
        //调用验证码生成器，创建验证码字符
        String code = producer.createText();

        //生成随机一个 key ，作为存入 Redis 数据库中的一个键
        String key = UUID.randomUUID().toString();

        //需要将验证码存储到 Redis 数据库  hash 数据类型：hset 键（键 - 值）
        //指定验证码过期时间为 60s
        redisUtil.hset(Const.CAPTCHA, key, code, 60);

        //将验证码字符转换为图片
        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);

        //转换为 Base64 格式字符串
        BASE64Encoder encoder = new BASE64Encoder();
        String str = "data:image/jpeg;base64,";
        String base64Image = str + encoder.encode(out.toByteArray());

        log.info("验证码---{}---{}", key, code);

//        Map map = new HashMap<>();
//        map.put("key", key);
//        map.get("captcha", base64Image);

        return Result.success(MapUtil.builder().put("key", key).put("captcha", base64Image).build());

    }

    @PostMapping("/checkPass")
    public Result checkPass(Principal principal){
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        String password = request.getParameter("password");
        boolean b = bCryptPasswordEncoder.matches(password, user.getPassword());
        return Result.success(b);
    }

    @PostMapping("/updatePass")
    public Result updatePass(Principal principal){
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        String currentPass = request.getParameter("currentPass");
        String encode = bCryptPasswordEncoder.encode(currentPass);
        user.setPassword(encode);

        return Result.success(userService.updateById(user));
    }
}
