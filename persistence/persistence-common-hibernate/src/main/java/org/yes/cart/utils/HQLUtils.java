/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.jpa.TypedParameterValue;
import org.hibernate.type.IntegerType;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 03/10/2017
 * Time: 15:01
 */
public final class HQLUtils {

    private HQLUtils() {
        // no instance
    }

    /**
     * Convert value to correct format for ilike match anywhere mode.
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static String criteriaIlikeAnywhere(final String value) {
        if (StringUtils.isBlank(value)) {
            return null; // no value
        }
        return "%" + value.toLowerCase() + "%";
    }


    /**
     * Convert value to correct format for like match anywhere mode.
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static String criteriaLikeAnywhere(final String value) {
        if (StringUtils.isBlank(value)) {
            return null; // no value
        }
        return "%" + value + "%";
    }


    /**
     * Convert value to correct format for case insensitive eq.
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static String criteriaIeq(final String value) {
        if (StringUtils.isBlank(value)) {
            return null; // no value
        }
        return value.toLowerCase();
    }

    /**
     * Convert value to correct format for case insensitive eq.
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static String criteriaEq(final String value) {
        if (StringUtils.isBlank(value)) {
            return null; // no value
        }
        return value;
    }

    /**
     * Create a test value to check if collection is empty or not.
     *
     * First part of check in form of: ?1 = 0 or e.x in (?2)
     * Sets ?1 to null or "1"
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static Object criteriaInTest(final Collection value) {
        if (CollectionUtils.isEmpty(value)) {
            return new TypedParameterValue(IntegerType.INSTANCE, 0); // no value
        }
        return new TypedParameterValue(IntegerType.INSTANCE, 1);
    }

    /**
     * Create a test value to check if collection is empty or not.
     *
     * Second part of check in form of: ?1 = 0 or e.x in (?2)
     * Sets ?2 to ["x"] or collection
     *
     * @param value raw value
     *
     * @return null or match anywhere
     */
    public static Collection criteriaIn(final Collection value) {
        if (CollectionUtils.isEmpty(value)) {
            return Collections.singletonList("x"); // no value
        }
        return value;
    }

    /**
     * Helper method for generic dynamic appending of "not contains" using blacklisting filter.
     *
     * @param toAppendTo    query to 'and' criteria to
     * @param params        parameter value for criteria
     * @param selector      property selector (e.g. 'cse.customer')
     * @param blacklist     filter selection for this criteria clause
     */
    public static void appendBlacklistCriteria(final StringBuilder toAppendTo,
                                               final List<Object> params,
                                               final String selector,
                                               final List<Pair<String, List>> blacklist) {

        if (blacklist != null) {

            final String join = " and ";

            boolean hasOneCriteria = false;

            for (final Pair<String, List> props : blacklist) {

                final List prop = props.getSecond();

                if (CollectionUtils.isNotEmpty(prop)) {

                    for (final Object val : prop) {

                        String matchProp;
                        String matchMode = " not like ";
                        Object matchValue;
                        if (val instanceof String) {

                            if (StringUtils.isBlank((String) val)) {
                                continue; // skip empty values
                            }
                            matchProp = " lower(" + selector + "." + props.getFirst() + ") ";
                            matchValue = HQLUtils.criteriaIlikeAnywhere((String) val);

                        } else {

                            matchProp = " " + selector + "." + props.getFirst() + " ";
                            matchValue = val;

                        }

                        final int pIdx = params.size() + 1;

                        if (hasOneCriteria) {
                            toAppendTo.append(join);
                        } else {
                            if (pIdx == 1) {
                                toAppendTo.append(" where (\n");
                            } else {
                                toAppendTo.append(" and (\n");
                            }
                        }

                        toAppendTo.append(" (" + matchProp + matchMode + "?" + pIdx + ") \n");
                        params.add(matchValue);

                        hasOneCriteria = true;

                    }
                }
            }

            if (hasOneCriteria) {
                toAppendTo.append(")");
            }

        }

    }

    /**
     * Helper method for generic dynamic appending of criteria using filter object.
     *
     * @param toAppendTo    query to 'and' criteria to
     * @param params        parameter value for criteria
     * @param selector      property selector (e.g. 'cse.customer')
     * @param filter        filter selection for this criteria clause
     */
    public static void appendFilterCriteria(final StringBuilder toAppendTo,
                                            final List<Object> params,
                                            final String selector,
                                            final Map<String, List> filter) {

        if (filter != null) {

            final List joinObj = filter.remove(SearchContext.JOIN);
            final String join;
            if (joinObj != null && !joinObj.isEmpty() && SearchContext.JoinMode.OR.matches(joinObj.get(0))) {
                join = " or ";
            } else {
                join = " and ";
            }

            boolean hasOneCriteria = false;

            for (final Map.Entry<String, List> props : filter.entrySet()) {

                final List prop = props.getValue();

                if (CollectionUtils.isNotEmpty(prop)) {

                    for (final Object val : prop) {

                        String matchProp;
                        String matchMode = " like ";
                        boolean nullCheck = false;
                        Object matchValue;
                        if (val instanceof String) {

                            if (StringUtils.isBlank((String) val)) {
                                continue; // skip empty values
                            }
                            matchProp = " lower(" + selector + "." + props.getKey() + ") ";
                            matchValue = HQLUtils.criteriaIlikeAnywhere((String) val);

                        } else if (val instanceof Pair) {


                            final SearchContext.MatchMode mm = (SearchContext.MatchMode) ((Pair) val).getFirst();
                            switch (mm) {
                                case LT:
                                    matchMode = " < ";
                                    break;
                                case LE:
                                    matchMode = " <= ";
                                    break;
                                case EQ:
                                    matchMode = " = ";
                                    break;
                                case GE:
                                    matchMode = " >= ";
                                    break;
                                case GT:
                                    matchMode = " > ";
                                    break;
                                case ANY:
                                    matchMode = " in ";
                                    break;
                                case NULL:
                                    matchMode = " is null ";
                                    nullCheck = true;
                                    break;
                                case NOTNULL:
                                    matchMode = " is not null ";
                                    nullCheck = true;
                                    break;
                                case EMPTY:
                                    matchMode = " = '' ";
                                    nullCheck = true;
                                    break;
                                case NOTEMPTY:
                                    matchMode = " != '' ";
                                    nullCheck = true;
                                    break;
                            }

                            matchProp = " " + selector + "." + props.getKey() + " ";
                            if (!nullCheck) {
                                matchValue = ((Pair) val).getSecond();
                                if (matchValue == null) {
                                    continue; // skip empty values
                                }
                                final boolean isString = matchValue instanceof String;
                                if (isString) {
                                    if (StringUtils.isBlank((String) matchValue)) {
                                        continue; // skip empty values
                                    }
                                    matchProp = " lower(" + selector + "." + props.getKey() + ") ";
                                    matchValue = HQLUtils.criteriaIeq((String) matchValue);
                                }
                            } else {
                                matchValue = null;
                            }

                        } else {

                            matchProp = " " + selector + "." + props.getKey() + " ";
                            matchValue = val;

                        }

                        final int pIdx = params.size() + 1;

                        if (hasOneCriteria) {
                            toAppendTo.append(join);
                        } else {
                            if (pIdx == 1) {
                                toAppendTo.append(" where (\n");
                            } else {
                                toAppendTo.append(" and (\n");
                            }
                        }

                        if (nullCheck) {
                            toAppendTo.append(" (" + matchProp + matchMode + ") \n");
                        } else {
                            toAppendTo.append(" (" + matchProp + matchMode + "?" + pIdx + ") \n");
                            params.add(matchValue);
                        }
                        hasOneCriteria = true;

                    }
                }
            }

            if (hasOneCriteria) {
                toAppendTo.append(")");
            }

        }

    }

}
