const PREFIX = ctx + "business/ktdk-pt";

function submitHandler() {
  var rows = $.common.isEmpty(parent.table.options.uniqueId) ? parent.$.table.selectFirstColumns() : parent.$.table.selectColumns(parent.table.options.uniqueId);
  let dataRequest = {
    ids: rows.join(),
    accountHcr: $("select[name='accountHcr']").val(),
    accountHpm: $("select[name='accountHpm']").val()
  }
  if ($.validate.form()) {
    $.ajax({
      type: "POST",
      url: PREFIX + "/retry",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      data: JSON.stringify(dataRequest),
      beforeSend: function () {
        $.modal.loading("Đang xử lý, vui lòng đợi...");
      },
      error: function (request) {
        $.modal.closeLoading();
        $.modal.alertError("System error!");
      },
      success: function (data) {
        if (data == null) {
          return;
        }
        if (data.code == 0) {
          $.modal.closeLoading();
          $.modal.close();
          parent.$.modal.alertSuccess("Gửi robot đồng bộ thành công!");
          parent.$.table.refresh();
        } else {
          $.modal.closeLoading();
          $.modal.alertError("System error! \r\n " + data.msg);
        }
      },
    });
  }
}