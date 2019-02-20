<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/common.jsp"%>
<html>
<head>
    <title>国博政通工作流引擎</title>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/bootstrap3/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/bootstrap-treeview/bootstrap-treeview.css">
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/mloading/jquery.mloading.css">
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/index.css">
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/jquery-ztree/css/metroStyle/metroStyle.css">

    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-3.3.1.js"></script>
    <script type="text/javascript" src="${ctxStatic}/bootstrap3/js/bootstrap.js"></script>
    <script type="text/javascript" src="${ctxStatic}/mloading/jquery.mloading.js"></script>
    <script type="text/javascript" src="${ctxStatic}/bootstrap-treeview/bootstrap-treeview.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-ztree/js/jquery.ztree.all-3.5.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-ztree/js/jquery.ztree.exhide-3.5.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-ztree/userztree.js"></script>

    <script type="text/javascript">
        var alert_tag = true;
        // var adminPath = 'http://localhost:8080/gbztworkflow';
        var adminPath = 'http://localhost:8666/gw';
    </script>
</head>
<body>

<nav class="navbar navbar-inverse" >
    <section class="container-fluid">
        <div class="navbar-header">
            <h2 class="navbar-text" id="logoTitle">工作流引擎 </h2>
        </div>
        <div class="pull-right navbar-text navTitleText">
            <a href="#"> <span class="glyphicon glyphicon-user"></span>&nbsp;admin</a>
            <a href="#">  <span class="glyphicon glyphicon-off"></span>&nbsp;退出</a>
            &nbsp;&nbsp;&nbsp;
            <!--<span class="glyphicon glyphicon-off">&nbsp;退出</span>-->
        </div>
    </section>
</nav>
<section class="container-fluid" id="main">
</section>


<div id="section_flowadd"></div>
<div id="section_bussadd"></div>

<script type="text/javascript" src="${ctxStatic}/bundle.js"></script>
</body>
</html>
