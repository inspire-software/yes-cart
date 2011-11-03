package org.yes.cart.service.domain.impl;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class BaseCoreDBTestCase extends DBTestCase {

    /**
     * A Spring application context that we'll create from a
     * test application context and use to create
     * our DAO object (and data source, session factory, etc.)
     */
    protected ApplicationContext ctx;
    protected SessionFactory sessionFactory;
    protected Session session;

    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(
                new File("../persistence/src/test/resources/initialdata.xml"),
                false,
                true
        );
    }

    protected void dumpDataBase(final String prefix, final String[] tables) {
        try {
            QueryDataSet queryDataSet = new QueryDataSet(getConnection());
            for (String tableName : tables) {
                queryDataSet.addTable(tableName);
            }
            FlatXmlDataSet.write(queryDataSet,
                    new FileOutputStream("target/test-classes/" + this.getClass().getName() + "_" + prefix + "_dataset.xml"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.REFRESH;
    }

    /**
     * {@inheritDoc}
     */
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.NONE;
    }

    public BaseCoreDBTestCase() {
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:hsqldb:mem:testnpadb");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
    }

    @Before
    public void setUp() throws Exception {
        if (DomainTestSuite.sharedContext == null) {
            DomainTestSuite.sharedContext = new ClassPathXmlApplicationContext(
                    "testApplicationContext.xml",
                    "core-aspects.xml");
        }
        ctx = DomainTestSuite.sharedContext;
        sessionFactory = (SessionFactory) ctx.getBean("sessionFactory");
        session = sessionFactory.openSession();
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        try {
            JMSServerManagerImpl jmsServerManager = (JMSServerManagerImpl) ctx.getBean("jmsServerManagerImpl");
            if (jmsServerManager != null) {
                jmsServerManager.stop();
            }
        } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            //nothing
        }
        sessionFactory.close();
        session.close();
    }
}
