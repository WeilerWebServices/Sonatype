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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

public abstract class AbstractMonitorServlet
    extends GenericServlet
{

    private static final long serialVersionUID = -7271350776954812609L;

    private final List<String> accessedUrls;

    public AbstractMonitorServlet()
    {
        this.accessedUrls = new ArrayList<String>();
    }

    public List<String> getAccessedUrls()
    {
        return this.accessedUrls;
    }

    @Override
    public final void service( ServletRequest request, ServletResponse response )
        throws ServletException, IOException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = ( (Request) req ).getHttpURI().getPathQuery();
        if ( !uri.endsWith( ".sha1" ) && !uri.endsWith( ".md5" ) )
        {
            accessedUrls.add( uri );
        }

        service( req, res );
    }

    protected void addUri( HttpServletRequest req )
    {
        String uri = ( (Request) req ).getHttpURI().getPathQuery();
        if ( !accessedUrls.contains( uri ) )
        {
            accessedUrls.add( uri );
        }
    }

    public abstract void service( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException;
}
