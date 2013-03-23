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

package org.yes.cart.shoppingcart.impl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.util.ShopCodeContext;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Map;

/**
 * .
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:29:32 PM
 *
 */
public class ShoppingCartCommandFactoryImpl implements ShoppingCartCommandFactory, ApplicationContextAware {

    private static final long serialVersionUID = 20100122L;

    private Map<String, Class<? extends ShoppingCartCommand>> commands;

    private ApplicationContext applicationContext;

    /**
     * IoC constructor.
     *
     * @param commands configured commands
     */
    public ShoppingCartCommandFactoryImpl(final Map<String, Class<? extends ShoppingCartCommand>> commands) {
        this.commands = commands;
    }


    /**
     * IoC.
     *
     * @param commands
     */
    public void setCommands(final Map<String, Class<? extends ShoppingCartCommand>> commands) {
        this.commands = commands;
    }
    
    private String getCmdKey(final Map pageParameters) {
        for (String cm : commands.keySet()) {
            if (pageParameters.containsKey(cm)) {
                return cm;
            }
        }
        return null;
    }

    /** {@inheritDoc */
    @SuppressWarnings({"unchecked"})
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /** {@inheritDoc */
    public ShoppingCartCommand create(final Map pageParameters) {
        String cmdKey = getCmdKey(pageParameters);
        if (cmdKey != null) {
            Class<? extends ShoppingCartCommand> shoppingCartCommandClass = commands.get(cmdKey);
            if (shoppingCartCommandClass != null) {
                try {
                    Constructor<? extends ShoppingCartCommand> constructor =
                            shoppingCartCommandClass.getConstructor(
                                    ApplicationContext.class,
                                    Map.class);
                    return constructor.newInstance(applicationContext, pageParameters);
                } catch (Exception e) {
                    ShopCodeContext.getLog(this).error(
                            MessageFormat.format(
                                    "Can not create command instance for given key {0}. Is appropriate constuctor with ApplicationContext and Map parameters existis ?", cmdKey),
                            e);
                }
            }
            ShopCodeContext.getLog(this).error("Command instance not found for given key {}", cmdKey);
        }
        return null;
    }

}
