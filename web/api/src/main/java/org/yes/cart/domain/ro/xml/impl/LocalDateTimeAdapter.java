package org.yes.cart.domain.ro.xml.impl;

import org.yes.cart.util.DateUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;

/**
 * User: denispavlov
 * Date: 12/02/2018
 * Time: 16:41
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(final String v) throws Exception {
        return DateUtils.ldtParseSDT(v);
    }

    @Override
    public String marshal(final LocalDateTime v) throws Exception {
        return DateUtils.formatSDT(v);
    }
}
