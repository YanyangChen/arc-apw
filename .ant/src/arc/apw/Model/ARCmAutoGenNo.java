package arc.apw.Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

import acf.acf.Abstract.ACFaAppModel;
import acf.acf.General.annotation.ACFgTable;

@ACFgTable(name = "arc_auto_gen_no")
public class ARCmAutoGenNo extends ACFaAppModel {

    public ARCmAutoGenNo() throws Exception {
        super();
    }


    @Id
    @Column(name = "form_id")
    public String form_id;
    
    @Column(name = "description")
    public String description;
    
    @Id
    @Column(name = "system_year")
    public BigDecimal system_year;
    
    @Id
    @Column(name = "system_month")
    public BigDecimal system_month;
    
    @Column(name = "three_digit_serial_no")
    public BigDecimal three_digit_serial_no;

    
    @Column(name = "four_digit_serial_no")
    public BigDecimal four_digit_serial_no;
    
    
    @Column(name = "five_digit_serial_no")
    public BigDecimal five_digit_serial_no;
    
    
    @Column(name = "six_digit_serial_no")
    public BigDecimal six_digit_serial_no;
    
    
    @Column(name = "seven_digit_serial_no")
    public BigDecimal seven_digit_serial_no;


    @Column(name = "eight_digit_serial_no")
    public BigDecimal eight_digit_serial_no;
    
}