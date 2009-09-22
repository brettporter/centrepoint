package com.effectivemaven.centrepoint.model.plugin;

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

/**
 * An individual item for displaying in a project panel plugin. Each can contain a label and an optional URL.
 */
public class PanelItem
{
    /** The label to display for the text or link. */
    private final String name;

    /** The optional URL to link to. */
    private final String url;

    public PanelItem( String name )
    {
        this.name = name;
        this.url = null;
    }

    public PanelItem( String name, String url )
    {
        this.name = name;
        this.url = url;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }
}
