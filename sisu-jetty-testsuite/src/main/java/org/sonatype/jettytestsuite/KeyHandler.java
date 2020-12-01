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
package org.sonatype.jettytestsuite;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.util.IOUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class KeyHandler
    extends AbstractHandler
{
    public static final long TEST_KEY_1 = 0xA3F9CCC081C4177DL;

    public static final long TEST_KEY_2 = 0x16E0CF8D6B0B9508L;

    private Map<String, String> keys = new LinkedHashMap<String, String>();

    public KeyHandler()
        throws IOException
    {
        addKey( TEST_KEY_1,
                IOUtil.toString( getClass().getResourceAsStream( "/com/sonatype/mercury/plexus/pgp/testkey1.txt" ) ) );
        addKey( TEST_KEY_2,
                IOUtil.toString( getClass().getResourceAsStream( "/com/sonatype/mercury/plexus/pgp/testkey2.txt" ) ) );
    }

    public void addKey( Long key, String content )
    {
        keys.put( ( "0x" + Long.toHexString( key ) ).toLowerCase(), content );
    }

    public void handle( String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response )
        throws IOException, ServletException
    {
        String key = request.getParameter( "search" );
        String result = getStringFromKey( key );

        if ( result == null )
        {
            throw new IllegalArgumentException( "Invalid key received " + key );
        }

        response.setContentType( "text/html; charset=UTF-8" );
        response.setStatus( HttpServletResponse.SC_OK );
        response.setBufferSize( result.length() );
        response.setHeader( "Server", "sks_www/1.1.0" );

        response.getWriter().print( result );

        ( (Request) request ).setHandled( true );
    }

    protected String getStringFromKey( String key )
        throws IOException
    {
        String result = keys.get( key.toLowerCase() );
        return result;
    }
}
