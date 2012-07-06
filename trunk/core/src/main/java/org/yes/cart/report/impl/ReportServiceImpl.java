package org.yes.cart.report.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.report.ReportService;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import java.io.*;
import java.util.List;

//JAXP
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

//FOP
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/2/12
 * Time: 2:46 PM
 */
public class ReportServiceImpl implements ReportService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportServiceImpl.class);


    private final GenericDAO<Object, Long> genericDAO;

    private final List<ReportDescriptor> reportDescriptors;
    
    private final String reportFolder;


    /**
     * Consstruct report service.
     * @param genericDAO report service
     * @param reportDescriptors list of configured reports.
     * @param reportFolder report folder
     */
    public ReportServiceImpl(GenericDAO<Object, Long> genericDAO, final List<ReportDescriptor> reportDescriptors, final String reportFolder) {
        this.genericDAO = genericDAO;
        this.reportDescriptors = reportDescriptors;

        if (StringUtils.isNotBlank(reportFolder)) {
            this.reportFolder = "WEB-INF" + File.separator + reportFolder + File.separator;
        } else {
            this.reportFolder = StringUtils.EMPTY;
        }

    }

    /**
     * Get the list of report descriptors.
     * @return
     */
    public List<ReportDescriptor> getReportDescriptors() {
        return reportDescriptors;
    }

    ReportDescriptor getReportDescriptorbyId(final String reportId) {

        Assert.notNull(reportId, "ReportId must be not null");

        return (ReportDescriptor) CollectionUtils.find(reportDescriptors, new Predicate() {
            public boolean evaluate(final Object o) {
                return reportId.equalsIgnoreCase(((ReportDescriptor) o).getReportId());
            }
        });

    }



    /**
     *
     * Download report.
     *
     * @param reportId report descriptor.
     * @param params report parameter values to pass it into hsql query.   Consequence of parameter must correspond to parameters in repoport description.
     * @param lang given lang to roduce report.
     * @return true in case if report was generated successfuly.
     * @
     */
    public byte[] downloadReport(String lang, String reportId, Object... params) throws Exception {
        final File tmpFile = File.createTempFile("yescartreport", "pdf");
        if (createReport(lang, reportId, tmpFile.getAbsolutePath(), params)) {
            return FileUtils.readFileToByteArray(tmpFile);
        } else {
            throw new Exception("Report can not be created , see server logs for more details. Sorry");
        }
    }



    /**
     * 
     * Run report by his id.
     * 
     * @param reportId report descriptor.
     * @param fileName report filename
     * @param params report parameter values to pass it into hsql query.   Consequence of parameter must correspond to parameters in repoport description.
     * @param lang given lang to roduce report.
     * @return true in case if report was generated successfuly.
     */
    public boolean createReport(final String lang, final String reportId, final String fileName, final Object... params) throws Exception {
        
        final ReportDescriptor reportDescriptor = getReportDescriptorbyId(reportId);

        final List<Object> rez = getQueryResult(reportDescriptor.getHsqlQuery(),  params);

        final String xmlFilename = getXml(rez);

        if (xmlFilename != null) {

            final File xmlfile = new File(xmlFilename);

            final File xsltfile = new File(reportFolder + reportDescriptor.getLangXslfo(lang));

            final File pdffile = new File(fileName);

            // configure fopFactory as desired
            final FopFactory fopFactory = FopFactory.newInstance();

            final FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired

            // Setup output
            OutputStream out = new java.io.FileOutputStream(pdffile);

            out = new java.io.BufferedOutputStream(out);

            try {
                // Construct fop with desired output format
                final Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

                // Setup XSLT
                final TransformerFactory factory = TransformerFactory.newInstance();
                final Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));

                // Set the value of a <param> in the stylesheet
                transformer.setParameter("versionParam", "2.0");

                // Setup input for XSLT transformation
                final Source src = new StreamSource(xmlfile);

                // Resulting SAX events (the generated FO) must be piped through to FOP
                final Result res = new SAXResult(fop.getDefaultHandler());

                // Start XSLT transformation and FOP processing
                transformer.transform(src, res);

            } catch (Exception ex) {

                LOG.error("Cannot create pdf", ex);

                return false;
                
            } finally {
                
                out.close();

                xmlfile.delete();
                
            }


            return true;
            
        }

        return false;

    }

    /**
     * Write into given file name xml rezult.
     * @param rez list of objects.
     * @return  tmp xml file name
     */
    String getXml(final List<Object> rez) {
        
        String fileName;
        
        ObjectOutputStream os = null;

        try {


            fileName = File.createTempFile("yes", "cart").getAbsolutePath();

            os = ReportObjectStreamFactory.getObjectOutputStream(fileName);

            for (Object obj : rez) {
                os.writeObject(obj);
            }



        } catch (Exception e) {
            fileName = null;
            LOG.error("Cannot create xml " , e);

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LOG.error("Cannot close file" , e);
                }
            }

        }
        
        return fileName;
    }

    /**
     * Get query result as object list.
     * @param query hsql query
     * @param params parameters.
     * @return list of objects.
     */
    List<Object> getQueryResult(final String query, final Object ... params) {

        return genericDAO.findByQuery(
                query,
                params
        );

    }
    
}
