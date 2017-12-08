<%@ taglib prefix="acf" uri="/acf/tld/acf-taglib" %> 
	
<!-- PAGE CONTENT BEGINS -->
<form id="frm_main" class="form-horizontal" data-role="form" >
<div class="col-md-12 nopadding">
	<img style="height:32px;vertical-align:top;" src="../img/TVBlogo50px.gif"/>
	<acf:Region id="reg_sect_main" title="GENERATE REPORT IN HTML">
		<div class="col-xs-12 form-padding oneline">
        	<label class="control-label col-md-2" for="p_report_date">Report Date</label>
      		<div class="col-md-4">      
      			<acf:DateTimePicker id="p_report_date" name="p_report_date" pickDate="true" pickTime="false" checkMandatory="true"/>
        	</div>
      		<div class="col-md-4">      
	     		<acf:Button id="btnGenerate" name="btnGenerate" title="Generate Report">
	    		<acf:Bind on="click">
	    			<script>
	    				p_report_date = $("#frm_main #p_report_date").getValue();
						if (p_report_date != "") {	
							p_report_date = moment(p_report_date, 'x').format("YYYY-MM-DD");
						} else {
					    	p_report_date = "NA";
						}
	  					$.ajax({
						headers: {
							'Accept'       : 'application/json',
							'Content-Type' : 'application/json; charset=utf-8'
						},
						async  : false,
						type   : "POST",
						url    : "apwf003-generate-report.ajax",
						data   : JSON.stringify({
							'p_report_date'  : p_report_date
						}),
						success: function(data) {
							if (data.report) {
								report = data.report;
								popupPrint(report);
								/*
								var dialog = Dialog.create($("#dialog"))
												   .setCaption("PURCHASE ORDER FORM")
												   .setWidth(1000);
									
								Action.isModified = false;
								Action.setMode('new');
							
								dialog
								.addButton("Print", function() {
									popupPrint(report);
								})
								.addDismissButton("Close", function() {
									Action.new();
								})
								.showHtml(report);
								*/
							} else {
								Alert.showError(ACF.getDialogMessage("ACF040E"));
							}
						}
	    			});
	    			</script>
	    		</acf:Bind>
	    		</acf:Button>
        	</div>
    	</div>

     </acf:Region>
</div>
</form>

<script>
$(document).on('new', function() {
	$('#frm_main #p_report_date').setValue(new Date());
});
</script>
