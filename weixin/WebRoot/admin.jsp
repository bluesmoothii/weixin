<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'admin.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  <c:if test="${admin=='admin' }">
  <body>
  	<h2 align="center">微信帐号审核</h2><a href="login.do?logout=1">退出登录</a>
    <table align="center" width="70%" border=1>
    <tr>
    	<th>姓名</th>
    	<!--  <th>照片</th>
    	<th>性别</th>
    	<th>部门</th>-->
    	<th>手机</th>
    	<th>邮箱</th>
    	<th>绑定</th>
    	<!-- <th>操作</th> -->
    </tr>
   <c:forEach var="wx" items="${list}"> 
    <tr>
	    <td>${wx.realName } ${wx.wxUserName }</td>
	    <!-- <td>${wx.photo }</td>
	    <td><c:if test="#wx.gender==1">男</c:if> <c:if test="#wx.gender==2">女</c:if></td>
	    <td>
	    <c:forEach items="$map" var="m">
	    	<c:if test="${my.key}=='#wx.department'">${m.value }</c:if>
	    </c:forEach>
	    </td>-->
	    <td>${wx.contactCell } </td>
	    <td>${wx.email }</td>
	    <td>
	    ${wx.sendInfo} <br/>
	    <c:choose>
	    	 <c:when test="${wx.isAudit ==1}">
	    		已绑定
	    		<a href="admin/0/${wx.id}">未绑定</a>
	    	 </c:when> 
	    	<c:when test="${wx.validateTimes ==3}">
	    		<a href="admin//1/${wx.id}">绑定</a>
	    		禁用
	    	</c:when>
	    	
	    	<c:otherwise>  
	    		<a href="admin/1/${wx.id}">绑定</a>
	    		禁用
	    	</c:otherwise>
	    </c:choose>
	    </td>
	    <!--<td>
	      <a href="">编辑</a>
	    </td>-->
    </tr>
    </c:forEach>
    </table>
  </body>
  </c:if>
</html>
