<%--
  Created by IntelliJ IDEA.
  User: ch
  Date: 2018/10/8
  Time: 14:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/common.jsp" %>
<html>
<head>
    <title>表单设计器</title>
    <link href="${pageContext.request.contextPath}/static/bootstrap3/css/bootstrap.css" rel="stylesheet"
          type="text/css"/>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/static/jquery/jquery-3.3.1.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/static/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/static/ueditor/ueditor.all.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/static/ueditor/lang/zh-cn/zh-cn.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${pageContext.request.contextPath}/static/ueditor/formdesign/leipi.formdesign.v4.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/message/message.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/message/message.js"></script>
</head>
<body>
<style type="text/css">


    input[type=radio]:checked::after {
        content: "";
        display: block;
        position: absolute;
        top: 2px;
        left: 2px;
        right: 0;
        bottom: 0;
        width: 6px;
        height: 6px;
        background-color: #ffc832;
    }
    input[type=radio], input[type=radio]:checked::after {
        border-radius: 50%;
    }
</style>
<div style="padding: 20px;">
    <form id="frm" method="post" target="mywin">
        <div style="font-size: 40px;width:100%;text-align: center;">表单设计</div>
        <button type="button" onclick="leipiFormDesign.exec('text',GetRequest());" class="btn btn-info btn-small">单行输入框</button>
        <button type="button" onclick="leipiFormDesign.exec('textarea',GetRequest());" class="btn btn-info btn-small">多行输入框</button>
        <button type="button" onclick="leipiFormDesign.exec('select',GetRequest());" class="btn btn-info btn-small">下拉菜单</button>
        <button type="button" onclick="leipiFormDesign.exec('radios',GetRequest());" class="btn btn-info btn-small">单选框</button>
        <button type="button" onclick="leipiFormDesign.exec('checkboxs',GetRequest());" class="btn btn-info btn-small">复选框</button>
        <%--<button type="button" onclick="leipiFormDesign.exec('macros',GetRequest());" class="btn btn-info btn-small">宏控件</button>--%>
        <button type="button" onclick="leipiFormDesign.exec('file',GetRequest());" class="btn btn-info btn-small">文件选择</button>
        <label><input type="radio" name="mode" value="0"/>紧凑模式</label>
        <label><input type="radio" name="mode" value="1" checked="checked"/>非紧凑模式</label>
       <%-- <button type="button" onclick="leipiFormDesign.exec('progressbar');" class="btn btn-info btn-small">进度条</button>--%>
       <%-- <button type="button" onclick="leipiFormDesign.exec('qrcode');" class="btn btn-info btn-small">二维码</button>--%>
        <div class="alert alert-warning">
            <button type="button" class="close" data-dismiss="alert">×</button>
            <strong>提醒：</strong>单选框和复选框，如：<code>{|-</code>选项<code>-|}</code>两边边界是防止误删除控件，程序会把它们替换为空，请不要手动删除！     树结构人员选择控件请复制: <code>"\${selectTree}"粘贴至相应位置</code> || 时间选择控件请复制: <code>"\${timeSelect}"粘贴至相应位置</code>
        </div>
        <textarea style="width:100%;height:100%" name="html" id="container"></textarea></form>

</div>
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">新增</h4>
            </div>
            <div class="modal-body">

                <div class="form-group">
                    <label for="txt_departmentname">表单名称</label>
                    <input type="text" name="txt_departmentname" class="form-control" id="txt_departmentname" placeholder="部门名称">
                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>关闭</button>
                <button type="button" id="btn_submit" class="btn btn-primary" data-dismiss="modal"><span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>保存</button>
            </div>
        </div>
    </div>
</div>
</div>
<script type="text/javascript">
    function treeselect(){
        UE.getEditor('container').execCommand('insertHtml', '${treeSelect}')
    }
    function GetRequest() {
        var url = location.search; //获取url中"?"符后的字串
        var theRequest = new Object();
        if (url.indexOf("?") != -1) {
            var str = url.substr(1);
            strs = str.split("&");
            for(var i = 0; i < strs.length; i ++) {
                theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
            }
        }
        var json=JSON.stringify(theRequest);
        var currentflowid=JSON.parse(json).id;
        /*document.cookie = "currentflowid="+currentflowid;*/
        return currentflowid;
    }

</script>
<!-- script start-->
<script type="text/javascript">
    $(function () {
        GetRequest();
       /* UE.getEditor('container').execCommand('insertHtml', $('#testcon').html());*/
    });
    var leipiEditor = UE.getEditor('container', {
        //allowDivTransToP: false,//阻止转换div 为p
        toolleipi: true,//是否显示，设计器的 toolbars
        tableDragable: false,
        textarea: 'design_content',
        //这里可以选择自己需要的工具按钮名称,此处仅选择如下五个
      /*  toolbars: [[
            'edittd', 'fullscreen', 'source', '|', 'undo', 'redo', '|', 'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'removeformat', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', '|', 'fontfamily', 'fontsize', '|', 'indent', '|', 'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'link', 'unlink', '|', 'horizontal', 'spechars', 'wordimage', '|', 'inserttable', 'deletetable', 'mergecells', 'splittocells',
            'insertparagraphbeforetable','splittocols','splittorows','formatmatch','mergeright'
        ]],*/
        toolbars: [[
            'fullscreen', 'source', '|', 'undo', 'redo', '|',
            'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'superscript', 'subscript', 'removeformat', 'formatmatch', 'autotypeset', 'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'selectall', 'cleardoc', '|',
            'rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
            'customstyle', 'paragraph', 'fontfamily', 'fontsize', '|',
            'directionalityltr', 'directionalityrtl', 'indent', '|',
            'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'touppercase', 'tolowercase', '|',
            'link', 'unlink', 'anchor', '|', 'imagenone', 'imageleft', 'imageright', 'imagecenter', '|',
             'emotion',  'insertcode', , 'pagebreak', 'template', 'background', '|',
            'horizontal', 'snapscreen', 'wordimage', '|',
            'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', 'charts', '|',
            'print', 'searchreplace', 'drafts', 'help','edittd'
        ]],

        //focus时自动清空初始化时的内容
        //autoClearinitialContent:true,
        //关闭字数统计
        wordCount: false,
        //关闭elementPath
        elementPathEnabled: false,
        //默认的编辑区域高度
        initialFrameHeight: 500,
        scaleEnabled:true,
        //禁止表格嵌套
        //,iframeCssUrl:"/Public/css/bootstrap/css/bootstrap.css" //引入自身 css使编辑器兼容你网站css
        //更多其他参数，请参考ueditor.config.js中的配置项
    });

    var leipiFormDesign = {
        /*执行控件*/
        exec: function (method,currentflowid) {
            leipiEditor.execCommand(method,currentflowid);
        },
        /*
            Javascript 解析表单
            template 表单设计器里的Html内容
            fields 字段总数
        */
        parse_form: function (template, fields) {
            //正则  radios|checkboxs|select 匹配的边界 |--|  因为当使用 {} 时js报错
            var preg = /(\|-<span(((?!<span).)*leipiplugins=\"(radios|checkboxs|select)\".*?)>(.*?)<\/span>-\||<(img|input|textarea|select).*?(<\/select>|<\/textarea>|\/>))/gi,
                preg_attr = /(\w+)=\"(.?|.+?)\"/gi, preg_group = /<input.*?\/>/gi;
            if (!fields) fields = 0;

            var template_parse = template, template_data = new Array(), add_fields = new Object(), checkboxs = 0;

            var pno = 0;
            template.replace(preg, function (plugin, p1, p2, p3, p4, p5, p6) {
                var parse_attr = new Array(), attr_arr_all = new Object(), name = '', select_dot = '',
                    is_new = false;
                var p0 = plugin;
                var tag = p6 ? p6 : p4;
                //alert(tag + " \n- t1 - "+p1 +" \n-2- " +p2+" \n-3- " +p3+" \n-4- " +p4+" \n-5- " +p5+" \n-6- " +p6);

                if (tag == 'radios' || tag == 'checkboxs') {
                    plugin = p2;
                } else if (tag == 'select') {
                    plugin = plugin.replace('|-', '');
                    plugin = plugin.replace('-|', '');
                }
                plugin.replace(preg_attr, function (str0, attr, val) {
                    if (attr == 'name') {
                        if (val == 'leipiNewField') {
                            is_new = true;
                            fields++;
                            val = 'data_' + fields;
                        }
                        name = val;
                    }

                    if (tag == 'select' && attr == 'value') {
                        if (!attr_arr_all[attr]) attr_arr_all[attr] = '';
                        attr_arr_all[attr] += select_dot + val;
                        select_dot = ',';
                    } else {
                        attr_arr_all[attr] = val;
                    }
                    var oField = new Object();
                    oField[attr] = val;
                    parse_attr.push(oField);
                })
                /*alert(JSON.stringify(parse_attr));return;*/
                if (tag == 'checkboxs') /*复选组  多个字段 */
                {
                    plugin = p0;
                    plugin = plugin.replace('|-', '');
                    plugin = plugin.replace('-|', '');
                    var name = 'checkboxs_' + checkboxs;
                    attr_arr_all['parse_name'] = name;
                    attr_arr_all['name'] = '';
                    attr_arr_all['value'] = '';

                    attr_arr_all['content'] = '<span leipiplugins="checkboxs"  title="' + attr_arr_all['title'] + '">';
                    var dot_name = '', dot_value = '';
                    p5.replace(preg_group, function (parse_group) {
                        var is_new = false, option = new Object();
                        parse_group.replace(preg_attr, function (str0, k, val) {
                            if (k == 'name') {
                                if (val == 'leipiNewField') {
                                    is_new = true;
                                    fields++;
                                    val = 'data_' + fields;
                                }

                                attr_arr_all['name'] += dot_name + val;
                                dot_name = ',';

                            }
                            else if (k == 'value') {
                                attr_arr_all['value'] += dot_value + val;
                                dot_value = ',';

                            }
                            option[k] = val;
                        });

                        if (!attr_arr_all['options']) attr_arr_all['options'] = new Array();
                        attr_arr_all['options'].push(option);

                        //if(!option['checked']) option['checked'] = '';
                        var checked = option['checked'] != undefined ? 'checked="checked"' : '';
                        attr_arr_all['content'] += '<input type="checkbox" name="' + option['name'] + '" value="' + option['value'] + '"  ' + checked + '/>' + option['value'] + '&nbsp;';

                        if (is_new) {
                            var arr = new Object();
                            arr['name'] = option['name'];
                            arr['leipiplugins'] = attr_arr_all['leipiplugins'];
                            add_fields[option['name']] = arr;

                        }

                    });
                    attr_arr_all['content'] += '</span>';

                    //parse
                    template = template.replace(plugin, attr_arr_all['content']);
                    template_parse = template_parse.replace(plugin, '{' + name + '}');
                    template_parse = template_parse.replace('{|-', '');
                    template_parse = template_parse.replace('-|}', '');
                    template_data[pno] = attr_arr_all;
                    checkboxs++;

                } else if (name) {
                    if (tag == 'radios') /*单选组  一个字段*/
                    {
                        plugin = p0;
                        plugin = plugin.replace('|-', '');
                        plugin = plugin.replace('-|', '');
                        attr_arr_all['value'] = '';
                        attr_arr_all['content'] = '<span leipiplugins="radios" name="' + attr_arr_all['name'] + '" title="' + attr_arr_all['title'] + '">';
                        var dot = '';
                        p5.replace(preg_group, function (parse_group) {
                            var option = new Object();
                            parse_group.replace(preg_attr, function (str0, k, val) {
                                if (k == 'value') {
                                    attr_arr_all['value'] += dot + val;
                                    dot = ',';
                                }
                                option[k] = val;
                            });
                            option['name'] = attr_arr_all['name'];
                            if (!attr_arr_all['options']) attr_arr_all['options'] = new Array();
                            attr_arr_all['options'].push(option);
                            //if(!option['checked']) option['checked'] = '';
                            var checked = option['checked'] != undefined ? 'checked="checked"' : '';
                            attr_arr_all['content'] += '<input type="radio" name="' + attr_arr_all['name'] + '" value="' + option['value'] + '"  ' + checked + '/>' + option['value'] + '&nbsp;';

                        });
                        attr_arr_all['content'] += '</span>';

                    } else {
                        attr_arr_all['content'] = is_new ? plugin.replace(/leipiNewField/, name) : plugin;
                    }
                    //attr_arr_all['itemid'] = fields;
                    //attr_arr_all['tag'] = tag;
                    template = template.replace(plugin, attr_arr_all['content']);
                    template_parse = template_parse.replace(plugin, '{' + name + '}');
                    template_parse = template_parse.replace('{|-', '');
                    template_parse = template_parse.replace('-|}', '');
                    if (is_new) {
                        var arr = new Object();
                        arr['name'] = name;
                        arr['leipiplugins'] = attr_arr_all['leipiplugins'];
                        add_fields[arr['name']] = arr;
                    }
                    template_data[pno] = attr_arr_all;


                }
                pno++;
            })
            var parse_form = new Object({
                'fields': fields,//总字段数
                'template': template,//完整html
                'parse': template_parse,//控件替换为{data_1}的html
                'data': template_data,//控件属性
                'add_fields': add_fields//新增控件
            });
        /*    var str=UE.getEditor("container");
            var htmlInfo=str.getAllHtml();*/
            var test=JSON.stringify(parse_form);
            return JSON.stringify(parse_form);
        },
        /*type  =  save 保存设计 versions 保存版本  close关闭 */
        fnCheckForm: function (type) {
            var mode=$("input[name='mode']:checked").val();
            if (leipiEditor.queryCommandState('source'))
                leipiEditor.execCommand('source');//切换到编辑模式才提交，否则有bug

            if (leipiEditor.hasContents()) {
                leipiEditor.sync();
                /*同步内容*/

                // alert("你点击了保存,这里可以异步提交....");
                //return false;

                var type_value = '', formid = 0, fields = $("#fields").val(), formeditor = '';

                if (typeof type !== 'undefined') {
                    type_value = type;
                }
                //获取表单设计器里的内容
                formeditor = leipiEditor.getContent();
                //解析表单设计器控件
                var parse_form = this.parse_form(formeditor, fields);
                //alert(parse_form);


                $("#leipi_type").val(type_value);
                $("#leipi_parse_form").val(parse_form);
                $("#saveform").attr("target", "_blank");
                $("#saveform").attr("action", "/index/parse.html");

                $("#saveform").submit();
                //$("#myModal").modal('show');
                //异步提交数据
                $.ajax({
                   type: 'POST',
                   url : '${ctx}/formDesign/save',
                   dataType : 'json',
                   data : {'type' : type_value,'formid':formid,'parse_form':parse_form,'currentFlowId':GetRequest(),'mode':mode},
                   success : function(data){
                     if(data.success==1){
                         $.message('保存成功');
                         $('#submitbtn').button('reset');
                     }else{
                         alert('保存失败！');
                     }
                   }
               });

            } else {
                alert('表单内容不能为空！')
                $('#submitbtn').button('reset');
                return false;
            }
        },
        /*预览表单*/
        fnReview: function () {
            if (leipiEditor.queryCommandState('source'))
                leipiEditor.execCommand('source');
            /*切换到编辑模式才提交，否则部分浏览器有bug*/

            if (leipiEditor.hasContents()) {
                leipiEditor.sync();
                /*同步内容*/

                /*设计form的target 然后提交至一个新的窗口进行预览*/
                document.saveform.target = "mywin";
                window.open('', 'mywin', "menubar=0,toolbar=0,status=0,resizable=1,left=0,top=0,scrollbars=1,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + "\"");

                document.saveform.action = "/index/preview.html";
                document.saveform.submit(); //提交表单
            } else {
                alert('表单内容不能为空！');
                return false;
            }
        }
    };


</script>
<!-- script end -->
<code id="testcon" style="display: none">
    ${html}
</code>
<script type="text/javascript">
    $(function () {
        window.setTimeout(setContent,1000);//一秒后再调用赋值方法
    });
    function setContent(){
        UE.getEditor('container').execCommand('insertHtml', $('#testcon').html());
    }
</script>
</body>
</html>
