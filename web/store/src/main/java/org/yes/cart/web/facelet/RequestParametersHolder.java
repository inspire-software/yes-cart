package org.yes.cart.web.facelet;


import org.yes.cart.web.constants.ManagedBeanELNames;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.CentralViewResolver;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;


/**
 * Responsible to hold necessary request parameters as
 * view state to use in ajax requests.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/3/11
 * Time: 10:12 PM
 */
@ManagedBean(name = WebParametersKeys.VIEW_PARAMETERS)
@ViewScoped
public class RequestParametersHolder implements Serializable {

    private static final long serialVersionUID = 20110603L;

    @ManagedProperty(ManagedBeanELNames.EL_CENTRAL_VIEW_RESOLVER)
    private transient CentralViewResolver centralViewResolver;

    private String viewLabel = null;
    private long categoryId;

    @PostConstruct
    public void init() {

    }

    /**
     * Get category id.
     *
     * @return category id.
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Set category id.
     *
     * @param categoryId category id.
     */
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Get the label for facelet to show.
     *
     * @return label of facelet to show.
     */
    public String getViewLabel() {
        if (viewLabel == null) {
            final FacesContext context = FacesContext.getCurrentInstance();
            final Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
            viewLabel = centralViewResolver.resolveMainPanelRendererLabel(paramMap);
        }
        return viewLabel;
    }

    /**
     * Set central view  label.
     * @param viewLabel central view label.
     */
    public void setViewLabel(final String viewLabel) {
        this.viewLabel = viewLabel;
    }

    /**
     * Set view resolver serice to use.
     *
     * @param centralViewResolver service to use.
     */
    public void setCentralViewResolver(final CentralViewResolver centralViewResolver) {
        this.centralViewResolver = centralViewResolver;
    }
}
