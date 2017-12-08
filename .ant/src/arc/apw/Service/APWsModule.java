package arc.apw.Service;
//package acf.acf.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acf.acf.Abstract.ACFaAppService;
import acf.acf.Abstract.ACFaSQLAss;
import acf.acf.Dao.ACFoModule;
import acf.acf.Dao.ACFoModuleOwner;
import acf.acf.Database.ACFdSQLAssDelete;
import acf.acf.Database.ACFdSQLAssInsert;
import acf.acf.Database.ACFdSQLAssSelect;
import acf.acf.Database.ACFdSQLAssUpdate;
import acf.acf.General.core.ACFgRawModel;
import acf.acf.Interface.ACFiCallback;
import acf.acf.Interface.ACFiSQLAssInterface;
import acf.acf.Interface.ACFiSQLAssWriteInterface;
import acf.acf.Model.ACFmModule;
import acf.acf.Static.ACFtDBUtility;
import acf.acf.Static.ACFtUtility;

@Service
public class APWsModule extends ACFaAppService {

    @Autowired ACFoModule moduleDao;
    @Autowired ACFoModuleOwner moduleOwnerDao;

    public APWsModule() throws Exception {
        super();
    }

    public List<ACFgRawModel> getAllModuleValuePairs() throws Exception {
        /*
        List<ACFmModule> modules = moduleDao.selectCachedItems();
        Collections.sort(modules);

        List<ACFgRawModel> results = new LinkedList<ACFgRawModel>();
        for(ACFmModule item: modules) {
            ACFgRawModel m = new ACFgRawModel();
            m.set("id", item.mod_id).set("text", item.mod_id + "-" + item.mod_name);
            results.add(m);
        }
        return results;
        */

        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
        ass.setConnection(ACFtDBUtility.getConnection("ARCDB"));
        //ass.setCustomSQL("select mod_id as id, mod_id || ' - ' || mod_name as text from acf_module order by mod_seq");
        ass.setCustomSQL("select section_no as id, section_no || ' - ' || DDS as text from apf_section order by section_no");
        return ass.executeQuery();

    }
    public List<ACFgRawModel> getlocationPairs() throws Exception {
        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
        ass.setConnection(ACFtDBUtility.getConnection("ARCDB"));
        //ass.setCustomSQL("select mod_id as id, mod_id || ' - ' || mod_name as text from acf_module order by mod_seq");
        ass.setCustomSQL("select location_code as id, location_code as text from apf_location order by location_code");
        return ass.executeQuery();
        
    }
    
    public List<ACFgRawModel> getLabour() throws Exception {
        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
        ass.setConnection(ACFtDBUtility.getConnection("ARCDB"));
        //ass.setCustomSQL("select mod_id as id, mod_id || ' - ' || mod_name as text from acf_module order by mod_seq");
        ass.setCustomSQL("select labour_type as id, labour_type as text from apf_section_lab order by labour_type");
        return ass.executeQuery();
        
    }
    
    public List<ACFgRawModel> getSection() throws Exception {
        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
        ass.setConnection(ACFtDBUtility.getConnection("ARCDB"));
        //ass.setCustomSQL("select mod_id as id, mod_id || ' - ' || mod_name as text from acf_module order by mod_seq");
        ass.setCustomSQL("select distinct section_no as id, section_no as text from apf_section_lab");
        return ass.executeQuery();
        
    }
    
    public List<ACFgRawModel> getSupplierCode() throws Exception {
        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
        ass.setConnection(ACFtDBUtility.getConnection("ARCDB"));
        //ass.setCustomSQL("select mod_id as id, mod_id || ' - ' || mod_name as text from acf_module order by mod_seq");
        ass.setCustomSQL("select distinct supplier_code as id, supplier_code as text from apw_po");
        return ass.executeQuery();
        
    }
    
    public List<ACFgRawModel> getSupplierCodefromitem() throws Exception {
        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
        ass.setConnection(ACFtDBUtility.getConnection("ARCDB"));
        //ass.setCustomSQL("select mod_id as id, mod_id || ' - ' || mod_name as text from acf_module order by mod_seq");
        ass.setCustomSQL("select distinct supplier_code as id, supplier_code as text from dev.apw_supplier");
        return ass.executeQuery();
        
    }
    
    public List<ACFgRawModel> getItem_No() throws Exception {
        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
        ass.setConnection(ACFtDBUtility.getConnection("ARCDB"));
        //ass.setCustomSQL("select mod_id as id, mod_id || ' - ' || mod_name as text from acf_module order by mod_seq");
        ass.setCustomSQL("select distinct item_no as id, item_no as text from dev.apw_item");
        return ass.executeQuery();
        
    }
    
    public String getPoSupplierGpIndex() throws Exception {
        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
        ass.setCustomSQL("select po.supplier_code, sp.supplier_desc, po.po_no, po.po_date " +
                "from apw_po po, apw_supplier sp" +
                "where po.supplier_code = sp.supplier_code" +
                "order by po.supplier_code, sp.supplier_name");
        return ACFtUtility.toJson(ass.getRows(ACFtDBUtility.getConnection("ARCDB")));
    }
    
    
    public ACFmModule getItemByModule(final String mod_id) throws Exception {
        return moduleDao.selectItem(new ACFiSQLAssInterface() {
            @Override
            public void customize(ACFaSQLAss ass) throws Exception {
                ass.setCustomSQL("select m.*, o.mod_owner"
                        + " from acf_module m left outer join (select mod_id, LISTAGG(user_id,chr(9)) as mod_owner from acf_module_owner group by mod_id) o"
                        + "   on m.mod_id = o.mod_id"
                        + " where m.mod_id = '%s'"
                        , mod_id);
            }
        });
    }
    
    public  String getSupplierNameBySupplierNo(String supplier_code) throws Exception
    {
        ACFdSQLAssSelect ass = new ACFdSQLAssSelect();
       /* ass.setCustomSQL("SELECT listagg(staff_name,chr(9)) as staff_name from ( " +
                         "  SELECT v.*, case when chi_name <> '' then chi_name else eng_name end as staff_name FROM APM_LOCAL_TEMP_PROG_PROD_MEMBER_VIEW v " +
                         ") " +
                         "WHERE prog_no = '%s' " +
                         "AND member_type = '%s' "
                         ,prog_no, staff_type);*/
        ass.setCustomSQL(
                "SELECT sp.supplier_desc from dev.apw_supplier sp "+
                "WHERE sp.supplier_code = '%s'"
                ,supplier_code);
        
        List<ACFgRawModel> result = ass.executeQuery(ACFtDBUtility.getConnection("ARCDB"));
       // String result = ass.executeQuery(ACFtDBUtility.getConnection("ARCDB"));
        
        return result.size()>0 ? result.get(0).getString("supplier_desc") : "";
        //return result;
        
    }

    public ACFmModule saveItems(List<ACFmModule> amendments) throws Exception {
        return moduleDao.saveItems(amendments, new ACFiSQLAssWriteInterface<ACFmModule>() {

            @Override
            public boolean insert(final ACFmModule newItem, ACFdSQLAssInsert ass) throws Exception {
                ass.setAfterExecute(new ACFiCallback() {
                    @Override
                    public void callback() throws Exception {
                        moduleOwnerDao.saveItems(null, newItem);
                    }

                });
                return false;
            }

            @Override
            public boolean update(final ACFmModule oldItem, final ACFmModule newItem, ACFdSQLAssUpdate ass) throws Exception {
                moduleOwnerDao.saveItems(oldItem, newItem);
                return false;
            }

            @Override
            public boolean delete(final ACFmModule oldItem, ACFdSQLAssDelete ass) throws Exception {
                moduleOwnerDao.saveItems(oldItem, null);
                return false;
            }
        });
    }

}
