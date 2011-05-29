package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Unique;

import java.io.Serializable;

/**
 * Image SEO DTO interface.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface SeoImageDTO extends Unique {

    /**
     * Get image name.
     *
     * @return image name.
     */
    String getImageName();

    /**
     * Set image name.
     *
     * @param imageName image name.
     */
    void setImageName(String imageName);

    /**
     * Get alternative text.
     *
     * @return alternative text
     */
    String getAlt();


    /**
     * Set alternative text.
     *
     * @param alt
     */
    void setAlt(String alt);

    /**
     * Get image title.
     *
     * @return image title
     */
    String getTitle();

    /**
     * Set image title.
     *
     * @param title
     */
    void setTitle(String title);

    /**
     * Get pk value.
     *
     * @return pk value
     */
    long getSeoImageId();

    /**
     * Set pk value.
     *
     * @param seoImageId pk value.
     */
    void setSeoImageId(long seoImageId);

}
