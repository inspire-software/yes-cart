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
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.mail.JavaMailSenderBuilder;
import org.yes.cart.utils.log.Markers;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * User: denispavlov
 * Date: 05/09/2015
 * Time: 11:35
 */
public class JavaMailSenderBuilderImpl implements JavaMailSenderBuilder {

    private final Logger LOG = LoggerFactory.getLogger(JavaMailSenderBuilderImpl.class);

    private final ShopService shopService;
    private final SystemService systemService;

    private String connectionTimeout = "5000";

    public JavaMailSenderBuilderImpl(final ShopService shopService, 
                                     final SystemService systemService) {
        this.shopService = shopService;
        this.systemService = systemService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "mailSenderBuilder-buildJavaMailSender")
    public JavaMailSender buildJavaMailSender(final String shopCode) {

        if (StringUtils.isNotEmpty(shopCode) && !"DEFAULT".equals(shopCode)) {
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
                    final String tlsv = getStringCfg(shop, AttributeNamesKeys.Shop.SHOP_MAIL_SERVER_STARTTLS_V);

                    final JavaMailSender shopSpecific = configureMailSender(shopCode, host, port, user, pass, smtpauth, starttls, tlsv);
                    if (shopSpecific != null) {
                        return shopSpecific;
                    }

                }
            }
        }
        
        final boolean enabled = Boolean.valueOf(this.systemService.getAttributeValue(AttributeNamesKeys.System.MAIL_SERVER_CUSTOM_ENABLE));
        if (enabled) {

            final String host = this.systemService.getAttributeValue(AttributeNamesKeys.System.MAIL_SERVER_HOST);
            final String port = this.systemService.getAttributeValue(AttributeNamesKeys.System.MAIL_SERVER_PORT);
            final String user = this.systemService.getAttributeValue(AttributeNamesKeys.System.MAIL_SERVER_USERNAME);
            final String pass = this.systemService.getAttributeValue(AttributeNamesKeys.System.MAIL_SERVER_PASSWORD);
            final String smtpauth = this.systemService.getAttributeValue(AttributeNamesKeys.System.MAIL_SERVER_SMTPAUTH_ENABLE);
            final String starttls = this.systemService.getAttributeValue(AttributeNamesKeys.System.MAIL_SERVER_STARTTLS_ENABLE);
            final String tlsv = this.systemService.getAttributeValue(AttributeNamesKeys.System.MAIL_SERVER_STARTTLS_V);

            return configureMailSender(shopCode, host, port, user, pass, smtpauth, starttls, tlsv);

        }

        return null;

    }

    JavaMailSender configureMailSender(final String shopCode, final String host, final String port, final String user, final String pass, final String smtpauth, final String starttls, final String tlsv) {
        
        if (host == null || port == null) {
            LOG.error(Markers.alert(), "Custom mail sender is missconfigured for {}, host or port missing", shopCode);
            return null;
        }

        if (Boolean.valueOf(smtpauth) && user == null || pass == null) {
            LOG.error(Markers.alert(), "Custom mail sender is missconfigured for {}, user or pass missing for SMTP-AUTH", shopCode);
            return null;
        }

        JavaMailSenderImpl shopMailSender = new JavaMailSenderImpl();
        shopMailSender.setHost(host);
        try {
            shopMailSender.setPort(Integer.valueOf(port));
        } catch (NumberFormatException nfe) {
            LOG.error(Markers.alert(), "Custom mail sender is missconfigured for {}, invalid port", shopCode);
            return null;
        }

        shopMailSender.setUsername(user);
        shopMailSender.setPassword(pass);

        final Properties properties = new Properties();
        properties.put("mail.smtp.auth", smtpauth);
        properties.put("mail.smtp.starttls.enable", starttls);
        if (Boolean.parseBoolean(starttls)) {
            try {
                if (StringUtils.isNotBlank(tlsv)) {
                    properties.put("mail.smtp.ssl.protocols", tlsv);
                    LOG.info("Analysing SSL protocols (supported: {}) ... forcing: {}",
                            String.join(" ", SSLContext.getDefault().getSupportedSSLParameters().getProtocols()),
                            tlsv);
                } else {
                    LOG.info("Analysing SSL protocols (supported: {}) ... using auto detect",
                            String.join(" ", SSLContext.getDefault().getSupportedSSLParameters().getProtocols()));
                }
            } catch (NoSuchAlgorithmException nsae) {
                LOG.error("Unable to determine SSL supported parameters", nsae);
            }
        }
        properties.put("mail.smtp.connectiontimeout", this.connectionTimeout);
        properties.put("mail.smtp.timeout", this.connectionTimeout);
        shopMailSender.setJavaMailProperties(properties);

        LOG.info("Detected custom mail sender {}:{} for shop {}", host, port, shopCode);

        return shopMailSender;
        
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

    public void setConnectionTimeout(final String connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
