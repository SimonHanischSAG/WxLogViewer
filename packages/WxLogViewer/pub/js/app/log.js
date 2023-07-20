var modal = undefined;
var filename = undefined;
var selectedRow = undefined;
var table = undefined;

$(function() {
    $("#logcontent").hide();
    $("#ajax-result").hide();
    table = $("#logfiles-table").DataTable();
    modal = $("#delete-confirmation");
});

$(".treectrl").click(function() {
    $this = $(this);
    var cmd = $this.data('command');
    $("#tree").jstree(cmd);
});

$("#btn_dwnl_pan").click(function() {
    var filename = $("#logcontent").data("filename");
    downloadLogfile(filename);
});

$("#btn_del_pan").click(function() {
    var filename = $("#logcontent").data("filename");
    $("#delete-conf-file").text(filename);
    modal.modal();
});

$(".fileaction").click(function(evt) {
    $this = $(this);
    filename = $this.data("filename");
    var action = $this.data("action");
    selectedRow = findTableRow($this);

    if (action == "view") {
        showLogfile(filename);
    } else if (action == "delete") {
        $("#delete-conf-file").text(filename);
        modal.modal();
    } else if (action == "download") {
        downloadLogfile(filename);
    } else if (action == "tail") {
        window.open('/WxLogViewer/logtail.html?file='+ encodeURI(filename)+'&initialsize=1000','_blank')
    }
});

$(".close-Log-panel").click(function(evt) {
    $("#logcontent").hide("fast");
});

function deleteLogfile() {
    modal.modal("hide");
    $.get(getRestUrl("deleteLogfile", filename), function(data) {
        $("#ajax-result").html(data);
        $("#ajax-result").removeClass("hidden");
    });

    if (selectedRow !== undefined) {
        table.row(selectedRow).remove().draw();
    }

    if ($("#logcontent").data("filename") === filename) {
        $("#logcontent").hide("fast");
    }
}

function downloadLogfile(filename) {
    var url = getRestUrl("readLogfile", filename, {download: true});
    window.open(url, "downloadLogfile");
}

function showLogfile(filename) {
    var panel = $("#logcontent");
    panel.show("fast");
    panel.data("filename", filename);
    $("#logcontent > div > h3.panel-title > span.servicename").html("<strong>Log: </strong>" + filename);
    scrollToAnchor(panel);
    $.getJSON(getRestUrl("readLogfile", filename), function(data) {
		var out = $("#log-out");
		if (data.lines && data.lines.length > 0) {
			out.text(data.lines.join('\n'));
			out.scrollTop(out.prop("scrollHeight"));
		}
		else {
			out.text("");
		}
    });
}
