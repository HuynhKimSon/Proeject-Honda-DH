const PREFIX = ctx + "business/psc";

function submitHandler() {
  $("#errorFile").html("");
  $("#errorsText").html("");

  // Check file have to selected by user
  let fileValue = $("#file").val();
  if (
    !fileValue ||
    (!$.common.endWith(fileValue, ".xls") && !$.common.endWith(fileValue, ".xlsx"))
  ) {
    $("#errorFile").html("Vui lòng chọn tệp có đuôi 'xls' hoặc ''xlsx'");
    return false;
  }

  // Check file size
  let fileSize = $("#file")[0].files[0].size;
  if (fileSize < 2 || fileSize > (1024 * 1024 * 2)) {
    $("#errorFile").html("Vui lòng chọn tệp có dung lượng từ 0MB - 2MB");
    return false;
  }

  // Get data
  let file = $("#file")[0];
  let unitCode = $('select[name="unitCode"]').val();
  let formData = new FormData();
  formData.append("file", file.files[0]);
  formData.append("unitCode", unitCode);

  //Call API upload data
  $.ajax({
    type: "POST",
    url: PREFIX + "/import",
    contentType: false,
    processData: false,
    data: formData,
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
        let errors = data.errors;
        console.log(errors);
        if (Array.isArray(errors) && errors.length > 0) {
          $.modal.closeLoading();
          errors.forEach(element => {
            $("#errorsText").append(`
                    <p> `+ element + `</p>
                `);
          });
        } else {
          $.modal.closeLoading();
          $.modal.close();
          parent.$.modal.alertSuccess("Nhập dữ liệu thành công!");
          parent.$.table.refresh();
        }
      } else {
        $.modal.closeLoading();
        $.modal.alertError("System error! \r\n " + data.msg);
      }
    }
  });
}

function handleFileChange() {
  $("#file-choose").html($("#file").val().split("\\").pop());
}

function downloadTemplate() {
  $.modal.loading("Đang tải file, vui lòng đợi ...");
  window.location.href = ctx + encodeURI("app/js/common/template/PSC_Template.xlsx");
  $.modal.closeLoading();
}

