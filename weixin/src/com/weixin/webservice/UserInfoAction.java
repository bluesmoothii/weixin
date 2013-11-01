package com.weixin.webservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.weixin.bean.WeixinInfo;
import com.weixin.service.UserService;
import com.weixin.service.impl.UserServiceImpl;
/**
 * 用户信息
 * @author lyh
 * @version
 */
@Path("info")
public class UserInfoAction {

	@Path("{token}/{id}")
	public String getUserInfo(@Context
	HttpServletRequest req, @Context
	HttpServletResponse res, @PathParam("token")
	String token, @PathParam("id")
	int id) throws Exception {
		UserService impl = new UserServiceImpl();
		Map params = new HashMap();
		params.put("wxUserName", token);
		List<WeixinInfo> lst = impl.findUserByParams(params);
		if (lst.size() > 0) {
			params.clear();
			params.put("id", id);
			WeixinInfo info = impl.findUserByParams(params).get(0);
			HttpSession session = req.getSession();
			session.setAttribute("info", info);
			req.getRequestDispatcher("/userinfo.jsp").forward(req, res);
		}

		return "";
	}
}
