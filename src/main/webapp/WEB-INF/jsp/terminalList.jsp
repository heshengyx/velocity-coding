<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<!DOCTYPE html>
<html>
    <head>
        <title>终端管理-数据收集平台</title>
        <style type="text/css">
        .tag-content {
            font-size: 12px;
            padding: 15px;
        }
        .td-right {
            width: 80px;
            text-align: right;
        }
        .td-center {
            width: 80px;
            text-align: center;
        }
        </style>
    </head>
    
    <body>
    <!-- content -->
    <div class="tag-content">
        <!-- search -->
    <form id="searchForm" method="post">
    <table>
        <tr>
        	<td class="td-right">终端名称：</td>
<td><input type="text" id="nameSearch" style="width:150px;"></td>
<td class="td-right">终端编码：</td>
<td><input type="text" id="codeSearch" style="width:150px;"></td>

            <td class="td-right">创建时间：</td>
            <td>
            <input class="easyui-datebox" type="text" id="createDateBegin" style="width:100px;">~
            <input class="easyui-datebox" type="text" id="createDateEnd" style="width:100px;"></td>
        </tr>
        <tr>
            <td></td>
            <td colspan="5">
            <a class="easyui-linkbutton" id="searchBtn" href="javascript:void(0);" data-options="iconCls:'icon-search'">查询</a>&nbsp;
            <a class="easyui-linkbutton" id="resetBtn" href="javascript:void(0);" data-options="iconCls:'icon-back'">重置</a>
            </td>
        </tr>
    </table>
    </form>
    <!-- search -->
        <!-- datagrid -->
    <div id="tools">
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" id="addBtn">新增</a>
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" id="addBatchBtn">批量</a>
        <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" id="deleteBtn">删除</a>
    </div>
    <table id="datagrid" style="width:100%;"></table>
    <!-- datagrid -->
    <!-- 新增 -->
    <div class="easyui-window" id="editWin" data-options="modal:true,closed:true,resizable:false,
                minimizable:false,
                maximizable:false,
                draggable:true,
                collapsible:false"
                style="width:330px;height:160px;padding:10px;">
        <form id="editForm" method="post">
        <input type="hidden" name="id" id="dataId">
        <table class="table">
            <tr>
<td align="right">终端名称：</td>
<td>
<input class="easyui-validatebox" type="text" name="name" id="nameEdit" style="width:150px;" data-options="required:true"></td>
</tr>
<tr>
<td align="right">终端编号：</td>
<td>
<input class="easyui-validatebox" type="text" name="code" id="codeEdit" style="width:150px;" data-options="required:true"></td>
</tr>

            <tr>
                <td></td>
                <td><a class="easyui-linkbutton" id="saveBtn" href="#" data-options="iconCls:'icon-save'">保存</a></td>
            </tr>
        </table>
        </form>
    </div>
    <!-- 新增 -->
    <!-- 批量 -->
    <div class="easyui-window" id="editBatchWin" data-options="modal:true,closed:true,resizable:false,
                minimizable:false,
                maximizable:false,
                draggable:true,
                collapsible:false"
                style="width:350px;height:400px;">
        <div id="tb" style="height:auto">
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" onclick="append()">添加行</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" onclick="removeit()">删除行</a>
            <a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="saveBatch()">保存</a>
        </div>
        <table id="dgBatch" style="width:100%;">
            <thead>
                <tr>
                    <th data-options="field:'name',editor:{type:'text'}">终端名称</th>

                </tr>
            </thead>
        </table>
        <form id="editBatchForm" method="post"></form>
    </div>
    <!-- 批量 -->
    </div>
    <!-- content -->
    <jscript>
    <!-- js -->
    <script type="text/javascript">
    $(function() {
        $('#datagrid').datagrid({
            url: '${ctx}/manager/terminal/query',
            toolbar: '#tools',
            idField: "id",
            autoRowHeight: true,
            fitColumns: true,
            showFooter: true,
            pagination: true,
            pageNumber: 1,
            pageSize: 20,
            singleSelect: false,
            rownumbers: true,
            selectOnCheck: true,
            checkOnSelect: true,
            columns: [[
                {field: 'id', title: '选择', width: 30, checkbox: true},
                {field: 'name', title: '终端名称'},
{field: 'code', title: '终端编号'},

                {field: 'createTime', title: '创建时间', formatter:function(val, row, idx) {
                    return to_date_hms(val);
                }},
                {field: 'opts', title: '操作', formatter:function(val, row, idx) {
                    return '<a href="#" class="easyui-linkbutton" data-options="iconCls:\'icon-edit\',plain:true" onclick="updateById(\'' + row.id + '\')">修改</a>';
                }}
            ]]
        });
        
        $('#dgBatch').datagrid({
            toolbar: '#tb',
            onClickRow: onClickRow
        });
        
        $('#searchBtn').click(function() {
            $('#datagrid').datagrid('load', {
                name: $('#nameSearch').val(),
code: $('#codeSearch').val(),

                createDateBegin: $('#createDateBegin').datebox('getValue'),
                createDateEnd: $('#createDateEnd').datebox('getValue')
            });
        });
        
        $('#addBtn').click(function() {
            $("#editWin").window({title:"新增"}).window("open").window("center");
        });
        
        $('#addBatchBtn').click(function() {
            append();
            $("#editBatchWin").window({title:"批量新增"}).window("open").window("center");
        });
        
        $('#deleteBtn').click(function() {
            var rows = $('#datagrid').datagrid('getChecked');
            if (!rows.length) {
                $.messager.alert('消息', '请至少选择一条记录', 'error');
            } else {
                $.messager.confirm({
                    title: '消息',
                    ok: '确定',
                    cancel: '取消',
                    msg: '确定要删除吗?',
                    fn: function(r){
                        if (r){
                            $.messager.progress();
                            var url = "${ctx}/manager/terminal/deleteBatch"
                            var params = {
                                ids: getRowIds(rows)    
                            };
                            $.post(url, params, function(result) {
                                $.messager.progress('close');
                                if (result.status) {
                                    $.messager.alert('消息', '删除成功', 'info');  
                                    $('#datagrid').datagrid('reload');
                                } else {
                                    $.messager.alert('消息', result.message, 'error');
                                }
                            }, 'json');
                        }
                    }
                });
            }
        });
        
        $('#saveBtn').click(function() {
            $.messager.progress();
            $('#editForm').submit();
        });
        
        $('#editForm').form({
            url: '${ctx}/manager/terminal/saveOrUpdate',
            onSubmit: function() {
                var isValid = $(this).form('validate');
                if (!isValid) {$.messager.progress('close');}
                return isValid;
            },
            success: function(result) {
                var result = $.parseJSON(result);
                $.messager.progress('close');
                if (result.status) {
                    $.messager.alert('消息', '保存成功', 'info', function(r) {
                        $('#editWin').window('close');
                        $('#datagrid').datagrid('reload');
                        $('#editForm').form('reset');
                    });
                } else {
                    $.messager.alert('消息', result.message, 'error');
                }
            }
        });
    });
    function updateById(id) {
        $.messager.progress();
        var url = "${ctx}/manager/terminal/getById"
        var params = {id: id};
        $.post(url, params, function(result) {
            $.messager.progress('close');
            if (result.status) {
                $('#dataId').val(result.data.id);
                $('#nameEdit').val(result.data.name);
$('#codeEdit').val(result.data.code);

                $("#editWin").window({title:"修改"}).window("open").window("center");
            } else {
                $.messager.alert('消息', result.message, 'error');
            }
        }, 'json');
    }
    var editIndex = undefined;
    function endEditing() {
        if (editIndex == undefined){return true}
        if ($('#dgBatch').datagrid('validateRow', editIndex)) {
            /* var ed = $('#dgBatch').datagrid('getEditor', {index:editIndex,field:'productid'});
            var productname = $(ed.target).combobox('getText');
            $('#dgBatch').datagrid('getRows')[editIndex]['productname'] = productname; */
            $('#dgBatch').datagrid('endEdit', editIndex);
            editIndex = undefined;
            return true;
        } else {
            return false;
        }
    }
    function append() {
        if (endEditing()) {
            $('#dgBatch').datagrid('appendRow', {});
            editIndex = $('#dgBatch').datagrid('getRows').length-1;
            $('#dgBatch').datagrid('selectRow', editIndex)
                    .datagrid('beginEdit', editIndex);
        }
    }
    function removeit() {
        if (editIndex == undefined){return;}
        $('#dgBatch').datagrid('cancelEdit', editIndex)
                .datagrid('deleteRow', editIndex);
        editIndex = undefined;
    }
    function onClickRow(index){
        if (editIndex != index){
            if (endEditing()){
                $('#dgBatch').datagrid('selectRow', index)
                        .datagrid('beginEdit', index);
                editIndex = index;
            } else {
                $('#dgBatch').datagrid('selectRow', editIndex);
            }
        }
    }
    function saveBatch() {
        if (endEditing()){
            var rows = $('#dgBatch').datagrid('getChanges');
            if (rows.length) {
                var inserted = $('#dgBatch').datagrid('getChanges', "inserted");
                if (inserted.length) {
                    var form = $('#editBatchForm');
                    form.children().remove();
                    for (var i=0; i<inserted.length; i++) {
                        form.append('<input type="hidden" name="name" value="' + inserted[i].name + '">');

                    }
                    var url = "${ctx}/manager/terminal/saveBatch"
                    $.post(url, $form.serialize(), function(result) {
                        if (result.status) {
                            $('#editBatchWin').window('close');
                            $('#datagrid').datagrid('reload');
                        } else {
                            $.messager.alert({
                                title: '消息',
                                ok: '确定',
                                msg: result.message
                            });
                        }
                    }, 'json');
                }
            }
        }
    }
    </script>
    <!-- js -->
    </jscript>
    </body>
</html>