package org.yes.cart.utils.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class RegexPatternFilenameFilterImpl implements FilenameFilter {

    private Pattern pattern;

    /**
     * Constructor.
     *
     * @param pattern reg exp pattern.
     */
    public RegexPatternFilenameFilterImpl(final Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Constructor.
     *
     * @param regExp regular expression.
     */
    public RegexPatternFilenameFilterImpl(final String regExp) {
        pattern = Pattern.compile(regExp);
    }

    /**
     * {@inheritDoc
     */
    public boolean accept(File dir, String name) {
        return (name != null) && this.pattern.matcher(name).matches();
    }


}
