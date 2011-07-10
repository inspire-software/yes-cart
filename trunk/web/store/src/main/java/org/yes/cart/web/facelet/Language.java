package org.yes.cart.web.facelet;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.shoppingcart.impl.ChangeLocaleCartCommandImpl;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.constants.ManagedBeanELNames;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.LanguageService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/25/11
 * Time: 9:07 PM
 */
@ManagedBean(name = WebParametersKeys.REQUEST_LANGUAGE)
@RequestScoped
public class Language extends BaseFacelet {

    @ManagedProperty(ManagedBeanELNames.EL_LANGUAGE_NAME_SERVICE)
    private LanguageService languageService;

    private static Map<String, String> supportedLocales = null;


    /**
     * Get supported locales.
     *
     * @return supported locales.
     */
    public Map<String, String> getSupportedLocales() {
        synchronized (Language.class.getName()) {
            if (supportedLocales == null) {
                supportedLocales = new LinkedHashMap<String, String>();
                Iterator<Locale> localeIterator = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
                while (localeIterator.hasNext()) {
                    Locale locale = localeIterator.next();
                    supportedLocales.put(
                            languageService.resolveLanguageName(locale.getLanguage()),
                            locale.getLanguage()
                    );
                }
            }
        }
        return supportedLocales;
    }

    /**
     * Get current locale.
     *
     * @return current cart locale
     */
    public String getCurrentLocale() {
        if (StringUtils.isBlank(getShoppingCart().getCurrentLocale())) {
            setCurrentLocale(FacesContext.getCurrentInstance().getApplication().getDefaultLocale().getLanguage());
        }
        return getShoppingCart().getCurrentLocale();
    }

    /**
     * Set current locale code to cart.
     *
     * @param currentLocale new current locale
     */
    public void setCurrentLocale(final String currentLocale) {
        new ChangeLocaleCartCommandImpl(
                ApplicationDirector.getApplicationContext(),
                Collections.singletonMap(ChangeLocaleCartCommandImpl.CMD_KEY, currentLocale)
        ).execute(getShoppingCart());
        FacesContext.getCurrentInstance().getViewRoot().setLocale(
                new Locale(getShoppingCart().getCurrentLocale()));

    }

    /**
     * Handle new locale selection.
     * @param changeEvent  change event.
     */
    public void selectedLocaleChange(final ValueChangeEvent changeEvent) {
        setCurrentLocale((String) changeEvent.getNewValue());
    }



    /**
     * Set language service to use.
     *
     * @param languageService language service to use.
     */
    public void setLanguageService(final LanguageService languageService) {
        this.languageService = languageService;
    }
}
