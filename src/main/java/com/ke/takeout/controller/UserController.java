package com.ke.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ke.takeout.common.R;
import com.ke.takeout.pojo.User;
import com.ke.takeout.service.UserService;
import com.ke.takeout.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        if (!phone.isEmpty()) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //发验证码
            //SMSUtils.sendMessage();
            log.info("验证码为："+code);
            // 放在session不安全，用户可以自己获得，改为存在redis
            // session.setAttribute(phone, code);
            redisTemplate.opsForValue().set(phone, code, 60, TimeUnit.SECONDS);
            return R.error("发送成功！");
        }
        return R.error("发送失败！");
    }

    @PostMapping("login")
    public R<String> login(@RequestBody Map map, HttpSession session) {
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");
        // 从redis拿
        //String codee= (String) session.getAttribute(phone);
        Object codee = redisTemplate.opsForValue().get(phone);
        if (codee != null && code != null && codee.equals(code)) {
            // 判断是否为新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            if (user.getStatus() != 1) {
                return R.error("账号异常，登录失败！");
            }
            session.setAttribute("user", user.getId());
            redisTemplate.delete(phone);
            return R.success("登陆成功！");
        }
        return R.error("验证错误，登录失败！");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        // 清理session中的用户信息
        session.removeAttribute("user");
        return R.success("退出成功!");
    }

    @GetMapping("/getUserInfo/{phone}")
    public R<User> getUserInfo(@PathVariable String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userService.getOne(queryWrapper);
        return R.success(user);
    }

    @PutMapping()
    public R<String> update(@RequestBody User user) {
        userService.updateById(user);
        return R.success("保存成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        Page<User> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(User::getCreateTime);
        userService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    @GetMapping("/{id}")
    public R<User> getUser(@PathVariable long id) {
        User user = userService.getById(id);
        return R.success(user);
    }

    @PutMapping("/updateStatus")
    public R<String> update1(@RequestBody User userGiven) {
        User user = userService.getById(userGiven);
        if (userGiven.getStatus() != user.getStatus()) {
            user.setStatus(userGiven.getStatus());
        } else user = userGiven;
        userService.updateById(user);
        return R.success("修改成功");
    }

    @GetMapping("/getUserCount")
    public R<Integer> count() {
        return R.success(userService.count());
    }
}
