package org.yes.cart.domain.ro.xml.impl;

import org.yes.cart.util.DateUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

/**
 * User: denispavlov
 * Date: 12/02/2018
 * Time: 16:39
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(final String v) throws Exception {
        return DateUtils.ldParseSDT(v);
    }

    @Override
    public String marshal(final LocalDate v) throws Exception {
        return DateUtils.formatSD(v);
    }

}
