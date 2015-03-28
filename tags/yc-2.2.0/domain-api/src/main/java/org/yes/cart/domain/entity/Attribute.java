/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.domain.entity;


/**
 * Attribute.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface Attribute extends Auditable, Rankable, Codable, Cloneable {

    /**
     * Get primary key.
     *
     * @return pk cvalue
     */
    long getAttributeId();

    /**
     * Set pk value.
     *
     * @param attributeId pk value.
     */
    void setAttributeId(long attributeId);


    /**
     * Get the attribute code.
     *
     * @return attribute code.
     */
    String getCode();

    /**
     * Set attribute code
     *
     * @param code code value
     */
    void setCode(String code);

    /**
     * Mandatory flag.
     *
     * @return true if value must be set for this attribute.
     */
    boolean isMandatory();

    /**
     * Set mandatory flag.
     *
     * @param mandatory flag value
     */
    void setMandatory(boolean mandatory);

    /**
     * Default string representation of attribute value, in case if attribute is mandatory.
     *
     * @return default value.
     */
    String getVal();

    /**
     * Set default value of attribute
     *
     * @param val default value.
     */
    void setVal(String val);

    /**
     * Attribute name.
     *
     * @return attribute name.
     */
    String getName();

    /**
     * Get attribute name
     *
     * @param name name
     */
    void setName(String name);

    /**
     * Attribute name.
     *
     * @return attribute name.
     */
    String getDisplayName();

    /**
     * Get attribute name
     *
     * @param name name
     */
    void setDisplayName(String name);

    /**
     * Get description.
     *
     * @return description.
     */
    String getDescription();

    /**
     * Set description
     *
     * @param description description value.
     */
    void setDescription(String description);

    /**
     * Get the system type.
     *
     * @return {@link Etype} type of attribute.
     */
    Etype getEtype();

    /**
     * Set type.
     *
     * @param etype type.
     */
    void setEtype(Etype etype);

    /**
     * Get attribute group.
     *
     * @return {@link AttributeGroup}
     */
    AttributeGroup getAttributeGroup();

    /**
     * Set {@link AttributeGroup}.
     *
     * @param attributeGroup attribute group.
     */
    void setAttributeGroup(AttributeGroup attributeGroup);

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
     * Get validation failed message. Message if type, mandatory or re validation failed.
     *
     * @return validation failed message.
     */
    String getValidationFailedMessage();

    /**
     * Set validation failed message.
     *
     * @param validationFailedMessage validation failed message.
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
     * @return comma separated choices.
     */
    String getChoiceData();

    /**
     * Set comma separated choices.
     *
     * @param choiceData comma separated choices.
     */
    void setChoiceData(String choiceData);


    /**
     * Create copy of current object;
     * @return copy of current object.
     */
    Attribute copy() throws CloneNotSupportedException;


}


