package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;

/**
 * Common attribute intefrace.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttributeDTO extends Unique {

    /**
     * Get group id.
     *
     * @return group id
     */
    long getAttributegroupId();

    /**
     * Set group id.
     *
     * @param attributegroupId group id
     */
    void setAttributegroupId(long attributegroupId);

    /**
     * Get primary key.
     *
     * @return pk cvalue
     */
    public long getAttributeId();

    /**
     * Set pk value.
     *
     * @param attributeId pk value.
     */
    public void setAttributeId(long attributeId);


    /**
     * Get the attribute code.
     *
     * @return attribute code.
     */
    public String getCode();

    /**
     * Set attribute code
     *
     * @param code code value
     */
    public void setCode(String code);

    /**
     * Mandatory flag.
     *
     * @return true if value must be set for this attribute.
     */
    public boolean isMandatory();

    /**
     * Set mandatory flag.
     *
     * @param mandatory flag value
     */
    public void setMandatory(boolean mandatory);

    /**
     * Default string representation of attribute value, in case if attribute is mandatory.
     *
     * @return default value.
     */
    public String getVal();

    /**
     * Set default value of attribute
     *
     * @param val default value.
     */
    public void setVal(String val);

    /**
     * Attribute name.
     *
     * @return attribute name.
     */
    public String getName();

    /**
     * Get atribute name
     *
     * @param name name
     */
    public void setName(String name);

    /**
     * Get description.
     *
     * @return description.
     */
    public String getDescription();

    /**
     * Set description
     *
     * @param description description value.
     */
    public void setDescription(String description);

    /**
     * Get the system type.
     *
     * @return Etype primary key.
     */
    public long getEtypeId();

    /**
     * Set type.
     *
     * @param EtypeId id.
     */
    public void setEtypeId(long EtypeId);

    /**
     * Get the system type name.
     *
     * @return Etype name.
     */
    public String getEtypeName();

    /**
     * Set type.
     *
     * @param etypeName etype name.
     */
    public void setEtypeName(String etypeName);

    /**
     * Is attribute duplicates allowed. Attribute can have several values. Example color - black and red.
     *
     * @return true if duplicates allowed
     */
    boolean isAllowduplicate();

    /**
     * Set duplicates allowed flag
     *
     * @param allowduplicate duplicates allowed flag
     */
    void setAllowduplicate(boolean allowduplicate);

    /**
     * Allow failover search for category attributes.
     *
     * @return true if failvover allowed.
     */
    boolean isAllowfailover();

    /**
     * Set search failower flag.
     *
     * @param allowfailover true if faiover allowed.
     */
    void setAllowfailover(boolean allowfailover);

    /**
     * Get regular expression to validate user input on UI.
     *
     * @return regular expression if any.
     */
    String getRegexp();

    /**
     * Set regular expression to validate user input on UI.
     *
     * @param regexp regular expression.
     */
    void setRegexp(String regexp);

    /**
     * Get validation message.
     *
     * @return validation message.
     */
    String getValidationFailedMessage();


    /**
     * Set validation message.
     *
     * @param validationFailedMessage messageto set.
     */
    void setValidationFailedMessage(String validationFailedMessage);

    /**
     * Get order in UI form.
     *
     * @return order in UI form.
     */
    int getRank();

    /**
     * Set order in UI form.
     *
     * @param rank order in UI form
     */
    void setRank(int rank);

    /**
     * Get the comma separated  [key-]value data or
     * service , that provide data in case if start from protocol,
     *
     * @return comma separated coices.
     */
    String getChoiceData();

    /**
     * Set comma separated coices.
     *
     * @param choiceData comma separated coices.
     */
    void setChoiceData(String choiceData);


}
