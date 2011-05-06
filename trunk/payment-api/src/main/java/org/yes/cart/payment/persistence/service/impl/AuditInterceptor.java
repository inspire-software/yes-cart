package org.yes.cart.payment.persistence.service.impl;

import org.apache.commons.lang.StringUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.yes.cart.payment.persistence.entity.Auditable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;


/**
 * Audit interseptor for entities.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:22:53
 */
public class AuditInterceptor extends EmptyInterceptor {

    private String getUserName() {

        if (SecurityContextHolder.getContext() != null) {

            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null) {

                if (auth.getPrincipal() instanceof User) {

                    return ((User) auth.getPrincipal()).getUsername();

                }

            }

        }

        return "user unknown";
    }

    private void dumpSecurityContext() {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onSave(final Object entity, final Serializable serializable,
                          final Object[] objects, final String[] propertyNames, final Type[] types) {
        dumpSecurityContext();
        if (entity instanceof Auditable) {
            final Auditable auditable = ((Auditable) entity);

            final Date date = new Date();
            final String userName = getUserName();

            if (StringUtils.isBlank(auditable.getGuid())) {
                final String guid = UUID.randomUUID().toString();
                setValue(objects, propertyNames, "guid", guid);
                auditable.setGuid(guid);
            }

            setValue(objects, propertyNames, "createdBy", userName);
            auditable.setCreatedBy(userName);
            setValue(objects, propertyNames, "createdTimestamp", date);
            auditable.setCreatedTimestamp(date);

            setValue(objects, propertyNames, "updatedBy", userName);
            auditable.setUpdatedBy(userName);
            setValue(objects, propertyNames, "updatedTimestamp", date);
            auditable.setUpdatedTimestamp(date);
        }
        return super.onSave(entity, serializable, objects, propertyNames, types);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onFlushDirty(final Object entity, final Serializable serializable, final Object[] currentState,
                                final Object[] previousState, final String[] propertyNames, final Type[] types) {
        dumpSecurityContext();
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

            if (StringUtils.isBlank(((Auditable) entity).getGuid())) {
                final String guid = UUID.randomUUID().toString();
                setValue(currentState, propertyNames, "guid", guid);
                auditable.setGuid(guid);
            }


            setValue(currentState, propertyNames, "updatedBy", userName);
            auditable.setUpdatedBy(userName);
            setValue(currentState, propertyNames, "updatedTimestamp", date);
            auditable.setUpdatedTimestamp(date);

        }

        return super.onFlushDirty(entity, serializable, currentState, previousState, propertyNames, types);

    }


    private void setValue(final Object[] currentState, final String[] propertyNames,
                          final String propertyToSet, final Object value) {
        int index = Arrays.asList(propertyNames).indexOf(propertyToSet);
        if (index > -1) {
            currentState[index] = value;
        }
    }


}