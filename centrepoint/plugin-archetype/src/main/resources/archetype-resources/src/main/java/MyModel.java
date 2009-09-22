#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.effectivemaven.centrepoint.model.ExtensionModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyModel
    implements ExtensionModel
{
    private String link;

    public String getId()
    {
        return "my-plugin";
    }

    public void setLink( String link )
    {
        this.link = link;
    }

    public String getLink()
    {
        return link;
    }

    public Map<String, String> getValuesAsMap()
    {
        Map<String, String> values = new HashMap<String, String>();
        values.put( "link", link );
        return values;
    }

    public void setValuesFromMap( Map<String, String> values )
    {
        this.link = values.get( "link" );
    }

    public List<String> getKeys()
    {
        return Arrays.asList( "link" );
    }

}
