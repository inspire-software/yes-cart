package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

/**
 * SEO DTO interface.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface SeoDTO extends Identifiable {

    /**
     * Get seo pk value.
     *
     * @return seo pk value.
     */
    public long getSeoId();

    /**
     * Set seo pk value.
     *
     * @param seoId
     */
    public void setSeoId(long seoId);

    /**
     * Get uri
     *
     * @return uri
     */
    public String getUri();

    /**
     * Set uri.
     *
     * @param uri uri.
     */
    public void setUri(String uri);

    /**
     * Get title.
     *
     * @return title.
     */
    public String getTitle();

    /**
     * Set title.
     *
     * @param title title
     */
    public void setTitle(String title);

    /**
     * Get meta keywords.
     *
     * @return meta keywords
     */
    public String getMetakeywords();

    /**
     * Set meta keywords
     *
     * @param metakeywords meta keywords
     */
    public void setMetakeywords(String metakeywords);

    /**
     * Get meta description.
     *
     * @return meta description
     */
    public String getMetadescription();

    /**
     * Set meta description.
     *
     * @param metadescription meta description.
     */
    public void setMetadescription(String metadescription);

}
