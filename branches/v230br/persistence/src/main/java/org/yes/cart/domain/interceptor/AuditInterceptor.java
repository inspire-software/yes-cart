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

package org.yes.cart.domain.interceptor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.yes.cart.domain.entity.Auditable;
import org.yes.cart.domain.entity.Codable;
import org.yes.cart.domain.entity.Guidable;
import org.yes.cart.domain.entity.Identifiable;

import java.io.Serializable;
import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 * <p/>
 * Audit interceptor for entities.
 */
public class AuditInterceptor extends EmptyInterceptor {

    private final Logger LOG = LoggerFactory.getLogger("AUDIT");

    private Map<String, Set<String>> prohibitedFields = new HashMap<String, Set<String>>();

    public AuditInterceptor() {
        // set logging headers
        LOG.info("Operation,Class,PK,user,guid");
    }

    private String getUserName() {

        if (SecurityContextHolder.getContext() != null) {

            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof User) {

                return ((User) auth.getPrincipal()).getUsername();

            }

        }

        return "anonymous";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onSave(final Object entity, final Serializable id,
                          final Object[] state, final String[] propertyNames, final Type[] types) {
        if (entity instanceof Guidable) {
            final Guidable guidable = (Guidable) entity;

            if (StringUtils.isBlank(guidable.getGuid())) {
                setGuidValue(state, propertyNames, guidable);
            }

        }

        if (entity instanceof Auditable) {
            final Auditable auditable = ((Auditable) entity);

            final Date date = new Date();
            final String userName = getUserName();

            setValue(state, propertyNames, "createdBy", userName);
            auditable.setCreatedBy(userName);
            setValue(state, propertyNames, "createdTimestamp", date);
            auditable.setCreatedTimestamp(date);

            setValue(state, propertyNames, "updatedBy", userName);
            auditable.setUpdatedBy(userName);
            setValue(state, propertyNames, "updatedTimestamp", date);
            auditable.setUpdatedTimestamp(date);

            logOperation("SAVE", (Auditable) entity, userName, id, state, propertyNames, types);
        }

        return super.onSave(entity, id, state, propertyNames, types);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDelete(final Object entity, final Serializable id,
                         final Object[] state, final String[] propertyNames, final Type[] types) {

        if (entity instanceof Auditable) {
            final String userName = getUserName();
            logOperation("DELETE", (Auditable) entity, userName, id, state, propertyNames, types);
        }
        super.onDelete(entity, id, state, propertyNames, types);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onFlushDirty(final Object entity, final Serializable id,
                                final Object[] currentState, final Object[] previousState, final String[] propertyNames, final Type[] types) {

        if (entity instanceof Guidable) {
            final Guidable guidable = (Guidable) entity;

            if (StringUtils.isBlank(guidable.getGuid())) {
                setGuidValue(currentState, propertyNames, guidable);
            }

        }

        if (entity instanceof Auditable) {
            final Auditable auditable = (Auditable) entity;

            final Date date = new Date();
            final String userName = getUserName();


            if (auditable.getCreatedTimestamp() == null) {
                setValue(currentState, propertyNames, "createdBy", userName);
                auditable.setCreatedBy(userName);
                setValue(currentState, propertyNames, "createdTimestamp", date);
                auditable.setCreatedTimestamp(date);
            }

            setValue(currentState, propertyNames, "updatedBy", userName);
            auditable.setUpdatedBy(userName);
            setValue(currentState, propertyNames, "updatedTimestamp", date);
            auditable.setUpdatedTimestamp(date);

            logOperation("FLUSH-PREV", (Auditable) entity, userName, id, previousState, propertyNames, types);
            logOperation("FLUSH-CURR", (Auditable) entity, userName, id, currentState, propertyNames, types);
        }

        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);

    }

    private void logOperation(final String operation, final Auditable entity, final String user,
                              final Serializable id, final Object[] state, final String[] propertyNames, final Type[] types) {
        if (LOG.isTraceEnabled()) {

            final String className = entity.getClass().getSimpleName();
            final Set<String> prohibited = prohibitedFields.get(className);

            final StringBuilder line = new StringBuilder();
            line.append(operation);
            line.append(",\"");
            line.append(className);
            line.append("\",\"");
            if (id == null || (id instanceof Number && ((Number) id).longValue() <= 0L)) {
                line.append("N/A");
            } else {
                line.append(id);
            }
            line.append("\",\"");
            line.append(user);
            line.append("\",\"");
            line.append(entity.getGuid());
            line.append('"');
            if (state != null) {
                for (int i = 0; i < propertyNames.length; i++) {
                    final Type type = types[i];
                    if (type.isCollectionType()) {
                        continue; // skip collections
                    }
                    final String prop = propertyNames[i];
                    final Object value = state[i];

                    line.append(",\"");
                    line.append(prop);
                    line.append(":");

                    if (prohibited == null || !prohibited.contains(prop)) {
                        if (type.isEntityType()) {
                            if (Hibernate.isInitialized(value) && value instanceof Identifiable) {
                                line.append(((Identifiable) value).getId());
                            } else {
                                line.append("lazy");
                            }
                        } else {

                            if (value instanceof String) {
                                line.append(((String) value).replace('"','\''));
                            } else {
                                line.append(value);
                            }

                        }
                    } else {
                        line.append("[prohibited]");
                    }

                    line.append("\"");

                }
            }
            LOG.trace(line.toString());
        }
    }

    private void setValue(final Object[] currentState, final String[] propertyNames,
                          final String propertyToSet, final Object value) {
        int index = Arrays.asList(propertyNames).indexOf(propertyToSet);
        if (index > -1) {
            currentState[index] = value;
        }
    }

    private void setGuidValue(final Object[] objects, final String[] propertyNames, final Guidable guidable) {
        final String guid;

        if (guidable instanceof Codable) {
            // put code value as GUID since this will make it more intuitive to work with these objects
            // from the business perspective (e.g. Bulk import is a lot better done on product code
            // rather than on PK's or random GUID's)
            guid = ((Codable) guidable).getCode();
        } else {
            guid = java.util.UUID.randomUUID().toString();
        }

        setValue(objects, propertyNames, "guid", guid);
        guidable.setGuid(guid);

    }


    /**
     * Set field that should not output to audit log due to security reasons.
     *
     * @param prohibitedFields class to fields map
     */
    public void setProhibitedFields(final Map<String, Set<String>> prohibitedFields) {
        if (prohibitedFields != null) {
            this.prohibitedFields.putAll(prohibitedFields);
        }
    }


}