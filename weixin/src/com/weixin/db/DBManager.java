package com.weixin.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.weixin.util.LoggerHandle;

public class DBManager {
	
	private static DBManager dbPool = new DBManager();
	private ComboPooledDataSource dataSource;

	public DBManager() {
		try {
			Properties prop = new Properties();
			InputStream in = DBManager.class.getResourceAsStream("/jdbc.properties");
			prop.load(in);
			String url = prop.getProperty("url").trim();
			String driver = prop.getProperty("driver").trim();
			String user = prop.getProperty("user").trim();
			String password = prop.getProperty("password").trim();
			dataSource = new ComboPooledDataSource();
			dataSource.setUser(user);
			dataSource.setPassword(password);
			dataSource.setJdbcUrl(url);
			dataSource.setDriverClass(driver);
			// 设置初始连接池的大小！
			dataSource.setInitialPoolSize(100);
			// 设置连接池的最小值！
			dataSource.setMinPoolSize(100);
			// 设置连接池的最大值！
			dataSource.setMaxPoolSize(100);
			// 设置连接池中的最大Statements数量！
			dataSource.setMaxStatements(0);
			// 设置连接池的最大空闲时间！
			dataSource.setMaxIdleTime(60);
			
			dataSource.setAcquireRetryDelay(100);
			dataSource.setBreakAfterAcquireFailure(false);
			in.close();

		} catch (Exception e) {
			LoggerHandle.handle("数据库初始化失败", e);
		}
	}

	public final static DBManager getInstance() {
		if (dbPool == null) {
			return new DBManager();
		}
		return dbPool;
	}

	public final Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("无法从数据源获取连接 ", e);
		}
	}

	public static ResultSet execQuery(String sql, Connection conn) {
		if (conn == null) {
			LoggerHandle.info("数据联接对象为空.不能进行查询操作...");
			return null;
		}
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			return ps.executeQuery(sql);
		} catch (SQLException ex) {
			LoggerHandle.handle("数据库执行查询失败,详细信息为:", ex);
			return null;
		}
	}

	public static void updateQuery(String sql, Connection conn) {
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException ex) {
			LoggerHandle.handle("数据库执行查询失败,详细信息为:", ex);
		}
	}
	
	public static void updateQuery(String sql, Object[] sqlParam, Connection conn) {
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			for (int i = 0; i < sqlParam.length; i++) {
				ps.setObject(i + 1, sqlParam[i]);
			}
			ps.executeUpdate();
		} catch (SQLException ex) {
			LoggerHandle.handle("数据库执行查询失败,详细信息为:", ex);
		}
	}

	/**
	 * 执行数据库的查询方法.外面操作完结果集,请记住调用close方法 list:SQL参数. 调用的时候需传入代替?号的对象数组. 如：new
	 * Object[]{val1,val2,val3}
	 * 
	 * @param sqlstr
	 *            查询sql语句
	 * @param sqlParam
	 *            sql参数
	 * @param conn
	 *            连接对象
	 * @return ResultSet结果集
	 */
	public static ResultSet execQuery(Object[] sqlParam, PreparedStatement ps) {
		if (ps == null) {
			LoggerHandle.info("数据联接对象为空.不能进行查询操作...");
			return null;
		}
		try {
			for (int i = 0; i < sqlParam.length; i++) {
				ps.setObject(i + 1, sqlParam[i]);
			}
			ResultSet rs = ps.executeQuery();
			return rs;
		} catch (SQLException ex) {
			LoggerHandle.info("数据库执行查询失败,详细信息为:" + ex.getMessage());
			return null;
		}
	}
	
	public static ResultSet execQueryDoubleParameter(String sqlstr, Object[] sqlParam, Connection conn,int count) {
		if (conn == null) {
			LoggerHandle.info("数据联接对象为空.不能进行查询操作...");
			return null;
		}
		try {
			PreparedStatement ps = conn.prepareStatement(sqlstr);
			for (int i = 0; i < count; i++) {
				ps.setObject(i + 1, sqlParam[i % 2]);
			}
			ResultSet rs = ps.executeQuery();
			return rs;
		} catch (SQLException ex) {
			LoggerHandle.info("数据库执行查询失败,详细信息为:" + ex.getMessage());
			return null;
		}
	}

	public static void main(String[] args) throws SQLException {
		Connection con = null;
		try {
			con = DBManager.getInstance().getConnection();
			ResultSet rs = DBManager.execQuery("SELECT * FROM cms_resource limit 1", con);
			while (rs.next()) {
				System.out.println(rs.getObject(1));
				System.out.println(rs.getObject(2));
				System.out.println(rs.getObject(3));
			}
		} catch (Exception e) {
		} finally {
			if (con != null)
				con.close();
		}
	}

}
