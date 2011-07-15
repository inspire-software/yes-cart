package org.yes.cart.payment.persistence.entity.impl;

import org.yes.cart.payment.persistence.entity.Descriptor;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:33:53
 */
public class DescriptorImpl implements Descriptor {

    private static final long serialVersionUID = 20100714L;

    protected String name;
    protected String description;
    protected String label;


    /**
     * Name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Name
     *
     * @param name name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Description.
     *
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description
     *
     * @param description Description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Label.
     *
     * @return label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Label.
     *
     * @param label label
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * Construct descriptor.
     *
     * @param name        name
     * @param description description
     * @param label       label
     */
    public DescriptorImpl(final String name, final String description, final String label) {
        this.name = name;
        this.description = description;
        this.label = label;
    }

    /**
     * Default constructor.
     */
    public DescriptorImpl() {
    }
}
