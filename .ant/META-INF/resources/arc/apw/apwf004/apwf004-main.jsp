<%@ page import="acf.acf.General.jsp.*"%>
<%@ page import="java.util.*" %>
<%@ taglib prefix="acf" uri="/acf/tld/acf-taglib" %> 
<%
	ACFgJspHelper helper = new ACFgJspHelper(session);
	helper.setConnection("ARCDB");
%>

<div class="col-xs-12 nopadding">
	<acf:Region id="reg_func_search" type="search" title="SEARCH">
		<acf:RegionAction>
			<a href="#" onClick="$(this).parents('.widget-box').pForm$clear();">Clear</a>
		</acf:RegionAction>
		<form id="frm_search" class="form-horizontal" data-role="search" >
	    	<div class="form-group">
	      		<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=s_supplier_code style="display:block">Supplier Code.</label>
	      			<acf:ComboBox id="s_supplier_code" name="supplier_code" maxlength="20">
	      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${getSupplierCode} );
	 				</script></acf:Bind>  
	 				</acf:ComboBox>
	      		</div>
	      		
	      		<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=s_purchase_order_no style="display:block">Purchase Order No.</label>
	      			<acf:ComboBox id="s_purchase_order_no" name="purchase_order_no" maxlength="20">
	      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${PurchaseOrderNo} );
	 				</script></acf:Bind>  
	      			</acf:ComboBox>
	      		</div>
	      		
	      		<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=s_start_date style="display:block">Selection From Date</label>
	      			<acf:DateTimePicker id="s_start_date" name="start_date" pickTime="false"/>  
	      		</div>
	      		
	      		<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=s_end_date style="display:block">Selection To Date</label>
	      			<acf:DateTimePicker id="s_end_date" name="end_date" pickTime="false"/>  
	      		</div>
	    	</div>
	    	
	    	
	    	
	    	
	    
		</form>

	</acf:Region>
	
	<form id="frm_main" class="form-horizontal" data-role="form" >
		<acf:Region id="reg_div_list" type="list" title="PURCHASE ORDER LIST">
   		<acf:RegionAction>
			<a href="javascript:$('#grid_browse').pGrid$prevRecord();">Previous</a>
			&nbsp;
			<a href="javascript:$('#grid_browse').pGrid$nextRecord();">Next</a>
		</acf:RegionAction>
		<div class="col-xs-12">
			<acf:Grid id="grid_browse" url="apwf004-search.ajax" readonly="true" autoLoad="false">
			<acf:Column name="purchase_order_no" caption="Purchase Order No." width="100"></acf:Column>
			<acf:Column name="purchase_order_date" type="date" caption="Purchase Order Date" width="100"></acf:Column>
			<acf:Column name="supplier_code" caption="Supplier Code" width="100"></acf:Column>
				
				
			</acf:Grid>
	    </div>
	</acf:Region>
	
		<acf:Region id="reg_func_main" type="form" title="MATERIAL RECEIPT MAINTENANCE">
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="purchase_order_no">Purchase Order No.:</label>
      		<div class="col-md-2">
      			<acf:TextBox id="purchase_order_no" name="purchase_order_no" readonly="true" maxlength="60"/>
      				
      			   
        	</div>
     		
    	</div>  
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="purchase_order_date">Purchase Order Date:</label>
      		<div class="col-md-2">
      			<acf:DateTimePicker id="purchase_order_date" name="purchase_order_date" pickTime="false" readonly="true"/> 
      		</div>
     		
    	</div>  
 		<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="latest_receive_date">Received Date:</label>
      		<div class="col-md-2">
      			<acf:DateTimePicker id="latest_receive_date" name="latest_receive_date" pickTime="false" checkMandatory="true">
      			<acf:Bind on="change"><script>
      			var today = new Date();
      			
      			//if(!$(this).getValue().match("^[0-9]{1,3}(\\\\\\.[0-9]{1,2})?$")){
      			console.log($(this).getValue() < today);
      			console.log("test po date");
      			console.log(($(this).getValue() < ($("#frm_main #purchase_order_date").getValue())));
      			if( ($(this).getValue() < ($("#frm_main #purchase_order_date").getValue())))
      			{
      				$(this).setError(ACF.getQtipHint("APW004V"), "APW004V");
			   	}
			   	
			   	if( $(this).getValue() >  today ){
      				$(this).setError(ACF.getQtipHint("APW004V"), "APW004V");
			   	}
			   	if ( $(this).getValue() <= today && ($(this).getValue() > ($("#frm_main #purchase_order_date").getValue())))
			   	{
			   		$(this).setError("", "APW004V");
			   		//"Invalid rate, only XXX.XX allowed"
			   	}
			   	</script></acf:Bind>
			   	</acf:DateTimePicker>
      		</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="receive_location">Receiving Loc:</label>
      		<div class="col-md-2">
      		       
      			<acf:RadioButton id="TKO" name="locationb" maxlength="60"  buttonValue="TKO" label="TKO" readonly="true" disabled="true">
      			<acf:Bind on="change"><script>
      			if ($(this).acfRadioButton("getValue") == 1){
        				$("#receive_location").setValue("TKO");
        				$("#receive_location").disable();
 						}
        			</script></acf:Bind>
        		</acf:RadioButton>   
        	
      		</div>
      		<div class="col-md-2">          
      			<acf:RadioButton id="Other" name="locationb" maxlength="60" label="Other" buttonValue="Other" readonly="true" disabled="true">
      			<acf:Bind on="change"><script>
        	if ($(this).acfRadioButton("getValue") == 1){
        				$("#receive_location").setValue("");
        				$("#receive_location").enable();
 						}
        			</script></acf:Bind>
        			</acf:RadioButton> 
      		</div>
      		
      		<div class="col-md-2">          
      			<acf:TextBox id="receive_location" name="receive_location" maxlength="60" label="Other" readonly="true" checkMandatory="true">
      			<acf:Bind on="change"><script>
      			if ($(this).getValue() == "TKO"){
      					//clear previous value
        				$("#TKO").prop("checked","false");
        				$("#Other").prop("checked","false");
        				
        				//set new value
        				$("#TKO").prop("checked","true");
 						}
 				if ($(this).getValue() != "TKO"){
 						//clear previous value
        				$("#TKO").prop("checked","false");
        				$("#Other").prop("checked","false");
        				
        				//set new value
        				$("#Other").prop("checked","true");
 						}
      			</script></acf:Bind>
      			</acf:TextBox> 
      		</div>
      	</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="department_reference_no">Dept. Ref. No.:</label>
      		<div class="col-md-2">
      			<acf:TextBox id="department_reference_no" name="department_reference_no" readonly="true" maxlength="60"/> 
      		</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="supplier_code">Supplier Code:</label>
      		<div class="col-md-2">
      			<acf:TextBox id="supplier_code" name="supplier_code" readonly="true" maxlength="60">
      			<acf:Bind on="change"><script>
   						supplier_code = $(this).getValue(); //this bloack is the same as onLoadSucess except $this value
   						
   						if (supplier_code == ""){
   							//$("#frm_main #supplier_desc").setValue("");	
   						}
   						else {					
	   						$.ajax({
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-get-supplier-desc.ajax",
								data   : JSON.stringify({
									'supplier_code'	: supplier_code,
								}),
								success: function(data) {
									if (data.supplier_name != null) {
										$("#frm_main #supplier_name").setValue(data.supplier_name);
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
									}
								}
							});
   						}
					</script></acf:Bind> 
					</acf:TextBox>
      		</div>
     		
     		<label class="control-label col-md-2" for="supplier_name">Supplier Name:</label>
      		<div class="col-md-6">
      			<acf:TextBox id="supplier_name" name="supplier_name" readonly="true" maxlength="60"/> 
      		</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="remarks">Remarks:</label>
      		<div class="col-md-6">
      			<acf:TextBox id="remarks" name="remarks" maxlength="60"/> 
      		</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="in_stock_location_code">In-stock Loc. Code:</label>
      		<div class="col-md-2">
      			<acf:ComboBox id="in_stock_location_code" name="in_stock_location_code" maxlength="20">
	      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${LocationCode} );
	 				</script></acf:Bind>  
	 				</acf:ComboBox>
      		</div>
     		
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		
      		
      		<div class="hidden">
      			<acf:TextBox id="cancel_indicator" name="cancel_indicator" maxlength="60"/> 
      		</div>
      		
      		<div class="hidden">
      			<acf:TextBox id="cancel_date" name="cancel_date" maxlength="60"/> 
      		</div>
      		
      		<div class="hidden">
      			<acf:TextBox id="printed_by" name="printed_by" maxlength="60"/> 
      		</div>
      		
      		<div class="hidden">
      			<acf:TextBox id="section_id" name="section_id" maxlength="60"/> 
      		</div>
      		
      		<div class="hidden">         
      			<acf:TextBox id="printed_at" name="printed_at" maxlength="30" /> <!-- shouldn't place in update list -->	
			 </div>
      		
      		<div class="hidden">
      			<acf:TextBox id="no_of_times_printed" name="no_of_times_printed" maxlength="60"/> 
      		</div>
     		
    	</div>
		
	</acf:Region>
	
	<acf:Region id="receipt_list" type="list" title="ITEM LIST">
   		<acf:RegionAction>
			<a href="javascript:$('#grid_browse').pGrid$prevRecord();">Previous</a>
			&nbsp;
			<a href="javascript:$('#grid_browse').pGrid$nextRecord();">Next</a>
		</acf:RegionAction>
		<div class="col-xs-12">
			<acf:Grid id="receipt_browse" url="apwf004-get-inventory-table.ajax" editable="true" autoLoad="false" addable="false" deletable="false" editable="true" rowNum="9999" multiLineHeader="true">
			<acf:Column name="item_no" caption="Item No." width="50" columnKey="true"></acf:Column>
			<acf:Column name="item_description_1" caption="Item Description" width="100" readonly="true"></acf:Column>
			<acf:Column name="order_quantity" caption="Order Qty." width="50" readonly="true"></acf:Column>
			<acf:Column name="trigcrq" caption="received quantity" hidden="true"></acf:Column>
			<acf:Column name="trigcbo" caption="received quantity" hidden="true"></acf:Column>
			<acf:Column name="pre_received_qty" caption="pre received qty" editable="false" width="50"></acf:Column>
			<acf:Column name="current_received_quantity" caption="Current received quantity" editable="true" width="100">
			<acf:Bind on="validate">
			<script>
			function validation (newValue, oldValue, newData, oldData, id) {		
			if( newValue < 0 )
				{
					return ACF.getQtipHint("APW104V");
			   		}
				
			return null;
		}
	</script></acf:Bind>
			<acf:Bind on="validate"><script>
			function validation (newValue, oldValue, newData, oldData, id) {
			
			   	
			//$("#receipt_browse").setRowData(id, {current_back_order_quantity: 0});
			console.log("current_received_quantity--------------------new value" + newValue);
			console.log("current_received_quantity--------------------compare value" + $("#receipt_browse").getRowData(id).trigcrq);
			//var id = $("#receipt_browse").pGrid$getSelectedId();
				
				//var trig = $("#receipt_browse").getRowData(id).trigcrq;
				//console.log(newValue);
				//console.log($("#receipt_browse").getRowData(id).trigcrq);
				if (newValue != $("#receipt_browse").getRowData(id).trigcrq) //use status key to decide process or not
				{
				if (newValue == "" || newValue == 0)
				{newValue = 0}
				var org = $("#receipt_browse").getRowData(id).received_quantity - $("#receipt_browse").getRowData(id).trigcrq;
				
				var added = parseInt(org) + parseInt(newValue);
				var unitcost = $("#receipt_browse").getRowData(id).unit_cost;
				ttl = parseInt(newValue) * parseInt(unitcost);
				
				
				//console.log(oldData);
				//console.log(oldData.back_order_quantity);
				//var oboq = oldData.back_order_quantity;
				//console.log(added);
				//$("#receipt_browse").setRowData(id, {back_order_quantity: 0});
				//$("#receipt_browse").setRowData(id, {back_order_quantity: oboq});
				
				$("#receipt_browse").setRowData(id, {received_quantity: added});
				$("#receipt_browse").setRowData(id, {amount: ttl});
				
				$("#receipt_browse").setRowData(id, {trigcrq: newValue}); //update status key
				$("#receipt_browse").setRowData(id, {out_standing_quantity: $("#receipt_browse").getRowData(id).order_quantity - $("#receipt_browse").getRowData(id).received_quantity - $("#receipt_browse").getRowData(id).back_order_quantity});
				if( $("#receipt_browse").getRowData(id).out_standing_quantity < 0 )
				{
					return ACF.getQtipHint("APW104V");
			   	}
				}
				}
				//$("#receipt_browse").setRowData(id, {current_received_quantity: ''});
				</script></acf:Bind>
			</acf:Column>
			<acf:Column name="received_quantity" caption="Total received quantity" width="100">
			<acf:Bind on="validate">
			<script>
			function validation (newValue, oldValue, newData, oldData, id) {		
			if( newValue < 0 )
				{
					return ACF.getQtipHint("APW104V");
			   		}
				
			return null;
		}
	</script></acf:Bind></acf:Column>
			
			<acf:Column name="pre_back_order_qty" caption="pre back order qty" editable="false" width="50"></acf:Column>
			<acf:Column name="current_back_order_qty" caption="Curr. Back Order Qty." editable="true" width="100">
			<acf:Bind on="validate">
			<script>
			function validation (newValue, oldValue, newData, oldData, id) {		
			if( newValue < 0 )
				{
					return ACF.getQtipHint("APW104V");
			   		}
				
			return null;
		}
	</script></acf:Bind>
			<acf:Bind on="validate"><script>
			function validation (newValue, oldValue, newData, oldData, id) {
			
			console.log("current_back_order_qty--------------------new value" + newValue);
			console.log("current_back_order_qty--------------------compare value" + $("#receipt_browse").getRowData(id).trigcbo);
			//	var id = $("#receipt_browse").pGrid$getSelectedId();
				//var trigcbo = $("#receipt_browse").getRowData(id).trigcbo;
				
				if (newValue != $("#receipt_browse").getRowData(id).trigcbo) //use status key to decide process or not
				{
				
				if (newValue == "" || newValue == 0)
				{newValue = 0}
				var org = $("#receipt_browse").getRowData(id).back_order_quantity - $("#receipt_browse").getRowData(id).trigcbo;
				
				var added = parseInt(org) + parseInt(newValue);
				
				$("#receipt_browse").setRowData(id, {back_order_quantity: added});
				
				$("#receipt_browse").setRowData(id, {trigcbo: newValue}); //update status key
				$("#receipt_browse").setRowData(id, {out_standing_quantity: $("#receipt_browse").getRowData(id).order_quantity - $("#receipt_browse").getRowData(id).received_quantity - $("#receipt_browse").getRowData(id).back_order_quantity});
				if( $("#receipt_browse").getRowData(id).out_standing_quantity < 0 )
				{
					return ACF.getQtipHint("APW104V");
			   	}
				}
				}
				//$("#receipt_browse").setRowData(id, {current_received_quantity: ''});
				</script></acf:Bind>
			</acf:Column>
			<acf:Column name="back_order_quantity" caption="TTL Back Order Qty" width="100">
			<acf:Bind on="validate"><script>
			function validation (newValue, oldValue, newData, oldData, id) {		
			if( newValue < 0 )
				{
					return ACF.getQtipHint("APW104V");
			   		}
				
			return null;
		}
	</script></acf:Bind>
			</acf:Column>
			
			<acf:Column name="out_standing_quantity" caption="O/S Qty" width="50">
			<acf:Bind on="validate"><script>
			function validation (newValue, oldValue, newData, oldData, id) {		
			if( newValue < 0 )
				{
					return ACF.getQtipHint("APW104V");
			   		}
				
			return null;
		}
	</script></acf:Bind></acf:Column>
			
			<acf:Column name="un_it" caption="UNIT" width="50"></acf:Column>
			<acf:Column name="unit_cost" caption="Unit Cost" width="50"></acf:Column>
			<acf:Column name="amount" caption="Current Received Amount" width="50"></acf:Column>
				
				
			</acf:Grid>
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
      		<div class="hidden">         
      			<acf:TextBox id="canceled_by" name="cancel_by" maxlength="30" />	
			 </div>
			 
    	</div>
    	<input type="hidden" id="allow_print" name="allow_print" value="1"/>
	</acf:Region>
	</form>
	
	
</div>

<script>
/*
$("#frm_search").pForm$setRelatedComboBox(${moduleGroups}, [$("#frm_search #s_sec_no"), $("#frm_search #s_func_gp_name")]);
$("#frm_main").pForm$setRelatedComboBox(${moduleGroups}, [$("#frm_main #mod_id"), $("#frm_main #func_gp_name")]);
*/



Controller.setOption({
	searchForm: $("#frm_search"),
	browseGrid: $("#grid_browse"),
	searchKey: "purchase_order_no",
	browseKey: "purchase_order_no",
	//redirect: "acff012-form",
	
	//initMode: "${mode}",
	recordForm: $("#frm_main"),
	recordKey: {
	purchase_order_no: "${purchase_order_no}",
	item_no: "${item_no}",
	},
	getUrl: "apwf004-get-form.ajax",
	saveUrl: "apwf004-save.ajax",
	
	getSaveData: function() {
		//$("#indirect_browse").pGrid$setHiddenValueForAllRecords("programme_no", $("#frm_main #programme_no").getValue());
		//$("#direct_browse").pGrid$setHiddenValueForAllRecords("programme_no", $("#frm_main #programme_no").getValue());
		//$("#detail_budget_browse").pGrid$setHiddenValueForAllRecords("account_allocation", $("#detail_budget_browse #budget_account_allocation").getValue());
		return JSON.stringify({
			'form' : Controller.opt.recordForm.pForm$getModifiedRecord( Action.getMode() ),
			'Receipt' : $("#receipt_browse").pGrid$getModifiedRecord(), 
		//	'AccountAllocation' : $("#detail_budget_browse").pGrid$getModifiedRecord(),
		//	'DirectBudget' : $("#direct_browse").pGrid$getModifiedRecord(), 
		
		});
	},
ready: function() { Action.setMode("search"); }
}).executeSearchBrowserForm();

$(document).on('new', function() {
	$('#receipt_browse').pGrid$clear(); //clear the sub grid-browser when creating new record.
		$("#frm_main #Other").enable();
	$("#frm_main #TKO").enable();
	$("#frm_main #section_id").setValue("03");
});

$(document).on('view', function() {
	//$("#frm_main #printed_at").setValue(2017-02-20);
	$("#frm_main #Other").disable();
	$("#frm_main #TKO").disable();
});

$(document).on('amend', function() {
	//$("#frm_main #printed_at").setValue(2017-02-20);
	$("#frm_main #Other").disable();
	$("#frm_main #TKO").disable();
});

</script>
<%
	helper.close();
%>