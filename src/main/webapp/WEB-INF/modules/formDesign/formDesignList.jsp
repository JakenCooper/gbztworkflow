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
    <title>表单设计列表</title>
    <link href="${pageContext.request.contextPath}/static/bootstrap3/css/bootstrap.css" rel="stylesheet"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/static/jquery/jquery-3.3.1.js"></script>

</head>
<table class="table  table-bordered table-condensed">
    <thead>
    <tr>
        <th style="text-align: center">id</th>
        <th style="text-align: center">创建时间</th>
        <th style="text-align: center">操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${list}" var="formDesign">
        <tr>
            <td style="text-align: center">
                    ${formDesign.id}
            </td>
            <td style="text-align: center">
                    ${formDesign.createDate}
            </td>
            <td>
                <a class="btn bg-info" href="${ctx}/formDesign/get?id=${formDesign.id}">设计</a>
                &nbsp;
                <a class="btn bg-danger" onclick="deleteFormDesign('${formDesign.id}')">删除</a>
                &nbsp;
                <a class="btn btn-default" href="${ctx}/formDesign/preView?id=${formDesign.id}">预览</a>
                <%--<a class="btn bg-danger" href="${ctx}/formDesign/deleteById?id=${formDesign.id}">测试</a>--%>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<body>

<script type="text/javascript">

    function deleteFormDesign(str) {
        $.ajax({
            type:'post',
            url:'${pageContext.request.contextPath}/formDesign/deleteById',
            dataType:'json',
            data:{"id":str},
            success:function(msg){
                if(msg.flag=="1"){
                    alert("删除成功");
                    window.location.href="${ctx}/formDesign/list";
                }else {
                    alert("删除失败");
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                // 状态码
                console.log(XMLHttpRequest.status);
                // 状态
                console.log(XMLHttpRequest.readyState);
                // 错误信息
                console.log(textStatus);
            }

        });
    }

</script>
</body>
</html>
