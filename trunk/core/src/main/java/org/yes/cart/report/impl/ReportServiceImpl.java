package org.yes.cart.report.impl;

import org.apache.fop.apps.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.dao.GenericDAO;
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


    /**
     * Consstruct report service.
     * @param genericDAO report service
     */
    public ReportServiceImpl(GenericDAO<Object, Long> genericDAO) {
        this.genericDAO = genericDAO;
    }

    /**
     * Run report by his descriptor.
     * @param reportDescriptor report descriptor.
     * @param fileName report filename
     * @param params report parameters to pass it into hsql query.
     */
    public boolean getReport(final ReportDescriptor reportDescriptor, final String fileName, final Object ... params) throws Exception {

        final List<Object> rez = getQueryResult(reportDescriptor.getHsqlQuery(),  params);

        final String xmlFilename = getXml(rez);

        if (xmlFilename != null) {

            final File xmlfile = new File(xmlFilename);

            final File xsltfile = new File(reportDescriptor.getXslfo());

            File pdffile = new File(fileName);

            // configure fopFactory as desired
            FopFactory fopFactory = FopFactory.newInstance();

            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired

            // Setup output
            OutputStream out = new java.io.FileOutputStream(pdffile);

            out = new java.io.BufferedOutputStream(out);

            try {
                // Construct fop with desired output format
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

                // Setup XSLT
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));

                // Set the value of a <param> in the stylesheet
                transformer.setParameter("versionParam", "2.0");

                // Setup input for XSLT transformation
                Source src = new StreamSource(xmlfile);

                // Resulting SAX events (the generated FO) must be piped through to FOP
                Result res = new SAXResult(fop.getDefaultHandler());

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
