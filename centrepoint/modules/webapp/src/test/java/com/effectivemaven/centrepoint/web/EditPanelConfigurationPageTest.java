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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.util.tester.FormTester;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.effectivemaven.centrepoint.model.ExtensionModel;
import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.effectivemaven.centrepoint.store.MemoryProjectStore;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class EditPanelConfigurationPageTest
    extends AbstractPageWithStoreTest
{
    private static final String EDIT_PANEL_CONFIG_FORM = "editPanelConfigurationForm";

    @BeforeClass( dependsOnMethods = "createInjector" )
    public void addPlugin()
    {
        Module module = new PageWithStoreModule()
        {
            @Override
            public void configure()
            {
                super.configure();
                ClassLoader classLoader = createClassLoader( "config-plugin" );
                bind( ClassLoader.class ).annotatedWith( Names.named( "Plugin ClassLoader" ) ).toInstance( classLoader );
            }
        };

        injector = Guice.createInjector( module );
    }
    
    @BeforeMethod
    public void createSampleProjects()
    {
        projectStore = injector.getInstance( MemoryProjectStore.class );

        Project project = createProject( "idValue", "the name", "the description" );
        project.setVersion( "1.2" );
        project.setUrl( "http://www.effectivemaven.com/" );

        MavenCoordinates coordinates = new MavenCoordinates();
        coordinates.setGroupId( "myGroupId" );
        coordinates.setArtifactId( "myArtifactId" );
        project.addExtensionModel( coordinates );

        projectStore.setProjects( Arrays.asList( project ) );
    }

    @Test
    public void testRenderEmpty()
    {
        PageParameters params = createParams( "idValue", "my-plugin" );
        wicketTester.startPage( EditPanelConfigurationPage.class, params );

        wicketTester.assertRenderedPage( EditPanelConfigurationPage.class );
        wicketTester.assertComponent( "feedback", FeedbackPanel.class );
        wicketTester.assertComponent( EDIT_PANEL_CONFIG_FORM, Form.class );

        wicketTester.assertNoErrorMessage();
        wicketTester.assertNoInfoMessage();

        assertForm( "link", null );
    }

    @SuppressWarnings( "unchecked" )
    private void assertForm( String name, String value )
    {
        wicketTester.assertComponent( EDIT_PANEL_CONFIG_FORM + ":row", RepeatingView.class );
        RepeatingView rv =
            (RepeatingView) wicketTester.getComponentFromLastRenderedPage( EDIT_PANEL_CONFIG_FORM + ":row" );
        assert rv.size() == 1;
        WebMarkupContainer c = (WebMarkupContainer) rv.get( 0 );
        assert name.equals( c.get( "name" ).getDefaultModelObjectAsString() );
        TextField<String> field = (TextField<String>) c.get( "value" );
        assert field.getModelObject() == value;
    }

    private PageParameters createParams( String id, String panel )
    {
        PageParameters params = new PageParameters();
        params.add( "id", id );
        params.add( "panel", panel );
        return params;
    }

    @Test
    public void testValidation()
    {
        PageParameters params = createParams( "idValue", "my-plugin" );
        wicketTester.startPage( EditPanelConfigurationPage.class, params );

        FormTester formTester = wicketTester.newFormTester( EDIT_PANEL_CONFIG_FORM );

        formTester.submit();

        wicketTester.assertRenderedPage( ViewProjectPage.class );
        wicketTester.assertNoErrorMessage();
    }

    @Test
    public void testNoParams()
    {
        wicketTester.startPage( EditPanelConfigurationPage.class );
        assert 404 == wicketTester.getServletResponse().getStatus();
    }

    @Test
    public void testMissingId()
    {
        PageParameters params = new PageParameters();
        params.add( "panel", "my-plugin" );
        wicketTester.startPage( EditPanelConfigurationPage.class, params );
        assert 404 == wicketTester.getServletResponse().getStatus();
    }

    @Test
    public void testMissingPanel()
    {
        PageParameters params = new PageParameters();
        params.add( "id", "idValue" );
        wicketTester.startPage( EditPanelConfigurationPage.class, params );
        assert 404 == wicketTester.getServletResponse().getStatus();
    }

    @Test
    public void testIncorrectId()
    {
        PageParameters params = new PageParameters();
        params.add( "id", "badValue" );
        params.add( "panel", "my-plugin" );
        wicketTester.startPage( EditPanelConfigurationPage.class, params );
        assert 404 == wicketTester.getServletResponse().getStatus();
    }

    @Test
    public void testIncorrectPanel()
    {
        PageParameters params = new PageParameters();
        params.add( "id", "idValue" );
        params.add( "panel", "badValue" );
        wicketTester.startPage( EditPanelConfigurationPage.class, params );
        assert 404 == wicketTester.getServletResponse().getStatus();
    }

    @Test
    public void testCreateSettings()
    {
        PageParameters params = createParams( "idValue", "my-plugin" );
        wicketTester.startPage( EditPanelConfigurationPage.class, params );

        FormTester formTester = wicketTester.newFormTester( EDIT_PANEL_CONFIG_FORM );
        formTester.setValue( "row:1:value", "http://www.effectivemaven.com/" );
        formTester.submit();

        wicketTester.assertRenderedPage( ViewProjectPage.class );
        wicketTester.assertNoErrorMessage();

        Project project = projectStore.getProjectById( "idValue" );
        ExtensionModel model = project.getExtensionModel( "my-plugin" );
        assert model != null;
        assert "http://www.effectivemaven.com/".equals( model.getValuesAsMap().get( "link" ) );
    }
    
    @Test
    public void testPrePopulateAndUpdate()
    {
        PluginManager manager = injector.getInstance( PluginManager.class );
        
        Project project = projectStore.getProjectById( "idValue" );
        ExtensionModel model = project.getExtensionModel( "my-plugin" );
        assert model == null;
        
        model = manager.getConfigurablePlugin( "my-plugin" ).getModel( project );
        assert model != null;
        
        Map<String, String> values = new HashMap<String, String>();
        values.put( "link", "original" );
        model.setValuesFromMap( values );

        PageParameters params = createParams( "idValue", "my-plugin" );
        wicketTester.startPage( EditPanelConfigurationPage.class, params );

        wicketTester.assertRenderedPage( EditPanelConfigurationPage.class );
        wicketTester.assertComponent( "feedback", FeedbackPanel.class );
        wicketTester.assertComponent( EDIT_PANEL_CONFIG_FORM, Form.class );

        wicketTester.assertNoErrorMessage();
        wicketTester.assertNoInfoMessage();

        assertForm( "link", "original" );

        FormTester formTester = wicketTester.newFormTester( EDIT_PANEL_CONFIG_FORM );
        formTester.setValue( "row:1:value", "http://www.effectivemaven.com/" );
        formTester.submit();

        wicketTester.assertRenderedPage( ViewProjectPage.class );
        wicketTester.assertNoErrorMessage();

        project = projectStore.getProjectById( "idValue" );
        model = project.getExtensionModel( "my-plugin" );
        assert model != null;
        assert "http://www.effectivemaven.com/".equals( model.getValuesAsMap().get( "link" ) );
    }
}
