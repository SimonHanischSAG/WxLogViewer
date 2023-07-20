		</div><!--  id: page-wrapper -->
	</div><!-- id: wrapper -->
	
	
	<script>
		$('#nav-item-%value id%').addClass( "active" );
		%ifvar navid -notempty%
		$("#%value navid%").addClass("active");
		$("#%value navid%").closest("li.toplevel").addClass("active");
		%end%
	</script>
  </body>
</html>