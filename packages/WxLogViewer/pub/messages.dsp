<!-- show user messages -->
%ifvar error -notempty%
	<div class="alert alert-danger topspace" role="alert">%value error%</div>
%end%
%ifvar warning -notempty%
	<div class="alert alert-warning topspace" role="alert">%value warning%</div>
%end%
%ifvar success -notempty%
	<div class="alert alert-success topspace" role="alert">%value success%</div>
%end%
%ifvar info -notempty%
	<div class="alert alert-info topspace" role="alert">%value info%</div>
%end%
<!-- end of messages -->