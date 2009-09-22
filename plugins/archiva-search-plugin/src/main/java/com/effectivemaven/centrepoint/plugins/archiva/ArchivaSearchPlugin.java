package com.effectivemaven.centrepoint.plugins.archiva;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.archiva.web.xmlrpc.api.SearchService;
import org.apache.archiva.web.xmlrpc.api.beans.Artifact;

import com.atlassian.xmlrpc.Binder;
import com.atlassian.xmlrpc.BindingException;
import com.atlassian.xmlrpc.ConnectionInfo;
import com.atlassian.xmlrpc.DefaultBinder;
import com.effectivemaven.centrepoint.model.MavenCoordinates;
import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.PanelItem;
import com.effectivemaven.centrepoint.model.plugin.PanelPlugin;

public class ArchivaSearchPlugin
    implements PanelPlugin
{
    private static Logger logger = Logger.getLogger( ArchivaSearchPlugin.class.getName() );

    /**
     * Get a list of items present in the panel.
     * 
     * @return the list of panel items
     */
    public List<PanelItem> getItems( Project project )
    {
        MavenCoordinates coordinates = (MavenCoordinates) project.getExtensionModel( "maven" );

        String repositoryUrl = project.getRepositoryUrl();
        if ( repositoryUrl == null )
        {
            return errorMessage( "No Repository URL configured in the POM" );
        }
        // remove repository ID
        int archivaUrlIndex = repositoryUrl.lastIndexOf( '/' );
        if ( archivaUrlIndex > 0 )
        {
            // remove /repository/
            archivaUrlIndex = repositoryUrl.lastIndexOf( '/', archivaUrlIndex - 1 );
            if ( !"/repository/".equals( repositoryUrl.substring( archivaUrlIndex, archivaUrlIndex + 12 ) ) )
            {
                logger.warning( "Invalid repository URL: " + repositoryUrl );
                return errorMessage( "Repository URL does not appear to be an Archiva repository" );
            }
        }
        if ( archivaUrlIndex < 0 )
        {
            logger.warning( "Invalid repository URL: " + repositoryUrl );
            return errorMessage( "Repository URL does not appear to be an Archiva repository" );
        }

        String archivaUrl = repositoryUrl.substring( 0, archivaUrlIndex );

        List<Artifact> artifacts;
        Binder binder = new DefaultBinder();
        try
        {
            URL serviceUrl = new URL( archivaUrl + "/xmlrpc" );
            ConnectionInfo connect = new ConnectionInfo();
            connect.setUsername( "guest" );
            connect.setPassword( "" );
            SearchService searchService = binder.bind( SearchService.class, serviceUrl, connect );

            artifacts = searchService.getArtifactVersions( coordinates.getGroupId(), coordinates.getArtifactId() );
        }
        catch ( MalformedURLException e )
        {
            logger.warning( "Invalid repository URL: " + archivaUrl + ": " + e.getMessage() );
            return errorMessage( "Repository URL does not appear to be an Archiva repository" );
        }
        catch ( BindingException e )
        {
            logger.warning( "Error binding to URL: " + archivaUrl + ": " + e.getMessage() );
            return errorMessage( "Error connecting to Archiva repository, see server logs for details" );
        }
        catch ( Exception e )
        {
            logger.warning( "Error retrieving results from: " + archivaUrl + ": " + e.getMessage() );
            e.printStackTrace();
            return errorMessage( "Error connecting to Archiva repository, see server logs for details" );
        }

        String browseUrl = archivaUrl + "/browse/" + coordinates.getGroupId() + "/" + coordinates.getArtifactId() + "/";
        List<PanelItem> items = new ArrayList<PanelItem>();
        for ( Artifact artifact : artifacts )
        {
            PanelItem item = new PanelItem( artifact.getVersion(), browseUrl + artifact.getVersion() );
            items.add( item );
        }
        
        if ( items.isEmpty() )
        {
            return errorMessage( "No versions found" );
        }
        
        return items;
    }

    private List<PanelItem> errorMessage( String msg )
    {
        return Collections.singletonList( new PanelItem( msg, "#" ) );
    }

    /**
     * The title to display for the panel.
     * 
     * @return the panel title
     */
    public String getTitle( Project project )
    {
        return "Archiva Search Results";
    }
}
