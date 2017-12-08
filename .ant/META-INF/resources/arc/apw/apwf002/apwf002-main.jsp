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
	      			<label for=s_func_id style="display:block">Search Material Desc.</label>
	      			<acf:TextBox id="s_other_material" name="other_material" maxlength="7"/> 
	      			
	      		
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
			<acf:Grid id="grid_browse" url="apwf002-search.ajax" readonly="true" autoLoad="false">
			<acf:Column name="other_material" caption="Other Material Desc." width="100" ></acf:Column>
			<acf:Column name="unit_cost" caption="Unit Cost" width="100" ></acf:Column>
			<acf:Column name="section_id" caption="Section ID" width="100" hidden="true"></acf:Column>
				
				
			</acf:Grid>
	    </div>
	</acf:Region>
	
		<acf:Region id="reg_func_main" type="form" title="SECTION MAINTENANCE">
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="other_material">Other Material Desc.:</label>
      		<div class="col-md-3">
      			<acf:TextBox id="other_material" name="other_material" maxlength="60" checkMandatory="true"/>
      				
      			   
        	</div>
     		
    	</div>  
    	<div class="col-xs-12 form-padding">
     		<label class="control-label col-md-2" for="unit_cost">Unit Cost:</label>
      		<div class="col-md-1">
      			<acf:TextBox id="unit_cost" name="unit_cost" maxlength="12" checkMandatory="true"/> 
      		</div>
     		
    	</div>  
 		<div class="hidden">
     		<label class="control-label col-md-2" for="section_id">Section ID:</label>
      		<div class="col-md-4">
      			<acf:TextBox id="section_id" name="section_id" maxlength="12"/> 
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
	
	
</div>

<script>
/*
$("#frm_search").pForm$setRelatedComboBox(${moduleGroups}, [$("#frm_search #s_sec_no"), $("#frm_search #s_func_gp_name")]);
$("#frm_main").pForm$setRelatedComboBox(${moduleGroups}, [$("#frm_main #mod_id"), $("#frm_main #func_gp_name")]);
*/



Controller.setOption({
	searchForm: $("#frm_search"),
	browseGrid: $("#grid_browse"),
	browseKey: "section_id,other_material,unit_cost",
	//redirect: "acff012-form",
	
	//initMode: "${mode}",
	recordForm: $("#frm_main"),
	recordKey: {
	
	},
	getUrl: "apwf002-get-form.ajax",
	saveUrl: "apwf002-save.ajax",
ready: function() { Action.setMode("search"); }		
}).executeSearchBrowserForm();

$(document).on('new', function() {
	$("#frm_main #section_id").setValue("03");//testing 2
});

</script>
<%
	helper.close();
%>