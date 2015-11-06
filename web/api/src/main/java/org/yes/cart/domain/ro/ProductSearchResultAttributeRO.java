package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 06/11/2015
 * Time: 08:01
 */
@XmlRootElement(name = "product-result-attribute")
public class ProductSearchResultAttributeRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private String code;
    private String value;
    private Map<String, String> displayValue;

    @XmlAttribute(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @XmlElement(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-values")
    public Map<String, String> getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(final Map<String, String> displayValue) {
        this.displayValue = displayValue;
    }
}
