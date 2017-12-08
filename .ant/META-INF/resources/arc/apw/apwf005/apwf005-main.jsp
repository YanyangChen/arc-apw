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
	      			<label for=consumption_form_no style="display:block">Consumption Form No.</label>
	      			<acf:TextBox id="s_consumption_form_no" name="consumption_form_no" editable="true" maxlength="60"/>  
	      		</div>
	      		
	    	
	      		<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=programme_no style="display:block">Programme No.</label>
	      			<acf:TextBox id="s_programme_no" name="programme_no" editable="true" maxlength="60"/>  
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
	
	<form id="frm_main" class="form-horizontal" data-role="form" >
		<acf:Region id="reg_div_list" type="list" title="SECTION LIST">
   		<acf:RegionAction>
			<a href="javascript:$('#grid_browse').pGrid$prevRecord();">Previous</a>
			&nbsp;
			<a href="javascript:$('#grid_browse').pGrid$nextRecord();">Next</a>
		</acf:RegionAction>
		<div class="col-xs-12">
			<acf:Grid id="grid_browse" url="apwf005-search.ajax" readonly="true" autoLoad="false">
			<acf:Column name="consumption_form_no" caption="Consumption Form No" width="100"></acf:Column>
			<acf:Column name="completion_date" caption="Completion Date" type="date"  width="100"></acf:Column>
			<acf:Column name="programme_no" caption="Programme No." width="100"></acf:Column>
			<acf:Column name="chinese_programme_name" caption="Chi. Prog Name" width="100"></acf:Column>	
			<acf:Column name="programme_name" caption="Eng. Prog Name" width="100"></acf:Column>
				
			</acf:Grid>
	    </div>
	</acf:Region>
	
		<acf:Region id="reg_func_main" type="form" title="CONSUMPTION MAINTENANCE">
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="consumption_form_no">Consumption Form No.:</label>
      		<div class="col-md-1">
      			<acf:TextBox id="consumption_form_no" name="consumption_form_no" maxlength="8" checkMandatory="false" readonly="true" editable="false"/>
      				
      			   
        	</div>
     		
     		<div class="col-md-3">
      				
      			   
        	</div>
     		
     		<label class="control-label col-md-2" for="adjustment_indicator">Adjustment:</label>
      		<div class="col-md-1">
      			
      			<acf:CheckBox id="adjustment_indicator" name="adjustment_indicator" choice="y,n"/>
      		</div>
     		
    	</div>  
    	
    	
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="completion_date">Completion Date:</label>
      		<div class="col-md-2">
      			<acf:DateTimePicker id="completion_date" name="completion_date" maxlength="8" pickTime="false" checkMandatory="true">
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
      		<div class="col-md-3">
      			<acf:ComboBox id="location_code" name="location_code" maxlength="3" checkMandatory="true">
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
										$("#frm_main #location_name").setValue(data.location_name);
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
      			<acf:TextBox id="location_name" name="location_name" readonly="true" maxlength="60"/> 
      		</div>
     		
    	</div>
    	
    
    	
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="programme_no">Programme No.:</label>
      		<div class="col-md-3">
      			<acf:ComboBox id="programme_no" name="programme_no" maxlength="9" checkMandatory="true">
      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${programmeno} );
	 				</script></acf:Bind>
      			<acf:Bind on="change"><script>
				var programme_no = $(this).getValue();
				
				//$("#grid_item").setRowData(id, {purchase_order_no: pono});
				
				$.ajax({ //grid combobox and multi-value input
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-programme-fields.ajax",
								data   : JSON.stringify({
									'programme_no'	: programme_no
								}),
								success: function(data) {
									console.log(data.programme_name);
									//console.log(data.item[0].un_it);
									if (data.programme_name != null) {
										//$("#frm_main #supplier_desc").setValue(data.sup_desc);
										
										//$("#grid_item").setRowData(id, {un_it: data.item[0].un_it, unit_cost: data.item[0].unit_cost, item_description_1: data.item[0].item_description_1});
										
										$("#frm_main #programme_name").setValue(data.programme_name);
										$("#frm_main #business_platform").setValue(data.business_platform);
										$("#frm_main #department").setValue(data.department);
										
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
      			<acf:TextBox id="programme_name" name="programme_name" readonly="true" maxlength="60"/> 
      		</div>
     		
    	</div>  
    	 
 		<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="business_platform">Business Platform:</label>
      		<div class="col-md-2">
      			<acf:ComboBox id="business_platform" name="business_platform"
						pickTime="false">
						<acf:Bind on="initData">
							<script>
	 					$(this).acfComboBox("init", ${BusinessPlatform} );
	 					
	 				</script>
						</acf:Bind>
					</acf:ComboBox>
      		</div>
     		<label class="control-label col-md-2" for="department">Programme Nat./Dept:</label>
      		<div class="col-md-2">
      			<acf:ComboBox id="department" name="department" editable="true"
						multiple="false">
						<acf:Bind on="initData">
							<script>
	 					$(this).acfComboBox("init", ${Department} );
	 				</script>
						</acf:Bind>
					</acf:ComboBox>
      		</div>
      		<div class="hidden">
      		<label class="control-label col-md-2" for=busi_description>Description:</label>
      		<div class="col-md-2">
      			<acf:TextBox id="busi_description" name="busi_description" readonly="true" maxlength="30">
      			
      			</acf:TextBox>
      		</div>
      		</div>
    	</div>
    	
    	
    	
    
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="from_episode_no">Episode No.From.:</label>
      		<div class="col-md-1">
      			<acf:TextBox id="from_episode_no" name="from_episode_no" checkMandatory="true" maxlength="7">
      			<acf:Bind on="validate"><script>
      			if( ($(this).getValue() > ($("#frm_main #to_episode_no").getValue())))
      			{
      				$(this).setError(ACF.getQtipHint("APW205V"), "APW205V");
			   	}
			   	
			   	else
			   	{
					$(this).setError("", "APW205V");
					$("#frm_main #to_episode_no").setError("", "APW205V");
			   	}
      				</script>
      				</acf:Bind>
      			   </acf:TextBox>
        	</div>
     		<label class="control-label col-md-1" for="to_episode_no">To.:</label>
      		<div class="col-md-1">
      			<acf:TextBox id="to_episode_no" name="to_episode_no" maxlength="7" checkMandatory="true"> 
      			<acf:Bind on="validate"><script>
      			if( ($(this).getValue() < ($("#frm_main #from_episode_no").getValue())))
      			{
      				$(this).setError(ACF.getQtipHint("APW205V"), "APW205V");
			   	}
			   	
			   	else
			   	{
					$(this).setError("", "APW205V");
					$("#frm_main #from_episode_no").setError("", "APW205V");
			   	}
      				</script>
      				</acf:Bind>
      			   </acf:TextBox>
      		</div>
    	</div>  
    	 
 		<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="job_description">Job Description:</label>
      		<div class="col-md-4">
      			<acf:TextBox id="job_description" name="job_description" maxlength="60"/> 
      		</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="construction_no">Construction No.:</label>
      		<div class="col-md-2">
      			<acf:TextBox id="construction_no" name="construction_no" maxlength="15"/> 
      		</div>
     		
    	</div>
    	
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="group_no">Group No.:</label>
      		<div class="col-md-3">
      			<acf:ComboBox id="group_no" name="group_no" maxlength="2">
      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${groupno} );
	 				</script></acf:Bind>
      			<acf:Bind on="change"><script>
				var group_no = $(this).getValue();
				
				//$("#grid_item").setRowData(id, {purchase_order_no: pono});
				
				$.ajax({ //grid combobox and multi-value input
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-get-group-name.ajax",
								data   : JSON.stringify({
									'group_no'	: group_no
								}),
								success: function(data) {
									//console.log(data.item);
									//console.log(data.item[0].un_it);
									if (data.group_name != null) {
										//$("#frm_main #supplier_desc").setValue(data.sup_desc);
										
										//$("#grid_item").setRowData(id, {un_it: data.item[0].un_it, unit_cost: data.item[0].unit_cost, item_description_1: data.item[0].item_description_1});
										console.log(data.group_name);
										$("#frm_main #group_name").setValue(data.group_name);
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
<!--      		<label class="control-label col-md-2" for="group_name">Desc./ Remark of Group:</label> -->
      		<div class="hidden">
      			<acf:TextBox id="group_name" readonly="true" name="group_name" maxlength="30"/> 
      		</div>
    	</div> 
    	
    	<div class="hidden">
     		<label class="control-label col-md-2" for="section_id">Section ID</label>
      		<div class="col-md-4">
      			<acf:TextBox id="section_id" name="section_id"/>
      				
      			   
        	</div>
     		
    	</div> 
    	 
    	
 		<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="remarks">Remarks:</label>
      		<div class="col-md-4">
      			<acf:TextBox id="remarks" name="remarks" maxlength="60"/> 
      		</div>
     		
    	</div>
    	
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="cancel_indicator">Cancel:</label>
      		<div class="col-md-1">
      			
      			<acf:CheckBox id="cancel_indicator" name="cancel_indicator" choice="y,n"/>
      		</div>
     		
     		<label class="control-label col-md-1" for="cancel_by">Cancel By.:</label>
      		<div class="col-md-1">
      			<acf:TextBox id="cancel_by" name="cancel_by" readonly="true" maxlength="20"/>
      				
      			   
        	</div>
        	
        	<label class="control-label col-md-1" for="cancel_date">Cancel Date:</label>
      		<div class="col-md-2">
      			<acf:DateTimePicker id="cancel_date" name="cancel_date" maxlength="8" readonly="true" pickTime="false">
      			
      			</acf:DateTimePicker>
      		</div>
    	</div>
    	
    	
    	
 		<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="input_date">Input Date:</label>
      		<div class="col-md-2">
      			<acf:DateTimePicker id="input_date" name="input_date" maxlength="8" pickTime="false" checkMandatory="true">
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
     		<label class="control-label col-md-1" for="cut_off_date">Cut-off Date:</label>
      		<div class="col-md-2">
      			<acf:DateTimePicker id="cut_off_date" name="cut_off_date" maxlength="8" readonly="true" pickTime="false"/> 
      		</div>
    	</div>
    	
    	
    </acf:Region>	
		
		
	<acf:Region id="Item_consumption" type="form" title="ITEM LIST">
   		
		<div class="col-xs-12">
			<acf:Grid id="item_browse" url="apwf005-get-consumption-item.ajax" editable="true" autoLoad="false" addable="true" deletable="true" editable="true" rowNum="9999" multiLineHeader="true">
				<acf:Column name="item_no" caption="ITEM No." width="100" editable="true" type="select" checkMandatory="true" columnKey="true" maxlength="7" initData="${itemnoselect}" >
				<acf:Bind on="change"><script>
				//get record from item_inventory for unit_cost, purchase_order(hidden), from item_master for description1
				//subtraction from item_inventory table required
				var id = $("#item_browse").pGrid$getSelectedId();  
				//var pono = $("#mod_main #purchase_order_no").getValue();
				
				//$("#grid_item").setRowData(id, {purchase_order_no: pono});
				var records = $("#item_browse").getRowData(id);
				$.ajax({ //grid combobox and multi-value input
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-item-inv.ajax",
								data   : JSON.stringify({
									'item_no'	: records.item_no
								}),
								success: function(data) {
									console.log("testing-------000");
									console.log(data);
									if (data.item[0] != null) {
									console.log("testing-------");
										console.log(data.item[0].unit_cost);
										$("#item_browse").setRowData(id, {unit_cost: data.item[0].unit_cost, purchase_order_no: data.item[0].purchase_order_no});
										
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
									}
								}
							});
				
				
				$.ajax({ //grid combobox and multi-value input
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-item-mas.ajax",
								data   : JSON.stringify({
									'item_no'	: input.newValue
								}),
								success: function(data) {
									//console.log(data.item);
									//console.log(data.item[0].un_it);
									if (data.item != null) {
										//$("#frm_main #supplier_desc").setValue(data.sup_desc);
										//console.log(data.item[0].unit_cost);
// 										$("#item_browse").setRowData(id, {item_description_1: data.item[0].item_description_1});
										
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
									}
								}
							});
							//}
				</script>
				</acf:Bind>
				</acf:Column>
				<acf:Column name="item_description_1" caption="ITEM DESC." width="100" hidden="true" readonly="true" maxlength="30"></acf:Column>
				
				<acf:Column name="purchase_order_date" caption="P.O Date" type="date" width="100"></acf:Column>
				
				<acf:Column name="unit_cost" caption="Unit Cost" width="100" readonly="true" maxlength="12"></acf:Column>
				
				<acf:Column name="re_used_indicator" caption="Reused Ind." width="100" type="checkBox" editable="true" readonly="false"></acf:Column> 
				<acf:Column name="consumption_quantity" caption="Consumption Qty." width="100" editable="true" maxlength="8">
				<acf:Bind on="validate"><script>
				function validation (newValue, oldValue, newData, oldData, id) {
				var unitcost = $("#item_browse").getRowData(id).unit_cost;
				var qty = $("#item_browse").getRowData(id).consumption_quantity;
				var ttl = unitcost * qty;
				$("#item_browse").setRowData(id, {amount:ttl});
				if( newValue < 0 )
				{
					return ACF.getQtipHint("APW105V");
			   		}
				
				return null;
				}
				</script></acf:Bind></acf:Column>
				<acf:Column name="account_allocation" caption="A/C Alloc." maxlength="2" width="100" editable="true" type="select" initData="${ACselect}" checkMandatory="true"></acf:Column>
				<acf:Column name="amount" caption="Amount" width="100" readonly="true" align = "right"></acf:Column>
				<acf:Column name="consumption_form_no" hidden="true" caption="Amount1" width="100"></acf:Column>
				<acf:Column name="programme_no" hidden="true" caption="Amount2" width="100"></acf:Column>
				<acf:Column name="purchase_order_no" hidden="true" caption="Amount4" width="100"></acf:Column>
				<acf:Column name="input_date" hidden="true" caption="Amount3" width="100"></acf:Column>
			</acf:Grid>
	    </div>
	</acf:Region>
	
	<acf:Region id="Other_material_consumption" type="form" title="MATERIAL LIST">
   		
		<div class="col-xs-12">
			<acf:Grid id="material_browse" url="apwf005-get-material-consumption.ajax" editable="true" autoLoad="false" addable="true" deletable="true" editable="true" rowNum="9999" multiLineHeader="true">
				<acf:Column name="sequence_no" caption="Seq. No" hidden="true" width="100">
				
				</acf:Column>
				<acf:Column name="other_material_description" caption="Other Material Cost Desc." editable="true" width="100" type="select" checkMandatory="true" maxlength="60" initData="${OtherMaterialselect}">
				<acf:Bind on="change"><script>
				var id = $("#material_browse").pGrid$getSelectedId();
				var records = $("#material_browse").getRowData(id);
				$.ajax({ //grid combobox and multi-value input
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-other-material-unitcost.ajax",
								data   : JSON.stringify({
									'other_material'	: records.other_material_description
								}),
								success: function(data) {
									console.log(data.unitcost);
									if (data.unitcost != null) {
											$("#material_browse").setRowData(id, {unit_cost: data.unitcost});
										}
									else {
										}
								}
							});
							//}
				</script>
				</acf:Bind>
				</acf:Column>
				<acf:Column name="account_allocation" caption="A/C Alloc." width="100" editable="true" type="select" maxlength="2" initData="${ACselect}" checkMandatory="true"></acf:Column> 
				<acf:Column name="unit_cost" caption="Unit Cost" width="100" editable="true" checkMandatory="true">
				<acf:Bind on="validate"><script>
				function validation (newValue, oldValue, newData, oldData, id) {
				var unitcost = $("#material_browse").getRowData(id).unit_cost;
				var ttl = unitcost;
				$("#material_browse").setRowData(id, {other_material_amount:ttl});
				}
				</script></acf:Bind>
				</acf:Column>
				<acf:Column name="other_material_amount" caption="Amount" readonly="true" maxlength="12" width="100" align="right" editable="false"></acf:Column>
				<acf:Column name="consumption_form_no" hidden="true" caption="Amount1" width="100"></acf:Column>
				<acf:Column name="programme_no" hidden="true" caption="Amount2" width="100"></acf:Column>
				<acf:Column name="input_date" hidden="true" caption="Amount3" width="100"></acf:Column>
				
			</acf:Grid>
	    </div>
	</acf:Region>
	
	<acf:Region id="Labour_consumption" type="form" title="LABOUR LIST">
   		
		<div class="col-xs-12">
			<acf:Grid id="labour_browse" url="apwf005-get-labour-consumption.ajax" editable="true" autoLoad="false" addable="true" deletable="true" editable="true" rowNum="9999" multiLineHeader="true">
				<acf:Column name="labour_type" caption="Labour Type" width="100" editable="true"  maxlength="2" readonly="true" type="select" initData="${LabourTypeselect}" checkMandatory="true">
				<acf:Bind on="change"><script>
				var id = $("#labour_browse").pGrid$getSelectedId();
				//var pono = $("#mod_main #purchase_order_no").getValue();
				
				//$("#grid_item").setRowData(id, {purchase_order_no: pono});
				var records = $("#labour_browse").getRowData(id);
				$.ajax({ //grid combobox and multi-value input
								headers: {
									'Accept'       : 'application/json',
									'Content-Type' : 'application/json; charset=utf-8'
								},
								async  : false,
								type   : "POST",
								url    : "${ctx}/arc/apw/apw-labour.ajax",
								data   : JSON.stringify({
									'labour_type'	: records.labour_type
								}),
								success: function(data) {
									//console.log(data.item);
									//console.log(data.item[0].un_it);
									if (data.item != null) {
										//$("#frm_main #supplier_desc").setValue(data.sup_desc);
										
										$("#labour_browse").setRowData(id, {labour_type_description: data.item[0].labour_type_description, hourly_rate: data.item[0].hourly_rate});
										
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
									}
								}
							});
							//}
				</script>
				</acf:Bind>
				</acf:Column>
				<acf:Column name="labour_type_description" caption="Labour Type Desc" readonly="true" maxlength="30" width="100"></acf:Column>
				<acf:Column name="no_of_hours" caption="No. of Hour" width="100" editable="true" maxlength="8" checkMandatory="true">
				<acf:Bind on="validate"><script>
				function validation (newValue, oldValue, newData, oldData, id) {
				var noofhours = $("#labour_browse").getRowData(id).no_of_hours;
				var hourlyrate = $("#labour_browse").getRowData(id).hourly_rate;
				var ttl = hourlyrate * noofhours;
				$("#labour_browse").setRowData(id, {amount:ttl});
				}
				</script></acf:Bind>
				</acf:Column> 
				<acf:Column name="hourly_rate" caption="Hour Rate" readonly="true" maxlength="12" width="100"></acf:Column>
				
				<acf:Column name="amount" caption="Amount" align="right" readonly="true" maxlength="12" width="100"></acf:Column>
				<acf:Column name="consumption_form_no" hidden="true" caption="Amount1" width="100"></acf:Column>
				<acf:Column name="programme_no" hidden="true" caption="Amount2" width="100"></acf:Column>
				<acf:Column name="input_date" hidden="true" caption="Amount3" width="100"></acf:Column>
				<acf:Column name="modified_at" caption="" hidden="true"></acf:Column>
			</acf:Grid>
	    </div>
	</acf:Region>
	
		   <div class="col-xs-12 form-padding oneline">
	    <div class="col-md-9">
      		</div>	
     		<label class="control-label col-md-2" for="total_payment_amount">Total Amount</label>
      		<div class="col-md-1">
      			<acf:TextBox id="total_amount" name="total_amount" readonly="true" useNumberFormat="true" align="right"></acf:TextBox>
        	</div>	        	
    	</div>
	
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
	
	
</div>

<script>
/*
$("#frm_search").pForm$setRelatedComboBox(${moduleGroups}, [$("#frm_search #s_sec_no"), $("#frm_search #s_func_gp_name")]);
$("#frm_main").pForm$setRelatedComboBox(${moduleGroups}, [$("#frm_main #mod_id"), $("#frm_main #func_gp_name")]);
*/



Controller.setOption({
	searchForm: $("#frm_search"),
	browseGrid: $("#grid_browse"),
	browseKey: "consumption_form_no,programme_no",
	//redirect: "acff012-form",
	
	//initMode: "${mode}",
	recordForm: $("#frm_main"),
	recordKey: {
	programme_no: "${programme_no}",
	consumption_form_no: "${consumption_form_no}",
	purchase_order_no: "${purchase_order_no}",
	item_no: "${item_no}",
	},
	getUrl: "apwf005-get-form.ajax",
	saveUrl: "apwf005-save.ajax",
	
	onLoadSuccess: function(data)
	{
	$("#total_amount").setValue($("#item_browse").pGrid$getSumOfColumn("amount") + $("#material_browse").pGrid$getSumOfColumn("other_material_amount") + $("#labour_browse").pGrid$getSumOfColumn("amount"));
	},
	
	getSaveData: function() {
	$("#item_browse").pGrid$setHiddenValueForAllRecords("consumption_form_no", $("#frm_main #consumption_form_no").getValue());
	$("#item_browse").pGrid$setHiddenValueForAllRecords("programme_no", $("#frm_main #programme_no").getValue());
	$("#item_browse").pGrid$setHiddenValueForAllRecords("input_date", $("#frm_main #input_date").getValue());
	$("#labour_browse").pGrid$setHiddenValueForAllRecords("consumption_form_no", $("#frm_main #consumption_form_no").getValue());
	$("#material_browse").pGrid$setHiddenValueForAllRecords("consumption_form_no", $("#frm_main #consumption_form_no").getValue());
	$("#material_browse").pGrid$setHiddenValueForAllRecords("programme_no", $("#frm_main #programme_no").getValue());
	$("#material_browse").pGrid$setHiddenValueForAllRecords("input_date", $("#frm_main #input_date").getValue());
	$("#labour_browse").pGrid$setHiddenValueForAllRecords("programme_no", $("#frm_main #programme_no").getValue());
	$("#labour_browse").pGrid$setHiddenValueForAllRecords("input_date", $("#frm_main #input_date").getValue());
			return JSON.stringify({
			'form' : Controller.opt.recordForm.pForm$getModifiedRecord( Action.getMode() ),
			'Item' : $("#item_browse").pGrid$getModifiedRecord(), 
			'Material' : $("#material_browse").pGrid$getModifiedRecord(),
			'Labour' : $("#labour_browse").pGrid$getModifiedRecord(),
		});
	},
	ready: function() { Action.setMode("search"); },
	onSaveSuccessCallback : function(data){
		if (data.action == 1)
           alert("record " + data.consumption_form_no  +" created");
        else if (data.action == 2){}
          // alert("record " + data.new_purchase_order_no  +" updated");
        
                  
      },
}).executeSearchBrowserForm();


function func()
{
if ($("#frm_main #cancel_indicator").getValue() == 'y')
	{
		return "view";
	}
		return "amend";
}

$(document).on("new", function() {
$('#item_browse').pGrid$clear();
$('#labour_browse').pGrid$clear();
$('#material_browse').pGrid$clear();
$("#frm_main #cut_off_date").setValue(moment("1900-01-01"));
$("#frm_main #cancel_date").setValue(moment("1900-01-01"));
$("#frm_main #cancel_by").setValue("");
$("#frm_main #section_id").setValue("03");
var today = new Date();
$("#frm_main #input_date").setValue(today);
}), 

$(document).on("amend", function() {

$('#labour_browse').setGridParam({getMode : func});
$('#item_browse').setGridParam({getMode : func});
$('#material_browse').setGridParam({getMode : func});

if ($("#frm_main #cancel_indicator").getValue() == 'y')
{
$("#frm_main").pForm$disableAll();
// $('#labour_browse').pGrid$refreshRecord();
$('#item_browse').pGrid$refreshRecord();
$('#labour_browse').pGrid$refreshRecord();
$('#material_browse').pGrid$refreshRecord();
}

if($('#item_browse').pGrid$getSumOfColumn("consumption_quantity") != 0)
{
$("#frm_main #cancel_indicator").disable();
}
}),

$(document).on('clone', function() {
$('#item_browse').pGrid$clear();
$('#labour_browse').pGrid$clear();
$('#material_browse').pGrid$clear();
});

$("#frm_main").pForm$setRelatedComboBox(${businessDepartment}, [$("#frm_main #business_platform"), $("#frm_main #department")]);


</script>
<%
	helper.close();
%>