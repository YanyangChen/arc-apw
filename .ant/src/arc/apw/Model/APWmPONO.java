package arc.apw.Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

import acf.acf.Abstract.ACFaAppModel;
import acf.acf.General.annotation.ACFgTable;
import acf.acf.Model.ACFmUser;

@ACFgTable(name = "apw_po")
public class APWmPONO extends ACFaAppModel {

    public APWmPONO() throws Exception {
        super();
    }

    @Id // don't forget to set up primary key
    @Column(name = "po_no")
    public String po_no;

    @Column(name = "po_date")
    public Timestamp po_date;

    @Column(name = "receive_location")
    public String receive_location;

    @Column(name = "dept_ref")
    public String dept_ref;

   // @Column(name = "manhour_rate")
   // public String manhour_rate; 
    
    @Column(name = "supplier_code")
    public String supplier_code; 
    
   // @Column(name = "supplier_desc")
  //  public String supplier_desc; 
    
    @Column(name = "remarks")
    public String remarks; 
    

    @Column(name = "cancel_ind")
    public String cancel_ind; 
    

    @Column(name = "cancel_by")
    public String cancel_by; 
    
    @Column(name = "cancel_date")
    public Timestamp cancel_date; 
    

    @Column(name = "printed_by")
    public String printed_by; 
    
    @Column(name = "last_printed_date")
    public Timestamp last_printed_date; 
    
    @Column(name = "no_of_prints")
    public BigDecimal no_of_prints; 
    
}
