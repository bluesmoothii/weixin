package com.weixin.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

public class RightFilter implements Filter {

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		HttpSession session = req.getSession(true);

		// 从session里取的用户名信息
		String username = (String) session.getAttribute("username");
		
		if (StringUtils.isBlank(username)) {
			username = req.getParameter("username");
		}

		// 判断如果没有取到用户信息,就跳转到登陆页面
		if ("bluesmoothii".equals(username)) {
			chain.doFilter(request, response);
		} else {
			req.setAttribute("islogin", "true");
			req.getRequestDispatcher("/login.jsp").forward(req, res);
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
