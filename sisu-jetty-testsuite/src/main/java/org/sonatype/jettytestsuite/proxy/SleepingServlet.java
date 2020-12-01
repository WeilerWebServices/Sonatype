/**
 * Copyright (c) 2007-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.jettytestsuite.proxy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

public class SleepingServlet
    extends DefaultServlet
{

    public static final String SLEEP_PARAM_KEY = "numberOfMillisecondsToSleep";
    
    private static final long serialVersionUID = -6822203057278773072L;

    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        if( this.getInitParameter( SLEEP_PARAM_KEY ) != null )
        {
            int numberOfMillisecondsToSleep = new Integer( this.getInitParameter( SLEEP_PARAM_KEY ) );
          try
          {
              Thread.sleep( numberOfMillisecondsToSleep );
          }
          catch ( InterruptedException e )
          {
              // we just care about the sleep part
          }
        }   
        
        super.doGet( request, response );
    }
}
