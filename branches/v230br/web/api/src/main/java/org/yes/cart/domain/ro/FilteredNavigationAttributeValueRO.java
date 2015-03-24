package org.yes.cart.domain.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: denispavlov
 * Date: 23/03/2015
 * Time: 22:49
 */
@XmlRootElement(name = "filtered-navigation-attribute-value")
public class FilteredNavigationAttributeValueRO {

    private String value;
    private String displayValue;
    private int count;

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @XmlElement(name = "display-value")
    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(final String displayValue) {
        this.displayValue = displayValue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

}
