<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/common.jsp"%>
<html>
<head>
    <title>测试模板页面</title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/bootstrap3/css/bootstrap.css">
    <script src="${ctxStatic}/require.js"></script>
    <script type="text/javascript">
        require(['${ctxStatic}/base.js'],function(){
            require(['modules/test/test'],function(testjs){});
        });
    </script>
</head>
<body>
    <section class="container-fluid">
        <h2>Hello</h2>
        <button class="btn btn-primary">Click</button>
    </section>
</body>
</html>
