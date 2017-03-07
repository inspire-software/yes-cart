package org.yes.cart.report.impl;

import org.apache.fop.apps.*;
import org.apache.fop.apps.io.ResourceResolverFactory;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.apache.xmlgraphics.io.TempResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.report.ReportGenerator;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 23/10/2015
 * Time: 15:24
 */
public abstract class AbstractFopReportGenerator implements ReportGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFopReportGenerator.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateReport(final ReportDescriptor descriptor,
                               final Map<String, Object> parameters,
                               final Object data,
                               final String lang,
                               final OutputStream outputStream) {

        if (data == null || data instanceof Collection && ((Collection) data).isEmpty()) {
            LOG.debug("No data, no report will be generated");
            return;

        }

        try {

            final InputStream config = getFopUserConfigInputStream(descriptor, parameters, data, lang);
            if (config == null) {
                LOG.error("FOP config file not  found, " +
                        "please put the fop-userconfig.xml file into the classpath of the  server, UTF-8 characters won't be displayed correctly");
                return;
            }

            final URI base = getBaseReportURI(descriptor, parameters, data, lang);

            final ResourceResolver rr = ResourceResolverFactory.createTempAwareResourceResolver(
                    getTempResourceResolver(descriptor, parameters, data, lang),
                    getResourceResolver(descriptor, parameters, data, lang)
            );
            final FopFactoryBuilder confBuilder = new FopConfParser(
                    config,
                    EnvironmentalProfileFactory.createRestrictedIO(base, rr)
            ).getFopFactoryBuilder();


            final Source xsltfile = getXsltFile(descriptor, parameters, data, lang);
            if (xsltfile == null) {
                LOG.error("Unable to read XSLT-FO file for {} in {}", descriptor, lang);
                return;
            }

            // configure fopFactory as desired
            final FopFactory fopFactory = confBuilder.build();
            final FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            final Source source = convertToSource(descriptor, parameters, data, lang);

            // Construct fop with desired output format
            final String mime = getOutputMimeType(descriptor, parameters, data, lang);
            final Fop fop = fopFactory.newFop(mime, foUserAgent, outputStream);

            // Setup XSLT 2.0
            final TransformerFactory factory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
            final Transformer transformer = factory.newTransformer(xsltfile);

            // Set the value of a <param> in the stylesheet
            transformer.setParameter("versionParam", "2.0");
            transformer.setOutputProperty("encoding", "UTF-8");


            // Resulting SAX events (the generated FO) must be piped through to FOP
            final Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(source, res);


        } catch (Exception exp) {
            LOG.error("Unable to generate report for " + descriptor + " in " + lang, exp);
        }

    }

    /**
     * Create resource resolver.
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return resource resolver
     */
    protected abstract TempResourceResolver getTempResourceResolver(final ReportDescriptor descriptor,
                                                                    final Map<String, Object> parameters,
                                                                    final Object data,
                                                                    final String lang);

    /**
     * Create resource resolver.
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return resource resolver
     */
    protected abstract ResourceResolver getResourceResolver(final ReportDescriptor descriptor,
                                                            final Map<String, Object> parameters,
                                                            final Object data,
                                                            final String lang);

    /**
     * Read FOP config file.
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return fop config as input stream
     */
    protected abstract InputStream getFopUserConfigInputStream(final ReportDescriptor descriptor,
                                                               final Map<String, Object> parameters,
                                                               final Object data,
                                                               final String lang);

    /**
     * Get required MIME type for report.
     *
     * Uses parameters['MIME'], defaults to {@link MimeConstants#MIME_PDF} is not provided
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return mime type
     */
    protected String getOutputMimeType(final ReportDescriptor descriptor,
                                       final Map<String, Object> parameters,
                                       final Object data,
                                       final String lang) {
        if (parameters.containsKey("MIME")) {
            return String.valueOf(parameters.get("MIME"));
        }
        return MimeConstants.MIME_PDF;
    }


    /**
     * Get base URI that will be used to resolve relative paths in XSLT including fonts.
     *
     * Default implementation provides blank string to allow resource resolvers correctly
     * provide relative paths.
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return URI
     */
    protected URI getBaseReportURI(final ReportDescriptor descriptor,
                                   final Map<String, Object> parameters,
                                   final Object data,
                                   final String lang) {
        try {
            return new URI("");
        } catch (URISyntaxException e) {
            LOG.error("Unable to set blank URI for reports root", e);
            return null;
        }
    }

    /**
     * Data to converts to source object to feed to XSL-FO template.
     *
     * E.g.
     * <pre>
     *   final Source src = new StreamSource(
     *             new InputStreamReader(new ByteArrayInputStream(xmlfile), "UTF-8"));
     * </pre>
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return source
     */
    protected abstract Source convertToSource(final ReportDescriptor descriptor,
                                              final Map<String, Object> parameters,
                                              final Object data,
                                              final String lang);



    /**
     * Get correct XSLT-FO file for given parameters.
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return xslt-fo file
     */
    protected abstract Source getXsltFile(final ReportDescriptor descriptor,
                                          final Map<String, Object> parameters,
                                          final Object data,
                                          final String lang);


}
