package com.github.community.controller;

import com.alibaba.fastjson.JSON;
import com.github.community.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
public class HelloController {

    @Autowired
    private HelloService helloService;

    @GetMapping("/select")
    @ResponseBody
    public String select() {
        return helloService.select();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot";
    }

    @GetMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println("name : " + name + ":" + "value :" + value);
        }
        System.out.println(request.getParameter("hello"));

        // 返回响应数据
        response.setContentType("application/json;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.write("{a:1;b:2}");
    }

    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudentById(@PathVariable("id") Integer id) {
        return "get Student id : " + id;
    }

    @GetMapping("/students")
    @ResponseBody
    public String getStudents(@RequestParam(name = "current", required = false, defaultValue = "1") int current,
                              @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "get Students success";
    }


    private static class UsernameAndPassword {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    @PostMapping("/submit")
    @ResponseBody
    public String submit(@RequestBody UsernameAndPassword usernameAndPassword) {
        String username = usernameAndPassword.getPassword();
        String password = usernameAndPassword.getPassword();
        Map<String, String> jsonObject = new HashMap<>();
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        return JSON.toJSONString(jsonObject);
    }

    @PostMapping("/test")
    @ResponseBody
    public String test(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");
        System.out.println(username);
        System.out.println(password);
        return "test success";
    }

    // 响应HTML数据
    @GetMapping("/teacher")
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "niubi");
        modelAndView.addObject("age", 30);
        modelAndView.setViewName("/demo");
        return modelAndView;
    }

    @GetMapping("/worker")
    public String getWorker(Model model) {
        model.addAttribute("name", "shabi");
        model.addAttribute("age", 31);
        return "/demo";
    }

    // 响应 JSON
    // 异步请求
    // 示例：当注册BiliBili时，刚刚输入完姓名，没有点击任何按钮，当前网页没有刷新，就会提示该名称已经被注册
    // Java 对象 -> JSON 字符串 -> JS 对象
    @GetMapping("/emp")
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 23);
        map.put("salary", 8000.00);
        return map;
    }

    @GetMapping("/emps")
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("name", "zhangsan");
        emp1.put("age", 23);
        emp1.put("salary", 8000.00);

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("name", "lisi");
        emp2.put("age", 25);
        emp2.put("salary", 10000.00);
        list.add(emp1);
        list.add(emp2);
        return list;
    }
}
