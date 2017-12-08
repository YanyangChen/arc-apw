package arc.apw.Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

import acf.acf.Abstract.ACFaAppModel;
import acf.acf.General.annotation.ACFgTable;

@ACFgTable(name = "apw_po_item")
public class APWmItem extends ACFaAppModel {

    public APWmItem() throws Exception {
        super();
    }


    @Id
    @Column(name = "item_no")
    public String item_no;
    
    @Column(name = "item_desc")
    public String item_desc;
    
    @Column(name = "unit")
    public String unit;
    

    @Column(name = "unit_cost")
    public String unit_cost;
}