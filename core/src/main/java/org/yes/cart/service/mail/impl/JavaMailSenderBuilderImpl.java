package org.yes.cart.service.mail.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.mail.JavaMailSenderBuilder;

import java.util.Properties;

/**
 * User: denispavlov
 * Date: 05/09/2015
 * Time: 11:35
 */
public class JavaMailSenderBuilderImpl implements JavaMailSenderBuilder {

    private final Logger LOG = LoggerFactory.getLogger(JavaMailSenderBuilderImpl.class);

    private final ShopService shopService;

    public JavaMailSenderBuilderImpl(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    @Cacheable(value = "mailSenderBuilder-buildJavaMailSender")
    public JavaMailSender buildJavaMailSender(final String shopCode) {

        final Shop shop = this.shopService.getShopByCode(shopCode);
        if (shop != null) {
            final boolean enabled = getBooleanCfg(shop, AttributeNamesKeys.Shop.SHOP_MAIL_SERVER_CUSTOM_ENABLE);
            if (enabled) {

                final String host = getStringCfg(shop, AttributeNamesKeys.Shop.SHOP_MAIL_SERVER_HOST);
                final String port = getStringCfg(shop, AttributeNamesKeys.Shop.SHOP_MAIL_SERVER_PORT);
                final String user = getStringCfg(shop, AttributeNamesKeys.Shop.SHOP_MAIL_SERVER_USERNAME);
                final String pass = getStringCfg(shop, AttributeNamesKeys.Shop.SHOP_MAIL_SERVER_PASSWORD);
                final String smtpauth = getStringCfg(shop, AttributeNamesKeys.Shop.SHOP_MAIL_SERVER_SMTPAUTH_ENABLE);
                final String starttls = getStringCfg(shop, AttributeNamesKeys.Shop.SHOP_MAIL_SERVER_STARTTLS_ENABLE);

                if (host == null || port == null) {
                    LOG.error("Custom mail sender is missconfigured for {}, host or port missing", shopCode);
                    return null;
                }

                if (Boolean.valueOf(smtpauth) && user == null || pass == null) {
                    LOG.error("Custom mail sender is missconfigured for {}, user or pass missing for SMTP-AUTH", shopCode);
                    return null;
                }

                JavaMailSenderImpl shopMailSender = new JavaMailSenderImpl();
                shopMailSender.setHost(host);
                try {
                    shopMailSender.setPort(Integer.valueOf(port));
                } catch (NumberFormatException nfe) {
                    LOG.error("Custom mail sender is missconfigured for {}, invalid port", shopCode);
                    return null;
                }

                shopMailSender.setUsername(user);
                shopMailSender.setPassword(pass);

                final Properties properties = new Properties();
                properties.put("mail.smtp.auth", smtpauth);
                properties.put("mail.smtp.starttls.enable", starttls);
                shopMailSender.setJavaMailProperties(properties);

                return shopMailSender;

            }
        }
        return null;

    }

    private boolean getBooleanCfg(final Shop shop, final String attrKey) {
        return Boolean.valueOf(shop.getAttributeValueByCode(attrKey));
    }

    private String getStringCfg(final Shop shop, final String attrKey) {
        final String av = shop.getAttributeValueByCode(attrKey);
        if (StringUtils.isNotBlank(av)) {
            return av;
        }
        return null;
    }

}
