const PREFIX = ctx + "business/robot";

function submitHandler() {
  if ($.validate.form()) {
    // Get status
    let status;
    if ($("#offlineRadio").prop("checked")) {
      status = "0";
    } else if ($("#busyRadio").prop("checked")) {
      status = "1";
    } else {
      status = "2";
    }

    // $.operate.save(PREFIX + "/add", $("#formAddRobot").serialize());
    let services = [];
    if ($("input[name='cbService1']").is(":checked") == true) {
      // Đồng bộ Kiểm tra định kỳ
      services.push(100);
    }

    if ($("input[name='cbService2']").is(":checked") == true) {
      // Đồng bộ Kiểm tra định kỳ có phụ tùng
      services.push(200);
    }

    if ($("input[name='cbService3']").is(":checked") == true) {
      // Đồng bộ Phiếu sửa chữa
      services.push(300);
    }

    let data = {
      uuid: $("input[name='uuId']").val(),
      ipAddress: $("input[name='ipAddress']").val(),
      status: status,
      services: services,
      remark: $("textarea[name='remark']").val()
    };

    // $.operate.save(PREFIX + "/add", JSON.stringify(data));

    $.ajax({
      cache: true,
      type: "POST",
      url: PREFIX + "/add",
      data: JSON.stringify(data),
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      async: false,
      error: function (request) {
        $.modal.alertError("System error!");
      },
      success: function (data) {
        $.operate.successCallback(data);
      },
    });
  }
}
