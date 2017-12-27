<%@ page import="acf.acf.General.jsp.*"%>
<%@ page import="java.util.*" %>
<%@ taglib prefix="acf" uri="/acf/tld/acf-taglib" %> 
<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>	
<!-- PAGE CONTENT BEGINS -->
<div class="col-md-12 nopadding">
	<acf:Region id="reg_div_list" title="SEARCH" type="search">
		<acf:RegionAction>
			<a href="#" onClick="$(this).parents('.widget-box').pForm$clear();">Clear</a>
		</acf:RegionAction>
		<form id="frm_search" class="form-horizontal" data-role="search">
	    	<div class="form-group">
	    		<div class="col-lg-5 col-md-6 col-sm-8 col-xs-10">
	      			<label for=s_po_no style="display:block">PO NO</label>
	      			<acf:TextBox id="s_po_no" name="purchase_order_no" editable="true" multiple="false"/>
	      			
	      			
	        	</div>
	        	
	       		<div class="col-lg-2 col-md-6 col-sm-6 col-xs-12">
	      			<label for=s_booking_start_date style="display:block">P.O. From Date</label>
	      			<acf:DateTimePicker id="s_po_start_date" name="po_start_date" pickTime="false"/>
	        	</div>
	        	
	        	<div class="col-lg-2 col-md-6 col-sm-6 col-xs-12">
	      			<label for=s_booking_end_date style="display:block">P.O. To Date</label>
	      			<acf:DateTimePicker id="s_po_end_date" name="po_end_date" pickTime="false"/>
	        	</div>
	        
	        
	        
	        	<div class="col-lg-2 col-md-6 col-sm-6 col-xs-12">
	      			<label for=s_supplier_code style="display:block">SUPPLIER CODE</label>
	      			<acf:ComboBox id="s_supplier_code" name="supplier_code" editable="true" multiple="false">
	      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${modules} );
	 				</script></acf:Bind>
	      			</acf:ComboBox>
	        	</div>

	    	</div>
		</form>
	</acf:Region>
</div> 



<div class="col-xs-12 nopadding">
	<form id="frm_main" class="form-horizontal" data-role="form" >
	<acf:Region id="reg_div_list" type="list" title="PURCHASE ORDER LIST">
   		<acf:RegionAction>
			<a href="javascript:$('#grid_browse').pGrid$prevRecord();">Previous</a>
			&nbsp;
			<a href="javascript:$('#grid_browse').pGrid$nextRecord();">Next</a>
		</acf:RegionAction>
		<div class="col-xs-12">
			<acf:Grid id="grid_browse" url="apwf003-search.ajax" readonly="true" autoLoad="false">
				<acf:Column name="purchase_order_no" caption="P.O. NO" width="75"></acf:Column>
				<acf:Column type="date" name="purchase_order_date" caption="P.O. Date" width="300"></acf:Column>
				<acf:Column name="supplier_code" caption="Supplier Code" width="100"></acf:Column>
				<acf:Column name="supplier_name" caption="Supplier Name" width="300"></acf:Column>
				<acf:Column name="department_reference_no" caption="Dept. Ref.NO." width="100"></acf:Column>
			
			</acf:Grid>
	    </div>
	</acf:Region>
	
	<acf:Region id="mod_main" title="PURCHASE ORDER MAINTENANCE" type="form">
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="purchase_order_no">P.O. No.:</label>
      		<div class="col-md-4">    
      			<acf:TextBox id="purchase_order_no" name="purchase_order_no" readonly="true" disabled="true" checkMandatory="false"/>
      			     
      		</div>
      		
      		<div class="col-md-4">
      		</div>	
      		<acf:Button id="Generate_Report" title="Print">
			<acf:Bind on="click">
	    			<script>
	    				purchase_order_no = $("#frm_main #purchase_order_no").getValue();
					//	if (p_report_date != "") {	
					//		p_report_date = moment(p_report_date, 'x').format("YYYY-MM-DD");
					//	} else {
					//    	p_report_date = "NA";
					//	}
	  					$.ajax({
						headers: {
							'Accept'       : 'application/json',
							'Content-Type' : 'application/json; charset=utf-8'
						},
						async  : false,
						type   : "POST",
						url    : "${ctx}/arc/apw/apwf003/apwf003-generate-report.ajax",
						data   : JSON.stringify({
							'purchase_order_no'  : purchase_order_no
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
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="purchase_order_date">P.O. Date:</label>
      		<div class="col-md-4">    
      			<!--<acf:DateTime id="po_date" name="po_date" maxlength="60" />-->   
      			<acf:DateTimePicker id="purchase_order_date" name="purchase_order_date" pickDate="true"  displayformat="YYYY/MM/DD" checkMandatory="true">
			   			<acf:Bind on="validate"><script>
			   				//var ts = $(this).getValue();
			   				//if (ts != "" && ts > 1420113600000) { // 2015/01/01 20:00
			   			//		$(this).setError(ACF.getQtipHint("EXE003V"), "EXE003V");
			   			//	}
			   			//	else {
			   			//		$(this).setError("", "EXE003V");
			   			//	}
			   			</script></acf:Bind>
			   		</acf:DateTimePicker>
      		</div>
      		
    	</div>  
    	
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="department_reference_no">Dept.Ref. No.:</label>
      		<div class="col-md-4">    
      			<acf:TextBox id="department_reference_no" name="department_reference_no"  maxlength="10"/>
      		</div>
      		<div class="hidden">  
      			<acf:TextBox id="latest_receive_date" name="latest_receive_date" />
      			<acf:TextBox id="section_id" name="section_id" />
      			<acf:TextBox id="in_stock_location_code" name="in_stock_location_code" />     
      		</div>
      	</div>
      	
      	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="receive_location">Receiving Loc:</label>
      		<div class="col-md-2">
      		       
      			<acf:RadioButton id="TKO" name="locationb" maxlength="60"  buttonValue="TKO" label="TKO" disabled="true" checkMandatory="true">
      			<acf:Bind on="change"><script>
      			if ($(this).acfRadioButton("getValue") == 1){
        				$("#receive_location").setValue("TKO");
        				$("#receive_location").disable();
 						}
        			</script></acf:Bind>
        		</acf:RadioButton>   
        	
      		</div>
      		<div class="col-md-2">          
      			<acf:RadioButton id="Other" name="locationb" maxlength="60" label="Other" buttonValue="Other" disabled="true">
      			<acf:Bind on="change"><script>
        	if ($(this).acfRadioButton("getValue") == 1){
        				$("#receive_location").setValue("");
        				$("#receive_location").enable();
 						}
        			</script></acf:Bind>
        			</acf:RadioButton> 
      		</div>
      		
      		<div class="col-md-2">          
      			<acf:TextBox id="receive_location" name="receive_location" maxlength="60" label="Other" checkMandatory="true">
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
    	
    	 <div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="department_reference_no">Supplier.:</label>
      		<div class="col-md-4">    
      			<acf:ComboBox id="supplier_code" name="supplier_code" checkMandatory="true">  
      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${modulesf} );
	 				</script></acf:Bind>  
      			<acf:Bind on="change" modified="true"><script>
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
					</acf:ComboBox>
      		</div>
      		
<!--       		<div class="col-md-4">           -->
<%--       			<acf:TextBox id="supplier_name" name="supplier_name" maxlength="60" readonly="true" checkMandatory="false" />   --%>
      			
      						
<!--       		</div> -->
    	</div>
    	
    	  <div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-sm-2" for="remarks">Remarks.:</label>
      		<div class="col-md-4">    
      			<acf:TextBox id="remarks" name="remarks"  maxlength="60"/>     
      		</div>
      	</div>
    	
    	
   	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="cancel_indicator">Cancel:</label>
      		
  		
<!--      		<div class="col-md-10">
	      			<acf:ButtonGroup id="cancel_indicator" name="cancel_indicator" type="radio" checkMandatory="true">
	      				<setting>
			    			<option id="cancel_ind_y" value="y" label="Cancelled" />
			    			<option id="cancel_ind_n" value="n" label="Not Cancelled" />
			    		</setting>
			    		
			    		<acf:Bind on="click"><script></script></acf:Bind>
	      			</acf:ButtonGroup>
	      		</div>   --> 
	      		
	      	<div class="col-md-1">
      			
      			<acf:CheckBox id="cancel_indicator" name="cancel_indicator" choice="Y,N"/>
      		</div>
      		
	      	<!-- </div>
      		 <div class="col-xs-12 form-padding oneline">  -->
      		<label class="control-label col-md-2" for="cancel_by">Cancelled By:</label>
      		<div class="col-md-2">         
      			<acf:TextBox id="canceled_by" name="cancel_by" maxlength="30" readonly="true" readonly="true"/>	
			 </div>
			 <label class="control-label col-md-2" for="Cancel_date">Cancelled Date :</label>
      		<div class="col-md-2">         
      			
      			<acf:DateTimePicker id="cancel_date" name="cancel_date" checkMandatory="false" readonly="true" displayformat="YYYY/MM/DD"   useDefValIfEmpty="true"/>
			   			<acf:Bind on="validate"><script>
			   				//var ts = $(this).getValue();
			   				//if (ts != "" && ts > 1420113600000) { // 2015/01/01 20:00
			   			//		$(this).setError(ACF.getQtipHint("EXE003V"), "EXE003V");
			   			//	}
			   			//	else {
			   			//		$(this).setError("", "EXE003V");
			   			//	}
			   			</script></acf:Bind>
			   		
			 </div>
    	</div>
    	
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="printed_by">Printed by:</label>
      		<div class="col-md-2">    
      			<acf:TextBox id="printed_by" name="printed_by" maxlength="20" readonly="true"/>
			   		
      			   
      		</div>
      		<label class="control-label col-md-2" for="printed_at">Last Printed Date:</label>
      		<div class="col-md-2">         
      			<acf:DateTimePicker id="printed_at" name="printed_at"  checkMandatory="false" displayformat="YYYY/MM/DD" readonly="true"   useDefValIfEmpty="true"/>
      			<acf:Bind on="validate"><script>
			   				//var ts = $(this).getValue();
			   				//if (ts != "" && ts > 1420113600000) { // 2015/01/01 20:00
			   			//		$(this).setError(ACF.getQtipHint("EXE003V"), "EXE003V");
			   			//	}
			   			//	else {
			   			//		$(this).setError("", "EXE003V");
			   			//	}
			   			</script></acf:Bind>	
			 </div>
			 <label class="control-label col-md-1" for="no_of_times_printed">Times Printed:</label>
      		<div class="col-md-1">         
      			<acf:TextBox id="no_of_times_printed" name="no_of_times_printed" readonly="true" maxlength="40" />	
			 </div>
    	</div>
    	
    	
    	
	</acf:Region>
	
	<acf:Region id="item_details" title="ITEM DETAILS" type="form">			
		<div class="col-md-12">
			<acf:Grid id="grid_item" url="apwf003-get-item-table.ajax" autoLoad="false" addable="true" deletable="true" editable="true" rowNum="9999" multiLineHeader="true">
				<acf:Column name="item_no" caption="Item No." width="200" editable="true" checkMandatory="true" type="select" initData="${moduleselect}">
				
				<acf:Bind on="change"><script>
				var id = $("#grid_item").pGrid$getSelectedId();
				var pono = $("#mod_main #purchase_order_no").getValue();
				
				$("#grid_item").setRowData(id, {purchase_order_no: pono});
				var records = $("#grid_item").getRowData(id);
				$.ajax({ //grid combobox and multi-value input
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-item.ajax",
								data   : JSON.stringify({
									'item_no'	: records.item_no
								}),
								success: function(data) {
// 									console.log(item_no);
// 									console.log(data.item);
// 									console.log(data.item[0].un_it);
									if (data.item.length != 0) {
										//$("#frm_main #supplier_desc").setValue(data.sup_desc);
										//console.log(data.item.un_it);
										$("#grid_item").setRowData(id, {un_it: data.item[0].un_it, unit_cost: data.item[0].unit_cost, item_description_1: data.item[0].item_description_1});
										
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
									}
								}
							});
							//}
				</script>
				</acf:Bind></acf:Column>
				<acf:Column name="purchase_order_no" caption="P.O. NO." width="50" editable="false" hidden="true"></acf:Column>
				<acf:Column name="item_description_1" caption="Item Description" width="150" editable="false" readonly="true" hidden="true"></acf:Column>
				<acf:Column name="unit_cost" caption="Unit Cost" type="number" width="75" editable="true" checkMandatory="true"></acf:Column>
				<acf:Column name="order_quantity" caption="Order Qty" type="number" width="75" editable="true" checkMandatory="true">
				
				
				<acf:Bind on="validate"><script>
				function validation (newValue, oldValue, newData, oldData, id) {
				var unitcost = $("#grid_item").getRowData(id).unit_cost;
				var ttl = unitcost * newValue;
				$("#grid_item").setRowData(id, {tl_cost:ttl});
				}
				</script></acf:Bind>
				</acf:Column>
				<acf:Column name="un_it" caption="Unit" width="75" readonly="true" editable="false"></acf:Column>
				
				<acf:Column name="tl_cost" caption="Amount($)" width="170" type="number" readonly="true" align="right" readonly="true" checkMandatory="true"></acf:Column>
				<acf:Column name="created_at" caption="" hidden="true"></acf:Column>
				<acf:Column name="created_by" caption="" hidden="true"></acf:Column>
				
				
				
			</acf:Grid>
			<div class="col-xs-12" style="height:20px"></div>
			
	    </div>
	    <div class="col-xs-12 form-padding oneline">
	    <div class="col-md-9">
      		</div>	
     		<label class="control-label col-md-2" for="total_payment_amount">Total Amount</label>
      		<div class="col-md-1">
      			<acf:TextBox id="total_payment_amount" name="total_payment_amount" readonly="true" useNumberFormat="true" align="right"></acf:TextBox>
        	</div>	        	
    	</div>
    	<div class="col-xs-12 form-padding oneline">
	    
     		        	
    	</div>
	</acf:Region>
	

	<acf:Region id="reg_stat" title="UPDATE STATISTICS">
		<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="modified_at">Modified At:</label>
      		<div class="col-md-4">          
        		<acf:DateTime id="modified_at" name="modified_at" readonly="true" useSeconds="true"/>  	
        	</div>
     		<label class="control-label col-md-2" for="created_at">Created At:</label>
      		<div class="col-md-4"> 
      			<acf:DateTime id="created_at" name="created_at" readonly="true" useSeconds="true"/>           
      		</div>
    	</div>
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="modified_by">Modified By:</label>
      		<div class="col-md-4">          
        		<acf:TextBox id="modified_by" name="modified_by" readonly="true"/>  	
        	</div>
     		<label class="control-label col-md-2" for="created_by">Created By:</label>
      		<div class="col-md-4"> 
      			<acf:TextBox id="created_by" name="created_by" readonly="true"/>           
      		</div>
    	</div>

	</acf:Region>	

   	</form>
	
</div>

<script>

Controller.setOption({
	searchForm: $("#frm_search"),
	browseGrid: $("#grid_browse"),
	searchKey: "purchase_order_no",
	browseKey: "purchase_order_no",
	//searchForm: $("#frm_search"),
	//browseKey: "section_no",
	
	
	//initMode: "${mode}",
	recordForm: $("#frm_main"),
	recordKey: {
		purchase_order_no: "${purchase_order_no}", //labout_type on the left is the key of the table, the value on the right is redundant(design problem).
		//supplier_code: "${supplier_code}"
	},
	getUrl: "apwf003-get-form.ajax",
	saveUrl: "apwf003-save.ajax",
	

	onLoadSuccess: function(data)
	{
	$("#total_payment_amount").setValue($("#grid_item").pGrid$getSumOfColumn("tl_cost"));
	supplier_code = $("#frm_main #supplier_code").getValue();
   						
   					//	if (supplier_code == ""){
   							//$("#frm_main #supplier_desc").setValue($("#frm_main #supplier_desc").oldValue);
   						//}
   						//else 
   						//{					
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
								//$("#frm_main #supplier_desc").setValue($("#frm_main #supplier_desc").oldValue);
									if (data.supplier_name != null) {
										$("#frm_main #supplier_name").setValue(data.supplier_name);
									
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
										$("#frm_main #supplier_name").setValue("2");
									}
								}
							});
							
							cancel_indicator = $("#frm_main #cancel_indicator").getValue(); //this block is the same as onLoadSucess except $this value
   							 purchase_order_no = $("#frm_main #purchase_order_no").getValue();
   							 
   						if (cancel_indicator == "n"){
   							//$("#frm_main #supplier_desc").setValue("");	
   							setTimeout(function(){
	   							$.ajax({
									headers: {
										'Accept'       : 'application/json',
										'Content-Type' : 'application/json; charset=utf-8'
									},
									async  : false,
									type   : "POST",
									url    : "${ctx}/arc/apw/apw-get-pono-in-inv.ajax",
									data   : JSON.stringify({
										'purchase_order_no'	: purchase_order_no,
									}),
									success: function(data) {
									var pono = data.inv;
									console.log(data.inv);
									console.log(purchase_order_no);
										if (pono == purchase_order_no) {
										console.log(123);
											$("#frm_main #cancel_indicator").disable();
										}
										else {
											//$("#frm_main #cancel_ind").enable();
										}
									}
								});
   							},100);				
	   						
   						}
   						//}
	
	//$("#frm_main #supplier_desc").setValue("1");
	},
	
getSaveData: function() {
	var item_no_string = new String();
	var rowKeys = $("#grid_item").getDataIDs();
	
	for ( i = 0; i < rowKeys.length; i++) {
	console.log($("#grid_item").pGrid$getRecord()[i+1]["item_no"]);
	item_no_string = item_no_string.concat($("#grid_item").pGrid$getRecord()[i+1]["item_no"],",")
	}
// $("#grid_item").pGrid$setHiddenValueForAllRecords("purchase_order_no", $("#frm_main #purchase_order_no").getValue());
		return JSON.stringify({
			'form' : Controller.opt.recordForm.pForm$getModifiedRecord( Action.getMode() ),
			'PoItem' : $("#grid_item").pGrid$getModifiedRecord(), 
			'PoItemrec' : $("#grid_item").pGrid$getRecord()["item_no"],
			'Item' : $("#grid_item").pGrid$getModifiedRecord(),
			'item_no_string' : item_no_string,
		//	'producer' : $("#grid_producer").pGrid$getModifiedRecord(),
		//	'director' : $("#grid_director").pGrid$getModifiedRecord(),
		
		'form_n' : [{
			"id":0,"action":2,
			"modified_at" : $("#frm_main #modified_at").getValue(),
			"modified_by" : "",
			"created_at":1497938154968,
			"created_by":"",
			"item_no":"",
			"purchase_order_no":"",
			"purchase_order_date":$("#frm_main #modified_at").getValue(),
			"order_quantity":"",
			"received_quantity":"",
			"current_received_quantity":"",
			"receive_date":"",
			"consumed_quantity":"",
			"adjusted_quantity":"",
			"back_order_quantity":"",
			"current_back_order_quantity":"",
			"unit_cost":"",
			"version":1, //reference version
			
			
			},
			{
			"id":0,"action":2,
			"modified_at" : $("#frm_main #modified_at").getValue(),
			"modified_by" : "",
			"created_at":1497938154968,
			"created_by":"",
			"item_no":"",
			"purchase_order_no":"",
			"purchase_order_date":$("#frm_main #modified_at").getValue(),
			"order_quantity":"",
			"received_quantity":"",
			"current_received_quantity":"",
			"receive_date":"",
			"consumed_quantity":"",
			"adjusted_quantity":"",
			"back_order_quantity":"",
			"current_back_order_quantity":"",
			"unit_cost":"",
			"version":2, //update version
			
			
			}],

		});
		
	},
ready: function() { Action.setMode("search"); },

onSaveSuccessCallback : function(data){
		if (data.action == 1)
           alert("record " + data.new_purchase_order_no  +" created");
        else if (data.action == 2){}
          // alert("record " + data.new_purchase_order_no  +" updated");
        
                  
      },

}).executeSearchBrowserForm(); 



$(document).on('amend', function() {
	$("#frm_main #section_id").setValue("03");
	$("#frm_main #supplier_name").disable();
	$("#frm_main #canceled_by").disable();
	$("#frm_main #printed_by").disable();
	$("#frm_main #printed_at").disable();
	$("#frm_main #no_of_times_printed").disable();
	$("#frm_main #Other").enable();
	$("#frm_main #TKO").enable();
	var grecord = $("#grid_item").pGrid$getRecord();
	console.log(grecord);
	console.log($("#grid_item").getDataIDs());
	console.log($("#grid_item").pGrid$getModifiedRecord());
	//get item_no from records
	
	
	if($("#mod_main #cancel_indicator").getValue() == "Y")
	{
	$("#mod_main #cancel_date").disable();	
	}
	else if ($("#mod_main #cancel_indicator").getValue() != "Y")
	{
	$("#mod_main #cancel_date").enable();	
	}
	
	setTimeout(function(){  //check inventory
	   							$.ajax({
									headers: {
										'Accept'       : 'application/json',
										'Content-Type' : 'application/json; charset=utf-8'
									},
									async  : false,
									type   : "POST",
									url    : "${ctx}/arc/apw/apw-get-pono-in-inv.ajax",
									data   : JSON.stringify({
										'purchase_order_no'	: purchase_order_no,
									}),
									success: function(data) {
									var total_received = data.inv;
									console.log(data.inv);
									console.log(purchase_order_no);
										if (total_received > 0) {
										console.log(123);
											$("#frm_main #cancel_indicator").disable();
										}
										else {
											//$("#frm_main #cancel_ind").enable();
										}
									}
								});
   							},10);			
	
});


$(document).on('new', function() {
	$("#frm_main #section_id").setValue("03");
	$('#grid_item').pGrid$clear();
	$("#mod_main #purchase_order_no").enable();	
	$("#frm_main #supplier_name").disable();
	$("#frm_main #canceled_by").disable();
	$("#frm_main #printed_by").disable();
	$("#frm_main #printed_at").disable();
	$("#frm_main #no_of_times_printed").setValue(0);
	$("#frm_main #latest_receive_date").setValue(0);
	$("#frm_main #cancel_indicator").setValue('n');
	$("#frm_main #no_of_times_printed").disable();
	$("#frm_main #cancel_date").setValue(moment("1900-01-01"));
	$("#frm_main #printed_at").setValue(moment("1900-01-01"));
	$("#frm_main #Other").enable();
	$("#frm_main #TKO").enable();
			//$.ajax({//generate new pruchase order number
				//				headers: {
				//					'Accept'       : 'application/json',
				//					'Content-Type' : 'application/json; charset=utf-8'
				//				},
				//				async  : false,
				//				type   : "POST",
				//				url    : "${ctx}/arc/apw/apwf003/apwf003-generate-name.ajax",
				//				//"${ctx}/arc/apw/apwf003/apwf003-generate-name.ajax"
				//				data   : JSON.stringify({
				//					//'supplier_code'	: supplier_code,
				//				}),
				//				success: function(data) {
				//				//$("#frm_main #supplier_desc").setValue($("#frm_main #supplier_desc").oldValue);
				//				//console.log(data.form_id + data.six_digit_serial_no);
				//					if (data.form_id != null) {
				//						$("#mod_main #purchase_order_no").setValue(data.form_id + data.six_digit_serial_no);
				//					
				//					}
				//					else {
				//						//$("#frm_main #supplier_desc").setValue("");
				//						$("#mod_main #purchase_order_no").setValue(data.form_id + data.six_digit_serial_no);
				//					}
				//				}
				//			});
	
});


$(document).on('view', function() {
	
	$("#frm_main #Other").disable();
	$("#frm_main #TKO").disable();
});
$(document).on("clone", function() {

$("#grid_item").pGrid$copyRecord();
$("#frm_main #printed_by").setValue("");
$("#frm_main #printed_at").setValue("");
$("#frm_main #no_of_times_printed").setValue(0);
});

function calculatePaymentAmount(){
	$.each($("#grid_item").pGrid$getRecord(), function(id, rec){
		if(rec.order_quantity) {
			$("#grid_item").setRowData(id, {
				tl_cost: rec.order_quantity * rec.unit_cost
			});
		}
	});
	}
</script>
						