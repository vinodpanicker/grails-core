package org.codehaus.groovy.grails.orm.hibernate.cfg

import grails.persistence.Entity
import grails.spring.BeanBuilder

import org.apache.commons.dbcp.BasicDataSource
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPlugin
import org.codehaus.groovy.grails.plugins.MockGrailsPluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.hibernate.SessionFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.context.support.StaticMessageSource

/**
 * @author Graeme Rocher
 * @since 1.1
 */
class GORMNamespaceHandlerTests extends GroovyTestCase {

    protected void setUp() {
        super.setUp()
        PluginManagerHolder.pluginManager = new MockGrailsPluginManager()
        PluginManagerHolder.pluginManager.registerMockPlugin([getName: { -> 'hibernate' }] as GrailsPlugin)
    }

    protected void tearDown() {
        super.tearDown()
        PluginManagerHolder.pluginManager = null
    }

   /* void testGORMSessionFromXML() {
        def appCtx = new GenericApplicationContext()

        def beanReader = new XmlBeanDefinitionReader(appCtx)
        def resource = new ByteArrayResource('''<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:gorm="http://grails.org/schema/gorm"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://grails.org/schema/gorm http://grails.org/schema/gorm/gorm.xsd http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.0.xsd">


    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="url" value="jdbc:h2:mem:grailsDB" />
        <property name="password" value="" />
        <property name="username" value="sa" />
        <property name="driverClassName" value="org.h2.Driver" />

    </bean>

    <gorm:sessionFactory base-package="org.grails.samples" data-source-ref="dataSource">
       <property name="hibernateProperties">
            <util:map>
                <entry key="hibernate.hbm2ddl.auto" value="update" />
            </util:map>
        </property>
    </gorm:sessionFactory>

</beans>
'''.bytes)
        beanReader.loadBeanDefinitions(resource)

        GrailsApplication grailsApplication = appCtx.getBean("grailsApplication")

        assertNotNull grailsApplication

        def testDomain = grailsApplication.getDomainClass("org.codehaus.groovy.grails.orm.hibernate.cfg.NamespaceTestBook")

        assertNotNull testDomain

        SessionFactory sessionFactory = appCtx.getBean("sessionFactory")

        assertNotNull sessionFactory

        def book = new NamespaceTestBook(title:"hello")
        book.save(flush:true)

        book = NamespaceTestBook.get(1)

        assertNotNull book
    }   */


    void testGORMSessionFactoryFromScript() {
        def bb = new BeanBuilder()

        def resource = new ByteArrayResource("""
import org.apache.commons.dbcp.BasicDataSource

beans {
   xmlns gorm:"http://grails.org/schema/gorm"


    dataSource(BasicDataSource) {
        driverClassName = "org.h2.Driver"
        url = "jdbc:h2:mem:grailsDB"
        username = "sa"
        password = ""
    }

    gorm.sessionFactory('data-source-ref':'dataSource', 'base-package':'org.codehaus.groovy.grails.orm.hibernate.cfg') {
        hibernateProperties = ['hibernate.show_sql':'true', "hibernate.hbm2ddl.auto":'update']
    }

}
""".bytes)

        bb.loadBeans(resource)

        def appCtx = bb.createApplicationContext()

        GrailsApplication grailsApplication = appCtx.getBean("grailsApplication")

        assertNotNull grailsApplication

        def testDomain = grailsApplication.getDomainClass("org.codehaus.groovy.grails.orm.hibernate.cfg.NamespaceTestBook")

        assertNotNull testDomain

        SessionFactory sessionFactory = appCtx.getBean("sessionFactory")

        assertNotNull sessionFactory

        def book = new NamespaceTestBook(title:"hello")
        book.save(flush:true)

        book = NamespaceTestBook.get(1)

        assertNotNull book
    }

    void testGORMSessionFactory() {
        def bb = new BeanBuilder()

        bb.beans {

            xmlns gorm:"http://grails.org/schema/gorm"

            messageSource(StaticMessageSource)
            dataSource(BasicDataSource) {
                driverClassName = "org.h2.Driver"
                url = "jdbc:h2:mem:grailsDB"
                username = "sa"
                password = ""
            }

            gorm.sessionFactory('data-source-ref':'dataSource', 'base-package':'org.codehaus.groovy.grails.orm.hibernate.cfg', 'message-source-ref':'messageSource') {
                hibernateProperties = ['hibernate.show_sql':'true', "hibernate.hbm2ddl.auto":'update']
            }
        }

        def appCtx = bb.createApplicationContext()

        GrailsApplication grailsApplication = appCtx.getBean("grailsApplication")

        assertNotNull grailsApplication

        def testDomain = grailsApplication.getDomainClass("org.codehaus.groovy.grails.orm.hibernate.cfg.NamespaceTestBook")

        assertNotNull testDomain

        SessionFactory sessionFactory = appCtx.getBean("sessionFactory")

        assertNotNull sessionFactory

        def book = new NamespaceTestBook(title:"hello")
        book.save(flush:true)

        book = NamespaceTestBook.get(1)

        assertNotNull book
    }
}

@Entity
class NamespaceTestBook {
    String title
}
