package arc.apw.Controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import acf.acf.Abstract.ACFaAppController;
import acf.acf.Database.ACFdSQLAssDelete;
import acf.acf.Database.ACFdSQLAssInsert;
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
import acf.acf.Interface.ACFiSQLAssWriteInterface;
import arc.apf.Dao.ARCoItemAdjustmentHistory;
import arc.apf.Dao.ARCoItemInventory;
import arc.apf.Dao.ARCoOtherMaterials;
import arc.apf.Model.ARCmItemAdjustmentHistory;
import arc.apf.Model.ARCmItemAdjustmentHistory;
import arc.apf.Model.ARCmItemInventory;
import arc.apf.Service.ARCsItemMaster;
import arc.apf.Service.ARCsLocation;
import arc.apf.Service.ARCsModel;
import arc.apw.Static.APWtGlobal;
import arc.apw.Static.APWtMapping;

@Controller
@Scope("session")
@ACFgFunction(id="APWF006")
@RequestMapping(value=APWtMapping.APWF006)
public class APWc006 extends ACFaAppController {
    
    @Autowired ARCsModel moduleService;
    @Autowired ARCsItemMaster ItemMasterService;
    @Autowired ARCsLocation LocationService;
    
    //@Autowired ACFoFunction functionDao;
    @Autowired ARCoItemAdjustmentHistory ItemAdjustmentHistoryDao; //modify according to the table
    @Autowired ARCoItemInventory ItemInventoryDao;
    //@Autowired APFsFuncGp funcGpService; //click the object and click import
    @ACFgAuditKey String section_id;
    @ACFgAuditKey String other_material;
    @ACFgAuditKey BigDecimal unit_cost;
    
    @ACFgAuditKey String item_no;
    @ACFgAuditKey Timestamp adjustment_date;
    
  //  Search search = new Search();

    private class Search extends ACFgSearch {
        public Search() {
            super();
            setCustomSQL("select * from (select A.*, A.adjustment_date as adjustment_datee, im.item_description_1 as item_desc " +
                    "from arc_item_adjustment_history A, arc_item_master im "
                    + "where A.item_no = im.item_no and section_id = '"+APWtGlobal.APW_SECTION_ID+"')");
            setKey("adjustment_date", "item_no");
            
            addRule(new ACFdSQLRule("item_no", RuleCondition._LIKE_, null, RuleCase.Insensitive));
        }// ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
        @Override
        public Search setValues(ACFgRequestParameters param) throws Exception { //use the search class to setup an object
            super.setValues(param);// param is a object, "Search" 's mother class passed
                if(!param.isEmptyOrNull("start_date")) {
                wheres.and("adjustment_date", ACFdSQLRule.RuleCondition.GE, param.get("start_date", Timestamp.class));
                }//// change date to column name
                if(!param.isEmptyOrNull("end_date")) {
                wheres.and("adjustment_date", ACFdSQLRule.RuleCondition.LT, new Timestamp(param.get("end_date", Long.class) + 24*60*60*1000));
                }
                //wheres.and("po_date", ACFdSQLRule.RuleCondition.LT, param.get("po_date_e", Timestamp.class));
            
            orders.put("adjustment_date", false);
            return this;
        }
    }
    Search search = new Search();

    
    Comparator<ARCmItemInventory> cii = new Comparator<ARCmItemInventory>(){
		
		@Override
		public int compare(ARCmItemInventory i,ARCmItemInventory j) {
			
			return i.purchase_order_no.compareTo(j.purchase_order_no);
		}};
    
    @RequestMapping(value=APWtMapping.APWF006_MAIN, method=RequestMethod.GET)
    public String main(ModelMap model) throws Exception {
        model.addAttribute("section_id", section_id);
        model.addAttribute("other_material", other_material); //set row keys
        model.addAttribute("itemnoselect", ItemMasterService.getItem_No_for_WP()); //acf's function, get data from ACFDB
        model.addAttribute("locationcode", LocationService.getLocationCode());
        model.addAttribute("WPItemno", ItemMasterService.getItem_No_for_WP());
        
        //model.addAttribute("unit_cost", unit_cost);
        //initial value in function maintenance form
        //model.addAttribute("modules", moduleService.getAllModuleValuePairs()); //acf's function, get data from ACFDB
        //System.out.println(moduleService.getAllModuleValuePairs());
        //search value groups in search form and main form
        //model.addAttribute("moduleGroups", funcGpService.getModuleFuncGpIndex()); // no need to group tables just now

        return view();
        
    }
    @RequestMapping(value=APWtMapping.APWF006_SEARCH_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getGrid(@RequestBody ACFgRequestParameters param) throws Exception {
      //The method getGrid responds to AJAX by obtain the Search JSON result and put in variable “grid_browse”.
        // ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
        search.setConnection(getConnection("ARCDB")); //get connection to the database
        search.setValues(param);
        search.setFocus(item_no, adjustment_date); //set two keys
        System.out.println(param);
       // System.out.println(search.getGridResult());
        return new ACFgResponseParameters().set("grid_browse", search.getGridResult()); // can only be called once, otherwise reset parameter
    }

    @RequestMapping(value=APWtMapping.APWF006_GET_FORM_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getForm(@RequestBody ACFgRequestParameters param) throws Exception {
        adjustment_date = param.get("adjustment_date", Timestamp.class); //pick the value of parameter “func_id” from client
        item_no = param.get("item_no", String.class);  //set two keys!!
        //unit_cost = param.get("unit_cost", BigDecimal.class);
        //retrieves the result by DAO, and put in the variable “frm_main”. 
        //ACF will forward the content to client and post to the form which ID equals to “frm_main”
        return new ACFgResponseParameters().set("frm_main", ItemAdjustmentHistoryDao.selectItem(item_no, adjustment_date)); //change dao here //set two keys!!
    }
    
    
    public static int get_remaining(ARCmItemInventory Inv)
    {
    	
    	int remaining = Inv.received_quantity.intValue() + Inv.adjusted_quantity.intValue() - Inv.consumed_quantity.intValue();
    	return remaining;
    }
    
    public static int get_remaining(List<ARCmItemInventory> InvL)
    {
    	int remaining = 0;
    	for (ARCmItemInventory Inv: InvL){
    	remaining += Inv.received_quantity.intValue() + Inv.adjusted_quantity.intValue() - Inv.consumed_quantity.intValue();
    	}
    	return remaining;
    }
    
    
    public void update_bothpositive(ARCmItemAdjustmentHistory oldItem, ARCmItemAdjustmentHistory newItem) throws Exception
    {
    	if ((oldItem.adjust_quantity.intValue() * newItem.adjust_quantity.intValue() >= 0) && (oldItem.adjust_quantity.intValue() + newItem.adjust_quantity.intValue() > 0))
    	{
    		System.out.println("-----------entering both positive  ----------------------------");
    	//define adjusting quantity
    	int q = newItem.adjust_quantity.intValue() - oldItem.adjust_quantity.intValue();
    	
    	//get record list
    	List<ARCmItemInventory> Invitems = ItemInventoryDao.selectItems(newItem.item_no);
    	
    	//get latest record
    	ARCmItemInventory maxinv = Collections.max(Invitems,cii);
    	
    	//modify latest record's adjusted quantity, since it it positive, no remaining quantity conditions to be matched
    	//it will only increase the storage, no short of deduction problems
		maxinv.adjusted_quantity = new BigDecimal(maxinv.adjusted_quantity.intValue() + q);
		if (get_remaining(Invitems)<0)
		{
			System.out.println("-----------entering both positive error----------------------------");
			
			//not enough remaining quantity of current item for such adjustment
			throw exceptionService.error("APW105E"); // to be modified
		}
		//update to table
		ItemInventoryDao.updateItem(maxinv);
		return;
    	}
    	
    }
    
    public void update_bothnegative(ARCmItemAdjustmentHistory oldItem, ARCmItemAdjustmentHistory newItem) throws Exception
    {
    	if ((oldItem.adjust_quantity.intValue() * newItem.adjust_quantity.intValue() >= 0) && (oldItem.adjust_quantity.intValue() + newItem.adjust_quantity.intValue() < 0))
    	{
    		System.out.println("-----------entering both negative----------------------------");
    	//define adjusting quantity
    	int q = newItem.adjust_quantity.intValue() - oldItem.adjust_quantity.intValue();
    	
    	//get record list
    	List<ARCmItemInventory> Invitems = ItemInventoryDao.selectItems(newItem.item_no);
    	
    	//get earliest record
    	//ARCmItemInventory mininv = Collections.min(Invitems,cii);
    	
    	if (q >= 0)// if the modification is to add adjustment, no remaining quantity matching problem
    	{
    	ARCmItemInventory mininv = Collections.min(Invitems,cii);
    	mininv.adjusted_quantity = new BigDecimal(mininv.adjusted_quantity.intValue() + q);
    	ItemInventoryDao.updateItem(mininv);
    	}
    	
    	if (q<0)
    	{
    		int cq = -q;
    		System.out.println("-----------entering both negative testing cq----------------------------"  + cq);
    		
    		if (cq > get_remaining(Invitems))
    		{
    			//not enough remaining quantity of current item for such adjustment
    			throw exceptionService.error("APW105E"); // to be modified
    		}
    		
    		do
    		{	
    			//remove processed inventory record (remaining quantity is equal to zero)
    			for (Iterator<ARCmItemInventory> iterator = Invitems.iterator(); iterator.hasNext(); ) {
            		ARCmItemInventory value = iterator.next();
            		System.out.println("---------------testing quantity inside do while loop-------------" + value.received_quantity.intValue());
            	    if (get_remaining(value) == 0) { //this should be remaining quantity
            	        iterator.remove();
            	    }
            	}
    			
    			//get the record with minimum PO in the list
    			ARCmItemInventory mininv = Collections.min(Invitems,cii);
    			
    			//if consuming less than remaining
    			
    			if(cq < get_remaining(mininv))
    			{
    				System.out.println("*************entering both negative testing cq < get_remaining******************"  + cq + "------remaining----- " + get_remaining(mininv));
    			mininv.adjusted_quantity = new BigDecimal(mininv.adjusted_quantity.intValue() -cq); //adjustment equals modified adjustment
    			cq = 0;
    			ItemInventoryDao.updateItem(mininv);
    			}
    			
    			
    			if(cq >= get_remaining(mininv))
    			{
    				System.out.println("-----------entering both negative testing cq >= get_remaining----------------------------"  + cq + "------remaining----- " + get_remaining(mininv));
        			
    			cq = cq - get_remaining(mininv);
    			
    			mininv.adjusted_quantity = new BigDecimal(mininv.adjusted_quantity.intValue() - get_remaining(mininv)); //set remaining to zero
    			//cq = cq - mininv.adjusted_quantity.intValue();
    			ItemInventoryDao.updateItem(mininv);
    			//update inventory record in loop
    			
    			}
    			
    			
    			System.out.println("---------------testing cq inside do while loop-------------" + cq); 
    		}
    		while (cq!=0);
    		
    	}
    	return;
    	}
    }
    
    
	public static List<ARCmItemInventory> filter( List<ARCmItemInventory> ItemInv)
    
    {
    	ArrayList<ARCmItemInventory> filtered = new ArrayList<ARCmItemInventory>();
    	for (ARCmItemInventory elem: ItemInv)
    	{
    		if (get_remaining(elem) != 0)
    		{filtered.add(elem);}
    	}
    	return filtered;
    }
    
    
    public void update_error(ARCmItemAdjustmentHistory oldItem, ARCmItemAdjustmentHistory newItem) throws Exception
    {
    	if (oldItem.adjust_quantity.intValue() * newItem.adjust_quantity.intValue() < 0)
    	{
    	throw exceptionService.error("APW205E"); //Adjustment amount cannot be modified for negative to positive or vice versa
    	}
    	return;
    }
    
    @ACFgTransaction
    @RequestMapping(value=APWtMapping.APWF006_SAVE_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters save(@RequestBody ACFgRequestParameters param) throws Exception { //function in the upper right "save" button
      //the controller obtains the changes of form data 
        List<ARCmItemAdjustmentHistory> amendments = param.getList("form", ARCmItemAdjustmentHistory.class);
        //and call DAO to save the changes
        ARCmItemAdjustmentHistory lastItem = ItemAdjustmentHistoryDao.saveItems(amendments, new ACFiSQLAssWriteInterface<ARCmItemAdjustmentHistory>(){
            
            
            //interface for the related functions
            @Override
            public boolean insert(ARCmItemAdjustmentHistory newItem, ACFdSQLAssInsert ass) throws Exception {
                //ass.columns.put("allow_print", 1); //without the allow_print column, the whole sql won't work
            	List<ARCmItemInventory> Invitems = ItemInventoryDao.selectItems(newItem.item_no);
            	
            	
//            	for (Iterator<ARCmItemInventory> iterator = Invitems.iterator(); iterator.hasNext(); ) {
//            		ARCmItemInventory value = iterator.next();
//            		System.out.println("---------------testing quantity  1-------------" + value.received_quantity.intValue());
//            	    if (value.received_quantity.intValue() == 0) {
//            	        iterator.remove();
//            	    }
//            	}
            	
            	
				
					
            	if (newItem.adjust_quantity.intValue() >= 0)
            	{
            		ARCmItemInventory maxinv = Collections.max(Invitems,cii);
            		maxinv.adjusted_quantity = new BigDecimal(maxinv.adjusted_quantity.intValue() + newItem.adjust_quantity.intValue());
            		ItemInventoryDao.updateItem(maxinv);
            	}
            	if (newItem.adjust_quantity.intValue() < 0)
            	{
            		
            		int cq = -(newItem.adjust_quantity.intValue());
            		if (cq > get_remaining(Invitems))
            		{
            			//adjustment is consuming more than all remaining goods in inventory
            			throw exceptionService.error("APW105E");
            		}
//            		Collections.sort(Invitems,cii);
            		//Collections.reverse(Invitems);
            		//ARCmItemInventory mininv = Collections.min(Invitems,cii);
            		
            		
            		
            		
            		do
            		{	
 
            			
            			Invitems = filter(Invitems);// filter those remaining = 0 records
            			
            			
            			//get the record with minimum PO in the list
            			ARCmItemInventory mininv = Collections.min(Invitems,cii);
            			
            			if(cq <=get_remaining(mininv))
            			{
            			mininv.adjusted_quantity = new BigDecimal(mininv.adjusted_quantity.intValue() - cq);
            			cq = 0;
            			ItemInventoryDao.updateItem(mininv);
            			}
            			
            			if(cq > get_remaining(mininv))
            			{
            			//mininv.adjusted_quantity = new BigDecimal(0);
            			cq = cq - get_remaining(mininv);
                			
                		mininv.adjusted_quantity = new BigDecimal(mininv.adjusted_quantity.intValue() - get_remaining(mininv)); //set remaining to zero
                			
                			
            			//update inventory record in loop
            			ItemInventoryDao.updateItem(mininv);
            			}
            		}
            		while (cq!=0);
            		//ItemInventoryDao.updateItem(mininv);
            	}
                return false;
            }

            @Override
            public boolean update(ARCmItemAdjustmentHistory oldItem, ARCmItemAdjustmentHistory newItem, ACFdSQLAssUpdate ass) throws Exception {
            	

					update_bothpositive(oldItem,newItem);
					update_bothnegative(oldItem,newItem);
					update_error(oldItem,newItem);
					
                return false;
            }

            @Override
            public boolean delete(ARCmItemAdjustmentHistory oldItem, ACFdSQLAssDelete ass) throws Exception {
                return false;
            }
        });
        item_no = lastItem!=null? lastItem.item_no: null;// what's the purpose of this?
        adjustment_date = lastItem!=null? lastItem.adjustment_date: null;
        //unit_cost = lastItem!=null? lastItem.unit_cost: null;

        return new ACFgResponseParameters();
    }

}