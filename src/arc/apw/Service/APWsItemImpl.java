package arc.apw.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import acf.acf.Database.ACFdSQLAssSelect;
import acf.acf.General.core.ACFgRawModel;
import acf.acf.Static.ACFtDBUtility;


@Service
public class APWsItemImpl extends APWsItem {

    public APWsItemImpl() throws Exception {
        super();
        // TODO Auto-generated constructor stub
    }
   
    public  List<ACFgRawModel> getItemUnits(String item_no) throws Exception
    {
        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
       
        ass.setCustomSQL(
                "SELECT * from arc_item_master i "+
                "WHERE i.item_no= '%s'"
                ,item_no);
        
        
       /*
        *  ass.setCustomSQL(
                "SELECT * from dev.apw_item i "+
                "WHERE i.item_no= '%1$s'" +                
                "and i.item_no= '%2$s'"
                ,id,id);
        * 
        * */ 
        List<ACFgRawModel> result = ass.executeQuery(ACFtDBUtility.getConnection("ARCDB"));
        return result;
      //  return result.size()>0 ? result.get(0).getString("supplier_desc") : "";
        
    }

}