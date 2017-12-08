package arc.apw.Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

import acf.acf.Abstract.ACFaAppModel;
import acf.acf.General.annotation.ACFgTable;

@ACFgTable(name = "apf_section_lab")
public class APWmLabour extends ACFaAppModel {

    public APWmLabour() throws Exception {
        super();
    }

    @Id// don't forget to set up primary key
    @Column(name = "labour_type")
    public String labour_type;

    @Column(name = "section_no")
    public String section_no;

    @Column(name = "section_desc")
    public String section_desc;

    @Column(name = "labour_type_desc")
    public String labour_type_desc;

   // @Column(name = "manhour_rate")
   // public String manhour_rate; 
    
    @Column(name = "manhour_rate")
    public BigDecimal manhour_rate; 
    
    @Column(name = "effective_date")
    public Timestamp effective_date; 
    
    @Column(name = "effective_date_to")
    public Timestamp effective_date_to; 
    

    
}
