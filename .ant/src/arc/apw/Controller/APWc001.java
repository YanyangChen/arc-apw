package arc.apw.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import acf.acf.Abstract.ACFaAppController;
import acf.acf.Abstract.ACFaSQLAssRead;
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
import acf.acf.Model.ACFmGridResult;
import acf.acf.Model.ACFmUser;
import arc.apf.Dao.ARCoItemInventory;
import arc.apf.Dao.ARCoItemMaster;
import arc.apf.Model.ARCmItemCategory;
import arc.apf.Model.ARCmItemInventory;
import arc.apf.Model.ARCmItemMaster;
import arc.apf.Model.ARCmPODetails;
import arc.apf.Service.ARCsItemCategory;
import arc.apf.Service.ARCsLocation;
//import arc.apf.Model.ARCmItemMaster;
//import arc.apf.Service.APFsFuncGp;
import arc.apf.Service.ARCsModel;
import arc.apf.Service.ARCsPoDetails;
//import arc.apw.Controller.APWc003.Search;
import arc.apw.Static.APWtMapping;

@Controller
@Scope("session")
@ACFgFunction(id="APWF001")
@RequestMapping(value=APWtMapping.APWF001)
public class APWc001 extends ACFaAppController {
    
    @Autowired ARCsModel moduleService;
    @Autowired ARCsLocation LocationService;
    @Autowired ARCsPoDetails PoDetailsService;
    @Autowired ARCsItemCategory ItemCategoryService;
    @Autowired ARCoItemMaster ItemMasterDao; //modify according to the table testing rtc 502
    @Autowired ARCoItemInventory ItemInventoryDao;
    //@Autowired APFsFuncGp funcGpService; //click the object and click import
    @ACFgAuditKey String item_no;
    @ACFgAuditKey String item_category_no;
    @ACFgAuditKey String purchase_order_no;
    //@ACFgAuditKey String sub_section_id;
    
  //  Search search = new Search();

    private class Search extends ACFgSearch {
        public Search() {
            super();
//            setModel(ARCmItemMaster.class); //define a Search which accept 4 filters from client
            setCustomSQL("select * from (select m.* from arc_item_master m where m.section_id = '03')");
            setKey("item_no");
            addRule(new ACFdSQLRule("item_no", RuleCondition.LIKE_, null, RuleCase.Insensitive)); //sec_id
            //addRule(new ACFdSQLRule("item_no", RuleCondition.EQ, null, RuleCase.Insensitive));//sub_sec_id
            addRule(new ACFdSQLRule("item_desc", RuleCondition._LIKE_, null, RuleCase.Insensitive));
            //addRule(new ACFdSQLRule("section_name", RuleCondition._LIKE_, null, RuleCase.Insensitive));
            //addRule(new ACFdSQLRule("report_caption", RuleCondition._LIKE_, null, RuleCase.Insensitive));
        }// ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
    }
    
    private class Search2 extends ACFgSearch {
        public Search2() {
            super();
//            setModel(ARCmItemMaster.class); //define a Search which accept 4 filters from client
           setCustomSQL("select * from (select ic.item_category_no from arc_item_category ic where section_id = '03') ");
			
            setKey("item_category_no");
           // addRule(new ACFdSQLRule("item_no", RuleCondition.LIKE_, null, RuleCase.Insensitive)); //sec_id
            //addRule(new ACFdSQLRule("item_no", RuleCondition.EQ, null, RuleCase.Insensitive));//sub_sec_id
           // addRule(new ACFdSQLRule("item_desc", RuleCondition._LIKE_, null, RuleCase.Insensitive));
            //addRule(new ACFdSQLRule("section_name", RuleCondition._LIKE_, null, RuleCase.Insensitive));
            //addRule(new ACFdSQLRule("report_caption", RuleCondition._LIKE_, null, RuleCase.Insensitive));
        }// ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
    }
    Search search = new Search();
    Search2 searchcat = new Search2();
    
    @RequestMapping(value=APWtMapping.APWF001_MAIN, method=RequestMethod.GET)
    public String main(ModelMap model) throws Exception {
        model.addAttribute("item_no", item_no);
       // model.addAttribute("sub_section_id", sub_section_id); //set tow keys
        //initial value in function maintenance form
        model.addAttribute("modules", ItemCategoryService.getCat_Noapw()); //acf's function, get data from ACFDB
        model.addAttribute("locationcode", LocationService.getLocationCode());
        //System.out.println(moduleService.getAllModuleValuePairs());
        //search value groups in search form and main form
        //model.addAttribute("moduleGroups", funcGpService.getModuleFuncGpIndex()); // no need to group tables just now

        return view();
        
    }
    @RequestMapping(value=APWtMapping.APWF001_SEARCH_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getGrid(@RequestBody ACFgRequestParameters param) throws Exception {
      //The method getGrid responds to AJAX by obtain the Search JSON result and put in variable “grid_browse”.
        // ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
        search.setConnection(getConnection("ARCDB")); //get connection to the database
        search.setValues(param);
        search.setFocus(item_no); 
        System.out.println(param);
       // System.out.println(search.getGridResult());
        return new ACFgResponseParameters().set("grid_browse", search.getGridResult()); // can only be called once, otherwise reset parameter
    }
    
    
    
    @RequestMapping(value=APWtMapping.APWF001_GET_FORM_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getForm(@RequestBody ACFgRequestParameters param) throws Exception {
        item_no = param.get("item_no", String.class); 
        getRemainingInventory(param.getRequestParameter("remaining_browse"));
        getInventory(param.getRequestParameter("inventory_browse"));
        getAdjustment(param.getRequestParameter("adjust_browse"));
       // sub_section_id = param.get("sub_section_id", String.class);  
        //return new ACFgResponseParameters().set("frm_main", ItemMasterDao.selectItem(item_no)); 
        return getResponseParameters().set("frm_main", ItemMasterDao.selectItem(item_no));
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value=APWtMapping.APWF001_GET_REMAINING_INVENTORY_AJAX, method=RequestMethod.POST)
    @ResponseBody
     public ACFgResponseParameters getRemainingInventory(@RequestBody ACFgRequestParameters param) throws Exception {
        ACFdSQLAssSelect select = new ACFdSQLAssSelect();
     
      select.setCustomSQL("select * from (select I.item_no, I.purchase_order_no, I.purchase_order_date, I.unit_cost, I.received_quantity + I.adjusted_quantity - I.consumed_quantity "
              + "as remaining_quantity, I.order_quantity, I.received_quantity, "
              + "I.back_order_quantity, I.modified_at, "
              + "I.receive_date, I.consumed_quantity, I.adjusted_quantity, I.order_quantity - I.received_quantity - I.back_order_quantity as out_standing_quantity "
              
              + "from arc_item_inventory I)");
      select.setKey("item_no","purchase_order_no");
      select.wheres.and("item_no", item_no);
      //select.orders.put("seq", true);
      ACFgResponseParameters rp = getResponseParameters().set("remaining_browse", select.executeGridQuery(getConnection("ARCDB"), param));
      
      for (String key : rp.keySet())
      {
    	  System.out.println("\n"+key + "->" + rp.get(key));
    	  System.out.println("getting class-----------------------" + (( rp.get("remaining_browse")).getClass()));
      }
      ACFmGridResult ar = (ACFmGridResult) rp.get("remaining_browse");
      if (ar.getRows().size()>0)
      {
      System.out.println("getting row result" + ar.getRows().get(0) + " class-----------------" + ar.getRows().get(0).getClass());
      List<ACFmGridResult> arr = (List<ACFmGridResult>) ar.getRows();
     
      //for (int i = 0; i < ar.getRows().size(); i++){
      System.out.println("***********************size of rows" + ar.getRows().size());
    	  for (int i = ar.getRows().size(); i > 0; i--){ 
    		  System.out.println("***********************bigger inner size of rows" + ar.getRows().size());
	LinkedHashMap<String, Object> lhm = (LinkedHashMap<String, Object>) ar.getRows().get(i-1); //should get all the rows
      System.out.println("getting remaining quantity-----------------------" + lhm.get("remaining_quantity"));
      if (lhm.get("remaining_quantity").equals(0.00) || lhm.get("remaining_quantity").toString().equals("0.00") || lhm.get("remaining_quantity").toString().equals("0"))
      {
    	  System.out.println("*********************** inner size of rows" + ar.getRows().size());  
      //lhm.put("remaining_quantity", "unavailable");
    	  System.out.println("*********************** index of lhm in arr" + arr.indexOf(lhm)); 
     // arr.set(i,(LinkedHashMap<String, Object>) lhm);}
    	  arr.remove(lhm);
      }}
      ar.setRows(arr);
      rp.put("remaining_browse", ar);
      }
      return rp;
   
        }

    @RequestMapping(value=APWtMapping.APWF001_GET_INVENTORY_AJAX, method=RequestMethod.POST)
    @ResponseBody
     public ACFgResponseParameters getInventory(@RequestBody ACFgRequestParameters param) throws Exception {
        ACFdSQLAssSelect select = new ACFdSQLAssSelect();
     
      select.setCustomSQL("select * from (select I.item_no, I.purchase_order_no, I.purchase_order_date, I.unit_cost, I.received_quantity + I.adjusted_quantity - I.consumed_quantity "
              + "as remaining_quantity, I.order_quantity, I.received_quantity, I.receive_date, I.consumed_quantity, I.adjusted_quantity, "
              + "I.back_order_quantity, I.modified_at, "
              + "I.order_quantity - I.received_quantity - I.back_order_quantity as out_standing_quantity "
              + "from arc_item_inventory I)");
      select.setKey("item_no","purchase_order_no");
      select.wheres.and("item_no", item_no);
      //select.orders.put("seq", true);
      return getResponseParameters().set("inventory_browse", select.executeGridQuery(getConnection("ARCDB"), param));
        }
    
    @RequestMapping(value=APWtMapping.APWF001_ADJUSTMENT_AJAX, method=RequestMethod.POST)
    @ResponseBody
     public ACFgResponseParameters getAdjustment(@RequestBody ACFgRequestParameters param) throws Exception {
        ACFdSQLAssSelect select = new ACFdSQLAssSelect();
     
      select.setCustomSQL("select * from(select H.item_no, H.adjustment_date, H.adjust_quantity, H.modified_at "
      		+ "from arc_item_adjustment_history H "
      		+ ")");
      select.setKey("item_no");
      select.wheres.and("item_no", item_no);
      //select.orders.put("seq", true);
      return getResponseParameters().set("adjust_browse", select.executeGridQuery(getConnection("ARCDB"), param));
        }
    
    
    @RequestMapping(value=APWtMapping.APWF001_GET_REFERENCE_PRICE, method=RequestMethod.POST)
    @ResponseBody 
    public ACFgResponseParameters getReferencePrice(@RequestBody ACFgRequestParameters param) throws Exception {
        setAuditKey("item_no", param.get("item_no", String.class));
        
       // code to pass values to client side as the name "sup_desc"
        getResponseParameters().put("unit_cost",         PoDetailsService.getReferencePrice((param.get("item_no", String.class))));
      //getResponseParameters().put("ep",               prodMemberService.getStaffByProgNo(param.get("prog_no", String.class), "EP"));
        return getResponseParameters(); // a ACFgResponseParameters type defined by base class
    } 
    
    @ACFgTransaction
    @RequestMapping(value=APWtMapping.APWF001_SAVE_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters save(@RequestBody ACFgRequestParameters param) throws Exception { //function in the upper right "save" button
      //the controller obtains the changes of form data 
        final List<ARCmItemMaster> amendments = param.getList("form", ARCmItemMaster.class);
       // final List<ARCmItemInventory> ItemInvRemamendments = param.getList("remain", ARCmItemInventory.class);
        final List<ARCmItemInventory> ItemInvamendments = param.getList("inv", ARCmItemInventory.class);
        System.out.println("******************testing list  **********************" + ItemInvamendments);
        //and call DAO to save the changes
        ARCmItemMaster lastItem = ItemMasterDao.saveItems(amendments, new ACFiSQLAssWriteInterface<ARCmItemMaster>(){
            
            
            //interface for the related functions
            @Override
            public boolean insert(final ARCmItemMaster newItem, ACFdSQLAssInsert ass) throws Exception {
                //ass.columns.put("allow_print", 1); //without the allow_print column, the whole sql won't work
                ass.setAfterExecute(new ACFiCallback() {
                   // @SuppressWarnings("null")
					@SuppressWarnings("unchecked")
					@Override
                    public void callback() throws Exception {
						if(newItem.item_no.length() != 7)
						{
							//Item No. must be 7 digits!
							throw exceptionService.error("APW201E");
						}
						//get all the item catogory no. from item cat table
						searchcat.setConnection(getConnection("ARCDB"));
//						searchcat.setCustomSQL("select * from (select ic.item_category_no, from arc_item_category ic where section_id = '03') ");
//						searchcat.setKey(item_category_no); 
						searchcat.setFocus(item_category_no); 
						
						ACFmGridResult result = searchcat.getGridResult();
						
						ArrayList<String> Cats = new ArrayList<String>();
						for( ACFgRawModel  i : (List<ACFgRawModel >)result.getRows()){
							System.out.println("************************testing i*****************"+i);
							Cats.add(i.getString("item_category_no"));
							System.out.println("************************testing i.getString(item_category_no)*****************"+i.getString("item_category_no"));
						}
//						Set<String> VALUES = new HashSet<String>(Cats);
						
						//get the first 3 character of the item_no
						String ItemCatNo = amendments.get(0).item_no.substring(0, 3);
						System.out.println("************************testing ItemCatNo*****************"+ItemCatNo);
						System.out.println("************************testing Cats*****************"+Cats);
						System.out.println("************************testing Cats.contains(ItemCatNo)*****************"+Cats.contains(ItemCatNo));
						if (!Cats.contains(ItemCatNo))
						{
							//The first 3 digits of the item No. must match item category No.!
							throw exceptionService.error("APW101E");
						}
//                        System.out.println("debug01" + ItemInvamendments);
                        if (ItemInvamendments != null)
                            ItemInventoryDao.saveItems(ItemInvamendments);
//                        else
//                        {
//                        	List<ARCmItemInventory> ItemInvamendments2 = ItemInvamendments;
//                        	ItemInvamendments2.get(0).item_no = amendments.get(0).item_no;
//                        	ItemInvamendments2.get(0).purchase_order_no = "0";
//                        }
                      
                    }
                });
                return false;
            }

            @Override
            public boolean update(ARCmItemMaster oldItem, ARCmItemMaster newItem, ACFdSQLAssUpdate ass) throws Exception {
                ass.setAfterExecute(new ACFiCallback() {
                    @Override
                    public void callback() throws Exception {
                       // if (ItemInvRemamendments != null)
                        //    ItemInventoryDao.saveItems(ItemInvRemamendments);
                        System.out.println("debug02" + ItemInvamendments);
                        if (ItemInvamendments != null)
                            ItemInventoryDao.saveItems(ItemInvamendments);
                      
                    }
                });
                return false;
                
            }

            @Override
            public boolean delete(ARCmItemMaster oldItem, ACFdSQLAssDelete ass) throws Exception {
            	boolean delete_indicator = true;
            	for(ARCmItemInventory II : ItemInvamendments){
//            		ARCmItemInventory II = ItemInventoryDao.selectItem(oldItem.item_no);
           		if ((II.consumed_quantity.intValue() > 0) || (II.received_quantity.intValue() > 0) || (II.adjusted_quantity.intValue() != 0)|| (II.order_quantity.intValue() > 0))
           		{
           			delete_indicator = false;
           			break;
            	}
            	}
            	
            	if (delete_indicator == false)
            	{throw exceptionService.error("APW003E");}
                return false;
            }
        });
        item_no = lastItem!=null? lastItem.item_no: null;

        return new ACFgResponseParameters();
    }

}