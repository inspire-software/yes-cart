package org.yes.cart.report.impl;

import org.slf4j.Logger;
import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.util.ShopCodeContext;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2015
 * Time: 21:13
 */
public class WebReportGeneratorImpl extends AbstractThemeAwareFopReportGenerator {

    public WebReportGeneratorImpl(final ThemeService themeService,
                                  final ShopService shopService,
                                  final ContentService contentService) {
        super(themeService, shopService, contentService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Source convertToSource(final ReportDescriptor descriptor, final Map<String, Object> parameters, final Object data, final String lang) {
        if (data instanceof Collection) {
            return getXml((Collection) data);
        } else if (data != null) {
            return getXml(Collections.singletonList(data));
        }
        return new StreamSource(new ByteArrayInputStream(new byte[0]));
    }


    /**
     * Write into given file name xml result.
     *
     * @param rez list of objects.
     *
     * @return xml as source
     */
    Source getXml(final Collection<Object> rez) {

        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream os = null;

        try {

            os = ReportObjectStreamFactory.getObjectOutputStream(new OutputStreamWriter(bytesOut));

            for (Object obj : rez) {
                os.writeObject(obj);
            }

        } catch (Exception e) {
            ShopCodeContext.getLog(this).error("Cannot create xml ", e);

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    ShopCodeContext.getLog(this).error("Cannot close file", e);
                }
            }

        }

        final Logger logger = ShopCodeContext.getLog(this);
        if (logger.isDebugEnabled()) {
            logger.debug("Report XML ==================================\n\n" + new String(bytesOut.toByteArray(), Charset.forName("UTF-8")));
        }

        return new StreamSource(new ByteArrayInputStream(bytesOut.toByteArray()));
    }

}
