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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;

import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.PanelItem;
import com.effectivemaven.centrepoint.model.plugin.PanelPlugin;
import com.effectivemaven.centrepoint.model.plugin.PluginManager;
import com.effectivemaven.centrepoint.store.ProjectStore;
import com.google.inject.Inject;

/**
 * Web page to display information and other panels about a given project.
 */
public class ViewProjectPage
    extends TemplatePage
{
    @Inject
    private ProjectStore projectStore;

    @Inject
    private PluginManager pluginManager;

    /**
     * Constructor
     */
    public ViewProjectPage( PageParameters parameters )
    {
        super( parameters );

        if ( !parameters.containsKey( "id" ) )
        {
            throw new AbortWithHttpStatusException( 404, true );
        }

        final String id = parameters.getString( "id" );

        Project project = projectStore.getProjectById( id );
        if ( project == null )
        {
            throw new AbortWithHttpStatusException( 404, true );
        }

        setPageTitle( project.getName() );

        add( new Label( "name", project.getName() ) );
        add( new MultiLineLabel( "description", project.getDescription() ) );
        add( new Label( "version", project.getVersion() ) );
        add( new HideableExternalLink( "url", project.getUrl() ) );
        add( new HideableExternalLink( "scmUrl", project.getScmUrl() ) );
        add( new HideableExternalLink( "issueTrackerUrl", project.getIssueTrackerUrl() ) );

        MavenCoordinates coordinates = (MavenCoordinates) project.getExtensionModel( "maven" );
        if ( coordinates == null )
        {
            coordinates = new MavenCoordinates();
        }
        add( new Label( "maven|groupId", coordinates.getGroupId() ) );
        add( new Label( "maven|artifactId", coordinates.getArtifactId() ) );

        for ( PanelPlugin panel : pluginManager.getPlugins() )
        {
            List<AbstractLink> links = new ArrayList<AbstractLink>();
            for ( PanelItem item : panel.getItems( project ) )
            {
                links.add( new HideableExternalLink( "link", item.getUrl(), item.getName() ) );
            }

            addPanel( new Panel( panel, project, links ) );
        }
    }
}
