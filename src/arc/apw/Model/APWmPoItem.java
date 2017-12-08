package arc.apw.Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

import acf.acf.Abstract.ACFaAppModel;
import acf.acf.General.annotation.ACFgTable;

@ACFgTable(name = "apw_po_item")
public class APWmPoItem extends ACFaAppModel {

    public APWmPoItem() throws Exception {
        super();
    }

    @Id // don't forget to set up primary key
    @Column(name = "po_no")
    public String po_no;

    @Id
    @Column(name = "item_no")
    public String item_no;
    
    @Column(name = "order_qty")
    public BigDecimal order_qty;
    

    @Column(name = "unit_cost")
    public BigDecimal unit_cost;
    

    @Column(name = "ttl_cost")
    public BigDecimal ttl_cost;
}