package org.yes.cart.domain.entity.impl;

import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Attribute;

import java.util.Date;

/**
 * User: denispavlov
 * Date: 21/07/2017
 * Time: 10:33
 */
public class AttrValueWithAttributeAdapter implements AttrValueWithAttribute {

    private final AttrValue value;
    private Attribute attribute;

    public AttrValueWithAttributeAdapter(final AttrValue value, final Attribute attribute) {
        this.value = value;
        this.attribute = attribute;
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public void setAttribute(final Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public long getAttrvalueId() {
        return value.getAttrvalueId();
    }

    @Override
    public void setAttrvalueId(final long attrvalueId) {
        value.setAttrvalueId(attrvalueId);
    }

    @Override
    public String getVal() {
        return value.getVal();
    }

    @Override
    public void setVal(final String val) {
        value.setVal(val);
    }

    @Override
    public String getDisplayVal() {
        return value.getDisplayVal();
    }

    @Override
    public void setDisplayVal(final String displayVal) {
        value.setDisplayVal(displayVal);
    }

    @Override
    public String getAttributeCode() {
        return value.getAttributeCode();
    }

    @Override
    public void setAttributeCode(final String attributeCode) {
        value.setAttributeCode(attributeCode);
    }

    @Override
    public Date getCreatedTimestamp() {
        return value.getCreatedTimestamp();
    }

    @Override
    public void setCreatedTimestamp(final Date createdTimestamp) {
        value.setCreatedTimestamp(createdTimestamp);
    }

    @Override
    public Date getUpdatedTimestamp() {
        return value.getUpdatedTimestamp();
    }

    @Override
    public void setUpdatedTimestamp(final Date updatedTimestamp) {
        value.setUpdatedTimestamp(updatedTimestamp);
    }

    @Override
    public String getCreatedBy() {
        return value.getCreatedBy();
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        value.setCreatedBy(createdBy);
    }

    @Override
    public String getUpdatedBy() {
        return value.getUpdatedBy();
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        value.setUpdatedBy(updatedBy);
    }

    @Override
    public long getId() {
        return value.getId();
    }

    @Override
    public String getGuid() {
        return value.getGuid();
    }

    @Override
    public void setGuid(final String guid) {
        value.setGuid(guid);
    }

    @Override
    public long getVersion() {
        return value.getVersion();
    }
}
