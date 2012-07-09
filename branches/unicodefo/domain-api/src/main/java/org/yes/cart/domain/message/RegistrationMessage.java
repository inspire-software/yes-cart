package org.yes.cart.domain.message;

import java.io.Serializable;
import java.util.Set;

/**
 * Registration message. Used for user or customer notification in case of registration
 * or password reset
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface RegistrationMessage extends Serializable {

    String getEmail();

    void setEmail(String email);

    long getShopId();

    void setShopId(long shopId);

    String getPassword();

    void setPassword(String password);

    /**
     * Get shop mail from addr.
     *
     * @return shop mail from addr.
     */
    String getShopMailFrom();

    /**
     * Set shop mail from addr.
     *
     * @param shopMailFrom shop mail from addr.
     */
    void setShopMailFrom(String shopMailFrom);


    /**
     * Get template name.
     * @return  template name.
     */
    String getTemplateName();

    /**
     * Set  template name.
     * @param templateName  template name.
     */
    void setTemplateName(String templateName);


    /**
     * Get path to template folder.
     * Example /some/path/shop/mailtemplates/ must hold folders with concrete templates
     * @return path to template folder.
     */
    String getPathToTemplateFolder();

    /**
     * Set path to template folder.
     * @param pathToTemplateFolder  path to template folder.
     */
    public void setPathToTemplateFolder(String pathToTemplateFolder);


    /**
     * Get shop code.
     *
     * @return shop code
     */
    String getShopCode();

    /**
     * Set sho pcode.
     *
     * @param shopCode shop code.
     */
    void setShopCode(String shopCode);

    /**
     * Get shop name.
     *
     * @return sho pname.
     */
    String getShopName();

    /**
     * Set shop name.
     *
     * @param shopName shop name
     */
    void setShopName(String shopName);

    /**
     * Get shop urls.
     *
     * @return shop urls.
     */
    Set<String> getShopUrl();

    /**
     * Set shop urls.
     *
     * @param shopUrl shop urls.
     */
    void setShopUrl(Set<String> shopUrl);

    /**
     * Get first name.
     *
     * @return first name
     */
    String getFirstname();

    /**
     * Set first name
     *
     * @param firstname value to set
     */
    void setFirstname(String firstname);

    /**
     * Get last name.
     *
     * @return last name
     */
    String getLastname();

    /**
     * Set last name
     *
     * @param lastname value to set
     */
    void setLastname(String lastname);

}
