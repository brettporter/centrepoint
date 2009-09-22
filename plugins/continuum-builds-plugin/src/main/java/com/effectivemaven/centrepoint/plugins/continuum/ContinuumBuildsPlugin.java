package com.effectivemaven.centrepoint.plugins.continuum;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.maven.continuum.xmlrpc.client.ContinuumXmlRpcClient;
import org.apache.maven.continuum.xmlrpc.project.BuildResultSummary;

import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.ConfigurablePanel;
import com.effectivemaven.centrepoint.model.plugin.PanelItem;
import com.effectivemaven.centrepoint.model.plugin.PanelPlugin;

public class ContinuumBuildsPlugin
    implements PanelPlugin, ConfigurablePanel<ContinuumModel>
{
    private static Logger logger = Logger.getLogger( ContinuumBuildsPlugin.class.getName() );

    /**
     * Get a list of items present in the panel.
     * 
     * @return the list of panel items
     */
    public List<PanelItem> getItems( Project project )
    {
        int projectId = getModel( project ).getProjectId();
        if ( projectId == 0 )
        {
            // No results as there is no project configured yet
            return errorMessage( "No Continuum project configured" );
        }

        String ciManagementUrl = project.getCiManagementUrl();
        if ( ciManagementUrl == null )
        {
            return errorMessage( "No Continuum URL configured in the POM" );
        }

        List<BuildResultSummary> results;
        ContinuumXmlRpcClient client;
        try
        {
            URL serviceURL = new URL( ciManagementUrl + "/xmlrpc" );
            client = new ContinuumXmlRpcClient( serviceURL );

            logger.info( "Connecting to " + serviceURL + " for project " + projectId );
            results = client.getBuildResultsForProject( projectId );
        }
        catch ( Exception e )
        {
            logger.warning( "Error reading build results: " + e.getMessage() );
            return errorMessage( "Unable to retrieve results: check application logs for details" );
        }

        String resultBaseUrl = ciManagementUrl + "/buildResult.action?projectId=" + projectId;
        List<PanelItem> items = new ArrayList<PanelItem>();
        for ( BuildResultSummary result : results )
        {
            String title = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ).format( result.getStartTime() ) + " - ";

            String status = client.getProjectStatusAsString( result.getState() );
            if ( result.isSuccess() || "OK".equals( status ) )
            {
                String time =
                    new SimpleDateFormat( "m'm'ss's'" ).format( result.getEndTime() - result.getStartTime() );
                title += "#" + result.getBuildNumber() + " (" + time + ")";
            }
            else
            {
                title += status;
            }

            String url = resultBaseUrl + "&buildId=" + result.getId();

            PanelItem item = new PanelItem( title, url );
            items.add( item );
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
        return "Continuum Build Results";
    }

    public ContinuumModel getModel( Project project )
    {
        ContinuumModel extensionModel = (ContinuumModel) project.getExtensionModel( getId() );
        if ( extensionModel == null )
        {
            extensionModel = new ContinuumModel();
            project.addExtensionModel( extensionModel );
        }
        return extensionModel;
    }

    public String getId()
    {
        return "continuum-builds";
    }
}
