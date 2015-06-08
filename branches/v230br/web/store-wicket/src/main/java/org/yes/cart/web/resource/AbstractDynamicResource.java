/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.web.resource;

/**
 * User: denispavlov
 * Date: 13-08-28
 * Time: 3:51 PM
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.time.Time;

import javax.servlet.http.HttpServletResponse;

/**
 * Base class for dynamically generated Resources.
 */
public abstract class AbstractDynamicResource extends AbstractResource
{
    private static final long serialVersionUID = 1L;

    /** The type */
    private String format;

    /** The last modified time of this resource */
    private Time lastModifiedTime;

    /**
     * Creates a dynamic resource from for the given locale
     *
     * @param format
     *            The image format ("png", "jpeg", etc)
     */
    public AbstractDynamicResource(String format)
    {
        setFormat(format);
    }

    /**
     * @return Returns the image format.
     */
    public synchronized final String getFormat()
    {
        return format;
    }

    /**
     * Sets the format of this resource
     *
     * @param format
     *            The format (jpg, png or gif..)
     */
    public synchronized final void setFormat(String format)
    {
        Args.notNull(format, "format");
        this.format = format;
    }

    /**
     * set the last modified time for this resource.
     *
     * @param time time
     */
    protected synchronized void setLastModifiedTime(Time time)
    {
        lastModifiedTime = time;
    }

    /**
     * Get image data for our dynamic image resource. If the subclass regenerates the data, it
     * should set the {@link AbstractDynamicResource#setLastModifiedTime(org.apache.wicket.util.time.Time)} when it does so. This
     * ensures that image caching works correctly.
     *
     * @param attributes
     *            the context bringing the request, response and the parameters
     *
     * @return The image data for this dynamic image. {@code null} means there is no image and 404
     *         (Not found) response will be return.
     */
    protected abstract byte[] getData(Attributes attributes);


    protected void configureResponse(final ResourceResponse response, final Attributes attributes)
    {

    }

    @Override
    protected ResourceResponse newResourceResponse(final Attributes attributes)
    {
        final ResourceResponse response = new ResourceResponse();

        if (lastModifiedTime != null)
        {
            response.setLastModified(lastModifiedTime);
        }
        else
        {
            response.setLastModified(Time.now());
        }

        if (response.dataNeedsToBeWritten(attributes))
        {
            response.setContentType(getFormat());

            response.setContentDisposition(ContentDisposition.INLINE);

            final byte[] imageData = getData(attributes);
            if (imageData == null)
            {
                response.setError(HttpServletResponse.SC_NOT_FOUND);
            }
            else
            {
                response.setWriteCallback(new WriteCallback()
                {
                    @Override
                    public void writeData(final Attributes attributes)
                    {
                        attributes.getResponse().write(imageData);
                    }
                });

                configureResponse(response, attributes);
            }
        }

        return response;
    }
}
