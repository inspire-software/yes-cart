package org.yes.cart.dao.impl;

import org.dbunit.AbstractDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
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
 * Time: 16:13:01
 */
public abstract class AbstractTestDAO {

    private ApplicationContext ctx;
    private SessionFactory sessionFactory;
    private Session session;
    private AbstractDatabaseTester dbTester;

    @Rule
    public ExternalResource dbResource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            ctx = createContext();
            sessionFactory = (SessionFactory) ctx().getBean("sessionFactory");
            session = sessionFactory.openSession();
            dbTester = createDatabaseTester();
            try {
                dbTester.onSetup();
            } catch (Exception e) {
                throw e;
            }
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
        return new ClassPathXmlApplicationContext("testApplicationContext.xml");
    }

    protected AbstractDatabaseTester createDatabaseTester() throws Exception {
        AbstractDatabaseTester dbTester = new JdbcDatabaseTester("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:testnpadb", "sa", "");
        dbTester.setSetUpOperation(DatabaseOperation.REFRESH);
        dbTester.setTearDownOperation(DatabaseOperation.NONE);
        dbTester.setDataSet(createDataSet());
        return dbTester;
    }

    protected IDataSet createDataSet() throws Exception {
        return new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream("initialdata.xml"), false);
    }

    protected void dumpDataBase(final String prefix, final String[] tables) throws Exception {
        QueryDataSet queryDataSet = new QueryDataSet(dbTester.getConnection());
        for (String tableName : tables) {
            queryDataSet.addTable(tableName);
        }
        FlatXmlDataSet.write(queryDataSet,
                new FileOutputStream("target/test-classes/" + prefix + "_dataset.xml"));

    }

    /**
     * Get db connection to perform verifications.
     *
     * @return db connection
     * @throws Exception in case or error
     */
    public IDatabaseConnection getConnection() throws Exception {
        return dbTester.getConnection();
    }
}
