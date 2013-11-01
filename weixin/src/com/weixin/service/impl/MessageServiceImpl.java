package com.weixin.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.weixin.bean.WeixinInfo;
import com.weixin.db.DBManager;
import com.weixin.service.MessageService;
import com.weixin.service.UserService;

public class MessageServiceImpl implements MessageService {

	public Integer updateUserInfo(String info, String userName, Boolean isunbind) {
		String[] strs = info.split(" ");
		String realName = strs[0];

		String sql = "select contactCell,isAudit,validateTimes from user_info where realName = '" + realName + "'";

		Connection conn = DBManager.getInstance().getConnection();
		ResultSet rs = DBManager.execQuery(sql, conn);
		try {
			if (rs.next()) {
				int isAudit = rs.getInt(2);
				int validateTime = rs.getInt(3);

				if (validateTime >= 3) {
					return 3;
				}

				// 已经验证
				if (isAudit == 1 && !isunbind) {
					return 2;
				}

				String updateSql = "update user_info set info='" + info + "',wxUserName='" + userName + "',validateTimes="
						+ (validateTime + 1) + " where realName='" + realName + "'";
				DBManager.updateQuery(updateSql, conn);

				String cellphone = rs.getString(1);
				if (strs.length>1 && cellphone.endsWith(strs[1])) {
					if (isAudit == 1 && isunbind) {
						String updateSql1 = "update user_info set isAudit = 0" + " where realName='" + realName + "'";
						DBManager.updateQuery(updateSql1, conn);
						// 解绑
						return 4;
					}

					String updateSql1 = "update user_info set isAudit = 1" + " where realName='" + realName + "'";
					DBManager.updateQuery(updateSql1, conn);
					return 1;// 验证通过
				}
				return -(validateTime + 1);// 验证失败
			} else {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public String addUserTags(String info) {
		String[] strs = info.split("，");
		String realName = strs[0];
		String userTags = strs[1];
		String sql = "select id,userTags from user_info where realName = '" + realName + "'";
		String update = "update user_info set userTags = ? where id = ?";
		Connection conn = DBManager.getInstance().getConnection();
		ResultSet rs = DBManager.execQuery(sql, conn);
		try {
			PreparedStatement pst = conn.prepareStatement(update);
			if (rs.next()) {
				int id = rs.getInt(1);
				String tags = rs.getString(2) == null ? "" : rs.getString(2);
				if (!tags.contains(userTags)) {
					pst.setString(1, tags + "," + userTags);
					pst.setInt(2, id);
					pst.executeUpdate();
				}
				return "标签添加成功！";
			} else {
				return "系统没有该姓名：" + strs[0];
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	public Boolean isWhiteUserName(String userName) {
		String sql = "select * from user_info where wxUserName=? and isAudit =1";
		Connection conn = DBManager.getInstance().getConnection();
		Boolean isWhite = false;
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = DBManager.execQuery(new Object[] { userName }, pst);
			isWhite = rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return isWhite;
	}
	
	private int diffDay(String date1, String date2) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		Date d1 = sdf.parse(date1);
		Date d2 = sdf.parse(date2);
		return (int) ((d1.getTime() - d2.getTime()) / (3600 * 1000 * 24));
	}
	

	public String findContactCell(String realName) {
		String sql = "select contactCell,realName,birthTime from user_info where realName like ? or userTags like ?";
		Connection conn = DBManager.getInstance().getConnection();
		StringBuilder strs = new StringBuilder("");
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			ResultSet rs = DBManager.execQuery(new Object[] { "%" + realName + "%", "%" + realName + "%" }, pst);
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
			while (rs.next()) {
				String contactCell = rs.getString(1);
				String name = rs.getString(2);
				Date birthTime = rs.getDate(3);
				int diffDate = 0;
				if (birthTime != null) {
					String birth = sdf.format(birthTime);
					String now = sdf.format(new Date());
					try {
						diffDate = diffDay(birth, now);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if (contactCell == null || contactCell.equals("")) {
					contactCell = "系统没有收录该用户的手机号码。";
				}
				if (name.trim().equals(realName.trim())) {
					strs.append(name + "的手机号码：" + contactCell);
					if(diffDate < 30 && diffDate > 0) {
						strs.append("\r\n"+name+"的生日快到了哦。");
					}
					return strs.toString();
				} else {
					if (rs.isFirst()) {
						strs.append("标签：“" + realName + "“的用户有：\r\n");
					}
					strs.append(name + "：" + contactCell + "\r\n");
				}
			}
			if (strs.toString().length() > 2) {
				return strs.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		String str = "";
		if (realName.length() > 4) {
			str = "请检查输入的内容，暂时只支持姓名和标签搜索。";
		} else {
			str = "没有收录该姓名：" + realName;
		}
		return str;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void updateInfoByParams(String wxUserName, Map params) {
		Connection conn = DBManager.getInstance().getConnection();
		try {
			StringBuffer str = new StringBuffer("");
			if (params.containsKey("phone")) {
				String phone = (String) params.get("phone");
				str.append("update user_info set contactCell='" + phone + "' ");
			} else if (params.containsKey("picUrl")) {
				String picUrl = (String) params.get("picUrl");
				str.append("update user_info set picUrl='" + picUrl + "' ");
			}
			
			str.append(" where wxUserName = '" + wxUserName + "' and isAudit=1");
			DBManager.updateQuery(str.toString(), conn);
		} catch (Exception e) {

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getPhoneBylastNumbers (String lastnumber) {
		Connection conn = DBManager.getInstance().getConnection();
		ResultSet rs = null;
		StringBuilder str = new StringBuilder("");
		try {
			String sql = "select realName from user_info where contactCell like '%"+lastnumber+"'";
			rs = DBManager.execQuery(sql, conn);
			if (rs.next()) {
				String realName = rs.getString(1);
				str.append("该号码的主人是："+realName);
				return str.toString();
			} 
			return "系统没有手机尾号是"+lastnumber+"的用户";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public WeixinInfo getPhotoMessageInfo(String name) {
		UserService service = new UserServiceImpl();
		Map params = new HashMap();
		params.put("realName", name);
		WeixinInfo info = service.findUserByParams(params).get(0);
		return info;
	}

}
