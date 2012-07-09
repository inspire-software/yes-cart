package org.yes.cart.service.domain.impl;

import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.NpaSystem;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.util.DomainApiUtil;

import java.io.File;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SystemServiceImpl implements SystemService {

    private NpaSystem system;

    private final GenericDAO<NpaSystem, Long> systemDao;

    public SystemServiceImpl(final GenericDAO<NpaSystem, Long> systemDao) {
        this.systemDao = systemDao;
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeValue(final String key) {
        return DomainApiUtil.getAttirbuteValue(key, getSystem().getAttribute());
    }

    /**
     * Is Google checkout enabled.
     * @return    true if google checkout enabled.
     */
    public boolean isGoogleCheckoutEnabled() {
        return getAttributeValue(AttributeNamesKeys.System.SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABELS).contains("googleCheckoutPaymentGatewayLabel");
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultShopURL() {
        return DomainApiUtil.getAttirbuteValue(AttributeNamesKeys.System.SYSTEM_DEFAULT_SHOP,
                getSystem().getAttribute());
    }

    /**
     * {@inheritDoc}
     */
    public String getMailResourceDirectory() {
        return addTailFileSeparator(DomainApiUtil.getAttirbuteValue(AttributeNamesKeys.SYSTEM_MAILTEMPLATES_FSPOINTER,
                getSystem().getAttribute()));
    }


    /**
     * {@inheritDoc}
     */
    public String getDefaultResourceDirectory() {
        return DomainApiUtil.getAttirbuteValue(AttributeNamesKeys.System.SYSTEM_DEFAULT_FSPOINTER,
                getSystem().getAttribute());
    }

    /**
     * {@inheritDoc}
     */
    public String getImageRepositoryDirectory() {
        return addTailFileSeparator(
                DomainApiUtil.getAttirbuteValue(AttributeNamesKeys.System.SYSTEM_IMAGE_VAULT,
                        getSystem().getAttribute()));
    }



    /**
     * {@inheritDoc}
     */
    public Integer getEtagExpirationForImages() {
        final String expirationTimeout = DomainApiUtil.getAttirbuteValue(
                AttributeNamesKeys.System.SYSTEM_ETAG_CACHE_IMAGES_TIME,
                getSystem().getAttribute());
        if (expirationTimeout != null) {
            return Integer.valueOf(expirationTimeout);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getEtagExpirationForPages() {
        final String expirationTimeout = DomainApiUtil.getAttirbuteValue(
                AttributeNamesKeys.System.SYSTEM_ETAG_CACHE_PAGES_TIME,
                getSystem().getAttribute());
        if (expirationTimeout != null) {
            return Integer.valueOf(expirationTimeout);
        }
        return 0;
    }


    private String addTailFileSeparator(final String str) {
        if (!str.endsWith(File.separator)) {
            return str + File.separator;
        }
        return str;
    }


    private NpaSystem getSystem() {
        if (system == null) {
            system = systemDao.findAll().get(0);
        }
        return system;
    }

}
