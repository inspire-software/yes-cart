package org.yes.cart.web.facelet;

import org.yes.cart.domain.entity.Category;
import org.yes.cart.web.constants.ManagedBeanELNames;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.support.entity.decorator.impl.CategoryDecoratorImpl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 6/28/11
 * Time: 11:07 AM
 */
@ManagedBean(name = WebParametersKeys.REQUEST_SUB_CATEGORIES)
@RequestScoped
public class SubCategories extends BaseFacelet {


    @ManagedProperty(ManagedBeanELNames.EL_VIEW_PARAMETERS)
    private transient RequestParametersHolder requestParametersHolder;


    /**
     * Get the list of subcategories, that belong to current category.
     * Current category - get parameter.
     *
     * @return list of subcategories to show
     */
    public List<CategoryDecorator> getCategories() {
        return decorate(
                getCategoryService().getChildCategoriesWithAvailability(
                        requestParametersHolder.getCategoryId(),
                        true)
        );
    }

    private  List<CategoryDecorator> decorate(final List<Category> categories) {
        final HttpServletRequest httpServletRequest =
                (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        final ArrayList<CategoryDecorator> rez = new ArrayList<CategoryDecorator>();
        for(Category cat : categories)  {
            rez.add(new CategoryDecoratorImpl(getCategoryImageService(), cat, httpServletRequest.getContextPath())) ;
        }
        return rez;
    }

    /**
     * Set {@link RequestParametersHolder} to use. IoC.
     * @param requestParametersHolder {@link RequestParametersHolder}
     */
    public void setRequestParametersHolder(final RequestParametersHolder requestParametersHolder) {
        this.requestParametersHolder = requestParametersHolder;
    }

}
