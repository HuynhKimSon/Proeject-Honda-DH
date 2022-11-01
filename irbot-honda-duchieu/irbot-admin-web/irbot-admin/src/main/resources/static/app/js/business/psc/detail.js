const PREFIX = ctx + "business/psc";

// Init screen
$(document).ready(function () {
    loadListSpare();

    loadListJob();
});

function loadListSpare() {

    if (!spareDetails) {
        $("#chi-tiet-phu-tung").hide();
        return;
    }

    const OPTIONS1 = {
        id: "bootstrap-table-spare",
        pagination: false,
        showSearch: false,
        showRefresh: false,
        showColumns: false,
        showToggle: false,
        modalName: 'Phụ tùng',
        data: spareDetails,
        columns: [
            {
                field: 'id',
                title: 'STT',
                align: 'center',
                formatter: function (value, row, index) {
                    return index + 1;
                }
            },
            {
                field: 'code',
                title: 'Mã phụ tùng',
            },
            {
                field: 'quantity',
                title: 'Số lượng'
            },
        ],
    };
    $.table.init(OPTIONS1);
}

function loadListJob() {
    if (!jobDetails) {
        $("#chi-tiet-cong-viec").hide();
        return;
    }
    const OPTIONS2 = {
        id: "bootstrap-table-job",
        pagination: false,
        showSearch: false,
        showRefresh: false,
        showColumns: false,
        showToggle: false,
        modalName: 'Công việc',
        data: jobDetails,
        columns: [
            {
                field: 'id',
                title: 'STT',
                align: 'center',
                formatter: function (value, row, index) {
                    return index + 1;
                }
            },
            {
                field: 'code',
                title: 'Mã công việc',
            },
            {
                field: 'price',
                title: 'Tiền công'
            },
            {
                field: 'description',
                title: 'Mô tả khác'
            },
        ],
    };
    $.table.init(OPTIONS2);
}
