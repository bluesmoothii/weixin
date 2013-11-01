<%@page language="java"contentType="text/html;charset=gb2312"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>  
    <head>  
        <title>Login.jsp</title>  
    </head>  
    <c:if test="${islogin==true }">
    <body style="text-align: center">  
        <form action="guessoneguess" method="post">  
            <table>  
                
                <tr>  
                    <td>ÓÃ»§Ãû</td>  
                        <td><input type="text"name="username"/></td>  
  
                </tr>  
                <tr>  
                    <td>ÃÜ&nbsp;&nbsp;Âë:</td>  
                    <td><input type="password"name="password"></td>  
                </tr>  
                <tr>  
                    <td>&nbsp;</td>  
                    <td>  
                        <input type="submit"name="submit"value="µÇÂ½"/>&nbsp;  
                    </td>  
                </tr>  
            </table>  
  
        </form>  
          
    </body> 
    </c:if> 
</html>  