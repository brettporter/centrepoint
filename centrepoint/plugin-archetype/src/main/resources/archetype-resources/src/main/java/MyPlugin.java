#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.effectivemaven.centrepoint.model.Project;
import com.effectivemaven.centrepoint.model.plugin.ConfigurablePanel;
import com.effectivemaven.centrepoint.model.plugin.PanelItem;
import com.effectivemaven.centrepoint.model.plugin.PanelPlugin;

import java.util.Arrays;
import java.util.List;

public class MyPlugin
    implements PanelPlugin, ConfigurablePanel<MyModel>
{
    /**
     * Get a list of items present in the panel.
     * 
     * @return the list of panel items
     */
    public List<PanelItem> getItems( Project project )
    {
        PanelItem item = new PanelItem( "My Link", getModel( project ).getLink() );
        return Arrays.asList( item );
    }

    /**
     * The title to display for the panel.
     * 
     * @return the panel title
     */
    public String getTitle( Project project )
    {
        return "Links";
    }

    public MyModel getModel( Project project )
    {
        MyModel extensionModel = (MyModel) project.getExtensionModel( getId() );
        if ( extensionModel == null )
        {
            extensionModel = new MyModel();
            project.addExtensionModel( extensionModel );
        }
        return extensionModel;
    }

    public String getId()
    {
        return "my-plugin";
    }
}

