package org.yes.cart.payment.service.impl;

import org.dbunit.AbstractDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileOutputStream;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 10:45:45
 */
public abstract class BasePaymentModuleDBTestCase {

    private ApplicationContext ctx;
    private SessionFactory sessionFactory;
    private Session session;
    private AbstractDatabaseTester dbTester;

    @Rule
    public ExternalResource dbResource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            ctx = createContext();
            sessionFactory = (SessionFactory) ctx().getBean("paySessionFactory");
            session = sessionFactory.openSession();
            dbTester = createDatabaseTester();
            dbTester.onSetup();
        }

        @Override
        protected void after() {
            try {
                dbTester.onTearDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //sessionFactory.close();
            session.close();
        }
    };

    public ApplicationContext ctx() {
        return ctx;
    }

    protected ApplicationContext createContext() {
        return new ClassPathXmlApplicationContext("test-payment-api.xml");
    }

    protected AbstractDatabaseTester createDatabaseTester() throws Exception {
        AbstractDatabaseTester dbTester = new JdbcDatabaseTester("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:testnpapaydb", "sa", "");
        dbTester.setSetUpOperation(DatabaseOperation.REFRESH);
        dbTester.setTearDownOperation(DatabaseOperation.NONE);
        dbTester.setDataSet(createDataSet());
        return dbTester;
    }

    protected IDataSet createDataSet() throws Exception {
        return new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream("payinitialdata.xml"), false);
    }

    protected void dumpDataBase(String prefix, String[] tables) throws Exception {
        QueryDataSet queryDataSet = new QueryDataSet(dbTester.getConnection());
        for (String tableName : tables) {
            queryDataSet.addTable(tableName);
        }
        FlatXmlDataSet.write(queryDataSet,
                new FileOutputStream("target/test-classes/" + prefix + "_dataset.xml"));
    }
}