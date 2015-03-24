package org.yes.cart.domain.ro;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 23/03/2015
 * Time: 22:43
 */
@XmlRootElement(name = "filtered-navigation")
public class FilteredNavigationRO {

    private List<FilteredNavigationAttributeRO> fnAttributes = new ArrayList<FilteredNavigationAttributeRO>();

    @XmlElement(name = "fn-attribute")
    public List<FilteredNavigationAttributeRO> getFnAttributes() {
        return fnAttributes;
    }

    public void setFnAttributes(final List<FilteredNavigationAttributeRO> fnAttributes) {
        this.fnAttributes = fnAttributes;
    }
}
