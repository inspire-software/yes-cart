package org.yes.cart.domain.dto.impl;

import dp.lib.dto.geda.annotations.Dto;
import dp.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.SeoImageDTO;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class SeoImageDTOImpl implements SeoImageDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "seoImageId")
    private long seoImageId;

    @DtoField(value = "imageName")
    private String imageName;

    @DtoField(value = "alt")
    private String alt;

    @DtoField(value = "title")
    private String title;

    /** {@inheritDoc}*/
    public long getSeoImageId() {
        return seoImageId;
    }

    /** {@inheritDoc}*/
    public long getId() {
        return seoImageId;
    }

    /** {@inheritDoc}*/
    public void setSeoImageId(final long seoImageId) {
        this.seoImageId = seoImageId;
    }

    /** {@inheritDoc}*/
    public String getImageName() {
        return imageName;
    }

    /** {@inheritDoc}*/
    public void setImageName(final String imageName) {
        this.imageName = imageName;
    }

    /** {@inheritDoc}*/
    public String getAlt() {
        return alt;
    }

    /** {@inheritDoc}*/
    public void setAlt(final String alt) {
        this.alt = alt;
    }

    /** {@inheritDoc}*/
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc}*/
    public void setTitle(final String title) {
        this.title = title;
    }
}
