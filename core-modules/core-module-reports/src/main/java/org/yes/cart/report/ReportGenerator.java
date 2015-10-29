package org.yes.cart.report;

import java.io.OutputStream;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 23/10/2015
 * Time: 15:15
 */
public interface ReportGenerator {

    /**
     * Generate report as bytes using given descriptor for data data object provided.
     *
     * @param descriptor report descriptor
     * @param parameters parameters
     * @param data data for the report
     * @param lang language of output
     * @param outputStream stream to write report to
     */
    void generateReport(ReportDescriptor descriptor,
                        Map<String, Object> parameters,
                        Object data,
                        String lang,
                        OutputStream outputStream);

}
