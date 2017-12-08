package arc.apw.Static;

public class APWtUtilityAndGlobal {

    /* constants used in ARC-APW Package */
    public final static String APW_IMAGES_PATH       = "/resource/img/";
    public final static String APW_IMAGES_URL        = "../img/";
    public final static String APW_TVB_LOGO_URL      = APW_IMAGES_URL + "slogo.png";
    public final static String APW_TVB_LOGO_PATH     = APW_IMAGES_PATH + "slogo.png";
    public final static String APW_END_OF_REPORT     = "*** END OF REPORT ***";
    public final static String HTML_PAGE_BREAK       = "<p style=\"page-break-after:always;\"></p>";
    public final static String HTML_TABLE_START      = "<TABLE>";
    public final static String HTML_TABLE_END        = "</TABLE>";
    public final static String HTML_TBODY_START      = "<TBODY>";
    public final static String HTML_TBODY_END        = "</TBODY>";
    public final static String HTML_ROW_START        = "<TR>";
    public final static String HTML_ROW_END          = "</TR>";
    public final static String HTML_COLUMN_START     = "<TD>";
    public final static String HTML_COLUMN_END       = "</TD>";
    public final static String HTML_SPACE            = "&nbsp;";
    
    /**
     * @see 
     * <a href='http://unicode.org/glossary/'>http://unicode.org/glossary/</a><br/>
     * <a href='http://www.java2s.com/Tutorial/Java/0120__Development/GetUTFStringSize.htm'>
     * http://www.java2s.com/Tutorial/Java/0120__Development/GetUTFStringSize.htm
     * </a>
     * @param s
     * @return
     */
    public static int getUTFStringSize(String s) {

        int len = (s == null) ? 0
                              : s.length();
        int l   = 0;

        for (int i = 0; i < len; i++) {
            int c = s.charAt(i);

            if ((c >= 0x0001) && (c <= 0x007F)) {
                l++;
            } else if (c > 0x07FF) {
                l += 3;
            } else {
                l += 2;
            }
        }

        return l;
    }

    /**
     * 
     * @param s
     * @param pad
     * @param padLength
     * @return
     */
    public static String m_Lpad(String s, String pad, int padLength){
        if(s.length() >= padLength)
            return s;
        StringBuffer buf = new StringBuffer(s);
        for(int i=0; i<padLength-s.length(); i++) {
            buf.insert(0, pad);
        }
        return buf.toString();
    }
    
    /**
     * @param target
     * @param replacement
     * @param builder
     */
    public static void m_replace(String target, String replacement, StringBuilder builder) {
        int indexOfTarget = -1;
        while ((indexOfTarget = builder.indexOf(target)) >= 0) {
            builder.replace(indexOfTarget, indexOfTarget + target.length(), replacement);
        }
    }    
}
