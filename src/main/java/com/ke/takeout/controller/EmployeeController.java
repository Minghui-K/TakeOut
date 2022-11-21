package com.ke.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ke.takeout.common.R;
import com.ke.takeout.pojo.Employee;
import com.ke.takeout.service.EmployeeService;
import com.ke.takeout.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录方法
     * @param employee 直接封装进入Employee的属性匹配
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        // 1 输入密码加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2 查询数据库获得密码
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3 如果没有查询到就失败了
        if (emp == null) return R.error("用户不存在，登陆失败！");

        // 4 密码比对
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误，登陆失败！");
        }

        // 5 查看员工状态
        if (emp.getStatus() != 1) {
            return R.error("用户状态异常，登陆失败！");
        }

        // 成功登录，放入session
        request.getSession().setAttribute("employee", emp.getId());
        log.info("后台登录成功");
        return R.success(emp);
    }

    /**
     * 登录退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 清理session中的用户信息
        request.getSession().removeAttribute("employee");
        return R.success("退出成功!");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("新增员工, {}", employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //谁创建了这个员工，当前的用户创建的所以拿session
        Long eid = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(eid);
        //employee.setUpdateUser(eid);

        employeeService.save(employee);

        return R.success("添加成功!");
    }

    /**
     * 分页查询返回条数
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        // 分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        // 是否查询名字如果不为空
        lambdaQueryWrapper.like(StringUtils.isNotBlank(name), Employee::getName, name);
        // 添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改信息
     */
    @PutMapping()
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request) {

        Long eid = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateUser(eid);
        //employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee emp = employeeService.getById(id);
        if (emp == null) {
            return R.error("没有查询到相关员工");
        }
        return R.success(emp);
    }

    @GetMapping("/getEmpCount")
    public R<Integer> count() {
        return R.success(employeeService.count());
    }
}
