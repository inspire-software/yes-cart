package org.yes.cart.domain.entityindexer.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entityindexer.StoredAttributes;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 04/11/2015
 * Time: 17:44
 */
public class StoredAttributesImpl implements StoredAttributes {

    public static final String SEPARATOR = "#$#";

    private final Map<String, Pair<String, I18NModel>> values = new HashMap<String, Pair<String, I18NModel>>();

    public StoredAttributesImpl(final Map<String, Pair<String, I18NModel>> values) {
        if (values != null && !values.isEmpty()) {
            this.values.putAll(values);
        }
    }

    public StoredAttributesImpl(final String raw) {
        if (raw != null && raw.length() > 0) {
            final String[] valueTriplets = StringUtils.splitByWholeSeparatorPreserveAllTokens(raw, SEPARATOR);
            for (int i = 0; i < valueTriplets.length - 2; i+=3)  {
                final String key = valueTriplets[i];
                final String value = valueTriplets[i + 1];
                final String displayValue = valueTriplets[i + 2];
                if (value != null && value.length() > 0) {
                    if (displayValue != null && displayValue.length() > 0) {
                        final I18NModel model = new StringI18NModel(displayValue);
                        if (!model.getAllValues().isEmpty()) {
                            values.put(key, new Pair<String, I18NModel>(value, model));
                            continue;
                        }
                    }
                    values.put(key, new Pair<String, I18NModel>(value, null));
                }
            }
        }
    }

    public StoredAttributesImpl() {
    }

    /** {@inheritDoc} */
    public Pair<String, I18NModel> getValue(final String code) {
        return values.get(code);
    }

    /** {@inheritDoc} */
    public void putValue(final String code, final String value, final Object displayValue) {
        if (value != null && value.length() > 0) {
            I18NModel model = null;
            if (displayValue instanceof I18NModel) {
                model = (I18NModel) displayValue;
            } else if (displayValue instanceof String) {
                model = new StringI18NModel((String) displayValue);
                if (model.getAllValues().isEmpty()) {
                    model = null;
                }
            }
            values.put(code, new Pair<String, I18NModel>(value, model));
        } else {
            values.remove(code);
        }
    }

    /** {@inheritDoc} */
    public Map<String, Pair<String, I18NModel>> getAllValues() {
        return values;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        for (final Map.Entry<String, Pair<String, I18NModel>> entry : values.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue().getFirst())) {
                out.append(entry.getKey()).append(SEPARATOR).append(entry.getValue().getFirst()).append(SEPARATOR);
                if (entry.getValue().getSecond() != null) {
                    out.append(entry.getValue().getSecond()).append(SEPARATOR);
                } else {
                    out.append(SEPARATOR);
                }
            }
        }
        return out.toString();
    }

}
