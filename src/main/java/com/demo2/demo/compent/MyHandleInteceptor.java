package com.demo2.demo.compent;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MyHandleInteceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("loginName");

        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        /* 星号表示所有的域都可以接受， */
        response.setHeader("Access-Control-Allow-Methods", "GET,POST");
        if(user == null){
            //如果未登陆
            request.setAttribute("msg","请先登陆");
            request.getRequestDispatcher("/").forward(request,response);

            return false;


        }else{
            //登录成功
            return true;
        }
    }
       // return false;}
}
