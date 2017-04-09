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

package org.yes.cart.search.dao.entity;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import org.apache.lucene.util.BytesRef;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.StoredAttributes;
import org.yes.cart.domain.entity.impl.StoredAttributesImpl;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.util.Date;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 07/04/2017
 * Time: 14:42
 */
public class LuceneDocumentAdapterUtils {

    private static final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    public static final String FIELD_PK = "_PK";
    public static final String FIELD_CLASS = "_CLASS";
    public static final String FIELD_OBJECT = "_OBJECT";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {

        MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        SimpleModule module = new SimpleModule("search", new Version(3, 4, 0, null));
        module.addAbstractTypeMapping(StoredAttributes.class, StoredAttributesImpl.class);
        module.addAbstractTypeMapping(I18NModel.class, StringI18NModel.class);

        MAPPER.registerModule(module);

    }

    private LuceneDocumentAdapterUtils() {
        // no instance
    }

    /**
     * Reads object from stored field.
     *
     * @param document document
     * @param clazz    object type
     */
    public static <T> T readObjectDefaultField(final Document document, final Class<T> clazz) {

        return readObjectField(document, FIELD_OBJECT, clazz);

    }

    /**
     * Reads object from stored field.
     *
     * @param document document
     * @param name     field name
     * @param clazz    object type
     */
    public static <T> T readObjectField(final Document document, final String name, final Class<T> clazz) {

        final String serialized = document.get(FIELD_OBJECT);
        if (StringUtils.isNotBlank(serialized)) {
            try {
                return (T) MAPPER.readValue(serialized, clazz);
            } catch (Exception exp) {
                LOGFTQ.error("Unable to de-serialise the object in field: " + name + ", object: " + serialized, exp);
            }
        }
        return null;
    }


    /**
     * Reads PK from stored field.
     *
     * @param document document
     */
    public static String readPkField(final Document document) {

        return document.get(FIELD_PK);

    }


    /**
     * Adds serialised version of object into stored field.
     *
     * @param document document
     * @param object   object
     */
    public static void addObjectDefaultField(final Document document, final Object object) {
        addObjectField(document, FIELD_OBJECT, object);
    }

    /**
     * Adds serialised version of object into stored field.
     *
     * @param document document
     * @param name     field name
     * @param object   object
     */
    public static void addObjectField(final Document document, final String name, final Object object) {
        if (object != null) {
            try {
                document.add(new StringField(name, MAPPER.writeValueAsString(object), Field.Store.YES));
            } catch (Exception exp) {
                LOGFTQ.error("Unable to serialise the object into field: " + name + ", object: " + object, exp);
            }
        }
    }

    /**
     * Adds a string field (stored and not tokenized). See {@link #FIELD_PK}.
     *
     * @param document document
     * @param clazz    field name
     * @param value    value
     */
    public static void addPkField(final Document document, Class clazz, final String value) {
        if (value != null) {

            /*
                Numeric field allows facet to have ranges. However for normal index it can be a simple
                string field. If a NumericDocValuesField is used, StringField with stored attribute is
                still needed for the projections.

                This is optional and should be declared separately as facet field
             */
            // document.add(new SortedNumericDocValuesField(FIELD_PK, NumberUtils.toLong(value)));

            /*
                Stored string is required for projections i.e. document.get('_PK')
             */
            document.add(new StringField(FIELD_PK, value, Field.Store.YES));

            /*
                Additional field to save type of the object
             */
            document.add(new StringField(FIELD_CLASS, clazz.getName(), Field.Store.YES));
        }
    }

    /**
     * Adds a string field (stored and not tokenized)
     *
     * @param document document
     * @param name     field name
     * @param value    value
     */
    public static void addStoredField(final Document document, final String name, final String value) {
        if (value != null) {
            document.add(new StringField(name, value, Field.Store.YES));
        }
    }


    /**
     * Adds a string field (not stored and not tokenized)
     *
     * @param document document
     * @param name     field name
     * @param value    value
     */
    public static void addSimpleField(final Document document, final String name, final String value) {
        if (value != null) {
            document.add(new StringField(name, value, Field.Store.NO));
        }
    }


    /**
     * Adds a string field (stored and not tokenized)
     *
     * @param document document
     * @param name     field name
     * @param model    value
     */
    public static void addSimpleFields(final Document document, final String name, final I18NModel model) {
        if (model != null && !model.getAllValues().isEmpty()) {
            for (final String value : model.getAllValues().values()) {
                addSimpleField(document, name, value);
            }
        }
    }


    /**
     * Adds a long point field with time long value
     *
     * @param document     document
     * @param name         field name
     * @param value        value
     * @param negativeNull true if null values are to be filled with {@link Long#MIN_VALUE}, false if to be filled with {@link Long#MAX_VALUE}
     */
    public static void addDateField(final Document document,
                                    final String name,
                                    final Date value,
                                    final boolean negativeNull) {

        final long datetime = value != null ? value.getTime() : (negativeNull ? Long.MIN_VALUE : Long.MAX_VALUE);
        /*
            LongPoint allows ranges as well LongPoint.newRangeQuery()
         */
        document.add(new LongPoint(name, datetime));

    }



    /**
     * Adds a long point field with long value
     *
     * @param document     document
     * @param name         field name
     * @param value        value
     * @param negativeNull true if null values are to be filled with {@link Long#MIN_VALUE}, false if to be filled with {@link Long#MAX_VALUE}
     */
    public static void addNumericField(final Document document,
                                       final String name,
                                       final Long value,
                                       final boolean negativeNull) {

        final long nonNull = value != null ? value : (negativeNull ? Long.MIN_VALUE : Long.MAX_VALUE);
        /*
            LongPoint allows ranges as well LongPoint.newRangeQuery()
         */
        document.add(new LongPoint(name, nonNull));

    }



    /**
     * Adds a sort field (with computed sort in index)
     *
     * To be able to sort field has to be of type Binary or Sorted, so need to
     * declare a separate field for this as SortedDocValuesField. This is not
     * tokenized and also sorting is stored in index.
     *
     * Note that multivalue fields cannot be sorted.
     *
     * @param document document
     * @param name     field name
     * @param value    value
     */
    public static void addSortField(final Document document, final String name, final String value) {
        if (value != null) {
            document.add(new SortedDocValuesField(name, new BytesRef(value.toLowerCase())));
        }
    }

    /**
     * Adds a sort field (with computed sort in index) for every language. For example if name is "field1" then for
     * en locale field1en would be populated with value.
     *
     * To be able to sort field has to be of type Binary or Sorted, so need to
     * declare a separate field for this as SortedDocValuesField. This is not
     * tokenized and also sorting is stored in index.
     *
     * Note that multivalue fields cannot be sorted.
     *
     * @param document document
     * @param name     field name
     * @param model    value
     */
    public static void addSortFields(final Document document, final String name, final I18NModel model) {
        if (model != null && !model.getAllValues().isEmpty()) {
            for (final Map.Entry<String, String> entry : model.getAllValues().entrySet()) {
                addSortField(document, name + entry.getKey(), entry.getValue());
            }
        }
    }


    /**
     * Adds a string field with time long value
     *
     * To be able to sort field has to be of type Binary or Sorted, so need to
     * declare a separate field for this as SortedDocValuesField. This is not
     * tokenized and also sorting is stored in index.
     *
     * Note that multivalue fields cannot be sorted.
     *
     * @param document     document
     * @param name         field name
     * @param value        value
     * @param negativeNull true if null values are to be filled with {@link Long#MIN_VALUE}, false if to be filled with {@link Long#MAX_VALUE}
     */
    public static void addSortField(final Document document,
                                    final String name,
                                    final Date value,
                                    final boolean negativeNull) {

        final long datetime = value != null ? value.getTime() : (negativeNull ? Long.MIN_VALUE : Long.MAX_VALUE);
        document.add(new SortedDocValuesField(name, new BytesRef(String.valueOf(datetime))));

    }

    /**
     * Adds a string field with long value
     *
     * To be able to sort field has to be of type Binary or Sorted, so need to
     * declare a separate field for this as SortedDocValuesField. This is not
     * tokenized and also sorting is stored in index.
     *
     * Note that multivalue fields cannot be sorted.
     *
     * @param document     document
     * @param name         field name
     * @param value        value
     * @param negativeNull true if null values are to be filled with {@link Long#MIN_VALUE}, false if to be filled with {@link Long#MAX_VALUE}
     */
    public static void addSortField(final Document document,
                                    final String name,
                                    final Long value,
                                    final boolean negativeNull) {

        final long notNull = value != null ? value : (negativeNull ? Long.MIN_VALUE : Long.MAX_VALUE);
        document.add(new SortedDocValuesField(name, new BytesRef(String.valueOf(notNull))));

    }


    /**
     * Adds a "stem" text field (not stored, tokenized).
     *
     * @param document document
     * @param name     field name
     * @param value    value
     */
    public static void addStemField(final Document document, final String name, final String value) {
        if (value != null) {
            /*
                Text field is tokenized and indexed, which is what is needed in most cases.
                If the value should not be tokenized a StringField has to be used instead.
             */
            document.add(new TextField(name, value, Field.Store.NO));
        }
    }

    /**
     * Adds a "stem" text field (not stored, tokenized)
     *
     * @param document document
     * @param name     field name
     * @param values   values
     */
    public static void addStemFields(final Document document, final String name, final String ... values) {

        final StringBuilder all = new StringBuilder();
        for (final String value : values) {
            if (value != null) {
                all.append(value).append(' ');
            }
        }
        if (all.length() > 0) {
            document.add(new TextField(name, all.toString(), Field.Store.NO));
        }
    }

    /**
     * Adds a "stem" text field (not stored, tokenized)
     *
     * TODO: add language specific analysers
     *
     * @param document document
     * @param name     field name
     * @param model    value
     */
    public static void addStemFields(final Document document, final String name, final I18NModel model) {
        if (model != null && !model.getAllValues().isEmpty()) {
            addStemFields(document, name, model.getAllValues().values().toArray(new String[model.getAllValues().size()]));
        }
    }


    /**
     * Adds a string field with time long value, sorted field with suffix "_sort" and long point for "_range"
     *
     * @param document     document
     * @param name         field name
     * @param sortName     field name for sort
     * @param rangeName    field name for range searches
     * @param value        value
     * @param negativeNull true if null values are to be filled with {@link Long#MIN_VALUE}, false if to be filled with {@link Long#MAX_VALUE}
     */
    public static void addSortableNumericField(final Document document,
                                               final String name,
                                               final String sortName,
                                               final String rangeName,
                                               final Long value,
                                               final boolean negativeNull) {

        final long nonNull = value != null ? value : (negativeNull ? Long.MIN_VALUE : Long.MAX_VALUE);
        document.add(new StringField(name, String.valueOf(nonNull), Field.Store.YES));
        document.add(new SortedNumericDocValuesField(sortName, nonNull));
        document.add(new LongPoint(rangeName, nonNull));

    }


    /**
     * Adds a simple facet field (not taxonomy)
     *
     * @param document document
     * @param name     field name
     * @param value    value
     */
    public static void addFacetField(final Document document, final String name, final String value) {
        if (value != null) {
            /*
                SortedSetDocValuesFacetField is basic implementation of facets which is kept in the same
                index as the original document, which is what we want most of the time because we want to
                drill down on facets inclusive the search criteria.

                Taxonomy Facets are kept in separate index from the main index because they are stored
                differently in Lucene for efficiency and hence are not suitable.
             */
            document.add(new SortedSetDocValuesFacetField(name, value));
        }
    }

    /**
     * Adds a simple facet field (not taxonomy)
     *
     * @param document document
     * @param name     field name
     * @param value    value
     */
    public static void addFacetField(final Document document, final String name, final Long value) {
        if (value != null) {
            /*
                Numeric field allows facet ranges.
             */
            document.add(new NumericDocValuesField(name, value));
        }
    }


}
