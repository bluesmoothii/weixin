package com.weixin.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LoggerHandle {

	private static Log logger = LogFactory.getLog(LoggerHandle.class);

	public static void handle(String message, Throwable throwable) {
		StringBuffer errMsgBuf = new StringBuffer();
		errMsgBuf.append("\r\n======== 异常日志开始 ========\r\n");
		errMsgBuf.append("异常描述: " + message + "\r\n");
		errMsgBuf.append("异常来源:  \r\n");
		errMsgBuf.append("\r======== 异常日志结束 ========");
		logger.error(errMsgBuf.toString() + "\n\r\n异常信息详细信息:", throwable);
	}

	public static void error(String message) {
		StringBuffer errMsgBuf = new StringBuffer();
		errMsgBuf.append("\r\n======== 异常日志开始 ========\r\n");
		errMsgBuf.append("异常描述: " + message + "\r\n");
		errMsgBuf.append("异常来源: \r\n");
		errMsgBuf.append("\r======== 异常日志结束 ========");
	}
	
	public static void info(String message) {
		logger.info( message);
	}

	public static void error(Throwable throwable) {
		logger.error(throwable);
	}
}
