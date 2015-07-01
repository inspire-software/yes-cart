/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.value.ValueMap;
import org.yes.cart.web.shoppingcart.CommandConfig;
import org.yes.cart.web.support.constants.WebParametersKeys;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 9:42 PM
 */
public class WicketUtil {


    private static final int DEFAULT_ITEMS = 10;

    private final CommandConfig commandConfig;

    /**
     * Proxy creation constructor - do not remove
     */
    public WicketUtil() {
        this.commandConfig = null;
    }

    public WicketUtil(final CommandConfig commandConfig) {
        this.commandConfig = commandConfig;
    }

    /**
     * Get  {@link HttpServletRequest} .
     *
     * @return {@link HttpServletRequest}
     */
    public HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
    }


    /**
     * Get current category id.
     *
     * @return category id if present in request, otherwise 0
     */
    public long getCategoryId(final PageParameters pageParameters) {
        final String categoryId = pageParameters.get(WebParametersKeys.CATEGORY_ID).toString();
        try {
            if (categoryId != null) {
                return Long.valueOf(categoryId);
            }
            return 0;
        } catch (Exception exp) {
            return 0L;
        }
    }


    /**
     * Transform wicket 1.5 page parameter to more traditional map.  Expensive operation.
     *
     * @param pageParameters given parameters to transform.
     * @return parameters transformed to map
     */
    public Map<String, String> pageParametesAsMap(final PageParameters pageParameters) {
        final Map<String, String> map = new LinkedHashMap<String, String>();
        if (pageParameters != null) {
            for (String key : pageParameters.getNamedKeys()) {
                if (!commandConfig.isInternalCommandKey(key)) {
                    map.put(key, pageParameters.get(key).toString());
                }
            }
        }
        return map;
    }


    /**
     * Transform wicket 1.5 page parameter to more traditional map.  Expensive operation.
     *
     * @param pageParameters given parameters to transform.
     * @return parameters transformed to map
     */
    public Map<String, List<String>> pageParametesAsMultiMap(final PageParameters pageParameters) {
        final Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
        if (pageParameters != null) {
            for (String key : pageParameters.getNamedKeys()) {
                if (!commandConfig.isInternalCommandKey(key)) {
                    final List<String> vals = new ArrayList<String>();
                    for (final StringValue value : pageParameters.getValues(key)) {
                        vals.add(value.toString());
                    }
                    map.put(key, vals);
                }
            }
        }
        return map;
    }


    /**
     * Get the filtered, from commands, request parameters,
     * that not contains given set of request parameter names
     *
     * @param parameters original request parameters
     * @return new filtered {@link PageParameters}
     */
    public PageParameters getFilteredRequestParameters(final PageParameters parameters) {

        if (parameters != null) {

            final PageParameters rez = new PageParameters(parameters);
            for (final String key : parameters.getNamedKeys()) {
                if (commandConfig.isCommandKey(key)) {
                    rez.remove(key);
                }
            }
            return rez;

        }

        return new PageParameters();
    }

    /**
     * Get the filtered request parameters, that not contains given set of request parameter names
     *
     * @param pageParameters original request parameters
     * @param nameFilter     set of parameter name to remove
     * @return new filtered {@link PageParameters}
     */
    public PageParameters getFilteredRequestParameters(final PageParameters pageParameters,
                                                              final Collection<String> nameFilter) {

        final PageParameters rez = getFilteredRequestParameters(pageParameters);
        for (String paramName : nameFilter) {
            rez.remove(paramName);
        }
        return rez;
    }


    /**
     * Get the filtered request parameters that does not
     * not contain given key and value.
     *
     * @param parameters original request parameters
     * @param key        key part to remove
     * @param value      value part to remove
     * @return new filtered {@link PageParameters}
     */
    public PageParameters getFilteredRequestParameters(final PageParameters parameters,
                                                              final String key,
                                                              final String value) {
        final PageParameters rez = getFilteredRequestParameters(parameters);
        final List<StringValue> vals = rez.getValues(key);
        if (vals.size() > 0) {
            rez.remove(key, value);
        } else {
            rez.remove(key);
        }
        return rez;
    }


    /**
     * Get the retained request parameters, that not contains given set of request parameter names
     *
     * @param pageParameters original request parameters
     * @param nameFilter     set of parameter name to retain
     * @return new filtered {@link PageParameters}
     */
    public PageParameters getRetainedRequestParameters(final PageParameters pageParameters,
                                                              final Set<String> nameFilter) {
        final PageParameters rez = getFilteredRequestParameters(pageParameters);
        for (String keyName : rez.getNamedKeys()) {
            if (!nameFilter.contains(keyName)) {
                rez.remove(keyName);

            }
        }
        return rez;
    }


    /**
     * Get selected items on page.
     *
     * @param pageParameters     page parameters from http request
     * @param itemsPerPageValues allowed values
     * @return selected items per page value if it in allowed list otherwise {@link WicketUtil#DEFAULT_ITEMS} value
     */
    public int getSelectedItemsPerPage(final PageParameters pageParameters, final List<String> itemsPerPageValues) {
        int result = DEFAULT_ITEMS;
        if (itemsPerPageValues != null && !itemsPerPageValues.isEmpty()) {
            String selectedItemPerPage = pageParameters.get(WebParametersKeys.QUANTITY).toString();
            if (!itemsPerPageValues.contains(selectedItemPerPage)) {
                selectedItemPerPage = itemsPerPageValues.get(0);
            }
            result = NumberUtils.toInt(selectedItemPerPage);
        }
        return result;
    }

    /**
     * Get selected product sorting on page.
     *
     * @param pageParameters page parameters from http request
     * @param sortOrder      current sorting order
     * @param sortField      current sorting field
     * @return true in case if given sorting order and field present in page parameters.
     */
    public boolean isSelectedProductSortOnPage(final PageParameters pageParameters, final String sortOrder, final String sortField) {
        return ((StringUtils.isNotBlank(sortOrder) && StringUtils.isNotBlank(sortField)))
                && (sortField.equals(pageParameters.get(sortOrder).toString()));
    }

    /**
     * Get selected product sorting on page.
     *
     * @param pageParameters    page parameters from http request
     * @param pageParameterName page parameter name.
     * @param page              current page
     * @return true in case if given sorting order and field present in page parameters.
     */
    public boolean isSelectedPageActive(final PageParameters pageParameters, final String pageParameterName, final int page) {
        return ((StringUtils.isNotBlank(pageParameterName)))
                && page == pageParameters.get(pageParameterName).toInt(0);
    }


    /**
     * Construct string which only has Latin characters and digits. If values contain non latin characters or
     * non digits then hex code is used for that character instead.
     *
     * E.g.
     *  if values are "color", "blue" then "colorblue" will be result of this method.
     *  if values are "color", "синий" then result is "color10891080108510801081".
     *
     * @param values array of string value
     *
     * @return latin characters and digits string
     */
    public String constructLatinStringValue(final String ... values) {
        final StringBuilder css = new StringBuilder();
        for (final String value : values) {
            for (final char c : value.toCharArray()) {
                if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                    css.append(c);
                } else {
                    css.append((int) c);
                }
            }
        }
        return css.toString();
    }

    /**
     * Create simple string resource model for wicket.
     *
     * @param component component to provide resources
     * @param resourceKey key
     *
     * @return simple label
     */
    public static StringResourceModel createStringResourceModel(final Component component,
                                                                final String resourceKey) {

        return new StringResourceModel(resourceKey, component, null);

    }

    /**
     * Create parametrized string resource model for wicket. (i.e. "The ${animal} jumped over the ${target}.")
     *
     * @param component component to provide resources
     * @param resourceKey key
     * @param parameters parameters which are specified by ${name} placeholders in the resource key
     *
     * @return parametrized label
     */
    public static StringResourceModel createStringResourceModel(final Component component,
                                                                final String resourceKey,
                                                                final Map<String, Object> parameters) {
        if (parameters != null) {
            final ValueMap params = new ValueMap(parameters);
            return new StringResourceModel(resourceKey, component, new Model<ValueMap>(params));
        }
        return new StringResourceModel(resourceKey, component, null);
    }

}
