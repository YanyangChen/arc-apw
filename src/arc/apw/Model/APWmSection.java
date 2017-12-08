package arc.apw.Model;

import javax.persistence.Column;
import javax.persistence.Id;

import acf.acf.Abstract.ACFaAppModel;
import acf.acf.General.annotation.ACFgTable;

@ACFgTable(name = "apf_section")
public class APWmSection extends ACFaAppModel {

    public APWmSection() throws Exception {
        super();
    }

    @Id
    @Column(name = "section_no")
    public String section_no;

    @Column(name = "sub_section_no")
    public String sub_section_no;

    @Column(name = "dds")
    public String dds;

    @Column(name = "section_name")
    public String section_name;

    @Column(name = "report_caption")
    public String report_caption;

    
}
