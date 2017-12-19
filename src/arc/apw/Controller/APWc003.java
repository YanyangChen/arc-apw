package arc.apw.Controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



















//import cal.apm.Controller.APMc101.SearchProdMember;
//import cal.exe.Model.EXEmFunction;
import acf.acf.Abstract.ACFaAppController;
//import acf.acf.Controller.ACFc182.Search;
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
import acf.acf.General.core.ACFgRawModel;
import acf.acf.General.core.ACFgRequestParameters;
import acf.acf.General.core.ACFgResponseParameters;
import acf.acf.General.core.ACFgSearch;
import acf.acf.Interface.ACFiCallback;
import acf.acf.Interface.ACFiSQLAssWriteInterface;
import acf.acf.Service.ACFsFuncGp;
import acf.acf.Service.ACFsSecurity;
import acf.acf.Static.ACFtUtility;
import arc.apf.Service.ARCsItemMaster;
import arc.apf.Service.ARCsPoHeader;
import arc.apf.Service.ARCsSupplier;
import arc.apf.Static.APFtUtilityAndGlobal;
import arc.apf.Dao.ARCoAutoGenNo;
import arc.apf.Model.ARCmAutoGenNo;
import arc.apf.Model.ARCmItemInventory;
import arc.apf.Model.ARCmPOHeader;
import arc.apf.Model.ARCmPODetails;
import arc.apf.Dao.ARCoItemInventory;
import arc.apf.Dao.ARCoPOHeader;
import arc.apf.Dao.ARCoPODetails;
import arc.apf.Dao.ARCoItemMaster;
import arc.apf.Service.ARCsModel;
import arc.apw.Static.APWtMapping;

@Controller
@Scope("session")
@ACFgFunction(id="APWF003")
@RequestMapping(value=APWtMapping.APWF003)

public class APWc003 extends ACFaAppController {

  
   // @Autowired APWsModule moduleService;
    @Autowired ARCsModel moduleService;
    @Autowired ARCsPoHeader PoHeaderService;
    @Autowired ARCsSupplier SupplierService;
    @Autowired ARCsItemMaster ItemMasterService;
    
    @Autowired ACFsSecurity SecurityService;
    
    @Autowired ARCoAutoGenNo AutoGenDao;
    
    @Autowired ARCoPOHeader POHeaderDao;
    @Autowired ARCoPODetails PODetailsDao;
    @Autowired ARCoItemMaster ItemMasterDao;
    @Autowired ARCoItemInventory ItemInventoryDao;
    

    @ACFgAuditKey String purchase_order_no;
    @ACFgAuditKey String supplier_code;
    @Autowired ACFsFuncGp funcGpService;
    
    
    java.text.DecimalFormat df = new java.text.DecimalFormat("#####0");
    private class Search extends ACFgSearch { //define the class
        public Search() {
            super();
            //SQL to combine two tables to form one grid in browser function
            setCustomSQL("select * from (select po.supplier_code, po.printed_at, sp.supplier_name, po.purchase_order_no, po.department_reference_no, po.purchase_order_date " +
                    "from arc_po_header po, arc_supplier sp " +
                    "where po.supplier_code = sp.supplier_code and section_id = '03')");
            setKey("purchase_order_no");////modify according to arc table's columns apw_ arc_po_header apw_supplier -> dev.arc_supplier
            
            //Search type and methods
            addRule(new ACFdSQLRule("purchase_order_no", RuleCondition._LIKE_, null, RuleCase.Insensitive));
            //addRule(new ACFdSQLRule("po_date", RuleCondition.EQ, null, RuleCase.Insensitive));
            addRule(new ACFdSQLRule("supplier_code", RuleCondition.EQ, null, RuleCase.Insensitive));
            addRule(new ACFdSQLRule("supplier_name", RuleCondition._LIKE_, null, RuleCase.Insensitive));
            //addRule(new ACFdSQLRule("location_name", RuleCondition.EQ, null, RuleCase.Insensitive));
        }
        
        @Override
        public Search setValues(ACFgRequestParameters param) throws Exception { //use the search class to setup an object
            super.setValues(param);// param is a object, "Search" 's mother class passed
                if(!param.isEmptyOrNull("po_start_date")) {
                wheres.and("purchase_order_date", ACFdSQLRule.RuleCondition.GE, param.get("po_start_date", Timestamp.class));
                }//// change date to column name
                if(!param.isEmptyOrNull("po_end_date")) {
                wheres.and("purchase_order_date", ACFdSQLRule.RuleCondition.LT, new Timestamp(param.get("po_end_date", Long.class) + 24*60*60*1000));
                }
                //wheres.and("po_date", ACFdSQLRule.RuleCondition.LT, param.get("po_date_e", Timestamp.class));
            
            orders.put("purchase_order_date", false);
            return this;
        }
    }
    Search search = new Search();

    

    
    @RequestMapping(value=APWtMapping.APWF003_MAIN, method=RequestMethod.GET)
    public String main(ModelMap model) throws Exception {
        model.addAttribute("modules", PoHeaderService.getSupplierCode()); //SQL code implemented in getSupplierCode() function here
        model.addAttribute("modulesf", SupplierService.getSupplierCodefromitem()); //these are codes to get combobox contents
        model.addAttribute("moduleselect", ItemMasterService.getItem_No_for_WP());
        return view(); // a srting type defined by base class  
    }
    
    @RequestMapping(value=APWtMapping.APWF003_GET_SUPPLIER_DESC_AJAX, method=RequestMethod.POST)
    @ResponseBody 
    public ACFgResponseParameters getProgrammee(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("supplier_code", param.get("supplier_code", String.class));
        
       // code to pass values to client side as the name "sup_desc"
        getResponseParameters().put("sup_desc",         moduleService.getSupplierNameBySupplierNo((param.get("supplier_code", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters(); // a ACFgResponseParameters type defined by base class
    } 
    
    
    ////form maintenance here, how to modify? imoprt apff001.java and remove grid in jsp file
    @RequestMapping(value=APWtMapping.APWF003_SEARCH_AJAX, method=RequestMethod.POST)
    @ResponseBody
     public ACFgResponseParameters getGrid(@RequestBody ACFgRequestParameters param) throws Exception {
          //The method getGrid responds to AJAX by obtain the Search JSON result and put in variable “grid_browse”.
            // ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
            search.setConnection(getConnection("ARCDB")); //get connection to the database
            search.setValues(param);
            search.setFocus(purchase_order_no);
           
            return new ACFgResponseParameters().set("grid_browse", search.getGridResult()); // can only be called once, otherwise reset parameter
    }
    
    @RequestMapping(value=APWtMapping.APWF003_GET_ITEM_TABLE_AJAX, method=RequestMethod.POST) //get rows of the second grid
    @ResponseBody
    public ACFgResponseParameters getItemTable(@RequestBody ACFgRequestParameters param) throws Exception {                                
      ACFdSQLAssSelect select = new ACFdSQLAssSelect();
      select.setCustomSQL("select * from (select PO.purchase_order_no, PI.item_no, I.item_description_1, PI.order_quantity, I.un_it, PI.unit_cost, PI.modified_at, PI.order_quantity * PI.unit_cost as tl_cost "
              + "from arc_po_header po, arc_po_details PI, arc_item_master I "
              + "where po.purchase_order_no = PI.purchase_order_no "
              + "and PI.item_no = I.item_no)");
      select.setKey("purchase_order_no","item_no");
      select.wheres.and("purchase_order_no", purchase_order_no);
      //select.orders.put("seq", true);
     
      return getResponseParameters().set("grid_item", select.executeGridQuery(getConnection("ARCDB"), param));
    
    }
    
    
     @RequestMapping(value=APWtMapping.APWF003_GET_FORM_AJAX, method=RequestMethod.POST) //get rows of the first grid
        @ResponseBody
        public ACFgResponseParameters getForm(@RequestBody ACFgRequestParameters param) throws Exception {
         purchase_order_no = param.get("purchase_order_no", String.class); //pick the value of parameter “func_id” from client
         getItemTable(param.getRequestParameter("grid_item"));      ////update Dao
            return getResponseParameters().set("frm_main", POHeaderDao.selectItem(purchase_order_no)); 
        }
     @ACFgTransaction
        @RequestMapping(value=APWtMapping.APWF003_SAVE_AJAX, method=RequestMethod.POST)
        @ResponseBody   ////change model name and Dao name
        public ACFgResponseParameters save(@RequestBody ACFgRequestParameters param) throws Exception { //function in the upper right "save" button
          //the controller obtains the changes of form data 
           final List<ARCmPOHeader> amendments = param.getList("form", ARCmPOHeader.class);
          // final List<ARCmItemInventory> Inventory1 = param.getList("form_n", ARCmItemInventory.class);
           final List<ARCmPODetails> PoItemamendments = param.getList("PoItem", ARCmPODetails.class);
           final String PoItemamendmentsx = param.get("item_no_string", String.class);
           System.out.println("******************testing with PoItemamendments**********************" + PoItemamendments);
           final ArrayList aList= new ArrayList(Arrays.asList(PoItemamendmentsx.split(",")));
           System.out.println("******************testing with String**********************" + aList.size());
         ARCmPOHeader lastItem = POHeaderDao.saveItems(amendments, new ACFiSQLAssWriteInterface<ARCmPOHeader>(){

                @Override
                public boolean insert(final ARCmPOHeader newItem, ACFdSQLAssInsert ass) throws Exception {
                    
                	ass.setAfterExecute(new ACFiCallback() {
                        // @SuppressWarnings("null")
						@Override
                         public void callback() throws Exception {
							 final List<ARCmPODetails> PoItemamendments2 = PoItemamendments;
							 final ARCmItemInventory s =new ARCmItemInventory();
                             final ARCmPOHeader poheadernew = newItem;
							 if (PoItemamendments2 != null)
                            	 for (ARCmPODetails pod : PoItemamendments2)
                            	 {	pod.purchase_order_no = newItem.purchase_order_no;
                            		 pod.setAction(1);}
                                 PODetailsDao.saveItems(PoItemamendments2,  new ACFiSQLAssWriteInterface<ARCmPODetails>() //function with inner class, object created once for processing
                                		 
                                		 {

											@Override
											public boolean insert(final ARCmPODetails newItem,ACFdSQLAssInsert ass)
													throws Exception {
												ass.setAfterExecute(new ACFiCallback() {
													@Override
							                         public void callback() throws Exception {
														 List<ARCmItemInventory> ls =new ArrayList<ARCmItemInventory>() ;
								                           
								                        	   ARCmItemInventory is =new ARCmItemInventory();
								                         	is.item_no = newItem.item_no;
								                         	is.purchase_order_no = newItem.purchase_order_no;
								                         	is.purchase_order_date = new Timestamp(System.currentTimeMillis());
								                         	is.order_quantity = newItem.order_quantity;
								                         	is.received_quantity = new BigDecimal(0);
								                         	is.current_received_quantity = new BigDecimal(0);
								                         	is.receive_date = new Timestamp(System.currentTimeMillis());
								                         	is.consumed_quantity = new BigDecimal(0);
								                         	is.adjusted_quantity = new BigDecimal(0);
								                         	is.back_order_quantity = new BigDecimal(0);
								                         	is.current_back_order_quantity = new BigDecimal(0);
								                         	is.unit_cost = newItem.unit_cost;
								                         	is.setAction(1);
								                         	ls.add(is);
								                         	ItemInventoryDao.insertOrUpdateItem(is);
								                         	//ItemInventoryDao.insertOrUpdateItem(s); //disable save action first
								                         	
								                           
								                           
								                           
													}
													});
												return false;
											}

											@Override
											public boolean update(final ARCmPODetails oldItem,final ARCmPODetails newItem,ACFdSQLAssUpdate ass)
													throws Exception {
												ass.setAfterExecute(new ACFiCallback() {
													@Override
							                         public void callback() throws Exception {
														 List<ARCmItemInventory> ls =new ArrayList<ARCmItemInventory>() ;
														 List<ARCmItemInventory> lso =new ArrayList<ARCmItemInventory>() ;
								                          
								                        	   ARCmItemInventory  uso = ItemInventoryDao.selectItem(oldItem.item_no, amendments.get(0).purchase_order_no);
								                        	   uso.setVersion(1);
								                        	   uso.setAction(2);
								                        	  // lso.add(uso);
								                        	   ARCmItemInventory us =new ARCmItemInventory();
								                         	us.item_no = newItem.item_no;
								                         	us.purchase_order_no = newItem.purchase_order_no;
								                         	us.purchase_order_date = new Timestamp(System.currentTimeMillis());
								                         	us.order_quantity = newItem.order_quantity;
								                         	us.received_quantity = new BigDecimal(0);
								                         	us.current_received_quantity = new BigDecimal(0);
								                         	us.receive_date = new Timestamp(System.currentTimeMillis());
								                         	us.consumed_quantity = new BigDecimal(0);
								                         	us.adjusted_quantity = new BigDecimal(0);
								                         	us.back_order_quantity = new BigDecimal(0);
								                         	us.current_back_order_quantity = new BigDecimal(0);
								                         	us.unit_cost = newItem.unit_cost;
								                         	us.setVersion(2);
								                         	us.setAction(2);
								                         	//ls.add(us);
								                         	 ItemInventoryDao.updateItem(uso,us);
								                         	//ItemInventoryDao.insertOrUpdateItem(s); //disable save action first
								                         	
								                          
													}
													});
												
												return false;
											}

											@Override
											public boolean delete(final ARCmPODetails oldItem,ACFdSQLAssDelete ass)throws Exception {
												ass.setAfterExecute(new ACFiCallback() {
													@Override
							                         public void callback() throws Exception {
														//List<ARCmItemInventory> ls =new ArrayList<ARCmItemInventory>() ;
								                        
														if (ItemInventoryDao.selectItem(oldItem.item_no, amendments.get(0).purchase_order_no).received_quantity.intValue() > 0)
														{
															
															throw exceptionService.error("APW003E");
														}
														
														ItemInventoryDao.deleteItem(ItemInventoryDao.selectItem(oldItem.item_no, newItem.purchase_order_no));
												}
													});
												return false;
											}
                                	 
                                	 
                                		 });
                        
                             
                           
                             {
                             }
						}
                           
                         
                     });
                     
                    return false;
                }
               

                @Override
                public boolean update(ARCmPOHeader oldItem, ARCmPOHeader newItem, ACFdSQLAssUpdate ass) throws Exception {
                	// validate(newItem);
                	System.out.println("testing****************************************amendments.get(0).purchase_order_no" + amendments.get(0).purchase_order_no);
               	 ACFdSQLAssSelect select = new ACFdSQLAssSelect();
                    select.setCustomSQL("select * from arc_item_inventory where purchase_order_no = '%s'", amendments.get(0).purchase_order_no);
                    select.setKey("purchase_order_no");
//                    select.wheres.and("purchase_order_no");
                    List<ACFgRawModel> inv_result = select.executeQuery(getConnection("ARCDB"));
                    //if iterate all results and the received items are all 0
                    int inventory_received = 0;
                    for (ACFgRawModel each : inv_result){
                   	 if(each.getBigDecimal("received_quantity").intValue() !=0){
                   		 inventory_received += each.getBigDecimal("received_quantity").intValue();
                   	 }
                    }
                    System.out.println("inventory_received are ********************" + inventory_received);
               	if (oldItem.cancel_indicator.equals("N") && newItem.cancel_indicator.equals("Y") && inventory_received != 0)
	               	 {
	               		 ass.columns.put("cancel_by", SecurityService.getCurrentUser().user_id);
	               		 ass.columns.put("cancel_date",  ACFtUtility.now());
	               		System.out.println("test**************canceled_by and canceled_date scripts entered");
	               		
	               	 }
                     ass.setAfterExecute(new ACFiCallback() {
                        // @SuppressWarnings("null")
						@Override
                         public void callback() throws Exception {
							 final List<ARCmPODetails> PoItemamendments2 = PoItemamendments;
							 final ARCmItemInventory s =new ARCmItemInventory();
                             if (PoItemamendments != null)
                            	 
                                 PODetailsDao.saveItems(PoItemamendments,  new ACFiSQLAssWriteInterface<ARCmPODetails>() //function with inner class, object created once for processing
                                		 
                                		 {

											@Override
											public boolean insert(final ARCmPODetails newItem,ACFdSQLAssInsert ass)
													throws Exception {
												ass.setAfterExecute(new ACFiCallback() {
													@Override
							                         public void callback() throws Exception {
														 List<ARCmItemInventory> ls =new ArrayList<ARCmItemInventory>() ;
								                           
								                        	   ARCmItemInventory is =new ARCmItemInventory();
								                         	is.item_no = newItem.item_no;
								                         	is.purchase_order_no = amendments.get(0).purchase_order_no;
								                         	is.purchase_order_date = new Timestamp(System.currentTimeMillis());
								                         	is.order_quantity = newItem.order_quantity;
								                         	is.received_quantity = new BigDecimal(0);
								                         	is.current_received_quantity = new BigDecimal(0);
								                         	is.receive_date = new Timestamp(System.currentTimeMillis());
								                         	is.consumed_quantity = new BigDecimal(0);
								                         	is.adjusted_quantity = new BigDecimal(0);
								                         	is.back_order_quantity = new BigDecimal(0);
								                         	is.current_back_order_quantity = new BigDecimal(0);
								                         	is.unit_cost = newItem.unit_cost;
								                         	is.setAction(1);
								                         	ls.add(is);
								                         	ItemInventoryDao.insertOrUpdateItem(is);
								                         	//ItemInventoryDao.insertOrUpdateItem(s); //disable save action first
								                         	
								                           
								                           
								                           
													}
													});
												return false;
											}

											@Override
											public boolean update(final ARCmPODetails oldItem,final ARCmPODetails newItem,ACFdSQLAssUpdate ass)
													throws Exception {
												ass.setAfterExecute(new ACFiCallback() {
													@Override
							                         public void callback() throws Exception {
														 List<ARCmItemInventory> ls =new ArrayList<ARCmItemInventory>() ;
														 List<ARCmItemInventory> lso =new ArrayList<ARCmItemInventory>() ;
								                          
								                        	   ARCmItemInventory  uso = ItemInventoryDao.selectItem(oldItem.item_no, amendments.get(0).purchase_order_no);
								                        	   uso.setVersion(1);
								                        	   uso.setAction(2);
								                        	  // lso.add(uso);
								                        	   ARCmItemInventory us =new ARCmItemInventory();
								                         	us.item_no = newItem.item_no;
								                         	us.purchase_order_no = newItem.purchase_order_no;
								                         	us.purchase_order_date = new Timestamp(System.currentTimeMillis());
								                         	us.order_quantity = newItem.order_quantity;
								                         	us.received_quantity = uso.received_quantity;
								                         	us.current_received_quantity = uso.current_received_quantity;
								                         	us.receive_date = uso.receive_date;
								                         	us.consumed_quantity = uso.consumed_quantity;
								                         	us.adjusted_quantity = uso.adjusted_quantity;
								                         	us.back_order_quantity = uso.back_order_quantity;
								                         	us.current_back_order_quantity = uso.current_back_order_quantity;
								                         	us.unit_cost = newItem.unit_cost;
								                         	us.setVersion(2);
								                         	us.setAction(2);
								                         	//ls.add(us);
								                         	 ItemInventoryDao.updateItem(uso,us);
								                         	//ItemInventoryDao.insertOrUpdateItem(s); //disable save action first
								                         	
								                          
													}
													});
												
												return false;
											}

											@Override
											public boolean delete(final ARCmPODetails oldItem,ACFdSQLAssDelete ass)throws Exception {
												
												ass.setAfterExecute(new ACFiCallback() {
													@Override
							                         public void callback() throws Exception {
														//List<ARCmItemInventory> ls =new ArrayList<ARCmItemInventory>() ;
														
														if ((ItemInventoryDao.selectItem(oldItem.item_no, amendments.get(0).purchase_order_no).received_quantity.intValue() > 0)
														|| (ItemInventoryDao.selectItem(oldItem.item_no, amendments.get(0).purchase_order_no).consumed_quantity.intValue() > 0)
														|| (ItemInventoryDao.selectItem(oldItem.item_no, amendments.get(0).purchase_order_no).adjusted_quantity.intValue() != 0))
														{
															
															throw exceptionService.error("APW003E");
														}
														
								                        ItemInventoryDao.deleteItem(ItemInventoryDao.selectItem(oldItem.item_no, amendments.get(0).purchase_order_no));
							                           
													}
													});
												return false;
											}
                                	 
                                	 
                                		 });

                           
                             {
               	 

//                         	
                             }
						}
                           
                         
                     });

                     
                     return false;
                }

                @Override
                public boolean delete(ARCmPOHeader oldItem, ACFdSQLAssDelete ass) throws Exception {
                	
                	boolean delete_indicator = true;
                	System.out.println("******************testing outside**********************");
                	System.out.println("******************testing outside**********************"+ PoItemamendments); 
                	System.out.println("******************testing outside size**********************"+ PoItemamendments.size()); 
                	System.out.println("******************testing outside**********************"+ PoItemamendmentsx); 
                	System.out.println("******************testing outside aList size**********************"+ aList.size()); 
                	for(int i = 0; i < aList.size(); i++){
                		System.out.println("******************testing list  **********************" + aList);
                		if(ItemInventoryDao.selectItem(aList.get(i), oldItem.purchase_order_no) != null)
                		{
                		ARCmItemInventory II = ItemInventoryDao.selectItem(aList.get(i), oldItem.purchase_order_no);
               		if ((II.consumed_quantity.intValue() > 0) || (II.received_quantity.intValue() > 0) || (II.adjusted_quantity.intValue() != 0))
               		{
               			System.out.println("******************delete set to false**********************");
               			delete_indicator = false;
               			break;
                	}
               		System.out.println("******************delete not set to false**********************");
               	
               		
                }
                   
                	}
                	if (delete_indicator == false)
//						|| (ItemInventoryDao.selectItem(oldItem.item_no, amendments.get(0).purchase_order_no).consumed_quantity.intValue() > 0)
//						|| (ItemInventoryDao.selectItem(oldItem.item_no, amendments.get(0).purchase_order_no).adjusted_quantity.intValue() != 0))
						{
							
							throw exceptionService.error("APW003E");
						}
                    
                // if delete_indicator == true, delete all the grid records in item inventory first then delete the PO header record
                	if (delete_indicator == true){
                    for(int i = 0; i < aList.size(); i++){
                    ItemInventoryDao.deleteItem(ItemInventoryDao.selectItem(aList.get(i), oldItem.purchase_order_no));}
                	}
                    return false;
                }
            });
            

            //return new ACFgResponseParameters();
            purchase_order_no = lastItem!=null? lastItem.purchase_order_no: null;
            ACFgResponseParameters r = getResponseParameters();
            r.set("new_purchase_order_no", purchase_order_no);
            r.set("action", amendments.get(0).getAction());
            return r;
            
        }
     
     
@ACFgTransaction
@RequestMapping(value=APWtMapping.APWF003_GENERATE_NAME_AJAX, method=RequestMethod.POST)
@ResponseBody
public ACFgResponseParameters generate_name(@RequestBody ACFgRequestParameters param) throws Exception { 
    ACFgResponseParameters resParam = new ACFgResponseParameters();
    ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
    
    ass.setCustomSQL("select * from arc_auto_gen_no "
            + "where form_id = 'WP'");
    
    List<ACFgRawModel> name = ass.executeQuery(getConnection("ARCDB"));
    BigDecimal six_digit_serial_no_addone = name.get(0).getBigDecimal("six_digit_serial_no");
    String form_id = name.get(0).getString("form_id");
    BigDecimal system_year = name.get(0).getBigDecimal("system_year");
    BigDecimal system_month = name.get(0).getBigDecimal("system_month");
    six_digit_serial_no_addone = six_digit_serial_no_addone.add(new BigDecimal(1)); //po number add one
    ARCmAutoGenNo updatelist = AutoGenDao.selectItem(form_id, system_year, system_month);
    System.out.println(updatelist.six_digit_serial_no);
    updatelist.six_digit_serial_no = six_digit_serial_no_addone;
    AutoGenDao.updateItem(updatelist); // update to database after added
    
    
    ass.setCustomSQL("select * from arc_auto_gen_no "
            + "where form_id = 'WP'");
    
    List<ACFgRawModel> newname = ass.executeQuery(getConnection("ARCDB"));
    resParam.put("form_id",  newname.get(0).getString("form_id"));
    resParam.put("six_digit_serial_no",  APFtUtilityAndGlobal.m_Lpad(df.format(newname.get(0).getBigDecimal("six_digit_serial_no")),"0",6));
    return resParam;
}

@ACFgTransaction
@RequestMapping(value=APWtMapping.APWF003_GENERATE_REPORT_AJAX, method=RequestMethod.POST)
@ResponseBody
public ACFgResponseParameters generate_report(@RequestBody ACFgRequestParameters param) throws Exception { 
    ACFgResponseParameters resParam = new ACFgResponseParameters();
    ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
     ass.setCustomSQL("select * from (select PO.purchase_order_no, PO.remarks, PO.department_reference_no, PO.no_of_times_printed, PO.printed_at, PI.item_no, PO.receive_location, PO.supplier_code, S.supplier_code, S.supplier_name, PI.item_no, I.item_description_1, I.item_description_2, PI.order_quantity, I.un_it, PI.unit_cost, PI.modified_at, PI.order_quantity * PI.unit_cost as tl_cost "
            + "from arc_po_header po, arc_po_details PI, arc_item_master I, arc_supplier S "
            + "where po.purchase_order_no = PI.purchase_order_no "
            + "and PI.item_no = I.item_no "
            + "and PO.supplier_code = S.supplier_code)");
    
    ass.setKey("purchase_order_no","item_no");
    ass.wheres.and("purchase_order_no", purchase_order_no);
    
    
    List<ACFgRawModel> report = ass.executeQuery(getConnection("ARCDB"));
    System.out.println(report.isEmpty());
    if (report.isEmpty() == true)
    {
    	throw exceptionService.error("APW103E");
    }
    BigDecimal n_print = report.get(0).getBigDecimal("no_of_times_printed");
    String po_no = report.get(0).getString("purchase_order_no");
    n_print = n_print.add(new BigDecimal(1));
    
    ARCmPOHeader updatelist = POHeaderDao.selectItem(purchase_order_no);
    updatelist.no_of_times_printed = n_print;
    updatelist.printed_by = SecurityService.getCurrentUser().user_id;
    updatelist.printed_at = ACFtUtility.now();
    POHeaderDao.updateItem(updatelist);
    java.text.DecimalFormat quantityFormat = new java.text.DecimalFormat("##,##0");
    java.text.DecimalFormat unitPriceFormat = new java.text.DecimalFormat("##,##0.00");
    java.text.DecimalFormat totalPriceFormat = new java.text.DecimalFormat("###,##0.00");
    java.text.DecimalFormat pageNumberFormat = new java.text.DecimalFormat("00");
    StringBuilder pageStyle = new StringBuilder(); 
    pageStyle
    .append("<style type=\"text/css\">")
    .append("body {")
    .append("  background: rgb(204,204,204); ")
    .append("}")
    /* define overall page attributes */
    .append("page {")
    .append("  background: white;")
    .append("  display: block;")
    .append("  margin: 0 auto;")
    .append("  margin-bottom: 0.5cm;")
    .append("  box-shadow: 0 0 0.5cm rgba(0,0,0,0.5);")
    .append("}")
    /* define page size for A4 portrait */
    .append("page[size=\"A4\"] {  ")
    .append("  width: 21cm;")
    .append("  height: 29.7cm; ")
    .append("}")
    /* define page size for A4 landscape */
    .append("page[size=\"A4\"][layout=\"landscape\"] {")
    .append("  width: 29.7cm;")
    .append("  height: 21cm;  ")
    .append("}")
    /* define page size for A3 portrait */
    .append("page[size=\"A3\"] {")
    .append("  width: 29.7cm;")
    .append("  height: 42cm;")
    .append("}")
    /* define page size for A3 landscape */
    .append("page[size=\"A3\"][layout=\"landscape\"] {")
    .append("  width: 42cm;")
    .append("  height: 29.7cm;  ")
    .append("}")
    /* define page size for A5 portrait */
    .append("page[size=\"A5\"] {")
    .append("  width: 14.8cm;")
    .append("  height: 21cm;")
    .append("}")
    /* define page size for A5 landscape */
    .append("page[size=\"A5\"][layout=\"landscape\"] {")
    .append("  width: 21cm;")
    .append("  height: 14.8cm;  ")
    .append("}")
    /* define print attributes using above definition */
    .append("@media print {")
    .append("  body, page {")
    .append("    margin: 0;")
    .append("    box-shadow: 0;")
    .append("  }")
    .append("}")
    .append(".footer{ ")
    .append("position: fixed; ")
    .append("text-align: center;")
    .append("bottom: 0px;")
    .append("width: 100%;")
    .append("}")
    .append("</style>");
    
    /* 
     * Define CSS for table border and font. This CSS affect all tables in HTML that used it.
     */
    StringBuilder tableStyle = new StringBuilder();
    tableStyle
    .append("<style type=\"text/css\">")
    
    .append(".col-seq {")
    .append("  width: 22mm;")
    .append("}")
    .append(".col-desc {")
    .append("  width: 90mm;")
    .append("}")
    .append(".col-unit_cost {")
    .append("  width: 25mm;")
    .append("}")
    .append(".col-amount {")
    .append("  width: 25mm;")
    .append("}")
    .append(".col-unit {")
    .append("  width: 25mm;")
    .append("}")
    .append(".col-total {")
    .append("  width: 30mm;")
    .append("}")
    /* Overall table font used */
    .append(".table-font {")
    .append("  font-family:Arial,Verdana,sans-serif;")
    .append("  font-size:1.0em; <font color='red'>/* Never set font sizes in pixels! */</font>")
    .append("  color:#00f;")
    .append("}")
    /* Normal table */
    .append("table.table-normal {")
    .append("  border-width: 1px;")
    .append("  border-spacing: 0px;") //fix each column's position
    .append("  border-style: solid;")
    .append("  border-color: black;")
    .append("  border-collapse: separate;")
    .append("  background-color: white;")
    .append("}")
    /* Table header */
    .append("table.table-normal th {")
    .append("  border-width: 1px;")
    .append("  padding: 1px;")
    .append("  border-style: solid;")
    .append("  border-color: black;")
    .append("  background-color: white;")
    .append("  -moz-border-radius: ;")
    .append("}")
    /* Table column */
    .append("table.table-normal td {")
    .append("  border-width: 1px;")
    .append("  padding: 1px;")
    .append("  border-style: solid;")
    .append("  border-color: black;")
    .append("  background-color: white;")
    .append("  -moz-border-radius: ;"
    		+ " word-wrap: break-word;")
    .append("  word-wrap: break-word;}")
    .append("")
    /* Outline table */
    .append("table.table-outline {")
    .append("  border-width: 1px;")
    .append("  border-spacing: 0px;")
    .append("  border-style: solid;")
    .append("  border-color: black;")
    .append("  border-collapse: separate;")
    .append("  background-color: white;")
    .append("}")
    /* row top border */
    .append("table.table-outline tr.top td {")
    .append("  border-top: thin solid black;")
    .append("}")
    /* row bottom border */
    .append("table.table-outline tr.bottom td {")
    .append("  border-bottom: thin solid black;")
    .append("}")
    /* row first column border */
    .append("table.table-outline tr.row td:first-child {")
    .append("  border-left: thin solid black;")
    .append("}")
    /* row last column border */
    .append("table.table-outline tr.row td:last-child {")
    .append("  border-right: thin solid black;")
    .append("}")
    /* Empty line using Paragraph tag */
    .append("p.pg-thin {  margin-top: 0.5em; margin-bottom: 0em; }")
    .append("</style>");
    
    /* 
     * Since HTML is implemented using plain text, you can use variable substitution to dynamically change the HTML content.
     * Recommended variable substitution format = {variable name} / $(variable name) 
     * Must be a unique variable name for each piece of HTML code insert since finally they will stick together into one string.
     * DO NOT use variable name that easily confuse with HTML tag !
     */
    String V_PO_NUMBER = "{po_number}";
    String V_SECTION = "{section}";
    String V_RECOMMENDED_SUPPLIER = "{recommended_supplier}";
    String V_DELIVERY_ADDRESS = "{delivery_address}";
    String V_OTHERS = "{others}";
    String V_REFERENCE_NO = "{reference_no}";
    String V_PAGE_NUMBER = "{page_number}";
    String V_ITEM_NO = "{item_no}";
    String V_ITEM_DESC = "{item_desc}";
    String V_ITEM_DESC2 = "{item_desc2}";
    String V_ITEM_PRICE = "{item_price}";
    String V_ITEM_QUANTITY = "{item_quantity}";
    String V_ITEM_UNIT = "{item_unit}";
    String V_ITEM_TOTAL = "{item_total}";
    String V_T_TOTAL = "{t_total}";
    String V_REMARKS = "{item_remarks}";
    
    /* Variable content table header */
    StringBuilder tableHeader = new StringBuilder();
    tableHeader
    /* Table begin tags */
    .append("<table class=\"table-outline table-font\" style=\"width: 100%; margin-left: auto; margin-right: auto;\">")
    .append("<tbody>")
        /* insert row using CSS class top & row */
        .append("<tr class=\"top row\">")
        /* insert 1 column that merged 4 columns */
//        .append("<img src=\""+APFtUtilityAndGlobal.APF_TVB_LOGO_URL+"\" alt=\"TVB Logo\" width=\"58\" height=\"50\" />")
        .append("<td style=\"text-align: right; vertical-align:top; padding: 5px\" colspan=\"4\"><img src=\""+"../img/slogo.png"+"\" alt=\"TVB Logo\" width=\"30\" height=\"30\" />Television Broadcasts Limited<br />\u96fb\u8996\u5ee3\u64ad\u6709\u9650\u516c\u53f8</td>")
        .append("</tr>")
            /* insert row using CSS class row */
        .append("<tr class=\"row\">")
        .append("<td colspan=\"2\">\u7f8e\u8853\u5206\u90e8\u5b58\u5009\u8ca8\u8acb\u8cfc\u8868</td>")
        /* P.O. No. display using font different from default font */
        .append("<td style=\"text-align: right; width: 10%;\"> P.O. No.:</td>")
        .append("<td style=\"font-family:\"Times New Roman\"\">"+V_PO_NUMBER+"</td>")
        .append("</tr>")
        /* insert row using CSS class row */
        .append("<tr class=\"row\">")
        .append("<td style=\"text-align: right;\"> \u7d44\u5225 :</td>")
        .append("<td colspan=\"3\">"+V_SECTION+"</td>")
        .append("</tr>")
        /* insert row using CSS class row */
        .append("<tr class=\"row\">")
        .append("<td colspan=\"4\">      </td>")
        .append("</tr>")
        /* insert row using CSS class row */
        .append("<tr class=\"row\">")
        .append("<td style=\"text-align: right;\"> \u5efa\u8b70\u4f9b\u61c9\u5546 : <br />     </td>")
        /* force break a lengthy word even no space character in between */
        .append("<td colspan=\"3\" style=\"word-break: break-all\">"+V_RECOMMENDED_SUPPLIER+"</td>")
        .append("</tr>")
        /* insert row using CSS class row */
        .append("<tr class=\"row\">")
        .append("<td style=\"text-align: right;\"> \u6536\u8ca8\u5730\u9ede :</td>")
        .append("<td colspan=\"3\">"+V_DELIVERY_ADDRESS+"</td>")
        .append("</tr>")
        /* insert row using CSS class row */
        .append("<tr class=\"row\">")
        .append("<td> </td>")
        .append("<td colspan=\"3\"> \u5176\u4ed6 "+V_OTHERS+"</td>")
        .append("</tr>")
        /* insert row using CSS class row */
        .append("<tr class=\"row\">")
        .append("<td style=\"width: 20%;\"> </td>")
        .append("<td style=\"width: 60%;\"> </td>")
        .append("<td style=\"text-align: right; width: 10%;\"> Ref. No. :</td>")
        .append("<td style=\"width: 10%;\">"+V_REFERENCE_NO+"</td>")
        .append("</tr>")
        /* insert row using CSS class row */
        .append("<tr class=\"row\">")
        .append("<td> </td>")
        .append("<td> </td>")
        .append("<td style=\"text-align: right;\">Page :</td>")
        .append("<td>"+V_PAGE_NUMBER+"</td>")
        .append("</tr>")
    /* Table end tags */
    .append("</tbody>")
    .append("</table>");
    
    /* Fixed content page footer */
    StringBuilder pageFooter = new StringBuilder();
    pageFooter
    .append("<div class=\"footer\">")
    //.append("<div style=\"position: absolute; bottom: 5px; background-color: green\">")
    .append("<table class=\"table-font\" style=\"width: 100%; margin-left: auto; margin-right: auto;\">")
    .append("<tbody>")
        .append("<tr>")
        .append("<td style=\"width: 10%;\">\u8acb\u8cfc\u8005</td>")
        .append("<td style=\"width: 1%;\">:</td>")
        .append("<td style=\"width: 39%;\">______________________________</td>")
        .append("<td style=\"width: 10%;\">\u8acb\u8cfc\u65e5\u671f</td>")
        .append("<td style=\"width: 1%;\">:</td>")
        .append("<td style=\"width: 39%;\">______________________________</td>")
        .append("</tr>")
        .append("<tr>")
        .append("<td style=\"width: 10%;\">&nbsp;</td>")
        .append("<td style=\"width: 1%;\">&nbsp;</td>")
        .append("<td style=\"width: 39%;\">&nbsp;</td>")
        .append("<td style=\"width: 10%;\">&nbsp;</td>")
        .append("<td style=\"width: 1%;\">&nbsp;</td>")
        .append("<td style=\"width: 39%;\">&nbsp;</td>")
        .append("</tr>")
//        &nbsp;
        .append("<tr>")
        .append("<td>\u8a02\u8ca8\u4eba</td>")
        .append("<td>:</td>")
        .append("<td>______________________________</td>")
        .append("<td>\u6aa2\u6838</td>")
        .append("<td>:</td>")
        .append("<td>______________________________</td>")
        .append("</tr>")
        .append("<tr>")
        .append("<td>&nbsp;</td>")
        .append("<td>&nbsp;</td>")
        .append("<td>&nbsp;</td>")
        .append("<td>&nbsp;</td>")
        .append("<td>&nbsp;</td>")
        .append("<td>&nbsp;</td>")
        .append("</tr>")
        .append("<tr>")
        .append("<td>\u8a02\u8ca8\u65e5\u671f</td>")
        .append("<td>:</td>")
        .append("<td>______________________________</td>")
        .append("<td>\u6279\u51c6</td>")
        .append("<td>:</td>")
        .append("<td>______________________________</td>")
        .append("</tr>")
    .append("</tbody>")
    .append("</table>")
    //.append("</div>")
    .append("</div>");
    
    /* Fixed content column header */
    StringBuilder columnHeader = new StringBuilder();
    columnHeader
    .append("<tr>")
    .append("<td> </td>")
    .append("<td style=\"text-align: center;\">\u8ca8\u54c1\u540d\u7a31</td>")
    .append("<td style=\"text-align: center;\">\u55ae\u50f9</td>")
    .append("<td style=\"text-align: center;\">\u6578\u91cf</td>")
    .append("<td style=\"text-align: center;\">\u55ae\u4f4d</td>")
    .append("<td style=\"text-align: center;\">\u5408\u8a08(HK$)</td>")
    .append("</tr>");
    
    /* Variable content item row */
    StringBuilder itemRow = new StringBuilder();
    itemRow
    .append("<tr>")
    .append("<td class=\"col-seq\" style=\"text-align: center;\">"+V_ITEM_NO+"</td>")
    .append("<td class=\"col-desc\" >"+V_ITEM_DESC+"</td>")
    .append("<td class=\"col-unit_cost\" style=\"text-align: right;\">"+V_ITEM_PRICE+"</td>")
    .append("<td class=\"col-amount\" style=\"text-align: right;\">"+V_ITEM_QUANTITY+"</td>")
    .append("<td class=\"col-unit\" style=\"text-align: center;\">"+V_ITEM_UNIT+"</td>")
    .append("<td class=\"col-total\" style=\"text-align: right;\">"+V_ITEM_TOTAL+"</td>")
    .append("</tr>");
    
    StringBuilder itemBlank = new StringBuilder();
    itemBlank
    .append("<tr>")
    .append("<td style=\"text-align: center;\">&nbsp;</td>")
    .append("<td>&nbsp;</td>")
    .append("<td style=\"text-align: right;\">&nbsp;</td>")
    .append("<td style=\"text-align: right;\">&nbsp;</td>")
    .append("<td style=\"text-align: center;\">&nbsp;</td>")
    .append("<td style=\"text-align: right;\">&nbsp;</td>")
    .append("</tr>");
    
    StringBuilder itemRow2 = new StringBuilder();
    itemRow2
    .append("<tr>")
    .append("<td class=\"col-seq\" style=\"text-align: center;\">"+""+"</td>")
    .append("<td class=\"col-desc\">"+V_ITEM_DESC2+"</td>")
    .append("<td class=\"col-unit_cost\" style=\"text-align: right;\">"+""+"</td>")
    .append("<td class=\"col-amount\" style=\"text-align: right;\">"+""+"</td>")
    .append("<td class=\"col-unit\" style=\"text-align: center;\">"+""+"</td>")
    .append("<td class=\"col-total\" style=\"text-align: right;\">"+""+"</td>")
    .append("</tr>");
    
    StringBuilder LastRow = new StringBuilder();
    LastRow
    .append("<tr>")
    .append("<td class=\"col-seq\" style=\"text-align: center;\" rowspan=\"2\">"+"\u5099\u6ce8"+"</td>")//備注
    .append("<td class=\"col-desc\" rowspan=\"2\">"+V_REMARKS+"</td>")
    .append("<td class=\"col-unit_cost\" style=\"text-align: right;\" rowspan=\"2\">"+""+"</td>")
    .append("<td class=\"col-amount\" style=\"text-align: right;\" rowspan=\"2\">"+""+"</td>")
    .append("<td class=\"col-unit\" style=\"text-align: center;\" rowspan=\"2\">"+"\u7e3d\u8a08"+"</td>")//總計
    .append("<td class=\"col-total\" style=\"text-align: right;\" rowspan=\"2\">"+V_T_TOTAL+"</td>")
    .append("</tr>");
    
    StringBuilder LastRow2 = new StringBuilder();
    LastRow2
    .append("<tr>")
    .append("<td class=\"col-seq\" style=\"text-align: center;\">"+""+"</td>")
    .append("<td class=\"col-desc\">&nbsp;</td>")
    .append("<td class=\"col-unit_cost\" style=\"text-align: right;\">"+""+"</td>")
    .append("<td class=\"col-amount\" style=\"text-align: right;\">"+""+"</td>")
    .append("<td class=\"col-unit\" style=\"text-align: center;\">&nbsp;</td>")
    .append("<td class=\"col-total\" style=\"text-align: right;\">&nbsp;</td>")
    .append("</tr>");
    
    //** construct final HTML result codes
    StringBuilder html = new StringBuilder(); 
    html.append(pageStyle);
    html.append(tableStyle);
    html.append("<page size=\"A4\">");
    
    int dataRowPerPage = 31;
    int row_cnt = 1;
    int cur_row = 1;
    int page_num = 1;
    double t_total = 0;
    //** loop through the data result set to construct list item HTML codes
    for (ACFgRawModel row : report) {
        if (cur_row == 1) {
            html.append(new StringBuilder(tableHeader));
            APFtUtilityAndGlobal.m_replace(V_PO_NUMBER, row.getString("purchase_order_no"), html);
            APFtUtilityAndGlobal.m_replace(V_REFERENCE_NO, row.getString("department_reference_no"), html);
            APFtUtilityAndGlobal.m_replace(V_SECTION, "\u6728\u5de5\u7d44", html);
            APFtUtilityAndGlobal.m_replace(V_RECOMMENDED_SUPPLIER, row.getString("supplier_name"), html);
            if (row.getString("receive_location").equals("TKO"))
            {   
            	APFtUtilityAndGlobal.m_replace(V_DELIVERY_ADDRESS, row.getString("receive_location"), html);
                APFtUtilityAndGlobal.m_replace(V_OTHERS, "", html);
              }
            if (!row.getString("receive_location").equals("TKO"))
            {
            	APFtUtilityAndGlobal.m_replace(V_DELIVERY_ADDRESS, "", html);
                APFtUtilityAndGlobal.m_replace(V_OTHERS, row.getString("receive_location"), html);
            }
            APFtUtilityAndGlobal.m_replace(V_PAGE_NUMBER, pageNumberFormat.format(page_num), html);
            html
            .append("<table class=\"table-normal table-font\" style=\"width: 100%; border: 1px solid black;\">")
            .append("<tbody>")
            .append(columnHeader);
           
        }
        
        /* insert new item row */
//        if(!row.getString("item_description_2").equals(""))//if there are things to display in desc2 field
//        {
//        for(int i = 0; i<15; i++ ){ //testing loop
        html.append(new StringBuilder(itemRow));
        APFtUtilityAndGlobal.m_replace(V_ITEM_NO, pageNumberFormat.format(row_cnt), html);
        APFtUtilityAndGlobal.m_replace(V_ITEM_DESC, row.getString("item_description_1"), html);
        APFtUtilityAndGlobal.m_replace(V_ITEM_PRICE, "$"+APFtUtilityAndGlobal.m_Lpad(unitPriceFormat.format(row.getBigDecimal("unit_cost"))," ",9), html);
        APFtUtilityAndGlobal.m_replace(V_ITEM_QUANTITY, APFtUtilityAndGlobal.m_Lpad(quantityFormat.format(row.getBigDecimal("order_quantity"))," ",5), html);
        APFtUtilityAndGlobal.m_replace(V_ITEM_UNIT, row.getString("un_it"), html);
        System.out.println("debug" + row.getBigDecimal("tl_cost"));
        APFtUtilityAndGlobal.m_replace(V_ITEM_TOTAL, "$"+APFtUtilityAndGlobal.m_Lpad(totalPriceFormat.format(row.getBigDecimal("tl_cost"))," ",9), html);
        //html.append(new StringBuilder(itemBlank));//testing blank row
        if(!row.getString("item_description_2").equals(""))//if there are things to display in desc2 field
        {
        	html.append(new StringBuilder(itemRow2)); 
        	APFtUtilityAndGlobal.m_replace(V_ITEM_DESC2, row.getString("item_description_2"), html);//replace the content of the field in row2
        	cur_row++;
        }
        t_total += row.getBigDecimal("tl_cost").intValue();
        
//        }}
      
        //** if it is the last data row of page or report, finalize the page and insert into HTML
        if (cur_row == (dataRowPerPage - 2) || row_cnt == report.size()) {
        	
        	
        	 if (cur_row != (dataRowPerPage - 2))
        	 {
        		
        		 for(int i = cur_row; i < (dataRowPerPage - 2); i++)
        		 {
        			 html.append(new StringBuilder(itemBlank));
        			 
        		 }
        	 }
        	//add last row, if remarks if over 30 characters, turn it to next line.
        	 //"next line" is created by row-span, substring and \n new line.
        	 if (row.getString("remarks").length() > 30)
        	 {
        	 html.append(new StringBuilder(LastRow));
         	APFtUtilityAndGlobal.m_replace(V_T_TOTAL, "$"+APFtUtilityAndGlobal.m_Lpad(totalPriceFormat.format(new BigDecimal(t_total))," ",9), html);
         	APFtUtilityAndGlobal.m_replace(V_REMARKS, row.getString("remarks").substring(0,30) +"\n "+ row.getString("remarks").substring(30,row.getString("remarks").length()), html);
        	 }
        	 else{
        	 html.append(new StringBuilder(LastRow));
          	APFtUtilityAndGlobal.m_replace(V_T_TOTAL, "$"+APFtUtilityAndGlobal.m_Lpad(totalPriceFormat.format(new BigDecimal(t_total))," ",9), html);
          	APFtUtilityAndGlobal.m_replace(V_REMARKS, row.getString("remarks"), html);
        	 }
            html
            .append(APFtUtilityAndGlobal.HTML_TBODY_END)
            .append(APFtUtilityAndGlobal.HTML_TABLE_END);

            //** insert page footer
            if (row_cnt == report.size()) {
                html
                .append("<p class=\"pg-thin\"></p>")
                 .append(new StringBuilder(pageFooter));
            } else {
                html.append(APFtUtilityAndGlobal.HTML_PAGE_BREAK);
            }
            cur_row = 1;
            page_num++;
        } else {
            cur_row++;
        }
        
        row_cnt++;
    }
    
    html.append("</page>");

//    String report_date = "Date: "+param.get("p_report_date", String.class).trim();
    
    resParam.put("report", html.toString());
    System.out.println(System.getProperty("user.dir"));
    System.out.println(System.getProperty( APFtUtilityAndGlobal.APF_TVB_LOGO_URL));
    System.out.println(System.getProperty( APFtUtilityAndGlobal.APF_TVB_LOGO_PATH));
    System.out.println(System.getProperty( APFtUtilityAndGlobal.APF_IMAGES_URL));
    System.out.println(System.getProperty( APFtUtilityAndGlobal.APF_IMAGES_PATH));
   
    System.out.println(html);
    System.out.println("size of the html was" + html.length());
    
    return resParam;
}
}