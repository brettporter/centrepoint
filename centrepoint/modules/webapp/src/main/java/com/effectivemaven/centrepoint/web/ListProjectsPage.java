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
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.store.ProjectStore;
import com.google.inject.Inject;

/**
 * Web page that lists all available projects in the system and a brief summary, linking through to the detailed
 * information page.
 */
public class ListProjectsPage
    extends TemplatePage
{
    @Inject
    private ProjectStore projectStore;

    /**
     * Constructor
     */
    @SuppressWarnings( "serial" )
    public ListProjectsPage()
    {
        super();

        setPageTitle( "Project List" );

        // add a feedback panel for errors and messages
        add( new FeedbackPanel( "feedback" ) );

        List<Project> projects = new ArrayList<Project>( projectStore.getAllProjects() );
        ListView<Project> projectListView = new ListView<Project>( "projects", projects )
        {
            @Override
            public void populateItem( final ListItem<Project> listItem )
            {
                Project project = listItem.getModelObject();
                PageParameters pageParameters = new PageParameters();
                pageParameters.add( "id", project.getId() );
                Link<ViewProjectPage> link =
                    new BookmarkablePageLink<ViewProjectPage>( "link", ViewProjectPage.class, pageParameters );
                link.add( new Label( "name", project.getName() ) );
                listItem.add( link );
                listItem.add( new MultiLineLabel( "description", project.getDescription() ) );
            }
        };
        add( projectListView );

        if ( projects.isEmpty() )
        {
            // add message to the feedback panel
            info( "There are currently no projects." );
        }

        addOperationLink( createOperationLink( "Add a New Project", AddProjectFromMavenPage.class ) );
    }
}
