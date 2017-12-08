<%@ page import="acf.acf.General.jsp.*"%>
<%@ page import="java.util.*" %>
<%@ taglib prefix="acf" uri="/acf/tld/acf-taglib" %> 
<%
	ACFgJspHelper helper = new ACFgJspHelper(session);
	helper.setConnection("ARCDB");
%>

<div class="col-xs-12 nopadding">
	<acf:Region id="reg_func_search" type="search" title="SECTION SEARCH">
		<acf:RegionAction>
			<a href="#" onClick="$(this).parents('.widget-box').pForm$clear();">Clear</a>
		</acf:RegionAction>
		<form id="frm_search" class="form-horizontal" data-role="search" >
	    	<div class="form-group">
	      		<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=s_item_no style="display:block">Item No.</label>
	      			<acf:ComboBox id="s_item_no" name="item_no" maxlength="7">  
	      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${WPItemno} );
	 				</script></acf:Bind>
	      			</acf:ComboBox>
	      		</div>
	      		
	    	
	    	
	    	<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=s_start_date style="display:block">Selection From Date.</label>
	      			<acf:DateTimePicker id="s_start_date" name="start_date" pickTime="false"/>  
	      		</div>
	      		
	    	
	      		<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=s_end_date style="display:block">Selection To Date.</label>
	      			<acf:DateTimePicker id="s_end_date" name="end_date" pickTime="false"/>  
	      		</div>
	      		
	      		
	    	</div>
	      		
	    	
		</form>

	</acf:Region>
	</div>
	<form id="frm_main" class="form-horizontal" data-role="form" >
		<acf:Region id="reg_div_list" type="list" title="ADJUSTMENT LIST">
   		<acf:RegionAction>
			<a href="javascript:$('#grid_browse').pGrid$prevRecord();">Previous</a>
			&nbsp;
			<a href="javascript:$('#grid_browse').pGrid$nextRecord();">Next</a>
		</acf:RegionAction>
		<div class="col-xs-12">
			<acf:Grid id="grid_browse" url="apwf006-search.ajax" readonly="true" autoLoad="false">
			<acf:Column name="item_no" caption="Item No." width="100"></acf:Column>
			<acf:Column name="item_desc" caption="Item Desc" width="100"></acf:Column>
			<acf:Column name="adjustment_date" caption="Adjustment Date" hidden="true" width="100"></acf:Column>
			<acf:Column name="adjustment_datee" caption="Adjustment Date" type = "date" width="100"></acf:Column>
			<acf:Column name="adjust_quantity" caption="Adjust Qty" width="100"></acf:Column>	
				
			</acf:Grid>
	    </div>
	</acf:Region>
	
		<acf:Region id="reg_func_main" type="form" title="SECTION MAINTENANCE">
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="item_no" >Item No.:</label>
      		<div class="col-md-2">
      			<acf:ComboBox id="item_no" name="item_no" checkMandatory="true">
      				<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${itemnoselect} );
	 				</script></acf:Bind>
      				<acf:Bind on="change"><script>
				var item_no = $(this).getValue();
				
				//$("#grid_item").setRowData(id, {purchase_order_no: pono});
				//testing here
				$.ajax({ //grid combobox and multi-value input
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-item-fields.ajax",
								data   : JSON.stringify({
									'item_no'	: item_no
								}),
								success: function(data) {
// 									console.log(data.item_description_1);
// 									console.log(data.location_code);
// 									console.log(data.unit_cost);
									//console.log(data.item[0].un_it);
									if (data.location_code != null) {
										//$("#frm_main #supplier_desc").setValue(data.sup_desc);
										
										//$("#grid_item").setRowData(id, {un_it: data.item[0].un_it, unit_cost: data.item[0].unit_cost, item_description_1: data.item[0].item_description_1});
										
										$("#frm_main #item_description").setValue(data.item_description_1);
										$("#frm_main #location_code").setValue(data.location_code);
// 										$("#frm_main #unit_cost").setValue(data.unit_cost);
										
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
									}
								}
							});
							$.ajax({
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apwf001/apwf001-get-reference-price.ajax",
								data   : JSON.stringify({
									'item_no'	: item_no,
								}),
								success: function(data) {
									if (data.unit_cost != null) {
										$("#frm_main #unit_cost").setValue(data.unit_cost);
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
									}
								}
							});
							//}
				</script>
				</acf:Bind>
      			   </acf:ComboBox>
        	</div>
     		
     		<div class="hidden">
      			<acf:TextBox id="item_description" name="item_description" editable="false" maxlength="60" readOnly="true"/> 
      		</div>
     		
    	</div>  
    	 
 		<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="adjustment_date">Adjust Date:</label>
      		<div class="col-md-2">
      			<acf:DateTimePicker id="adjustment_date" name="adjustment_date" maxlength="60" pickTime="false" checkMandatory="true"> 
      			<acf:Bind on="change"><script>
      			var today = new Date();
      			if( $(this).getValue() >  today ){
      				$(this).setError(ACF.getQtipHint("APW005V"), "APW005V");
			   	}
			   	if ( $(this).getValue() <= today )
			   	{
			   		$(this).setError("", "APW005V");
			   		//"Invalid rate, only XXX.XX allowed"
			   	}
			   	</script></acf:Bind>
      			</acf:DateTimePicker> 
      		</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="location_code">Location Code:</label>
      		<div class="col-md-2">
      			<acf:ComboBox id="location_code" name="location_code" editable="false" maxlength="60" readonly="true" checkMandatory="true">
      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${locationcode} );
	 				</script></acf:Bind>
      			<acf:Bind on="change"><script>
				var location_code = $(this).getValue();
				
				//$("#grid_item").setRowData(id, {purchase_order_no: pono});
				
				$.ajax({ //grid combobox and multi-value input
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-get-location-name.ajax",
								data   : JSON.stringify({
									'location_code'	: location_code
								}),
								success: function(data) {
									//console.log(data.item);
									//console.log(data.item[0].un_it);
									if (data.location_name != null) {
										//$("#frm_main #supplier_desc").setValue(data.sup_desc);
										
										//$("#grid_item").setRowData(id, {un_it: data.item[0].un_it, unit_cost: data.item[0].unit_cost, item_description_1: data.item[0].item_description_1});
										//console.log(data.location_name);
										$("#frm_main #location_description").setValue(data.location_name);
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
									}
								}
							});
							//}
				</script>
				</acf:Bind>
				</acf:ComboBox> 
      		</div>
     		
     		<div class="hidden">
      			<acf:TextBox id="location_description" name="location_description" editable="false" readonly="true" maxlength="60"/> 
      		</div>
     		
    	</div>
    	
    	
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="unit_cost">Unit Cost:</label>
      		<div class="col-md-2">
      			<acf:TextBox id="unit_cost" name="unit_cost" editable="false" readonly="true" maxlength="60"/> 
      		</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="adjust_quantity">Adjust Qty.:</label>
      		<div class="col-md-2">
      			<acf:TextBox id="adjust_quantity" name="adjust_quantity" maxlength="60" checkMandatory="true"/> 
      		</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="remarks">Remarks:</label>
      		<div class="col-md-8">
      			<acf:TextBox id="remarks" name="remarks" maxlength="60" /> 
      		</div>
     		
    	</div>
    	
    	<div class="hidden">
     		<label class="control-label col-md-2" for="action_code">Action Code:</label>
      		<div class="col-md-1">
      			<acf:TextBox id="action_code" name="action_code" maxlength="3" readonly="true"/> 
      		</div>
     		
    	</div>
    	
		
	</acf:Region>
	<acf:Region id="reg_stat" title="UPDATE STATISTICS">
		<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="modified_at">Modified At:</label>
      		<div class="col-md-4">          
        		<acf:DateTime id="modified_at" name="modified_at" readonly="true" useSeconds="true"/>  	
        	</div>
     		<label class="control-label col-md-2" for="created_at">Created At:</label>
      		<div class="col-md-4"> 
      			<acf:DateTime id="created_at" name="created_at" readonly="true" useSeconds="true"/>           
      		</div>
    	</div>
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="modified_by">Modified By:</label>
      		<div class="col-md-4">          
        		<acf:TextBox id="modified_by" name="modified_by" readonly="true"/>  	
        	</div>
     		<label class="control-label col-md-2" for="created_by">Created By:</label>
      		<div class="col-md-4"> 
      			<acf:TextBox id="created_by" name="created_by" readonly="true"/>           
      		</div>
    	</div>
    	<input type="hidden" id="allow_print" name="allow_print" value="1"/>
	</acf:Region>
	</form>
	
	


<script>
/*
$("#frm_search").pForm$setRelatedComboBox(${moduleGroups}, [$("#frm_search #s_sec_no"), $("#frm_search #s_func_gp_name")]);
$("#frm_main").pForm$setRelatedComboBox(${moduleGroups}, [$("#frm_main #mod_id"), $("#frm_main #func_gp_name")]);
*/



Controller.setOption({
	searchForm: $("#frm_search"),
	browseGrid: $("#grid_browse"),
	browseKey: "item_no,adjustment_date",
	//redirect: "acff012-form",
	
	//initMode: "${mode}",
	recordForm: $("#frm_main"),
	recordKey: {
	
	},
	getUrl: "apwf006-get-form.ajax",
	saveUrl: "apwf006-save.ajax",
	ready: function() { Action.setMode("search"); }
}).executeSearchBrowserForm();

$(document).on('new', function() {

	$("#frm_main #action_code").setValue("N");
});

$(document).on('amend', function() {
	$("#frm_main #item_description").disable();
	$("#frm_main #action_code").setValue("A");
});

</script>
<%
	helper.close();
%>