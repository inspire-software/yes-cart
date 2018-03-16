package org.yes.cart.service.async.impl;

import org.slf4j.Logger;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.async.model.JobStatus;
import org.yes.cart.util.log.Markers;

/**
 * User: denispavlov
 * Date: 10/11/2015
 * Time: 15:14
 */
public class JobStatusListenerLoggerWrapperImpl implements JobStatusListener {

    private final Logger logger;


    public JobStatusListenerLoggerWrapperImpl(final Logger logger) {
        this.logger = logger;
    }

    /** {@inheritDoc} */
    @Override
    public JobStatus getLatestStatus() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getJobToken() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void notifyPing() {

    }

    /** {@inheritDoc} */
    @Override
    public void notifyPing(final String msg) {

    }

    /** {@inheritDoc} */
    @Override
    public void notifyMessage(final String message) {
        logger.debug(message);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyWarning(final String warning) {
        logger.warn(warning);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error) {
        logger.error(error);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyError(final String error, final Exception exp) {
        logger.error(Markers.alert(), error, exp);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyCompleted() {

    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public long getTimeoutValue() {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTimedOut() {
        return false;
    }
}
