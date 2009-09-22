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

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;

import com.effectivemaven.centrepoint.model.ExtensionModel;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.ConfigurablePanel;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.effectivemaven.centrepoint.store.ProjectStore;
import com.google.inject.Inject;

/**
 * Page for editing panel configuration. The user is prompted for values for each field and the configuration saved
 * afterwards.
 */
public class EditPanelConfigurationPage
    extends TemplatePage
{
    @Inject
    private ProjectStore store;

    @Inject
    private PluginManager pluginManager;

    /**
     * Constructor.
     */
    public EditPanelConfigurationPage( PageParameters parameters )
    {
        super( parameters );

        if ( !parameters.containsKey( "id" ) )
        {
            throw new AbortWithHttpStatusException( 404, true );
        }

        String id = parameters.getString( "id" );

        Project project = store.getProjectById( id );
        if ( project == null )
        {
            throw new AbortWithHttpStatusException( 404, true );
        }

        if ( !parameters.containsKey( "panel" ) )
        {
            throw new AbortWithHttpStatusException( 404, true );
        }

        String panel = parameters.getString( "panel" );

        ConfigurablePanel<? extends ExtensionModel> plugin = pluginManager.getConfigurablePlugin( panel );
        if ( plugin == null )
        {
            throw new AbortWithHttpStatusException( 404, true );
        }
        
        setPageTitle( "Edit Configuration" );

        add( new FeedbackPanel( "feedback" ) );

        add( new InputForm( "editPanelConfigurationForm", project, panel, plugin ) );
    }

    @SuppressWarnings( "serial" )
    private static class InputFormModel
        implements IModel<String>
    {
        Map<String, String> values;

        String key;

        private InputFormModel( Map<String, String> values, String key )
        {
            this.values = values;
            this.key = key;
        }

        public String getObject()
        {
            return values.get( key );
        }

        public void setObject( String s )
        {
            values.put( key, s );
        }

        public void detach()
        {
        }
    }

    @SuppressWarnings( "serial" )
    private class InputForm
        extends Form<InputFormModel>
    {
        private String panel;

        private String projectId;

        private Map<String, String> values;

        private InputForm( String name, Project project, String panel, ConfigurablePanel<? extends ExtensionModel> plugin )
        {
            super( name );

            ExtensionModel model = project.getExtensionModel( panel );
            if ( model == null )
            {
                model = plugin.getModel( project );
            }

            values = new HashMap<String, String>();
            RepeatingView rv = new RepeatingView( "row" );
            if ( model != null )
            {
                values.putAll( model.getValuesAsMap() );

                for ( String key : model.getKeys() )
                {
                    WebMarkupContainer parent = new WebMarkupContainer( rv.newChildId() );
                    parent.add( new Label( "name", key ) );

                    TextField<String> textField = new TextField<String>( "value", new InputFormModel( values, key ) );
                    textField.setLabel( new Model<String>( key ) );
                    textField.setRequired( false );
                    parent.add( textField );

                    rv.add( parent );
                }
            }
            add( rv );

            this.projectId = project.getId();
            this.panel = panel;
        }

        @Override
        protected void onSubmit()
        {
            Project project = store.getProjectById( projectId );
            ExtensionModel extensionModel = project.getExtensionModel( panel );
            extensionModel.setValuesFromMap( values );
            store.store( project );

            // forward to the project viewing page for the newly imported project
            PageParameters pageParameters = new PageParameters();
            pageParameters.add( "id", project.getId() );
            setResponsePage( ViewProjectPage.class, pageParameters );
        }
    }
}
