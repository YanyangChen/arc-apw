package arc.apw.Controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


//The org.springframework.beans and org.springframework.context packages are the
//basis for Spring Framework’s IoC container.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

//Component and further stereotype annotations
import org.springframework.stereotype.Controller;

//The ModelMap class is essentially a glorified Map that can make adding objects that are to be displayed
//in (or on) a View adhere to a common naming convention.
import org.springframework.ui.ModelMap;

//web mvc framework
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import acf.acf.Abstract.ACFaAppController;
import acf.acf.Database.ACFdSQLAssDelete;
import acf.acf.Database.ACFdSQLAssInsert;
import acf.acf.Database.ACFdSQLAssSelect;
import acf.acf.Database.ACFdSQLAssUpdate;
import acf.acf.Database.ACFdSQLRule;
import acf.acf.Database.ACFdSQLRule.RuleCase;
import acf.acf.Database.ACFdSQLRule.RuleCondition;
import acf.acf.General.annotation.ACFgAuditKey;
import acf.acf.General.annotation.ACFgFunction;
import acf.acf.General.annotation.ACFgTransaction;
import acf.acf.General.core.ACFgRequestParameters;
import acf.acf.General.core.ACFgResponseParameters;
import acf.acf.General.core.ACFgSearch;
import acf.acf.Interface.ACFiCallback;
import acf.acf.Interface.ACFiSQLAssWriteInterface;
import acf.acf.Service.ACFsSecurity;
import acf.acf.Static.ACFtUtility;
import arc.apf.Dao.ARCoItemInventory;
import arc.apf.Dao.ARCoOtherMaterials;
import arc.apf.Dao.ARCoWPConsumptionHeader;
import arc.apf.Dao.ARCoWPConsumptionItem;
import arc.apf.Dao.ARCoWPLabourConsumption;
import arc.apf.Dao.ARCoWPOtherMaterialConsumption;
import arc.apf.Model.ARCmItemInventory;
import arc.apf.Model.ARCmPODetails;
import arc.apf.Model.ARCmWPConsumptionHeader;
import arc.apf.Model.ARCmWPConsumptionItem;
import arc.apf.Model.ARCmWPLabourConsumption;
import arc.apf.Model.ARCmWPOtherMaterialConsumption;
import arc.apf.Service.ARCsAccountAllocation;
import arc.apf.Service.ARCsBusinessPlatform;
import arc.apf.Service.ARCsGroup;
import arc.apf.Service.ARCsItemInventory;
import arc.apf.Service.ARCsItemMaster;
import arc.apf.Service.ARCsLabourType;
import arc.apf.Service.ARCsLocation;
import arc.apf.Service.ARCsModel;
import arc.apf.Service.ARCsOtherMaterial;
import arc.apf.Service.ARCsProgrammeMaster;
import arc.apw.Static.APWtMapping;
import arc.apw.Controller.APWc006;

@Controller
@Scope("session")
@ACFgFunction(id="APWF005")
@RequestMapping(value=APWtMapping.APWF005)
public class APWc005 extends ACFaAppController {
    
    @Autowired ARCsModel moduleService;
    @Autowired ARCsLabourType LabourTypeService;
    //@Autowired ACFoFunction functionDao;
    @Autowired ARCoOtherMaterials othermaterialsDao; //modify according to the table
    @Autowired ARCoWPConsumptionHeader WPconsumptionHeaderDao;
    @Autowired ARCoWPConsumptionItem WPconsumptionItemDao;
    @Autowired ARCoWPOtherMaterialConsumption OtherMaterialConsumptionDao;
    @Autowired ARCoWPLabourConsumption WPLabourConsumptionDao;
    @Autowired ACFsSecurity SecurityService;
    @Autowired ARCoItemInventory ItemInventoryDao;
    @Autowired ARCsItemInventory InventoryService;
    @Autowired ARCsOtherMaterial OtherMaterialService;
    @Autowired ARCsLocation LocationService;
    @Autowired ARCsProgrammeMaster ProgrammeMasterService;
    @Autowired ARCsItemMaster ItemMasterService;
    @Autowired ARCsGroup GroupService;
    @Autowired ARCsBusinessPlatform BusinessPlatformService;
    @Autowired ARCsAccountAllocation AccountAllocationService;
    //@Autowired APFsFuncGp funcGpService; //click the object and click import
    @ACFgAuditKey String consumption_form_no;
    @ACFgAuditKey String item_no;
    @ACFgAuditKey String purchase_order_no;
    Comparator<ARCmItemInventory> cii = new Comparator<ARCmItemInventory>(){
		
		@Override
		public int compare(ARCmItemInventory i,ARCmItemInventory j) {
			
			return i.purchase_order_no.compareTo(j.purchase_order_no);
		}};
   // @ACFgAuditKey String other_material;
    //@ACFgAuditKey BigDecimal unit_cost;
    
  //  Search search = new Search();

    private class Search extends ACFgSearch {
        public Search() {
            super();
            //setModel(ARCmWPConsumptionHeader.class); //define a Search which accept 4 filters from client
            setCustomSQL("select * from (select H.*, P.programme_name, P.chinese_programme_name, L.location_name " +
                    "from arc_wp_consumption_header H, arc_programme_master P, arc_location L " +
                    "where H.programme_no = P.programme_no "
                    + "and H.section_id = '03' "
                    + "and H.location_code = L.location_code)");
            setKey("consumption_form_no");////modify according to arc table's columns apw_ arc_po_header apw_supplier -> dev.arc_supplier
            addRule(new ACFdSQLRule("consumption_form_no", RuleCondition.EQ, null, RuleCase.Insensitive));
            addRule(new ACFdSQLRule("programme_no", RuleCondition.EQ, null, RuleCase.Insensitive));
        }// ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
        @Override
        public Search setValues(ACFgRequestParameters param) throws Exception { //use the search class to setup an object
            super.setValues(param);// param is a object, "Search" 's mother class passed
                if(!param.isEmptyOrNull("start_date")) {
                wheres.and("completion_date", ACFdSQLRule.RuleCondition.GE, param.get("start_date", Timestamp.class));
                }//// change date to column name
                if(!param.isEmptyOrNull("end_date")) {
                wheres.and("completion_date", ACFdSQLRule.RuleCondition.LT, new Timestamp(param.get("end_date", Long.class) + 24*60*60*1000));
                }
                //wheres.and("po_date", ACFdSQLRule.RuleCondition.LT, param.get("po_date_e", Timestamp.class));
            
            orders.put("completion_date", false);
            return this;
        }
    }
    Search search = new Search();

    
    @RequestMapping(value=APWtMapping.APWF005_MAIN, method=RequestMethod.GET)
    public String main(ModelMap model) throws Exception {
        model.addAttribute("consumption_form_no", consumption_form_no);
        //model.addAttribute("other_material", other_material); //set row keys
        //model.addAttribute("unit_cost", unit_cost);
        //initial value in function maintenance form
        model.addAttribute("itemnoselect", ItemMasterService.getItem_No_for_WP()); //acf's function, get data from ACFDB
        model.addAttribute("OtherMaterialselect", OtherMaterialService.getOtherMaterial());
        model.addAttribute("ACselect", AccountAllocationService.getActualAccountAllocation());
        model.addAttribute("locationcode", LocationService.getLocationCode());
        model.addAttribute("programmeno", ProgrammeMasterService.getProgNamePairs());
        model.addAttribute("groupno", GroupService.getGroupNamePairs());
        model.addAttribute("LabourTypeselect", LabourTypeService.getAllEffLabourType());
        model.addAttribute("businessDepartment", BusinessPlatformService.getAllBusinessDepartmentValue());
        model.addAttribute("BusinessPlatform", BusinessPlatformService.getBBusinessPlatform());
        model.addAttribute("Department", BusinessPlatformService.getBDepartment());
        //System.out.println(moduleService.getAllModuleValuePairs());
        //search value groups in search form and main form
        //model.addAttribute("moduleGroups", funcGpService.getModuleFuncGpIndex()); // no need to group tables just now

        return view();
        
    }
    @RequestMapping(value=APWtMapping.APWF005_SEARCH_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getGrid(@RequestBody ACFgRequestParameters param) throws Exception {
      //The method getGrid responds to AJAX by obtain the Search JSON result and put in variable “grid_browse”.
        // ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
        search.setConnection(getConnection("ARCDB")); //get connection to the database
        search.setValues(param);
        search.setFocus(consumption_form_no); //set two keys
        System.out.println(param);
       // System.out.println(search.getGridResult());
        return new ACFgResponseParameters().set("grid_browse", search.getGridResult()); // can only be called once, otherwise reset parameter
    }

    @RequestMapping(value=APWtMapping.APWF005_GET_FORM_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getForm(@RequestBody ACFgRequestParameters param) throws Exception {
        consumption_form_no = param.get("consumption_form_no", String.class); //pick the value of parameter “func_id” from client
        getConsumptionItem(param.getRequestParameter("item_browse")); 
        getMaterialConsumption(param.getRequestParameter("material_browse")); 
        getLabourConsumption(param.getRequestParameter("labour_browse")); 
        //other_material = param.get("other_material", String.class);  //set two keys!!
        //unit_cost = param.get("unit_cost", BigDecimal.class);
        //retrieves the result by DAO, and put in the variable “frm_main”. 
        //ACF will forward the content to client and post to the form which ID equals to “frm_main”
        return getResponseParameters().set("frm_main", WPconsumptionHeaderDao.selectItem(consumption_form_no)); //change dao here //set two keys!!
    }
    
    @RequestMapping(value=APWtMapping.APWF005_GET_CONSUMPTION_ITEM_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getConsumptionItem(@RequestBody ACFgRequestParameters param) throws Exception {                                
        ACFdSQLAssSelect select = new ACFdSQLAssSelect(); 
      //  select.setCustomSQL("");
      
        select.setCustomSQL("select * from(select I.*, P.purchase_order_date, P.purchase_order_no, I.consumption_quantity * I.unit_cost as amount, IM.item_description_1 "
                + "from arc_wp_consumption_item I, arc_po_header P, arc_item_master IM "
                + "where P.purchase_order_no = I.purchase_order_no "
                + "and I.item_no = IM.item_no)");
        select.setKey("consumption_form_no");
        select.wheres.and("consumption_form_no", consumption_form_no);
        //select.orders.put("seq", true);
        return getResponseParameters().set("item_browse", select.executeGridQuery(getConnection("ARCDB"), param));
        //only getResponseParameters() is right, not 'new ACFgResponseParameters()' otherwise parameters won't pass
      
      }
    
    @RequestMapping(value=APWtMapping.APWF005_GET_MATERIAL_CONSUMPTION_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getMaterialConsumption(@RequestBody ACFgRequestParameters param) throws Exception {                                
        ACFdSQLAssSelect select = new ACFdSQLAssSelect(); 
      //  select.setCustomSQL("");
      
        select.setCustomSQL("select m.*, m.unit_cost as other_material_amount from arc_wp_other_material_consumption m");
        select.setKey("consumption_form_no");
        select.wheres.and("consumption_form_no", consumption_form_no);
        //select.orders.put("seq", true);
        return getResponseParameters().set("material_browse", select.executeGridQuery(getConnection("ARCDB"), param));
      
      }
    
    @RequestMapping(value=APWtMapping.APWF005_GET_LABOUR_CONSUMPTION_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getLabourConsumption(@RequestBody ACFgRequestParameters param) throws Exception {                                
        ACFdSQLAssSelect select = new ACFdSQLAssSelect(); 
      //  select.setCustomSQL("");
      
        select.setCustomSQL("select * from(select l.*, lt.labour_type_description, lt.labour_type, l.no_of_hours * l.hourly_rate as amount "
                + "from arc_wp_labour_consumption l, arc_labour_type lt "
                + "where l.labour_type = lt.labour_type)");
        select.setKey("consumption_form_no");
        select.wheres.and("consumption_form_no", consumption_form_no);
        //select.orders.put("seq", true);
        return getResponseParameters().set("labour_browse", select.executeGridQuery(getConnection("ARCDB"), param));
      
      }

    @ACFgTransaction
    @RequestMapping(value=APWtMapping.APWF005_SAVE_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters save(@RequestBody ACFgRequestParameters param) throws Exception { //function in the upper right "save" button
      //the controller obtains the changes of form data 
        List<ARCmWPConsumptionHeader> amendments = param.getList("form", ARCmWPConsumptionHeader.class);
        final List<ARCmWPConsumptionItem> Itemamendments = param.getList("Item", ARCmWPConsumptionItem.class);
        final List<ARCmWPOtherMaterialConsumption> Materialamendments = param.getList("Material", ARCmWPOtherMaterialConsumption.class);
        final List<ARCmWPLabourConsumption> Labouramendments = param.getList("Labour", ARCmWPLabourConsumption.class);
        //and call DAO to save the changes
        ARCmWPConsumptionHeader lastItem = WPconsumptionHeaderDao.saveItems(amendments, new ACFiSQLAssWriteInterface<ARCmWPConsumptionHeader>(){
            
            
            //interface for the related functions
            @Override
            public boolean insert(ARCmWPConsumptionHeader newItem, ACFdSQLAssInsert ass) throws Exception {
                //ass.columns.put("allow_print", 1); //without the allow_print column, the whole sql won't work
                ass.setAfterExecute(new ACFiCallback() {
                    @Override
                    public void callback() throws Exception {
                        if (Itemamendments != null)
                        	 WPconsumptionItemDao.saveItems(Itemamendments,new ACFiSQLAssWriteInterface<ARCmWPConsumptionItem>()
                            		{

										@Override
										public boolean insert(ARCmWPConsumptionItem newItem,ACFdSQLAssInsert ass)throws Exception {
											// TODO Auto-generated method stub
											
											//consumption quantity must not be smaller than 0
											int cq = newItem.consumption_quantity.intValue();
 											if (cq < 0)
											{
												throw exceptionService.error("APW105E");
											}
 											
 											//consumption quantity not equals to 0
 											if (cq > 0 && newItem.re_used_indicator.equals("0"))
 											{
 											List<ARCmItemInventory> ls = ItemInventoryDao.selectItems(newItem.item_no);
 										
 											System.out.println("------testing item list---------------");
 											int rqs = 0;
 											for (ARCmItemInventory II : ls)
 											{
 												System.out.println(II);
 												System.out.println(II.purchase_order_no);
 												rqs += APWc006.get_remaining(II); //remaining 
 											}
 											System.out.println("-------------testing all received quantities---------------");
 											System.out.println(rqs);
 											
 											if (cq > rqs) //consumption quantity is greater than remaining quantity summation
 											{
 												System.out.println("received quantities is not enough!");
 												
// 												return false; //break the loop and won't do any process
 												throw exceptionService.error("APW005E");
 											}
 											
 											
 											List<ARCmItemInventory> Invitems = APWc006.filter(ls);
 											do // consumption from inventory
											{	
 												Invitems = APWc006.filter(Invitems);
												ARCmItemInventory mininv = Collections.min(Invitems,cii);
 												
 												if(cq <=APWc006.get_remaining(mininv))
 												{
 												mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + cq);
 						            			cq = 0;
 						            			ItemInventoryDao.updateItem(mininv);
 												}
 												
 												if(cq > APWc006.get_remaining(mininv))
 						            			{
 						            			//mininv.adjusted_quantity = new BigDecimal(0);
 						            			cq = cq - APWc006.get_remaining(mininv);
 						                			
 						                		mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + APWc006.get_remaining(mininv)); //set remaining to zero
 						                			
 						                			
 						            			//update inventory record in loop
 						            			ItemInventoryDao.updateItem(mininv);
 						            			}
											}
												//once consumption is finished, break the loop
												while (cq != 0);
 											
 											}
 											
// 											System.out.println(ls.get(1));
 											return false;
										}
										// the following should also happen when insert
										@Override
										public boolean update(ARCmWPConsumptionItem oldItem,ARCmWPConsumptionItem newItem,ACFdSQLAssUpdate ass)throws Exception {
											// TODO Auto-generated method stub
											int cq = newItem.consumption_quantity.intValue() - oldItem.consumption_quantity.intValue();
 											if (cq < 0)
											{
												throw exceptionService.error("APW105E");
											}
 											
 											//consumption quantity not equals to 0
 											if (cq > 0 && newItem.re_used_indicator.equals("0"))
 											{
 											List<ARCmItemInventory> ls = ItemInventoryDao.selectItems(newItem.item_no);
 											
 											System.out.println("------testing item list---------------");
 											int rqs = 0;
 											for (ARCmItemInventory II : ls)
 											{
 												System.out.println(II);
 												System.out.println(II.purchase_order_no);
 												rqs += APWc006.get_remaining(II); //remaining 
 											}
 											System.out.println("-------------testing all received quantities---------------");
 											System.out.println(rqs);
 											
 											if (cq > rqs) //consumption quantity is greater than remaining quantity summation
 											{
 												System.out.println("received quantities is not enough!");
 												
// 												return false; //break the loop and won't do any process
 												throw exceptionService.error("APW005E");
 											}
 											
 											
 											List<ARCmItemInventory> Invitems = APWc006.filter(ls);
 											do // consumption from inventory
											{	
 												Invitems = APWc006.filter(Invitems);
												ARCmItemInventory mininv = Collections.min(Invitems,cii);
 												
 												if(cq <=APWc006.get_remaining(mininv))
 												{
 												mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + cq);
 						            			cq = 0;
 						            			ItemInventoryDao.updateItem(mininv);
 												}
 												
 												if(cq > APWc006.get_remaining(mininv))
 						            			{
 						            			//mininv.adjusted_quantity = new BigDecimal(0);
 						            			cq = cq - APWc006.get_remaining(mininv);
 						                			
 						                		mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + APWc006.get_remaining(mininv)); //set remaining to zero
 						                			
 						                			
 						            			//update inventory record in loop
 						            			ItemInventoryDao.updateItem(mininv);
 						            			}
											}
												//once consumption is finished, break the loop
												while (cq != 0);
 											
 											}
 											
// 											System.out.println(ls.get(1));
 											return false;
										}

										@Override
										public boolean delete(ARCmWPConsumptionItem oldItem,ACFdSQLAssDelete ass)throws Exception {
											// TODO Auto-generated method stub
											
											return false;
										}
                            	
                            		});
                       if (Materialamendments != null)
                            OtherMaterialConsumptionDao.saveItems(Materialamendments);
                        if (Labouramendments != null)
                            WPLabourConsumptionDao.saveItems(Labouramendments);
                 
                    }
                });
                return false;
            }

            @Override
            public boolean update(final ARCmWPConsumptionHeader oldItem, final ARCmWPConsumptionHeader newItem, ACFdSQLAssUpdate ass) throws Exception {
                ass.setAfterExecute(new ACFiCallback() {
                    
//                    if(newItem.c)
//                    {
//                    	
//                    }
                    @Override
                    public void callback() throws Exception {
                    	System.out.println("--------------------testing indicator-----------------!" + newItem.cancel_indicator);
                    	//System.out.println(newItem.cancel_indicator);
                    	
                    	if (newItem.cancel_indicator.equals("y"))
                    	{
                    		System.out.println("--------------------loop entered-----------------!");
                    		ARCmWPConsumptionHeader newItem2 = newItem;
                    		newItem2.cancel_by = SecurityService.getCurrentUser().user_id;
                    		newItem2.cancel_date  = ACFtUtility.now();
                    		WPconsumptionHeaderDao.updateItem(newItem2);
//                    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    		Date date = sdf.parse("1900-01-01 00:00:00");
//                    		long time = date.getTime();
//                    		newItem2.cut_off_date = new Timestamp(time);
                    		
                    	}
                        if (Itemamendments != null)
                            WPconsumptionItemDao.saveItems(Itemamendments,new ACFiSQLAssWriteInterface<ARCmWPConsumptionItem>()
                            		{

										@Override
										public boolean insert(ARCmWPConsumptionItem newItem,ACFdSQLAssInsert ass)throws Exception {
											// TODO Auto-generated method stub
											
											//consumption quantity must not be smaller than 0
											int cq = newItem.consumption_quantity.intValue();
 											if (cq < 0 && newItem.re_used_indicator.equals("0"))
											{
												throw exceptionService.error("APW105E");
											}
 											
 											//consumption quantity not equals to 0 re_used_indicator == 1 situation has automatically been neglected.
 											if (cq > 0 && newItem.re_used_indicator.equals("0"))
 											{
 											List<ARCmItemInventory> ls = ItemInventoryDao.selectItems(newItem.item_no);
 											
 											System.out.println("------testing item list---------------");
 											int rqs = 0;
 											for (ARCmItemInventory II : ls)
 											{
 												System.out.println(II);
 												System.out.println(II.purchase_order_no);
 												rqs += APWc006.get_remaining(II); //remaining 
 											}
 											System.out.println("-------------testing all received quantities---------------");
 											System.out.println(rqs);
 											
 											if (cq > rqs) //consumption quantity is greater than remaining quantity summation
 											{
 												System.out.println("received quantities is not enough!");
 												
// 												return false; //break the loop and won't do any process
 												throw exceptionService.error("APW005E");
 											}
 											
 											
 											List<ARCmItemInventory> Invitems = APWc006.filter(ls);
 											do // consumption from inventory
											{	
 												Invitems = APWc006.filter(Invitems);
												ARCmItemInventory mininv = Collections.min(Invitems,cii);
 												
 												if(cq <=APWc006.get_remaining(mininv))
 												{
 												mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + cq);
 						            			cq = 0;
 						            			ItemInventoryDao.updateItem(mininv);
 												}
 												
 												if(cq > APWc006.get_remaining(mininv))
 						            			{
 						            			//mininv.adjusted_quantity = new BigDecimal(0);
 						            			cq = cq - APWc006.get_remaining(mininv);
 						                			
 						                		mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + APWc006.get_remaining(mininv)); //set remaining to zero
 						                			
 						                			
 						            			//update inventory record in loop
 						            			ItemInventoryDao.updateItem(mininv);
 						            			}
											}
												//once consumption is finished, break the loop
												while (cq != 0);
 											
 											}
 											
// 											System.out.println(ls.get(1));
 											return false;
										}
										// the following should also happen when insert
										@Override
										public boolean update(ARCmWPConsumptionItem oldItem,ARCmWPConsumptionItem newItem,ACFdSQLAssUpdate ass)throws Exception {
											// TODO Auto-generated method stub
											int cq = newItem.consumption_quantity.intValue() - oldItem.consumption_quantity.intValue();
 											//situation  1
											if (oldItem.re_used_indicator.equals("0") && newItem.re_used_indicator.equals("1"))// if the user mistaken reused to not reused and want to modify back
											{
												//throw exceptionService.error("APW105E");
 												List<ARCmItemInventory> ls = ItemInventoryDao.selectItems(newItem.item_no);
 												List<ARCmItemInventory> Invitems = APWc006.filter(ls);
												ARCmItemInventory mininv = Collections.min(Invitems,cii);
												mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() - oldItem.consumption_quantity.intValue());//add back the previously consumed items
												//nothing to do with new item
												
												ItemInventoryDao.updateItem(mininv);
											}
											
											
											
											
 											
 											
 											
 											
 											
 											//situation 2
 											//if the new reused indicator indicates consume without reuse, take the new input consumption_quantity as consumption quantity for calculation
 											if ((newItem.re_used_indicator.equals("0"))&& (oldItem.re_used_indicator.equals("1")))
 													{
 														int cq2 = newItem.consumption_quantity.intValue();
 														
 				 											List<ARCmItemInventory> ls = ItemInventoryDao.selectItems(newItem.item_no);
 				 											
 				 											System.out.println("------testing item list---------------");
 				 											int rqs = 0;
 				 											for (ARCmItemInventory II : ls)
 				 											{
 				 												System.out.println(II);
 				 												System.out.println(II.purchase_order_no);
 				 												rqs += APWc006.get_remaining(II); //remaining 
 				 											}
 				 											System.out.println("-------------testing all received quantities---------------");
 				 											System.out.println(rqs);
 				 											
 				 											if (cq2 > rqs) //consumption quantity is greater than remaining quantity summation
 				 											{
 				 												System.out.println("received quantities is not enough!");
 				 												
// 				 												return false; //break the loop and won't do any process
 				 												throw exceptionService.error("APW005E");
 				 											}
 				 											
 				 											
 				 											List<ARCmItemInventory> Invitems = APWc006.filter(ls);
 				 											do // consumption from inventory
 															{	
 				 												Invitems = APWc006.filter(Invitems);
 																ARCmItemInventory mininv = Collections.min(Invitems,cii);
 				 												
 				 												if(cq2 <=APWc006.get_remaining(mininv))
 				 												{
 				 												mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + cq2);
 				 						            			cq2 = 0;
 				 						            			ItemInventoryDao.updateItem(mininv);
 				 												}
 				 												
 				 												if(cq2 > APWc006.get_remaining(mininv))
 				 						            			{
 				 						            			//mininv.adjusted_quantity = new BigDecimal(0);
 				 						            			cq2 = cq2 - APWc006.get_remaining(mininv);
 				 						                			
 				 						                		mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + APWc006.get_remaining(mininv)); //set remaining to zero
 				 						                			
 				 						                			
 				 						            			//update inventory record in loop
 				 						            			ItemInventoryDao.updateItem(mininv);
 				 						            			}
 															}
 																//once consumption is finished, break the loop
 																while (cq2 != 0);
 				 											
 				 											
 													}
 											
 											//situation 3
 											if (cq < 0 && newItem.re_used_indicator.equals("0") && (oldItem.re_used_indicator.equals("0")))
											{
												//throw exceptionService.error("APW105E");
 												List<ARCmItemInventory> ls = ItemInventoryDao.selectItems(newItem.item_no);
 												List<ARCmItemInventory> Invitems = APWc006.filter(ls);
												ARCmItemInventory mininv = Collections.min(Invitems,cii);
												mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + cq);//add back the error consumed difference
												ItemInventoryDao.updateItem(mininv);
											}
 											
 											//consumption quantity not equals to 0
 											if (cq > 0 && (newItem.re_used_indicator.equals("0")) && (oldItem.re_used_indicator.equals("0")))
 											{
 											List<ARCmItemInventory> ls = ItemInventoryDao.selectItems(newItem.item_no);
 											
 											System.out.println("------testing item list---------------");
 											int rqs = 0;
 											for (ARCmItemInventory II : ls)
 											{
 												System.out.println(II);
 												System.out.println(II.purchase_order_no);
 												rqs += APWc006.get_remaining(II); //remaining 
 											}
 											System.out.println("-------------testing all received quantities---------------");
 											System.out.println(rqs);
 											
 											if (cq > rqs) //consumption quantity is greater than remaining quantity summation
 											{
 												System.out.println("received quantities is not enough!");
 												
// 												return false; //break the loop and won't do any process
 												throw exceptionService.error("APW005E");
 											}
 											
 											
 											List<ARCmItemInventory> Invitems = APWc006.filter(ls);
 											do // consumption from inventory
											{	
 												Invitems = APWc006.filter(Invitems);
												ARCmItemInventory mininv = Collections.min(Invitems,cii);
 												
 												if(cq <=APWc006.get_remaining(mininv))
 												{
 												mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + cq);
 						            			cq = 0;
 						            			ItemInventoryDao.updateItem(mininv);
 												}
 												
 												if(cq > APWc006.get_remaining(mininv))
 						            			{
 						            			//mininv.adjusted_quantity = new BigDecimal(0);
 						            			cq = cq - APWc006.get_remaining(mininv);
 						                			
 						                		mininv.consumed_quantity = new BigDecimal(mininv.consumed_quantity.intValue() + APWc006.get_remaining(mininv)); //set remaining to zero
 						                			
 						                			
 						            			//update inventory record in loop
 						            			ItemInventoryDao.updateItem(mininv);
 						            			}
											}
												//once consumption is finished, break the loop
												while (cq != 0);
 											
 											}
 											
 											//situation  4
											if (oldItem.re_used_indicator.equals("1") && newItem.re_used_indicator.equals("1"))
											{
												
											}
// 											System.out.println(ls.get(1));
 											return false;
										}

										@Override
										public boolean delete(ARCmWPConsumptionItem oldItem,ACFdSQLAssDelete ass)throws Exception {
											// TODO Auto-generated method stub
											
											return false;
										}
                            	
                            		});
                        if (Materialamendments != null)
                            OtherMaterialConsumptionDao.saveItems(Materialamendments);
                        if (Labouramendments != null)
                            WPLabourConsumptionDao.saveItems(Labouramendments);
                 
                    }
                });
                return false;
            }

            @Override
            public boolean delete(ARCmWPConsumptionHeader oldItem, ACFdSQLAssDelete ass) throws Exception {
                return false;
            }
        });
        consumption_form_no = lastItem!=null? lastItem.consumption_form_no: null;
        ACFgResponseParameters r = getResponseParameters();
        r.set("consumption_form_no", consumption_form_no);
        r.set("action", amendments.get(0).getAction());
        return r;
    }

}