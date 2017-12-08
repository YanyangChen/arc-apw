package arc.apw.Controller; //testing upload
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;





















//import cal.amm.Static.AMMtMapping;
//import cal.exe.Model.EXEmFunction;
import acf.acf.Abstract.ACFaAppController;
import acf.acf.General.annotation.ACFgFunction;
import acf.acf.General.core.ACFgRequestParameters;
import acf.acf.General.core.ACFgResponseParameters;
import arc.apw.Service.APWsItem;
import arc.apw.Service.APWsModule;
import arc.apw.Service.APWsSupplierDes;
import arc.apf.Service.ARCsBusinessPlatform;
import arc.apf.Service.ARCsGroup;
import arc.apf.Service.ARCsItemInventory;
import arc.apf.Service.ARCsItemMaster;
import arc.apf.Service.ARCsLabourType;
import arc.apf.Service.ARCsLocation;
import arc.apf.Service.ARCsOtherMaterial;
import arc.apf.Service.ARCsProgrammeMaster;
import arc.apf.Static.APFtMapping;
import arc.apw.Static.APWtMapping;

@Controller
@Scope("session")
@ACFgFunction(id="")
@RequestMapping(value=APWtMapping.APW) 
public class APWc extends ACFaAppController {
    
    @Autowired APWsModule moduleService;
    @Autowired APWsSupplierDes supplierdescService;
    @Autowired APWsItem itemService;

    @Autowired ARCsLocation LocationService;
    @Autowired ARCsLabourType LabourTypeService;
    @Autowired ARCsGroup GroupService;
    @Autowired ARCsOtherMaterial OtherMaterialService;
    @Autowired ARCsItemInventory InventoryService;
    @Autowired ARCsItemMaster ItemMasterService;
    @Autowired ARCsProgrammeMaster ProgrammeMasterService;
    @Autowired ARCsBusinessPlatform businessPlatformService;
    
    public APWc() throws Exception { //testing RTC2
        super();
    }
    @RequestMapping(value=APWtMapping.APW_GET_SUPPLIER_DESC_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getProgramme(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("supplier_code", param.get("supplier_code", String.class));
       // getResponseParameters().put("programme",        progService.unfillItem(progService.getActTempProgramme(param.get("prog_no", String.class))));
       // getResponseParameters().put("producer_code",    prodMemberService.getProducerListValuePairs(param.get("prog_no", String.class)));
       // getResponseParameters().put("aa_staff",         prodMemberService.getStaffNameByProgNo((param.get("prog_no", String.class)), "AA"));
        getResponseParameters().put("supplier_name",         supplierdescService.getSupplierNameBySupplierNo((param.get("supplier_code", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters();
    } 
    
    @RequestMapping(value=APWtMapping.APW_GET_LOCATION_NAME_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getLocationName(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("location_code", param.get("location_code", String.class));
       // getResponseParameters().put("programme",        progService.unfillItem(progService.getActTempProgramme(param.get("prog_no", String.class))));
       // getResponseParameters().put("producer_code",    prodMemberService.getProducerListValuePairs(param.get("prog_no", String.class)));
       // getResponseParameters().put("aa_staff",         prodMemberService.getStaffNameByProgNo((param.get("prog_no", String.class)), "AA"));
        getResponseParameters().put("location_name",         LocationService.getLocationNameById((param.get("location_code", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters();
    }
    
    @RequestMapping(value=APWtMapping.APW_GET_GROUP_NAME_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getGroupName(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("group_no", param.get("group_no", String.class));
       // getResponseParameters().put("programme",        progService.unfillItem(progService.getActTempProgramme(param.get("prog_no", String.class))));
       // getResponseParameters().put("producer_code",    prodMemberService.getProducerListValuePairs(param.get("prog_no", String.class)));
       // getResponseParameters().put("aa_staff",         prodMemberService.getStaffNameByProgNo((param.get("prog_no", String.class)), "AA"));
        getResponseParameters().put("group_name",         GroupService.getGroupNameByNo((param.get("group_no", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters();
    }
      
    
    @RequestMapping(value=APWtMapping.APW_ITEM_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getItems(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("item_no", param.get("item_no", String.class));
        System.out.println(param.get("item_no", String.class));
       // getResponseParameters().put("programme",        progService.unfillItem(progService.getActTempProgramme(param.get("prog_no", String.class))));
       // getResponseParameters().put("producer_code",    prodMemberService.getProducerListValuePairs(param.get("prog_no", String.class)));
       // getResponseParameters().put("aa_staff",         prodMemberService.getStaffNameByProgNo((param.get("prog_no", String.class)), "AA"));
        getResponseParameters().put("item",         itemService.getItemUnits((param.get("item_no", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters();
    }
    
    @RequestMapping(value=APWtMapping.APW_ITEM_INV_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getIteminv(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("item_no", param.get("item_no", String.class));
         getResponseParameters().put("item",         InventoryService.getItemUnits((param.get("item_no", String.class))));
        return getResponseParameters();
    }
    
    @RequestMapping(value=APWtMapping.APW_OTHER_MATERIAL_UNITCOST_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getUnitcost(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("other_material", param.get("other_material", String.class));
         getResponseParameters().put("unitcost",         OtherMaterialService.getUnitcost((param.get("other_material", String.class))));
        return getResponseParameters();
    }
    
    @RequestMapping(value=APWtMapping.APW_ITEM_MAS_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getItemInvs(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("item_no", param.get("item_no", String.class));
          getResponseParameters().put("item",         ItemMasterService.getItemUnits((param.get("item_no", String.class))));
        return getResponseParameters();
    }
    
    @RequestMapping(value=APWtMapping.APW_LABOUR_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getLabour(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("labour_type", param.get("labour_type", String.class));
        System.out.println(param.get("item_no", String.class));
       // getResponseParameters().put("programme",        progService.unfillItem(progService.getActTempProgramme(param.get("prog_no", String.class))));
       // getResponseParameters().put("producer_code",    prodMemberService.getProducerListValuePairs(param.get("prog_no", String.class)));
       // getResponseParameters().put("aa_staff",         prodMemberService.getStaffNameByProgNo((param.get("prog_no", String.class)), "AA"));
        getResponseParameters().put("item",         LabourTypeService.getLabourUnits((param.get("labour_type", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters();
    }
    
    @RequestMapping(value=APWtMapping.APW_GET_PONO_IN_INV_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getPono(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("purchase_order_no", param.get("purchase_order_no", String.class));
       // System.out.println(param.get("purchase_order_no", String.class));
       // getResponseParameters().put("programme",        progService.unfillItem(progService.getActTempProgramme(param.get("prog_no", String.class))));
       // getResponseParameters().put("producer_code",    prodMemberService.getProducerListValuePairs(param.get("prog_no", String.class)));
       // getResponseParameters().put("aa_staff",         prodMemberService.getStaffNameByProgNo((param.get("prog_no", String.class)), "AA"));
        getResponseParameters().put("inv",         InventoryService.getInventory((param.get("purchase_order_no", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters();
    }
    
    @RequestMapping(value=APWtMapping.APW_PROGRAMME_FIELDS_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters programmeFields(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("programme_no", param.get("programme_no", String.class));
        System.out.println(param.get("item_no", String.class));
       // getResponseParameters().put("programme",        progService.unfillItem(progService.getActTempProgramme(param.get("prog_no", String.class))));
       // getResponseParameters().put("producer_code",    prodMemberService.getProducerListValuePairs(param.get("prog_no", String.class)));
       // getResponseParameters().put("aa_staff",         prodMemberService.getStaffNameByProgNo((param.get("prog_no", String.class)), "AA"));
        getResponseParameters().put("programme_name",         ProgrammeMasterService.getProgrammeName((param.get("programme_no", String.class))));
        getResponseParameters().put("business_platform",         ProgrammeMasterService.getProgrammePlatform((param.get("programme_no", String.class))));
        getResponseParameters().put("department",         ProgrammeMasterService.getProgrammeDepartment((param.get("programme_no", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters();
    }
    
    @RequestMapping(value=APWtMapping.APW_ITEM_FIELDS_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters ItemFields(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("item_no", param.get("item_no", String.class));
        System.out.println(param.get("item_no", String.class));
       // getResponseParameters().put("programme",        progService.unfillItem(progService.getActTempProgramme(param.get("prog_no", String.class))));
       // getResponseParameters().put("producer_code",    prodMemberService.getProducerListValuePairs(param.get("prog_no", String.class)));
       // getResponseParameters().put("aa_staff",         prodMemberService.getStaffNameByProgNo((param.get("prog_no", String.class)), "AA"));
        getResponseParameters().put("location_code",         ItemMasterService.getLocationcodeByItemNo((param.get("item_no", String.class))));
        getResponseParameters().put("item_description_1",         ItemMasterService.getItemDescByItemNo((param.get("item_no", String.class))));
        getResponseParameters().put("unit_cost",         ItemMasterService.getUniteCostByItemNo((param.get("item_no", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters();
    }
    
    @RequestMapping(value=APWtMapping.APW_BUSINESS_DESCRIPTION, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getBusinessDescription(@RequestBody ACFgRequestParameters param) throws Exception {
		setAuditKey("business_platform", param.get("business_platform", String.class));
		getResponseParameters().put("busi_description", businessPlatformService.getBusiPlatformDesc(param.get("business_platform", String.class), param.get("department", String.class)));
		return getResponseParameters();
    }
    
    
}
