package org.yes.cart.domain.ro;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 06/10/2015
 * Time: 16:16
 */
@Dto
@XmlRootElement(name = "promotion")
public class PromotionRO {

    private String originalCode;
    @DtoField(readOnly = true)
    private String code;
    @DtoField(readOnly = true)
    private String couponCode;
    @DtoField(readOnly = true)
    private String type;
    @DtoField(readOnly = true)
    private String action;
    @DtoField(readOnly = true)
    private String context;
    @DtoField(value = "name.allValues", readOnly = true)
    private Map<String, String> name;
    @DtoField(value = "description.allValues", readOnly = true)
    private Map<String, String> description;
    @DtoField(readOnly = true)
    private Date activeFrom;
    @DtoField(readOnly = true)
    private Date activeTo;

    @XmlElement(name = "original-code")
    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(final String originalCode) {
        this.originalCode = originalCode;
    }

    @XmlElement(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    @XmlElement(name = "coupon-code")
    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(final String couponCode) {
        this.couponCode = couponCode;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public String getContext() {
        return context;
    }

    public void setContext(final String context) {
        this.context = context;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-names")
    public Map<String, String> getName() {
        return name;
    }

    public void setName(final Map<String, String> name) {
        this.name = name;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-descriptions")
    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(final Map<String, String> description) {
        this.description = description;
    }

    @XmlElement(name = "active-from")
    public Date getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(final Date activeFrom) {
        this.activeFrom = activeFrom;
    }

    @XmlElement(name = "active-to")
    public Date getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(final Date activeTo) {
        this.activeTo = activeTo;
    }
}
