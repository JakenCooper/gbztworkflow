<%@ taglib prefix="sys" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--
  Created by IntelliJ IDEA.
  User: ch
  Date: 2018/10/9
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/common.jsp" %>
<html>
<head>
    <title>预览表单</title>
    <link href="${pageContext.request.contextPath}/static/bootstrap3/css/bootstrap.css" rel="stylesheet"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/static/jquery/jquery-3.3.1.js"></script>

</head>

<body>
<div class="container">
    <div class="page-header">
        <h1>预览表单</h1>
    </div>
    <%= request.getSession().getAttribute("html")%>
</div><!--end container-->
</body>
</html>
