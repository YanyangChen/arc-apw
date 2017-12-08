package arc.apw.Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

import acf.acf.Abstract.ACFaAppModel;
import acf.acf.General.annotation.ACFgTable;

@ACFgTable(name = "apw_po_item")
public class APWmSupplier extends ACFaAppModel {

    public APWmSupplier() throws Exception {
        super();
    }


    @Id
    @Column(name = "suplier_code")
    public String suplier_code;
    
    @Column(name = "supplier_name")
    public String supplier_name;
}