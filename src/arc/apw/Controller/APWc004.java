package arc.apw.Controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;







//import cal.aes.Model.AESmEventPlan;
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
import acf.acf.Model.ACFmGridResult;
import acf.acf.Model.ACFmUser;
import acf.acf.Static.ACFtUtility;
import arc.apf.Dao.ARCoItemInventory;
import arc.apf.Dao.ARCoItemMaster;
import arc.apf.Dao.ARCoItemReceiveHistory;
import arc.apf.Dao.ARCoPOHeader;
import arc.apf.Model.ARCmIndirectBudget;
import arc.apf.Model.ARCmItemInventory;
import arc.apf.Model.ARCmItemMaster;
import arc.apf.Model.ARCmItemReceiveHistory;
import arc.apf.Model.ARCmPOHeader;
import arc.apf.Service.ARCsLocation;
import arc.apf.Service.ARCsModel;
import arc.apf.Service.ARCsPoHeader;
//import arc.apw.Controller.APWc004.Search;
import arc.apw.Static.APWtMapping;

@Controller
@Scope("session")
@ACFgFunction(id="APWF004")
@RequestMapping(value=APWtMapping.APWF004)
public class APWc004 extends ACFaAppController {
    
    @Autowired ARCsModel moduleService;
    //@Autowired ACFoFunction functionDao;
    @Autowired ARCoPOHeader POHeaderDao; //modify according to the table
    @Autowired ARCoItemInventory ItemInventoryDao;
    @Autowired ARCsPoHeader PoHeaderService;
    @Autowired ARCsLocation LocationService;
    @Autowired ARCoItemMaster ItemMasterDao;
    @Autowired ARCoItemReceiveHistory ItemReceiveHistoryDao;
    //@Autowired APFsFuncGp funcGpService; //click the object and click import
    @ACFgAuditKey String purchase_order_no;
    @ACFgAuditKey String item_no;
    @ACFgAuditKey BigDecimal unit_cost;
    
  //  Search search = new Search();

    private class Search extends ACFgSearch {
        public Search() {
            super();
            setCustomSQL("select * from (select p.* from arc_po_header p where p.section_id = '03')");
            setKey("purchase_order_no");
            addRule(new ACFdSQLRule("supplier_code", RuleCondition.EQ, null, RuleCase.Insensitive));
            addRule(new ACFdSQLRule("purchase_order_no", RuleCondition.EQ, null, RuleCase.Insensitive));
          //addRule(new ACFdSQLRule("other_material", RuleCondition._LIKE_, null, RuleCase.Insensitive));
        }// ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
        @Override
        public Search setValues(ACFgRequestParameters param) throws Exception { //use the search class to setup an object
            super.setValues(param);// param is a object, "Search" 's mother class passed
                if(!param.isEmptyOrNull("start_date")) {
                wheres.and("purchase_order_date", ACFdSQLRule.RuleCondition.GE, param.get("start_date", Timestamp.class));
                }//// change date to column name
                if(!param.isEmptyOrNull("end_date")) {
                wheres.and("purchase_order_date", ACFdSQLRule.RuleCondition.LT, new Timestamp(param.get("end_date", Long.class) + 24*60*60*1000));
                }
                //wheres.and("po_date", ACFdSQLRule.RuleCondition.LT, param.get("po_date_e", Timestamp.class));
            
            orders.put("purchase_order_date", false);
            return this;
        }
    }
    Search search = new Search();

    
    @RequestMapping(value=APWtMapping.APWF004_MAIN, method=RequestMethod.GET)
    public String main(ModelMap model) throws Exception {
        model.addAttribute("getSupplierCode", PoHeaderService.getSupplierCode());
        model.addAttribute("PurchaseOrderNo", PoHeaderService.getPurchaseOrderNo());
        //model.addAttribute("purchase_order_no", purchase_order_no);
        model.addAttribute("LocationCode", LocationService.getLocationCode());
       // model.addAttribute("item_no", item_no); //set row keys
        //model.addAttribute("unit_cost", unit_cost);
        //initial value in function maintenance form
        //model.addAttribute("modules", moduleService.getAllModuleValuePairs()); //acf's function, get data from ACFDB
        //System.out.println(moduleService.getAllModuleValuePairs());
        //search value groups in search form and main form
        //model.addAttribute("moduleGroups", funcGpService.getModuleFuncGpIndex()); // no need to group tables just now

        return view();
        
    }
    @RequestMapping(value=APWtMapping.APWF004_SEARCH_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getGrid(@RequestBody ACFgRequestParameters param) throws Exception {
      //The method getGrid responds to AJAX by obtain the Search JSON result and put in variable “grid_browse”.
        // ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
        search.setConnection(getConnection("ARCDB")); //get connection to the database
        search.setValues(param);
        search.setFocus(purchase_order_no); //set two keys
        System.out.println(param);
       // System.out.println(search.getGridResult());
        return new ACFgResponseParameters().set("grid_browse", search.getGridResult()); // can only be called once, otherwise reset parameter
    }
    
    @RequestMapping(value=APWtMapping.APWF004_GET_INVENTORY_TABLE_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getInventoryTable(@RequestBody ACFgRequestParameters param) throws Exception {                                
        ACFdSQLAssSelect select = new ACFdSQLAssSelect(); 
      //  select.setCustomSQL("");
      
        select.setCustomSQL("select * from(select I.*, IM.un_it, IM.item_description_1, PD.unit_cost, 0 as trigcrq, 0 as trigcbo, '' as current_received_quantity, '' as current_back_order_qty, I.received_quantity as pre_received_qty, I.back_order_quantity as pre_back_order_qty, "
                + " I.order_quantity - I.received_quantity - I.back_order_quantity as out_standing_quantity "
                //+ " I.received_quantity + I.current_received_qty as received_quantity, "
              // + " I.back_order_quantity + I.current_back_order_qty as back_order_quantity, "
               // + " 0 as current_back_order_qty, "
              //  + " 0 as current_received_qty "
                + " from arc_item_inventory I, arc_item_master IM, arc_po_details PD "
                + " where I.purchase_order_no = PD.purchase_order_no "
                + " and I.item_no = PD.item_no "
                + " and I.item_no = IM.item_no"
                + " )");
        select.setKey("purchase_order_no");
        select.wheres.and("purchase_order_no", purchase_order_no);
        //select.orders.put("seq", true);
        ACFmGridResult result = select.executeGridQuery(getConnection("ARCDB"), param);
//        int count = 0;
//		for(ARCmItemMaster i : (List<ARCmItemMaster>)result.getRows()){
//			if (i.material_type.toString().equals("2"))
//			{
//				count+=i.
//			}
//		}
        return getResponseParameters().set("receipt_browse", result);
      
      }

    @RequestMapping(value=APWtMapping.APWF004_GET_FORM_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getForm(@RequestBody ACFgRequestParameters param) throws Exception {
        purchase_order_no = param.get("purchase_order_no", String.class); //pick the value of parameter “func_id” from client
        getInventoryTable(param.getRequestParameter("receipt_browse")); 
        //other_material = param.get("other_material", String.class);  //set two keys!!
        //unit_cost = param.get("unit_cost", BigDecimal.class);
        //retrieves the result by DAO, and put in the variable “frm_main”. 
        //ACF will forward the content to client and post to the form which ID equals to “frm_main”
        return getResponseParameters().set("frm_main", POHeaderDao.selectItem(purchase_order_no)); //change dao here //set two keys!!
    }

//    @RequestMapping(value=APWtMapping.APWF003_GET_FORM_AJAX, method=RequestMethod.POST) //get rows of the first grid
//    @ResponseBody
//    public ACFgResponseParameters getForm(@RequestBody ACFgRequestParameters param) throws Exception {
//        purchase_order_no = param.get("purchase_order_no", String.class); //pick the value of parameter “func_id” from client
//     getItemTable(param.getRequestParameter("grid_item"));      ////update Dao
//        return getResponseParameters().set("frm_main", POHeaderDao.selectItem(purchase_order_no)); //change dao here
//    }
    
    @ACFgTransaction
    @RequestMapping(value=APWtMapping.APWF004_SAVE_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters save(@RequestBody ACFgRequestParameters param) throws Exception { //function in the upper right "save" button
      //the controller obtains the changes of form data 
        final List<ARCmPOHeader> amendments = param.getList("form", ARCmPOHeader.class);
        final List<ARCmItemInventory> Inventoryamendments = param.getList("Receipt", ARCmItemInventory.class);
        System.out.println(Inventoryamendments);
        //and call DAO to save the changes
        ARCmPOHeader lastItem = POHeaderDao.saveItems(amendments, new ACFiSQLAssWriteInterface<ARCmPOHeader>(){
            
            
            //interface for the related functions
            @Override
            public boolean insert(ARCmPOHeader newItem, ACFdSQLAssInsert ass) throws Exception {
                //ass.columns.put("allow_print", 1); //without the allow_print column, the whole sql won't work
                ass.setAfterExecute(new ACFiCallback() {
                    @Override
                    public void callback() throws Exception {
                    	List<ARCmItemInventory>Inventoryamendments2 = new ArrayList<ARCmItemInventory>();
                    	                     	for ( ARCmItemInventory each : Inventoryamendments)
                    	                     	{
                    	                     		Inventoryamendments2.add(each);
                    	                     	}
                    	                         if (Inventoryamendments2 != null)
                    	                         	
                    	 //                            ItemInventoryDao.saveItems(Inventoryamendments);
                    	                         	ItemInventoryDao.saveItems(Inventoryamendments2, new ACFiSQLAssWriteInterface<ARCmItemInventory>(){
                    	 
                    	 								@Override
                    	 								public boolean insert(
                    	 										ARCmItemInventory newItem,
                    	 										ACFdSQLAssInsert ass) throws Exception {
                    	 									newItem.purchase_order_date = ACFtUtility.now();
                    	  									newItem.receive_date = ACFtUtility.now();
                    	 								System.out.println("testing ********************* newItem.receive_date **** i ***** insert" +  newItem.receive_date);
                    	 										
                   	  									// TODO Auto-generated method stub
                    	 									return false;
                    	 								}
                    	 
                    	 								@Override
                    	 								public boolean update(
                    	 										ARCmItemInventory oldItem,
                    	 										ARCmItemInventory newItem,
                    	 										ACFdSQLAssUpdate ass) throws Exception {
                    	 									newItem.purchase_order_date = ACFtUtility.now();
                    	 									newItem.receive_date = ACFtUtility.now();
                    	 									System.out.println("testing ********************* newItem.receive_date **** i ***** update" + newItem.receive_date);
                    	 									// TODO Auto-generated method stub
                    	 									//update history records to receive_history table
                    	 									
                    	 									return false;
                    	 								}
                    	 
                    	 								@Override
                    	 								public boolean delete(
                    	 									ARCmItemInventory oldItem,
                    	 									ACFdSQLAssDelete ass) throws Exception {
                    										// TODO Auto-generated method stub
                    	 									return false;
                    	 								}});
                        
                      
                    }
                });
                return false;
            }

            @Override
            public boolean update(ARCmPOHeader oldItem, ARCmPOHeader newItem, ACFdSQLAssUpdate ass) throws Exception {
                ass.setAfterExecute(new ACFiCallback() {
                    @Override
                    public void callback() throws Exception {
                        if (Inventoryamendments != null)
                        	ItemInventoryDao.saveItems(Inventoryamendments, new ACFiSQLAssWriteInterface<ARCmItemInventory>(){

 								@Override
 								public boolean insert(
										ARCmItemInventory newItem,
										ACFdSQLAssInsert ass) throws Exception {
 									ass.columns.put("purchase_order_date", amendments.get(0).purchase_order_date);
									ass.columns.put("receive_date", amendments.get(1).latest_receive_date);
//									newItem.purchase_order_date = amendments.get(0).purchase_order_date;
// 									newItem.receive_date = amendments.get(1).latest_receive_date;
 									System.out.println("testing ********************* newItem.receive_date **** i ***** insert" + newItem.receive_date);
 									
 									
 									// TODO Auto-generated method stub
									return false;
								}

								@Override
								public boolean update(
										ARCmItemInventory oldItem,
										ARCmItemInventory newItem,
										ACFdSQLAssUpdate ass) throws Exception {
									ass.columns.put("purchase_order_date", amendments.get(0).purchase_order_date);
									ass.columns.put("receive_date", amendments.get(1).latest_receive_date);
									//if the item belongs to 'report in category'
									
									//create history records to receive_history table
 									ARCmItemReceiveHistory reviHist = new ARCmItemReceiveHistory();
									reviHist.item_no = newItem.item_no;
									reviHist.purchase_order_no = newItem.purchase_order_no;
									reviHist.received_date = amendments.get(1).latest_receive_date;
									reviHist.received_quantity = new BigDecimal(newItem.received_quantity.intValue() - oldItem.received_quantity.intValue());
									reviHist.back_order_quantity = new BigDecimal(newItem.back_order_quantity.intValue() - oldItem.back_order_quantity.intValue());
									ItemReceiveHistoryDao.insertItem(reviHist);
									
									return false;
								}

 								@Override
 								public boolean delete(
 										ARCmItemInventory oldItem,
 										ACFdSQLAssDelete ass) throws Exception {
 									// TODO Auto-generated method stub
 									return false;
 								}});
                        
                      
                    }
                });
                return false;
            }

            @Override
            public boolean delete(ARCmPOHeader oldItem, ACFdSQLAssDelete ass) throws Exception {
                return false;
            }
        });
        purchase_order_no = lastItem!=null? lastItem.purchase_order_no: null;// what's the purpose of this?
       // other_material = lastItem!=null? lastItem.other_material: null;
        //unit_cost = lastItem!=null? lastItem.unit_cost: null;

        return new ACFgResponseParameters();
    }

}