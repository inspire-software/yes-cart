package org.yes.cart.shoppingcart.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartCommandFactoryImpl.class);

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
                    LOG.error(
                            MessageFormat.format(
                                    "Can not create command instance for given key {0}. Is appropriate constuctor with ApplicationContext and Map parameters existis ?", cmdKey),
                            e);
                }
            }
            LOG.error(MessageFormat.format("Command instance not found for given key {0}", cmdKey));
        }
        return null;
    }

}
