$(function () {
    initTable();
    initDate();
});

function doQuery(params){
    $('#demo-table').bootstrapTable('refresh');    //刷新表格
}

function initTable(){
    var url = "/report/Query?random="+Math.random();
    $('#demo-table').bootstrapTable({
        method:'POST',
        dataType:'json',
        contentType: "application/x-www-form-urlencoded",
        cache: false,
        striped: true,                              //是否显示行间隔色
        sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
        url:url,
        height: $(window).height() - 110,
        width:$(window).width(),
        showColumns:true,
        pagination:true,
        queryParams : queryParams,
        minimumCountColumns:2,
        pageNumber:1,                       //初始化加载第一页，默认第一页
               pageSize: 20,                       //每页的记录行数（*）
              pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
              uniqueId: "id",                     //每一行的唯一标识，一般为主键列
        showExport: true,                    
        exportDataType: 'all',
        responseHandler: responseHandler,
        columns: [
        {
            field: '',
                    title: 'Sort No.',
                    formatter: function (value, row, index) {
                    return index+1;
             }
        },
        {
            field : 'id',
            title : 'User ID',
            align : 'center',
            valign : 'middle',
            sortable : true
        }, {
            field : 'institutionCode',
            title : 'Institution Code',
            align : 'center',
            valign : 'middle',
            sortable : true
        }, {
            field : 'institutionName',
            title : 'Institution Name',
            align : 'center',
            valign : 'middle'
        }, {
            field : 'loginId',
            title : 'Login Name',
            align : 'center',
            valign : 'middle',
            sortable : true
        }, {
            field : 'realName',
            title : 'Real Name',
            align : 'center',
            valign : 'middle'
        }, {
            field : 'createTime',
            title : 'Create Time',
            align : 'center',
            valign : 'left',
            formatter : function (value, row, index){
                return new Date(value).format('yyyy-MM-dd hh:mm:ss');
            }
        }, {
            field : 'homeAddress',
            title : 'Address',
            align : 'center',
            valign : 'middle'
        }]
    });
}

function initDate() {
	// 执行一个laydate实例
	laydate.render({
		elem : '#startDate' // 指定元素
	});
	laydate.render({
		elem : '#endDate' // 指定元素
	});
}


function queryParams(params) {
    var param = {
        orgCode : $("#orgCode").val(),
        userName : $("#userName").val(),
        startDate : $("#startDate").val(),
        endDate : $("#endDate").val(),
        limit : this.limit, // 页面大小
        offset : this.offset, // 页码
        pageindex : this.pageNumber,
        pageSize : this.pageSize
    }
    return param;
} 

// 用于server 分页，表格数据量太大的话 不想一次查询所有数据，可以使用server分页查询，数据量小的话可以直接把sidePagination: "server"  改为 sidePagination: "client" ，同时去掉responseHandler: responseHandler就可以了，
function responseHandler(res) { 
    if (res) {
        return {
            "rows" : res.result,
            "total" : res.totalCount
        };
    } else {
        return {
            "rows" : [],
            "total" : 0
        };
    }
}