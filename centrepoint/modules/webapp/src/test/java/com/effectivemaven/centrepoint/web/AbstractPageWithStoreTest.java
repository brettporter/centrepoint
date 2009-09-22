package com.effectivemaven.centrepoint.web;

/**
 * Copyright 2009
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.util.tester.WicketTester;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.effectivemaven.centrepoint.maven.repository.RepositoryService;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.effectivemaven.centrepoint.store.MemoryProjectStore;
import com.effectivemaven.centrepoint.store.ProjectStore;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public abstract class AbstractPageWithStoreTest
{
    static class PageWithStoreModule
        extends AbstractModule
    {
        @Override
        protected void configure()
        {
            bind( ProjectStore.class ).to( MemoryProjectStore.class );
            bind( RepositoryService.class ).to( TestRepositoryService.class );
            bind( PluginManager.class ).to( PluginManagerImpl.class ).in( Scopes.SINGLETON );
        }
    }

    protected static Project createProject( String id, String name, String description )
    {
        Project p = new Project();
        p.setId( id );
        p.setName( name );
        p.setDescription( description );
        return p;
    }

    protected WicketTester wicketTester;

    protected MemoryProjectStore projectStore;

    protected Injector injector;

    @BeforeClass
    public void createInjector()
    {
        Module module = new PageWithStoreModule()
        {
            @Override
            public void configure()
            {
                super.configure();
                ClassLoader classLoader = getClass().getClassLoader();
                bind( ClassLoader.class ).annotatedWith( Names.named( "Plugin ClassLoader" ) ).toInstance( classLoader );
            }
        };

        injector = Guice.createInjector( module );

        projectStore = injector.getInstance( MemoryProjectStore.class );
    }

    @BeforeMethod
    public void setUpTester()
    {
        wicketTester = new WicketTester( new CentrepointApplication( injector ) );
    }

    @SuppressWarnings( "unchecked" )
    public void assertBookmarkablePageLink( String path, Class<?> expectedPageClass )
    {
        BookmarkablePageLink<String> pageLink =
            (BookmarkablePageLink<String>) wicketTester.getComponentFromLastRenderedPage( path );
        assert expectedPageClass.equals( pageLink.getPageClass() );
    }

    @SuppressWarnings( "unchecked" )
    public void assertBookmarkablePageLink( String path, Class<?> expectedPageClass, PageParameters expectedParams )
    {
        BookmarkablePageLink<String> pageLink =
            (BookmarkablePageLink<String>) wicketTester.getComponentFromLastRenderedPage( path );
        assert expectedPageClass.equals( pageLink.getPageClass() );
        assert expectedParams.equals( pageLink.getPageParameters() );
    }

    protected ClassLoader createClassLoader( String plugin )
    {
        URL resource = getClass().getResource( "/" + plugin + ".jar" );
        return new URLClassLoader( new URL[] { resource } );
    }

}