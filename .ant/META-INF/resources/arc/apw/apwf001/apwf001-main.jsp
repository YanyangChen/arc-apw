<%@ page import="acf.acf.General.jsp.*"%>
<%@ page import="java.util.*" %>
<%@ taglib prefix="acf" uri="/acf/tld/acf-taglib" %> 
	
<!-- PAGE CONTENT BEGINS -->
<div class="col-md-12 nopadding">
	<acf:Region id="reg_div_list" title="ITEM SEARCH" type="search">
		<acf:RegionAction>
			<a href="#" onClick="$(this).parents('.widget-box').pForm$clear();">Clear</a>
		</acf:RegionAction>
		<form id="frm_search" class="form-horizontal" data-role="search">
	    	<div class="form-group">
	      		<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=s_mod_id style="display:block">Item No/Cat.</label>
	      			<acf:ComboBox id="s_item_no" name="item_no" editable="true" multiple="false">
	      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${modules} );
	 				</script></acf:Bind>
	      			</acf:ComboBox>
	      			</div>
	      			<!-- <label for=s_mod_id style="display:block">Item No.</label>
	      			<acf:TextBox id="s_item_no" name="item_no" editable="true" multiple="false"></acf:TextBox> -->
	      			<div class="col-lg-2 col-md-2 col-sm-2 col-xs-3">
	      			<label for=s_mod_id style="display:block">Item Desc.</label>
	      			<acf:TextBox id="s_item_description_1" name="item_description_1" editable="true" multiple="false"></acf:TextBox>
	      			
	        	</div>
	    	</div>
		</form>
	</acf:Region>
</div> 

<div class="col-xs-12 nopadding">
	<form id="frm_main" class="form-horizontal" data-role="form" >
	<acf:Region id="reg_div_list" type="list" title="ITEM LIST">
   		<acf:RegionAction>
			<a href="javascript:$('#grid_browse').pGrid$prevRecord();">Previous</a>
			&nbsp;
			<a href="javascript:$('#grid_browse').pGrid$nextRecord();">Next</a>
		</acf:RegionAction>
		<div class="col-xs-12">
			<acf:Grid id="grid_browse" url="apwf001-search.ajax" readonly="true" autoLoad="false">
				<acf:Column name="item_no" caption="Item No." width="100"></acf:Column>
				<acf:Column name="material_type" caption="Material Type" hidden="true" width="100"></acf:Column>
				<acf:Column name="location_code" caption="Location Code" hidden="true" width="100">
				
				</acf:Column>
				<acf:Column name="item_description_1" caption="Item Desc1" width="100"></acf:Column>
				<acf:Column name="un_it" caption="Unit" width="100" hidden="true"></acf:Column>
				<acf:Column name="reference_unit_cost" caption="Reference Unit Cost" width="100" hidden="true"></acf:Column>
			
			</acf:Grid>
	    </div>
	</acf:Region>
	
	<acf:Region id="reg_mod_main" title="ITEM MAINTENANCE" type="form">
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="item_no">Item No.:</label>
      		<div class="col-md-1">    
      			<acf:TextBox id="item_no" name="item_no" maxlength="7" checkMandatory="true"/>
      			     
      		</div>
    	
    	</div> 
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="item_description_1">Item Desc. (1):</label>
      		<div class="col-md-4">    
      			<acf:TextBox id="item_description_1" name="item_description_1" maxlength="30" checkMandatory="true"/>
      			     
      		</div>
    	</div> 
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="item_description_2">Item Desc. (2):</label>
      		<div class="col-md-4">    
      			<acf:TextBox id="item_description_2" name="item_description_2" maxlength="30" checkMandatory="false"/>
      			     
      		</div>
    	</div>
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="material_type">Material Type:</label>
      		<div class="col-md-4">  
      			<acf:ButtonGroup id="material_type" name="material_type" type="radio" checkMandatory="true">
	      				<setting>
			    			<option id="material_type1" value="1" label="wood material" />
			    			<option id="material_type2" value="2" label="setting material" />
			    			<option id="material_type3" value="3" label="special effect" />
			    		</setting>
			    		
			    		<acf:Bind on="click"><script></script></acf:Bind>
	      			</acf:ButtonGroup>  
      			   
      		</div>
		</div>
    	
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="location_code">Location Code.:</label>
      		<div class="col-md-3">    
      			<acf:ComboBox id="location_code" name="location_code" forceCase = "upper" maxlength="3" checkMandatory="true">
      			<acf:Bind on="initData"><script>
	 					$(this).acfComboBox("init", ${locationcode} );
	 				</script></acf:Bind>

					</acf:ComboBox>
      			     
      		</div>
      		
      		
      		
    	</div> 
    	
    	
    	 
    	
    	<div class="hidden">    
      			<acf:TextBox id="section_id" name="section_id" />
      			     
      	</div>
    	
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="unit">Unit:</label>
      		<div class="col-md-2">    
      			<acf:TextBox id="unit" name="un_it" maxlength="10" checkMandatory="true"/>
      			     
      		</div>
    	</div> 
    	
    	
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="reference_unit_cost">Reference Unit Cost:</label>
      		<div class="col-md-2">    
      			<acf:TextBox id="reference_unit_cost" name="reference_unit_cost" readonly="true">
      			<acf:Bind on="change"><script>
	 					item_no = $("#frm_main #item_no").getValue(); //this bloack is the same as onLoadSucess except $this value
   						
   						
   						
   						if (item_no == ""){
   							//$("#frm_main #supplier_desc").setValue("");	
   						}
   						else {	
   						//if($("#frm_main #reference_unit_cost").getValue() != 0){				
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
										$("#frm_main #reference_unit_cost").setValue(data.unit_cost);
									}
									else {
										//$("#frm_main #supplier_desc").setValue("");
									}
								}
							});
   						}
   						//}
	 				</script></acf:Bind>
      			     </acf:TextBox>
      		</div>
    	</div> 
    	
    	
    	<div class="col-xs-12 form-padding oneline">
     		<label class="control-label col-md-2" for="safety_quantity">Safe Qty:</label>
      		<div class="col-md-2">    
      			<acf:TextBox id="safety_quantity" name="safety_quantity" maxlength="8"  checkMandatory="true"/>
<!--       			     format="^(([0-9]){0,5}[.]([0-9]){0,2})$" -->
      		</div>
    	</div> 

    <acf:Region id="remaining_inventory" type="form" title="INVENTORY LIST">
   		
		<div class="col-xs-12">
			<acf:Grid id="remaining_browse" url="apwf001-get-remaining-inventory.ajax" autoLoad="false" addable="false" deletable="false" editable="true" rowNum="9999" multiLineHeader="true">
				<acf:Column name="purchase_order_no" caption="Purchase Order No" width="100"></acf:Column>
				<acf:Column name="purchase_order_date" caption="Purchase Order Date" type="date" width="100"></acf:Column>
				<acf:Column name="remaining_quantity" caption="Remaining Qty" width="100"></acf:Column> 
				<acf:Column name="unit_cost" caption="Unit Cost" width="100"></acf:Column>
			</acf:Grid>
	    </div>
	</acf:Region>
	
	 <acf:Region id="adjustment_inventory" type="form" title="ADJUSTMENT LIST">
   		
		<div class="col-xs-12">
			<acf:Grid id="adjust_browse" url="apwf001-get-remaining-inventory.ajax" autoLoad="false" addable="false" deletable="false" editable="true" rowNum="9999" multiLineHeader="true">
				
				<acf:Column name="adjustment_date" caption="Adjustment Date" type="date" width="100"></acf:Column>
				<acf:Column name="adjust_quantity" caption="Adjustment Qty" width="100"></acf:Column> 
				
			</acf:Grid>
	    </div>
	</acf:Region>
	
	
	<acf:Region id="inventory" type="form" title="PENDING DELIVERY">
   		
		<div class="col-xs-12">
			<acf:Grid id="inventory_browse" url="apwf001-get-inventory.ajax" autoLoad="false" addable="false" deletable="false" editable="true" rowNum="9999" multiLineHeader="true">
				<acf:Column name="purchase_order_no" caption="Purchase Order No" width="100"></acf:Column>
				<acf:Column name="purchase_order_date" caption="Purchase Order Date" type="date" width="100"></acf:Column>
				<acf:Column name="receive_date" caption="Receive Date" type="date" width="100"></acf:Column>
				<acf:Column name="order_quantity" caption="Order Qty" width="100" ></acf:Column>
				<acf:Column name="received_quantity" caption="Receive Qty" width="100" ></acf:Column>
				<acf:Column name="back_order_quantity" caption="Back Order Qty" width="100"></acf:Column>
				<acf:Column name="out_standing_quantity" caption="O/S Quantity" width="100"></acf:Column>
			
			</acf:Grid>
	    </div>
	</acf:Region>
	
	<div class="col-xs-12">
	
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
	searchKey: "item_no",
	browseKey: "item_no",
	//searchForm: $("#frm_search"),
	//browseKey: "section_no",
	
	
	//initMode: "${mode}",
	recordForm: $("#frm_main"),
	recordKey: {
		item_no: "${item_no}"
	},
	getUrl: "apwf001-get-form.ajax",
	saveUrl: "apwf001-save.ajax",
	
	getSaveData: function() {
		

		return JSON.stringify({
			'form' : Controller.opt.recordForm.pForm$getModifiedRecord( Action.getMode() ),
			'inv' : $("#inventory_browse").pGrid$getModifiedRecord(), 
			'remain' : $("#remaining_browse").pGrid$getModifiedRecord(),

		});
	},
ready: function() { Action.setMode("search"); }	
}).executeSearchBrowserForm(); 
$(document).on("new", function() {
$('#remaining_browse').pGrid$clear();
$('#inventory_browse').pGrid$clear();
$("#frm_main #section_id").setValue("03");
if (($("#frm_main #reference_unit_cost").getValue() == '') || ($("#frm_main #reference_unit_cost").getValue() == null))
  {
  //console.log("-----------" + test + "---------------------");
  $("#frm_main #reference_unit_cost").setValue(0.00);
  }
}),

$(document).on("amend", function() {
console.log($("#inventory_browse").pGrid$getModifiedRecord());
}),


$(document).on("clone", function() {
$('#remaining_browse').pGrid$clear();
$('#inventory_browse').pGrid$clear();

if (($("#frm_main #reference_unit_cost").getValue() == '') || ($("#frm_main #reference_unit_cost").getValue() == null))
  {
  //console.log("-----------" + test + "---------------------");
  $("#frm_main #reference_unit_cost").setValue(0.00);
  }
}),
$(document).on("beforeSave", function() {
	//var test = $("#frm_main #reference_unit_cost").getValue()
	
  

	
	if (Action.getMode() == 'amend') {
		
		var mod = $('#frm_main').pForm$getModifiedRecord();
		delete mod[0].version;
		delete mod[1].version;
		if(mod[0].equals(mod[1])) {
			throw ACF.getDialogMessage("ACF027E");
			return false;
		}
	}

});

</script>
						