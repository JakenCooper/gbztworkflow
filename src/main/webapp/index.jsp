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
        var adminPath = 'http://10.18.198.183:8080/gbztworkflow';
    </script>
</head>
<body>

    <nav class="navbar navbar-inverse">
        <section class="container-fluid">
            <div class="navbar-header">
                <h2 class="navbar-text">国博政通工作流引擎 </h2>
            </div>
            <div class="pull-right navbar-text">
                &nbsp;&nbsp;<strong> [ 囍 ]  ╮(￣▽￣| ")╭  [ 囍 ]</strong>
            </div>
        </section>
    </nav>
    <section class="container-fluid" id="main">
    </section>


    <div id="section_flowadd"></div>

    <script type="text/javascript" src="${ctxStatic}/bundle.js"></script>

    <script type="text/javascript">

        $(function(){
            $('#index_menugroup .panel-title').click(function(event){
                $(event.target).find('a').click();
            });
            $('#linecanvas').hide();
           /* $('body').mLoading({
                mask:false,
                text:' ☎ 提交ing... ☏'
            });
            $('body').mLoading('hide');*/


            //buss method ...
           /* $('button:contains("页面编辑器")').click(function(e){
                alert(window.currentflowid);
                window.open("{ctx}/formDesign/get?id="+window.currentflowid);
            });*/
        });
    </script>
</body>
</html>
