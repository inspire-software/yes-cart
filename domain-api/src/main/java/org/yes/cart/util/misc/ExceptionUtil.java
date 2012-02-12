package org.yes.cart.util.misc;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2/12/12
 * Time: 11:30 AM
 */
public class ExceptionUtil {


    public static String toString(final Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }


}
