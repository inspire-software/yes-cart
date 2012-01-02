package org.yes.cart.service.domain;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface SystemService {

    /**
     * Get etag expiration in minutes for images.
     *
     * @return etag expiration in minutes if configured, otherwise default value 0 will be returned.
     */
    Integer getEtagExpirationForImages();

    /**
     * Get etag expiration in minutes for pages.
     *
     * @return etag expiration in minutes if configured, otherwise default value 0 will be returned.
     */
    Integer getEtagExpirationForPages();

    /**
     * Get atribute value
     *
     * @param key key
     * @return value if found, otherwise is null
     */

    String getAttributeValue(String key);

    /**
     * Need to know default shop URL, in case if shop can not be resolvel by domain name.
     *
     * @return URL of default shop
     */
    String getDefaultShopURL();

    /**
     * Get the mail templates directory.
     * Mail template directory has following structure:
     * <p/>
     * /mail/template/directory
     * <p/>
     * /mail/template/directory/shopCode1/tamplatename - has two templates text and html
     * /mail/template/directory/shopCode1/tamplatename/resources has resources to inline into html template     *
     * /mail/template/directory/shopCode2/tamplatename - has two templates text and html
     * /mail/template/directory/shopCode2/tamplatename/resources has resources to inline into html template
     *
     * @return mail templates directory with tail File.separator
     */
    String getMailResourceDirectory();

    /**
     * Get default (failover) directory.
     *
     * @return absolute path to default directory.
     */
    String getDefaultResourceDirectory();

    /**
     * Get the image repository directory
     *
     * @return path to image repository directory.
     */
    String getImageRepositoryDirectory();


    /**
     * Is Google checkout enabled.
     * @return    true if google checkout enabled.
     */
    boolean isGoogleCheckoutEnabled();





}
