package org.yes.cart.domain.ro.xml.impl;

import org.yes.cart.util.DateUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;

/**
 * User: denispavlov
 * Date: 12/02/2018
 * Time: 16:41
 */
public class InstantAdapter extends XmlAdapter<String, Instant> {

    @Override
    public Instant unmarshal(final String v) throws Exception {
        return DateUtils.iParseSDT(v);
    }

    @Override
    public String marshal(final Instant v) throws Exception {
        return DateUtils.formatSDT(v);
    }
}
