package com.weixin.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.weixin.bean.WeixinInfo;
import com.weixin.service.UserService;
import com.weixin.service.impl.UserServiceImpl;
import com.weixin.util.LoggerHandle;
import com.weixin.util.MD5Util;
/**
 * 用户管理，后台管理
 * @author lyh
 * @version
 */
@Path("admin")
public class UserManager {

	@Path("addUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String addUser(@Context
	HttpServletRequest req, @Context
	HttpServletResponse res, @FormParam("realName")
	String realName, @FormParam("contactCell")
	String contactCell, @FormParam("email")
	String email, @FormParam("birthTime")
	String birthTime) throws Exception {
		UserService impl = new UserServiceImpl();
		impl.addUser(realName, email, contactCell, birthTime);
		req.getRequestDispatcher("/addUser.jsp").forward(req, res);
		return null;
	}

	@GET
	@Path("admin")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String userLogin(@Context
	HttpServletRequest req, @Context
	HttpServletResponse res) {
		req.setAttribute("islogin", "true");
		try {
			req.getRequestDispatcher("/login.jsp").forward(req, res);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Path("admin")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String userLogin(@Context
	HttpServletRequest req, @Context
	HttpServletResponse res, @FormParam("username")
	String username, @FormParam("password")
	String password) {
		HttpSession session = req.getSession();
		Integer failTimes = (Integer) session.getAttribute("failTimes");

		if (failTimes == null) {
			failTimes = 0;
		}

		try {
			if (failTimes >= 3) {
				req.getRequestDispatcher("/index.jsp").forward(req, res);
			} else {

				String logout = req.getParameter("logout");

				if ("1".equals(logout)) {
					session.removeAttribute("username");
				}

				String md5 = MD5Util.md5(password);
				UserService userService = new UserServiceImpl();
				Boolean isLogin = userService.validateLogin(username, md5);

				String ip = getIp(req);
				LoggerHandle.info("ip地址：" + ip + "正在登录" + isLogin);
				if (isLogin) {
					session.setAttribute("username", username);
					res.sendRedirect("index");
				} else {
					req.setAttribute("islogin", "true");
					req.getRequestDispatcher("/login.jsp").forward(req, res);
					session.setAttribute("failTimes", failTimes + 1);
				}
			}
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Path("index")
	public String adminManager(@Context
	HttpServletRequest req, @Context
	HttpServletResponse res) throws Exception {
		UserService impl = new UserServiceImpl();
		List<WeixinInfo> list = new ArrayList<WeixinInfo>();
		try {
			list = impl.findUserByParams(new HashMap());
		} catch (Exception e) {
			LoggerHandle.handle("后台数据库连接出错", e);
		}

		req.setAttribute("list", list);
		req.setAttribute("admin", "admin");
		// req.setAttribute("map", departmentMap);

		req.getRequestDispatcher("/admin.jsp").forward(req, res);
		return null;
	}

	@Path("index/{status}/{id}")
	public String updateUserStatus(@Context
	HttpServletRequest req, @Context
	HttpServletResponse res, @PathParam("status")
	int status, @PathParam("id")
	int id) throws Exception {
		UserService impl = new UserServiceImpl();
		impl.updateUserStatusById(status, id);

		return adminManager(req, res);
	}

	private String getIp(HttpServletRequest req) {
		String ip = req.getHeader("HTTP_X_FORWARDED_FOR");
		if (ip != null && ip.indexOf(".") > 1) {
			return ip;
		}

		ip = req.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("WL-Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getRemoteAddr();
		}

		ip = ip.indexOf(",") != -1 ? ip.split(",")[0] : ip;

		if (ip.length() > 15) {
			ip = ip.substring(0, 15);
		}
		return ip;
	}

}
