<%@page language="java"contentType="text/html;charset=utf8"%>  
<form action="api/admin/addUser" method="post">
姓名：<input type="text" name="realName" value=""/><br/>
邮件：<input type="text" name="email" value=""/><br/>
手机：<input type="text" name="contactCell" value=""/><br/>
生日：<input type="text" name="birthTime" value=""/><br/>
<input type="submit" name="保存"/>
</form>