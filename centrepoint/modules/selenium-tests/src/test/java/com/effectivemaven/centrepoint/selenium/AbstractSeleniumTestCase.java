package com.effectivemaven.centrepoint.selenium;

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

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

@Test( groups = "selenium" )
public abstract class AbstractSeleniumTestCase
{
    protected static Selenium selenium;

    @BeforeSuite
    @Parameters( { "selenium.browser", "selenium.port", "jetty.port" } )
    public void startSelenium( @Optional("*firefox") String browser, @Optional("4444") int port, @Optional("8080") int jettyPort )
    {
        selenium = new DefaultSelenium( "localhost", port, browser, "http://localhost:" + jettyPort );
        selenium.start();

    }

    @AfterSuite
    public void stopSelenium()
    {
        selenium.stop();
    }

}