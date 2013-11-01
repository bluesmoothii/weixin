package com.weixin.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.weixin.bean.WeixinInfo;
import com.weixin.db.DBManager;
import com.weixin.service.UserService;

public class UserServiceImpl implements UserService {
	public void addUser(String realName, String email, String contactCell, String birthTime) {
		Connection conn = DBManager.getInstance().getConnection();
		try {
			String sql = "insert into user_info(realName,contactCell,email,birthTime,modifyTime) values(?,?,?,?,now())";
			DBManager.updateQuery(sql, new Object[] { realName, contactCell, email, birthTime }, conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Boolean validateLogin(String username, String password) {
		String sql = "select * from user where username = ? and password=?";
		Connection conn = DBManager.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, username);
			pst.setString(2, password);
			rs = pst.executeQuery();
			while (rs.next()) {
				return true;
			}

			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void updateUserStatusById(int status, int id) {
		Connection conn = DBManager.getInstance().getConnection();
		String sql = "update user_info set isAudit=?,modifyTime=now() where id=?";
		try {
			DBManager.updateQuery(sql, new Object[] { status, id }, conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<WeixinInfo> findUserByParams(Map params) {
		//String sql = "select id,realName,contactCell,email,wxUserName,info,validateTimes,isAudit,gender,department,photo from user_info order by info desc,id desc";
		StringBuffer sql = new StringBuffer("select id,realName,contactCell,email,wxUserName,info,validateTimes,isAudit,picUrl from user_info where 1=1 ");
		List<WeixinInfo> list = new ArrayList<WeixinInfo>();
		Connection conn = DBManager.getInstance().getConnection();
		
		if (params.containsKey("id")) {
			int id = (Integer) params.get("id");
			sql.append(" and id = " + id);
		}

		if (params.containsKey("wxUserName")) {
			String wxUserName = (String) params.get("wxUserName");
			sql.append(" and wxUserName = '" + wxUserName+"'");
		}
		
		if (params.containsKey("realName")) {
			String realName = (String) params.get("realName");
			sql.append(" and realName like '%" + realName + "%' or userTags like '%" + realName + "%' ");
		}
		
		sql.append(" order by info desc,id desc");
		ResultSet rs = DBManager.execQuery(sql.toString(), conn);
		try {
			while (rs.next()) {
				WeixinInfo info = new WeixinInfo();
				int id = rs.getInt(1);
				String realName = rs.getString(2);
				String contactCell = rs.getString(3);
				String email = rs.getString(4);
				String wxUserName = rs.getString(5);
				String sendInfo = rs.getString(6);
				int validateTimes = rs.getInt(7);
				int isAudit = rs.getInt(8);
				String picUrl = rs.getString(9);
				
				if (StringUtils.isBlank(picUrl)) {
					picUrl = "";
				}
				/*int gender = rs.getInt(9);
				int department = rs.getInt(10);
				String photo = rs.getString(11);*/
				info.setId(id);
				info.setRealName(realName);
				info.setEmail(email);
				info.setContactCell(contactCell);
				info.setSendInfo(sendInfo);
				info.setWxUserName(wxUserName);
				info.setValidateTimes(validateTimes);
				info.setIsAudit(isAudit);
				info.setPicUrl(picUrl);
				/*info.setGender(gender);
				info.setPhoto(photo);
				info.setDepartment(department);*/
				
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

}
