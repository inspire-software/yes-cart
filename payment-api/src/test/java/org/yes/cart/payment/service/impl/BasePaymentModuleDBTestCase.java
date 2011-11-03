package org.yes.cart.payment.service.impl;

import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileOutputStream;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:45:45
 */
public abstract class BasePaymentModuleDBTestCase extends DBTestCase {

    /**
     * A Spring application context that we'll create from a
     * test application context and use to create
     * our DAO object (and data source, session factory, etc.)
     */
    protected ApplicationContext ctx;
    protected SessionFactory sessionFactory;
    protected Session session;

    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream("payinitialdata.xml"), false);
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

    public BasePaymentModuleDBTestCase() {
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "org.hsqldb.jdbcDriver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:hsqldb:mem:testnpapaydb");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "sa");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "");
    }


    @Before
    public void setUp() throws Exception {
        ctx = new ClassPathXmlApplicationContext("test-payment-api.xml");
        sessionFactory = (SessionFactory) ctx.getBean("paySessionFactory");
        session = sessionFactory.openSession();
        super.setUp();
    }

    @Before
    public void setUp(String[] configurationXmls) throws Exception {
        ctx = new ClassPathXmlApplicationContext(configurationXmls);
        sessionFactory = (SessionFactory) ctx.getBean("paySessionFactory");
        session = sessionFactory.openSession();
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        sessionFactory.close();
        session.close();
    }
}