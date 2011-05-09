package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.SeoDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class SeoDTOImpl implements SeoDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "seoId", readOnly = true)
    private long seoId;

    @DtoField(value = "uri")
    private String uri;

    @DtoField(value = "title")
    private String title;

    @DtoField(value = "metakeywords")
    private String metakeywords;

    @DtoField(value = "metadescription")
    private String metadescription;

    /** {@inheritDoc}*/
    public long getSeoId() {
        return seoId;
    }

    /** {@inheritDoc}*/
    public void setSeoId(long seoId) {
        this.seoId = seoId;
    }

    /** {@inheritDoc}*/
    public String getUri() {
        return uri;
    }

    /** {@inheritDoc}*/
    public void setUri(String uri) {
        this.uri = uri;
    }

    /** {@inheritDoc}*/
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc}*/
    public void setTitle(String title) {
        this.title = title;
    }

    /** {@inheritDoc}*/
    public String getMetakeywords() {
        return metakeywords;
    }

    /** {@inheritDoc}*/
    public void setMetakeywords(String metakeywords) {
        this.metakeywords = metakeywords;
    }

    /** {@inheritDoc}*/
    public String getMetadescription() {
        return metadescription;
    }

    /** {@inheritDoc}*/
    public void setMetadescription(String metadescription) {
        this.metadescription = metadescription;
    }
}
