/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2020, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.clustering.web.spring.hotrod;

import java.net.URL;

import org.infinispan.protostream.SerializationContextInitializer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.clustering.web.spring.hotrod.context.ConfigContextLoaderListener;
import org.wildfly.clustering.web.spring.hotrod.servlet.SessionServlet;
import org.wildfly.clustering.web.spring.servlet.ServletHandler;
import org.wildfly.clustering.web.spring.servlet.TestSerializationContextInitializer;

/**
 * @author Paul Ferraro
 */
@RunWith(Arquillian.class)
@RunAsClient
public class AnnotationSmokeITCase extends AbstractSmokeITCase {
    public static final String CONTAINER_1 = "tomcat-1";
    public static final String CONTAINER_2 = "tomcat-2";
    public static final String DEPLOYMENT_1 = "deployment-1";
    public static final String DEPLOYMENT_2 = "deployment-2";

    @Deployment(name = DEPLOYMENT_1, testable = false)
    @TargetsContainer(CONTAINER_1)
    public static Archive<?> deployment1() {
        return deployment();
    }

    @Deployment(name = DEPLOYMENT_2, testable = false)
    @TargetsContainer(CONTAINER_2)
    public static Archive<?> deployment2() {
        return deployment();
    }

    private static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class, AnnotationSmokeITCase.class.getSimpleName() + ".war")
                .addPackage(ServletHandler.class.getPackage())
                .addPackage(SessionServlet.class.getPackage())
                .addPackage(ConfigContextLoaderListener.class.getPackage())
                .addAsWebInfResource(AnnotationSmokeITCase.class.getPackage(), "applicationContext-annotation.xml", "applicationContext.xml")
                .addAsServiceProvider(SerializationContextInitializer.class.getName(), TestSerializationContextInitializer.class.getName() + "Impl")
                ;
    }

    @Override
    @Test
    public void test(@ArquillianResource(SessionServlet.class) @OperateOnDeployment(DEPLOYMENT_1) URL baseURL1, @ArquillianResource(SessionServlet.class) @OperateOnDeployment(DEPLOYMENT_2) URL baseURL2) throws Exception {
        super.test(baseURL1, baseURL2);
    }
}
