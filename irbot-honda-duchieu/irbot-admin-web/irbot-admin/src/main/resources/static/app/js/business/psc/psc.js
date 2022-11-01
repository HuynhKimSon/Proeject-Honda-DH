const PREFIX = ctx + "business/psc";

let date = new Date();
let firstDay = lastDay = date;
$("input[name='params[beginTime]']").val(parseTime(firstDay, "{y}-{m}-{d}"));
$("input[name='params[endTime]']").val(parseTime(lastDay, "{y}-{m}-{d}"));

// Init screen
$(document).ready(function () {
  loadList();

  $("input").keyup(function (event) {
    if (event.keyCode == 13) {
      $.table.search();
      event.preventDefault();
    }
  });
});

function loadList() {
  const OPTIONS = {
    url: PREFIX + "/list",
    removeUrl: PREFIX + "/remove",
    detailUrl: PREFIX + "/detail/{id}",
    updateStatusUrl: PREFIX + "/update-status",
    retryUrl: PREFIX + "/retry",
    importUrl: PREFIX + "/import",
    modalName: "Phiếu",
    pageSize: 20,
    pageList: [10, 20, 50, 100, 200],
    columns: [
      {
        checkbox: true,
      },
      {
        field: 'id',
        title: 'Id',
        visible: false
      },
      {
        field: 'status',
        title: 'Trạng thái',
        align: 'center',
        formatter: function (value, row, index) {
          return processStatusFormat(row.status);
        }
      },
      {
        field: 'vehicleCode',
        title: 'Số máy'
      },
      {
        field: 'vehicleNumber',
        title: 'Biển số xe'
      },
      {
        field: 'km',
        title: 'Số Km'
      },
      {
        field: 'repairType',
        title: 'Loại dịch vụ'
      },
      {
        field: 'technician',
        title: 'NV phụ trách'
      },
      {
        field: 'finalCheck',
        title: 'Kiểm tra cuối'
      },
      {
        field: 'unitCode',
        title: 'Cửa hàng',
        align: 'center',
        formatter: function (value, row, index) {
          return unitCodeFormat(row.unitCode);
        }
      },
      {
        field: 'createTime',
        title: 'Ngày tạo'
      },
      {
        field: 'jobCard',
        title: 'JC'
      },
      {
        field: 'step',
        title: 'Step',
        visible: false
      },
      {
        title: "Hành động",
        align: "center",
        formatter: function (value, row, index) {
          var actions = [];
          actions.push(
            '<a class="btn btn-primary btn-xs ma2" href="javascript:void(0)" title="Chi tiết" onclick="$.operate.detail(\'' +
            row.id +
            '\')"><i class="fa fa-eye"  ></i>&nbsp;Chi tiết</a> '
          );
          actions.push(
            '<a class="btn btn-success btn-xs ma2" href="javascript:void(0)" title="Lịch sử" onclick="viewHistory(\'' +
            row.id +
            '\')"><i class="fa fa-history"></i>&nbsp;Lịch sử làm lệnh</a> '
          );
          return actions.join("");
        },
      },
    ],
  };
  $.table.init(OPTIONS);
}

function processStatusFormat(status) {
  if (status == 1) {
    return '<span class="label label-warning"> Chờ thực hiện</span>'
  } else if (status == 2) {
    return '<span class="label label-warning"> Đã gửi</span>'
  } else if (status == 3) {
    return '<span  class="label label-success"> Đang làm</span>'
  } else if (status == 4) {
    return '<span class="label label-danger"> Thất bại</span>'
  } else if (status == 5) {
    return '<span class="label label-primary">Thành công</span>';
  } else {
    return '<span class="label label-default"> Chưa làm</span>';
  }
}

function unitCodeFormat(unitCode) {
  if (unitCode == 200) {
    return '<span class="label label-success">Đức Hiệu 1</span>'
  } else {
    return '<span class="label label-primary">Đức Hiệu 2</span>';
  }
}

function viewHistory(id) {
  var _url = PREFIX + "/history/" + id;
  var options = {
    title: "Chi tiết làm lệnh",
    width: "900px",
    url: _url,
    skin: 'layui-layer-gray',
    btn: ['Đóng'],
    yes: function (index, layero) {
      layer.close(index);
    }
  };
  $.modal.openOptions(options);
}

function updateStatus() {
  table.set();
  $.modal.open("Cập nhật trạng thái", table.options.updateStatusUrl, '500', '380');
}

function importExcel() {
  table.set();
  $.modal.open("Nhập dữ liệu", table.options.importUrl, '600', '550');
}

function retry() {
  table.set();
  $.modal.open("Gửi lệnh robot", table.options.retryUrl, '500', '380');
}
