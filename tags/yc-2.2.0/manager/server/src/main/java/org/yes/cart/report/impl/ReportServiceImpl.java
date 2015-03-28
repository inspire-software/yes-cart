/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.report.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.xml.sax.SAXException;
import org.yes.cart.report.ReportService;
import org.yes.cart.report.ReportWorker;

import javax.servlet.ServletContext;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//JAXP
//FOP


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/2/12
 * Time: 2:46 PM
 */
public class ReportServiceImpl implements ReportService, ServletContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final List<ReportDescriptor> reportDescriptors;

    private final Map<String, ReportWorker> reportWorkers;

    private final String reportFolder;

    private ServletContext servletContext;

    /**
     * Construct report service.
     *
     * @param reportDescriptors list of configured reports.
     * @param reportWorkers     report workers
     * @param reportFolder      report folder
     */
    public ReportServiceImpl(final List<ReportDescriptor> reportDescriptors,
                             final Map<String, ReportWorker> reportWorkers,
                             final String reportFolder) {

        this.reportDescriptors = reportDescriptors;
        this.reportWorkers = reportWorkers;

        if (StringUtils.isNotBlank(reportFolder)) {
            this.reportFolder = "WEB-INF" + File.separator + reportFolder + File.separator;
        } else {
            this.reportFolder = StringUtils.EMPTY;
        }

    }


    /**
     * {@inheritDoc}
     */
    public List<ReportPair> getParameterValues(final String lang, final String reportId, final String param, final Map<String, Object> currentSelection) {

        if (reportWorkers.containsKey(reportId)) {
            return reportWorkers.get(reportId).getParameterValues(lang, param, currentSelection);
        }
        return Collections.emptyList();

    }

    /**
     * Get the list of report descriptors.
     *
     * @return list of reports which visible on UI.
     */
    @SuppressWarnings("unchecked")
    public List<ReportDescriptor> getReportDescriptors() {

        return (List<ReportDescriptor>) CollectionUtils.select(
                reportDescriptors,
                new Predicate() {
                    public boolean evaluate(Object object) {
                        return ((ReportDescriptor)object).isVisible();
                    }
                }

        );
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
     * {@inheritDoc}
     */
    public byte[] downloadReport(String lang, String reportId, Map<String, Object> params) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (createReport(lang, reportId, baos, params)) {
            return baos.toByteArray();
        } else {
            throw new Exception("Unable to create report");
        }
    }


    private boolean createReport(final String lang, final String reportId, final OutputStream reportStream, final Map<String, Object> currentSelection) throws Exception {

        final List<Object> rez = getQueryResult(lang, reportId, currentSelection);

        return createReport(lang, reportId, reportStream, rez);

    }

    /*
     * @param reportId report descriptor.
     * @param reportStream report filename
     * @param lang     given lang to produce report.
     * @param rez      list of object for report
     * @return true in case if report was generated
     * @throws SAXException
     * @throws IOException
     */
    private boolean createReport(String lang, String reportId, OutputStream reportStream, List<Object> rez) throws SAXException, IOException {

        final byte[] xmlfile = getXml(rez);

        if (LOG.isDebugEnabled()) {
//            System.out.println(new String(xmlfile));
            LOG.debug(new String(xmlfile));
        }

        final String xslFoFile = getReportDescriptorbyId(reportId).getLangXslfo(lang);

        if (xmlfile != null && xmlfile.length > 0) {

            final File xsltfile;

            // configure fopFactory as desired
            final FopFactory fopFactory = FopFactory.newInstance();

            final FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired


            final URL configFileUrl =
                    this.getClass().getClassLoader().getResource("fop-userconfig.xml");
            if (configFileUrl == null) {
                LOG.error("FOP config file not  found, " +
                        "please put the fop-userconfig.xml file into the classpath of the  server, UTF - 8characters won't be displayed correctly");
            } else {
                File userConfigXml = new
                        File(configFileUrl.getFile());
                fopFactory.setUserConfig(userConfigXml);
            }


            if (servletContext == null) {
                xsltfile = new File(reportFolder + xslFoFile);
            } else {
                xsltfile = new File(servletContext.getRealPath(reportFolder + xslFoFile));
                fopFactory.getFontManager().setFontBaseURL(servletContext.getRealPath("WEB-INF"));
                foUserAgent.setBaseURL("file:///" + servletContext.getRealPath("WEB-INF/report/"));
            }

            if (!xsltfile.exists()) {
                LOG.error("XSLT file does not exist: " + xsltfile.getAbsolutePath());
                return false;
            }

            // Setup output
            OutputStream out = new BufferedOutputStream(reportStream);

            try {
                // Construct fop with desired output format
                final Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

                // Setup XSLT 2.0
                final TransformerFactory factory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
                final Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));

                // Set the value of a <param> in the stylesheet
                transformer.setParameter("versionParam", "2.0");
                transformer.setOutputProperty("encoding", "UTF-8");


                // Setup input for XSLT transformation
                final Source src = new StreamSource(
                        new InputStreamReader(new ByteArrayInputStream(xmlfile), "UTF-8"));

                // Resulting SAX events (the generated FO) must be piped through to FOP
                final Result res = new SAXResult(fop.getDefaultHandler());

                // Start XSLT transformation and FOP processing
                transformer.transform(src, res);

            } catch (Exception ex) {

                LOG.error("Cannot create pdf " + ex.getMessage(), ex);

                return false;

            } finally {

                out.close();

            }


            return true;

        }

        return false;
    }

    /**
     * Write into given file name xml result.
     *
     * @param rez list of objects.
     * @return tmp xml file name
     */
    byte[] getXml(final List<Object> rez) {

        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream os = null;

        try {

            os = ReportObjectStreamFactory.getObjectOutputStream(new OutputStreamWriter(bytesOut));

            for (Object obj : rez) {
                os.writeObject(obj);
            }

        } catch (Exception e) {
            LOG.error("Cannot create xml ", e);

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LOG.error("Cannot close file", e);
                }
            }

        }

        return bytesOut.toByteArray();
    }

    /**
     * Get query result as object list.
     *
     * @param lang language
     * @param reportId reportId
     * @param currentSelection parameters.
     * @return list of objects.
     */
    List<Object> getQueryResult(final String lang, final String reportId, final Map<String, Object> currentSelection) {

        if (reportWorkers.containsKey(reportId)) {
            return reportWorkers.get(reportId).getResult(lang, currentSelection);
        }
        return Collections.emptyList();

    }

    /**
     * {@inheritDoc}
     */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
