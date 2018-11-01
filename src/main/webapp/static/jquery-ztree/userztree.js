var treearr = new Array();

function checkele(e,treeId,treeNode){
    var treeObj=$.fn.zTree.getZTreeObj(treeId),
        nodes=treeObj.getCheckedNodes(true),
        v="";
    for(var i=0;i<nodes.length;i++){
        v+=nodes[i].name + ",";
        if(nodes[i].value!='invalid'){
           // alert(nodes[i].id); //获取选中节点的值
        }
    }
}
function userTreeCheckAllOuter(treeId,tag){
    if(typeof tag == 'undefined'){
        tag = true;
    }
    var treeObj=$.fn.zTree.getZTreeObj(treeId);
    treeObj.checkAllNodes(tag);
}

var tree, setting = {
    view:{
        selectedMulti:false,
        dblClickExpand:false,
        showLine: true,
        fontCss:{'color':'pink','font-weight':'bold'}
    },
    check:{
        enable:true,
        nocheckInherit:true,
        chkStyle: "checkbox"
    },
    data:{
        simpleData:{enable:true,
            idKey: "id",
            pIdKey: "parentId",
            rootPId: "-1"
        }}
    /*,callback:{
        beforeClick: function(treeId, treeNode) {
            zTree = $.fn.zTree.getZTreeObj("user_tree");
            if (treeNode.isParent) {
                zTree.expandNode(treeNode);//如果是父节点，则展开该节点
            }else{
                zTree.checkNode(treeNode, !treeNode.checked, true, true);//单击勾选，再次单击取消勾选
            }
        },
        onCheck:checkele
    }*/
};