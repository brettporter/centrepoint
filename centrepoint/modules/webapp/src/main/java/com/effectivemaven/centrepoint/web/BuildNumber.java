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

import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.inject.Singleton;

/**
 * Capture the build number and report to the application.
 */
@Singleton
public class BuildNumber
{
    private final String buildMessage;

    private Date buildDate = null;

    public BuildNumber()
    {
        String msg;
        try
        {
            ResourceBundle bundle = ResourceBundle.getBundle( "build" );
            msg = bundle.getString( "build.message" );
            buildDate = new Date( Long.valueOf( bundle.getString( "build.timestamp" ) ) );
        }
        catch ( MissingResourceException e )
        {
            msg = "Unknown Build";
        }
        buildMessage = msg;
    }

    public String getBuildMessage()
    {
        return buildMessage;
    }

    public String getBuildDate()
    {
        return buildDate.toString();
    }
}
