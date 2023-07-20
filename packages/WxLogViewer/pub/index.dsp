<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>WxLogViewer</title>

        <!-- Bootstrap -->
        <link href="/WxLogViewer/css/bootstrap.min.css" rel="stylesheet">
        <link href="/WxLogViewer/css/bootstrap-select.min.css" rel="stylesheet">
        <link href="/WxLogViewer/css/bootstrap-checkbox.min.css" rel="stylesheet">
        <link href="/WxLogViewer/css/dataTables.bootstrap.min.css" rel="stylesheet">
        <link href="/WxLogViewer/css/datepicker3.min.css" rel="stylesheet">
        <link href="/WxLogViewer/css/sb-admin-2.min.css" rel="stylesheet">
        <link href="/WxLogViewer/css/vendor/font-awesome-4.1.0/css/font-awesome.min.css" rel="stylesheet">
        <link href="/WxLogViewer/css/vendor/metisMenu/metisMenu.min.css" rel="stylesheet">
        <link href="/WxLogViewer/css/jstree.min.css" rel="stylesheet">

        <script src="/WxLogViewer/js/config.js"></script>
        <script src="/WxLogViewer/js/jquery.min.js"></script>
        <script src="/WxLogViewer/js/bootstrap.min.js"></script>
        <script src="/WxLogViewer/js/bootstrap-select.min.js"></script>
        <script src="/WxLogViewer/js/bootstrap-datepicker.min.js"></script>
        <script src="/WxLogViewer/js/bootstrap-checkbox.min.js"></script>
        <script src="/WxLogViewer/js/app.min.js"></script>	
        <script src="/WxLogViewer/js/vendor/metisMenu/metisMenu.min.js"></script>
    </head>
    <body>
        
            <div id="page-wrapper">
                <div id="ajax-error-panel" class="alert alert-danger topspace start-hidden" role="alert">
                </div>  




%invoke wx.logviewer.impl:getLogfiles%
%endinvoke%

%include messages.dsp%
<div class="row">
    <div class="col-lg-12">
        <h1 class="page-header">Logfiles (last 30 days)</h1>
    </div>
</div>
<div class="row">
    <div class="col-lg-12">
        <div id="ajax-result" class="alert alert-info start-hidden"></div>
        <div id="logcontent" class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">
                    <span class="servicename"></span>
                </h3>			
            </div>		
            <div class="panel-body" style="padding: 0">
				<pre id="log-out" style="height:400px; margin:0; margin-top:1px; border:none"></pre>
			</div>
        </div>	
    </div>
</div>
<div class="row">
    <div class="col-lg-12">
        <div class="table-responsive">
			<table class="table table-hover table-striped table-bordered table-condensed" id="logfiles-table">
				<thead>
					<tr>
						<th>Action</th>
						<th>Filename</th>
						<th style="width:100%">Filepath</th>
						<th>Last modified</th>
						<th>Size</th>
					</tr>  			
				</thead>
				<tbody>
					%loop logfiles%
					
					<tr>
						 <td>
							<button type="button" class="btn btn-primary btn-xs fileaction" 
									data-toggle="tooltip" data-placement="top" data-original-title="View last 100 lines"
									data-action="view" data-filename="%value absolutePath%">
								<i class="fa fa-search"></i>
							</button>
							<button id="btn_dwnl_pan" type="button" class="btn btn-primary btn-xs fileaction" 
									data-toggle="tooltip" data-placement="top" data-original-title="Download log"
									data-action="download" data-filename="%value absolutePath%">
								<i class="fa fa-download"></i>
							</button>
							<button type="button" class="btn btn-primary btn-xs fileaction" 
									data-toggle="tooltip" data-placement="top" data-original-title="Tail log"
									data-action="tail" data-filename="%value absolutePath%">
								<i class="fa fa-refresh"></i>
							</button>
						</td>
						<td>%value filename%</td>
						<td><input type="text" class="ellipsis" value="%value absolutePath%"/></td>
						<td><span class="hidden">%value sortModificationDate%</span>%value modificationDate%</td>
						<td><span class="hidden">%value sortSize%</span>%value size%</td>
					</tr>
					%endloop%
				</tbody>
				
			</table>
		</div>
    </div>
</div>

<!-- Modal -->
<div class="modal fade" id="delete-confirmation" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="myModalLabel">Warning!</h4>
            </div>
            <div class="modal-body">
                Do you really wan't to delete the log file <code id="delete-conf-file"></code>?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-danger" onclick="deleteLogfile();"><span class="glyphicon glyphicon-trash"></span> Delete</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>
    </div>
</div>

<script src="js/jquery.dataTables.min.js"></script>
<script src="js/dataTables.bootstrap.min.js"></script>
<script src="js/app/log.js"></script>

<style>
#logfiles-table td, #logfiles-table th {
	white-space: nowrap;
}
.ellipsis {
	width: 100%;
	border: none;
	background: none;
	text-overflow: ellipsis;
	overflow: hidden;
}
</style>

<script>
$('#logfiles-table').dataTable({
	order: [[3, 'desc']],
	columns: [
		{ searchable: false, sortable: false },
		null,
		{ searchable: false },
		null,
		null
	]
});


</script>
%include footer.dsp%