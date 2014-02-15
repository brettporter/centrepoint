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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;

import com.effectivemaven.centrepoint.model.ExtensionModel;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.ConfigurablePanel;
import com.effectivemaven.centrepoint.model.plugin.PanelPlugin;
import com.google.inject.Inject;

public class TemplatePage
    extends WebPage
{
    /** Title of the current page. */
    private String pageTitle = "Centrepoint";

    @Inject
    private BuildNumber buildNumber;

    private List<AbstractLink> operationLinks = new ArrayList<AbstractLink>();

    private ListView<AbstractLink> operationsListView;

    private List<Panel> panels = new ArrayList<Panel>();

    private ListView<Panel> panelsListView;

    TemplatePage()
    {
        super();

        addComponents( null );
    }

    TemplatePage( PageParameters parameters )
    {
        super( parameters );

        addComponents( parameters.getString( "id" ) );
    }

    @SuppressWarnings( "serial" )
    private void addComponents( final String id )
    {
        add( new Label( "build", buildNumber.getBuildMessage() ) );
        add( new Label( "buildDate", buildNumber.getBuildDate() ) );
        String ip;
        try
        {
            ip = java.net.InetAddress.getLocalHost().getHostAddress();
        }
        catch (java.net.UnknownHostException e)
        {
            ip = "Unknown";
        }
        add( new Label( "host", ip ) );
        add( new Label( "title", new PropertyModel<String>( this, "pageTitle" ) ) );
        add( new BookmarkablePageLink<String>( "homeLink", ListProjectsPage.class ) );

        operationsListView = createListOfLinks( "operations", operationLinks );

        operationsListView.setVisible( false );

        add( operationsListView );

        panelsListView = new ListView<Panel>( "panels", panels )
        {
            @SuppressWarnings( "unchecked" )
            @Override
            protected void populateItem( ListItem<Panel> listItem )
            {
                Panel panel = listItem.getModelObject();
                listItem.add( new Label( "name", panel.plugin.getTitle( panel.project ) ) );
                listItem.add( createListOfLinks( "items", panel.links ) );

                if ( panel.plugin instanceof ConfigurablePanel )
                {
                    ConfigurablePanel<ExtensionModel> plugin = (ConfigurablePanel<ExtensionModel>) panel.plugin;
                    PageParameters params = new PageParameters();
                    params.add( "id", id );
                    params.add( "panel", plugin.getId() );
                    Link<ViewProjectPage> link =
                        new BookmarkablePageLink<ViewProjectPage>( "editPanelLink", EditPanelConfigurationPage.class,
                                                                   params );
                    listItem.add( link );
                }
                else
                {
                    WebMarkupContainer container = new WebMarkupContainer( "editPanelLink" );
                    container.setVisible( false );
                    listItem.add( container );
                }
            }
        };

        panelsListView.setVisible( false );

        add( panelsListView );
    }

    void addPanel( Panel panel )
    {
        panels.add( panel );

        panelsListView.setVisible( true );
    }

    @SuppressWarnings( "serial" )
    static class Panel
        implements Serializable
    {
        private final List<AbstractLink> links;

        private final PanelPlugin plugin;

        private final Project project;

        public Panel( PanelPlugin plugin, Project project, List<AbstractLink> links )
        {
            this.plugin = plugin;
            this.project = project;
            this.links = links;
        }
    }

    @SuppressWarnings( "serial" )
    ListView<AbstractLink> createListOfLinks( String path, List<AbstractLink> links )
    {
        return new ListView<AbstractLink>( path, links )
        {
            @Override
            protected void populateItem( ListItem<AbstractLink> listItem )
            {
                AbstractLink link = listItem.getModelObject();
                listItem.add( link );
            }

        };
    }

    protected static Link<String> createOperationLink( String text, Class<? extends WebPage> page )
    {
        Link<String> link = new BookmarkablePageLink<String>( "operationLink", page );
        link.add( new Label( "name", text ) );
        return link;
    }

    public void setPageTitle( String pageTitle )
    {
        this.pageTitle = "Centrepoint :: " + pageTitle;
    }

    public String getPageTitle()
    {
        return pageTitle;
    }

    protected void addOperationLink( Link<String> operationLink )
    {
        operationLinks.add( operationLink );

        operationsListView.setVisible( true );
    }
}
