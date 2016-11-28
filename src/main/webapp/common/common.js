$(function() {
	$('#dgBatch').datagrid({
        toolbar: '#tb',
        onClickRow: onClickRow
    });
	
	$('#addBtn').click(function() {
        $("#editWin").window({title:"新增"}).window("open").window("center");
    });
    
    $('#addBatchBtn').click(function() {
        append();
        $("#editBatchWin").window({title:"批量新增"}).window("open").window("center");
    });
    
    $('#saveBtn').click(function() {
        $.messager.progress();
        $('#editForm').submit();
    });
});

function getRowIds(rows) {
	var ids = new Array();
	for(var i = 0; i < rows.length; i++){
		ids.push(rows[i].id);
	}
	return ids.join(",");
}

function submitForm(controller) {
	$('#editForm').form({
        url: '${ctx}/manager/' + controller + '/saveOrUpdate',
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
}