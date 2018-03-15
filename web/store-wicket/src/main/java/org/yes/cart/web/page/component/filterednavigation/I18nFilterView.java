package org.yes.cart.web.page.component.filterednavigation;

import java.util.List;

/**
 * User: denispavlov
 * Date: 27/11/2017
 * Time: 11:51
 */
public class I18nFilterView extends BaseFilterView {

    /**
     * Construct base filter view.
     *
     * @param id       view id.
     * @param code     attribute code
     * @param head     the head of widget
     * @param linkList the content of filtering widget.
     * @param recordLimit max number of records to display
     */
    public I18nFilterView(final String id,
                          final String code,
                          final String head,
                          final List<AbstractProductFilter.Value> linkList,
                          final int recordLimit) {
        super(id, code, head, linkList, recordLimit);
    }

    @Override
    protected String getValueLabel(final AbstractProductFilter.Value keyValue) {
        try {
            return getString(keyValue.getLabel());
        } catch (Exception exp) {
            // catch for wicket unknown resource key exception
        }
        return super.getValueLabel(keyValue);
    }
}
