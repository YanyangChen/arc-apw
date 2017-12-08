package arc.apw.Controller;

import java.math.BigDecimal;
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
//import acf.acf.Controller.ACFc012.Search;
import acf.acf.Dao.ACFoFunction;
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
import acf.acf.Model.ACFmFunction;
import acf.acf.Service.ACFsFuncGp;
import acf.acf.Service.ACFsModule;
import acf.acf.Static.ACFtMapping;
import arc.apf.Dao.APFoSection;
import arc.apf.Dao.ARCoOtherMaterials;
import arc.apf.Dao.ARCoSection;
import arc.apf.Model.ARCmOtherMaterials;
import arc.apf.Model.ARCmSection;
//import arc.apf.Service.APFsFuncGp;
import arc.apf.Service.APFsModule;
import arc.apf.Service.ARCsModel;
import arc.apw.Static.APWtMapping;

@Controller
@Scope("session")
@ACFgFunction(id="APWF002")
@RequestMapping(value=APWtMapping.APWF002)
public class APWc002 extends ACFaAppController {
    
    @Autowired ARCsModel moduleService;
    //@Autowired ACFoFunction functionDao;
    @Autowired ARCoOtherMaterials othermaterialsDao; //modify according to the table
    //@Autowired APFsFuncGp funcGpService; //click the object and click import
    @ACFgAuditKey String section_id;
    @ACFgAuditKey String other_material;
    @ACFgAuditKey BigDecimal unit_cost;
    
  //  Search search = new Search();

    private class Search extends ACFgSearch {
        public Search() {
            super();
            //setModel(ARCmOtherMaterials.class); //define a Search which accept 4 filters from client
            setCustomSQL("select * from (select * " +
                    "from arc_other_materials " +
                    "where section_id = '03')");
            
            setKey("other_material"); //order asc by this key
            addRule(new ACFdSQLRule("other_material", RuleCondition._LIKE_, null, RuleCase.Insensitive));
        }// ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
    }
    Search search = new Search();

    
	@RequestMapping(value=APWtMapping.APWF002_MAIN, method=RequestMethod.GET)
	public String main(ModelMap model) throws Exception {
	    model.addAttribute("section_id", section_id);
	    model.addAttribute("other_material", other_material); //set row keys
	    //model.addAttribute("unit_cost", unit_cost);
	    //initial value in function maintenance form
        //model.addAttribute("modules", moduleService.getAllModuleValuePairs()); //acf's function, get data from ACFDB
        //System.out.println(moduleService.getAllModuleValuePairs());
        //search value groups in search form and main form
        //model.addAttribute("moduleGroups", funcGpService.getModuleFuncGpIndex()); // no need to group tables just now

        return view();
		
	}
	@RequestMapping(value=APWtMapping.APWF002_SEARCH_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getGrid(@RequestBody ACFgRequestParameters param) throws Exception {
      //The method getGrid responds to AJAX by obtain the Search JSON result and put in variable “grid_browse”.
        // ACF will forward the content to client and post to the grid which ID equals to “grid_browse”.
        search.setConnection(getConnection("ARCDB")); //get connection to the database
        search.setValues(param);
        search.setFocus(section_id, other_material, unit_cost); //set two keys
        System.out.println(param);
       // System.out.println(search.getGridResult());
        return new ACFgResponseParameters().set("grid_browse", search.getGridResult()); // can only be called once, otherwise reset parameter
    }

    @RequestMapping(value=APWtMapping.APWF002_GET_FORM_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters getForm(@RequestBody ACFgRequestParameters param) throws Exception {
        section_id = param.get("section_id", String.class); //pick the value of parameter “func_id” from client
        other_material = param.get("other_material", String.class);  //set two keys!!
        //unit_cost = param.get("unit_cost", BigDecimal.class);
        //retrieves the result by DAO, and put in the variable “frm_main”. 
        //ACF will forward the content to client and post to the form which ID equals to “frm_main”
        return new ACFgResponseParameters().set("frm_main", othermaterialsDao.selectItem(section_id, other_material)); //change dao here //set two keys!!
    }

    @ACFgTransaction
    @RequestMapping(value=APWtMapping.APWF002_SAVE_AJAX, method=RequestMethod.POST)
    @ResponseBody
    public ACFgResponseParameters save(@RequestBody ACFgRequestParameters param) throws Exception { //function in the upper right "save" button
      //the controller obtains the changes of form data 
        List<ARCmOtherMaterials> amendments = param.getList("form", ARCmOtherMaterials.class);
        //and call DAO to save the changes
        ARCmOtherMaterials lastItem = othermaterialsDao.saveItems(amendments, new ACFiSQLAssWriteInterface<ARCmOtherMaterials>(){
            
            
            //interface for the related functions
            @Override
            public boolean insert(ARCmOtherMaterials newItem, ACFdSQLAssInsert ass) throws Exception {
                //ass.columns.put("allow_print", 1); //without the allow_print column, the whole sql won't work
                return false;
            }

            @Override
            public boolean update(ARCmOtherMaterials oldItem, ARCmOtherMaterials newItem, ACFdSQLAssUpdate ass) throws Exception {
                return false;
            }

            @Override
            public boolean delete(ARCmOtherMaterials oldItem, ACFdSQLAssDelete ass) throws Exception {
                return false;
            }
        });
        section_id = lastItem!=null? lastItem.section_id: null;// what's the purpose of this?
        other_material = lastItem!=null? lastItem.other_material: null;
        //unit_cost = lastItem!=null? lastItem.unit_cost: null;

        return new ACFgResponseParameters();
    }

}