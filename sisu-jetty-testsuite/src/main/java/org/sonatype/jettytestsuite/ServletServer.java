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

import java.net.Socket;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;

/**
 * The Class ServletServer. Heavily based on Joakim Erfeldt's work in wagon-webdav tests.
 * 
 * @author cstamas
 */
public class ServletServer
    implements Initializable, Startable
{

    /** The Constant ROLE. */
    public static final String ROLE = ServletServer.class.getName();

    /** The server. */
    private Server server;

    /** The port. */
    private int port;

    /** The webapp contexts. */
    private List<WebappContext> webappContexts;
    
    private List<EventListenerInfo> eventListenerInfos;

    /**
     * Gets the server.
     * 
     * @return the server
     */
    public Server getServer()
    {
        return server;
    }

    /**
     * Sets the server.
     * 
     * @param server the new server
     */
    public void setServer( Server server )
    {
        this.server = server;
    }

    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Sets the port.
     * 
     * @param port the new port
     */
    public void setPort( int port )
    {
        this.port = port;
    }

    /**
     * Gets the webapp contexts.
     * 
     * @return the webapp contexts
     */
    public List<WebappContext> getWebappContexts()
    {
        return webappContexts;
    }

    /**
     * Sets the webapp contexts.
     * 
     * @param webappContexts the new webapp contexts
     */
    public void setWebappContexts( List<WebappContext> webappContexts )
    {
        this.webappContexts = webappContexts;
    }

    public String getUrl( String context )
    {
        return "http://localhost:" + getPort() + "/" + context;
    }

    // ===
    // Initializable iface

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable#initialize()
     */
    public void initialize()
        throws InitializationException
    {
        setServer( new Server() );

        ServerConnector connector = new ServerConnector(server);
        connector.setPort( getPort() );
        server.setConnectors( new Connector[] { connector } );
        
        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        getServer().setHandler( contextHandlerCollection );

        if ( getWebappContexts() != null )
        {
            for ( WebappContext webappContext : getWebappContexts() )
            {
                try
                {
                    ServletContextHandler context = null;
                    if ( webappContext.getAuthenticationInfo() != null )
                    {
                        context = new ServletContextHandler(
                            contextHandlerCollection,
                            webappContext.getContextPath(),
                            ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY );
                       
                        HashLoginService loginService = new HashLoginService( "default" );
                        loginService.setConfig( webappContext.getAuthenticationInfo().getCredentialsFilePath() );

                        Constraint constraint = new Constraint(
                            webappContext.getAuthenticationInfo().getAuthMethod(),
                            Constraint.ANY_AUTH );
                        constraint.setAuthenticate( true );

                        ConstraintMapping constraintMapping = new ConstraintMapping();
                        constraintMapping.setPathSpec( webappContext.getAuthenticationInfo().getAuthPathSpec() );
                        constraintMapping.setConstraint( constraint );

                        ConstraintSecurityHandler csh = (ConstraintSecurityHandler) context.getSecurityHandler();
                        
                        csh.setLoginService( loginService );
                        csh.setAuthMethod( webappContext.getAuthenticationInfo().getAuthMethod() );
                        csh.setConstraintMappings( new ConstraintMapping[] { constraintMapping } );
                    }
                    else
                    {
                        context = new ServletContextHandler(
                            contextHandlerCollection,
                            webappContext.getContextPath(),
                            ServletContextHandler.SESSIONS | ServletContextHandler.NO_SECURITY );
                    }
                    context.setDisplayName( webappContext.getName() );
                   
                    if( this.eventListenerInfos != null )
                    {
                        for ( EventListenerInfo eventListenerInfo : this.eventListenerInfos )
                        {
                            context.addEventListener( eventListenerInfo.getEventListener() );
                        }
                    }

                    for ( ServletInfo servletInfo : webappContext.getServletInfos() )
                    {
                        ServletHolder servletHolder = new ServletHolder();
                        servletHolder.setInitOrder( servletInfo.getInitOrder() );
                        servletHolder.setClassName( servletInfo.getServletClass() );
                        if( servletInfo.getName() != null)
                        {
                            servletHolder.setName( servletInfo.getName() );
                        }
                        
                        context.addServlet( servletHolder, servletInfo.getMapping() );
                        
                        for ( Map.Entry<Object, Object> entry : servletInfo.getParameters().entrySet() )
                        {
                            servletHolder.setInitParameter( entry.getKey().toString(), entry.getValue().toString() );
                        }
                    }
                    
                    // add the servlet filters
                    for( ServletFilterInfo filterInfo : webappContext.getServletFilterInfos())
                    {
                        FilterHolder filter = context.addFilter( filterInfo.getFilterClass(), filterInfo.getMapping(), null );
                        // add the init params
                        for ( Map.Entry<Object, Object> entry : filterInfo.getParameters().entrySet() )
                        {
                            filter.setInitParameter( entry.getKey().toString(), entry.getValue().toString() );
                        }
                    }
                }
                catch ( Exception e )
                {
                    throw new InitializationException(
                        "Unable to initialize webapp context " + webappContext.getName(),
                        e );
                }
            }
        }
    }

    // ===
    // Startable iface

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable#start()
     */
    public void start()
        throws StartingException
    {
        try
        {
            getServer().start();

            // NEXUS-4809 make sure all is well, port is open
            final long start = System.currentTimeMillis();
            boolean success = false;
            final int port = getPort();
            while ( !success && System.currentTimeMillis() - start < 10000 )
            {
                try {
                    Socket socket = new Socket( "127.0.0.1", port );
                    socket.close();
                    return;
                }
                catch (Throwable t)
                {
                    Thread.sleep( 500 );
                }
            }
            throw new IllegalStateException( String.format( "Port %s did not open in 10s", port ) );
        }
        catch ( Exception e )
        {
            throw new StartingException( "Error starting embedded Jetty server.", e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable#stop()
     */
    public void stop()
        throws StoppingException
    {
        try
        {
            getServer().stop();
        }
        catch ( Exception e )
        {
            throw new StoppingException( "Error stopping embedded Jetty server.", e );
        }
    }

    // ===
    // Private stuff

}
