package org.yes.cart.report;

import org.yes.cart.report.impl.ReportDescriptor;

import java.util.List;

/**
 *
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/2/12
 * Time: 2:46 PM
 */
public interface ReportService {


    /**
     * Get the list of report descriptors.
     * @return configured reports
     */
    List<ReportDescriptor> getReportDescriptors();


    /**
     *
     * Run report by his id.
     *
     * @param reportId report descriptor.
     * @param fileName report filename
     * @param params report parameter values to pass it into hsql query.   Consequence of parameter must correspond to parameters in repoport description.
     * @param lang given lang to roduce report.
     * @return true in case if report was generated successfuly.
     * @throws Exception in case of errors
     */
    boolean createReport(String lang, String reportId, String fileName, Object... params) throws Exception;


    /**
     *
     * Download report.
     *
     * @param reportId report descriptor.
     * @param params report parameter values to pass it into hsql query.   Consequence of parameter must correspond to parameters in repoport description.
     * @param lang given lang to roduce report.
     * @return true in case if report was generated successfuly.
     * @throws Exception in case of errors
     */
    byte[] downloadReport(String lang, String reportId, Object... params) throws Exception;



}
