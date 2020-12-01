/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.requestinterceptor.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.util.SelectorUtils;
import org.sonatype.nexus.logging.AbstractLoggingComponent;
import org.sonatype.nexus.plugins.requestinterceptor.RequestInterceptor;
import org.sonatype.nexus.plugins.requestinterceptor.RequestInterceptorConfiguration;
import org.sonatype.nexus.plugins.requestinterceptor.RequestInterceptors;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.access.Action;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.repository.RequestProcessor;

@Named
@Singleton
public class DefaultRequestInterceptors
    extends AbstractLoggingComponent
    implements RequestInterceptors
{

    private final RepositoryRegistry repositories;

    private final Map<String, List<RequestInterceptorConfiguration>> configurations;

    private final RequestProcessor requestProcessor;

    private final Map<String, RequestInterceptor> interceptors;

    @Inject
    public DefaultRequestInterceptors( final RepositoryRegistry repositories,
                                       final Map<String, RequestInterceptor> generators )
    {
        this.repositories = repositories;
        interceptors = generators;
        configurations = new HashMap<String, List<RequestInterceptorConfiguration>>();
        requestProcessor = new RequestInterceptorRequestProcessor( this );

        getLogger().debug( "Interceptors {})", interceptors );
    }

    @Override
    public void handle( final String repositoryId, final String requestPath, final Action action )
    {
        getLogger().debug( "Handling request for {}:{}({})", new Object[]{ repositoryId, requestPath, action } );
        final Collection<RequestInterceptorConfiguration> configurations = getConfigurations( repositoryId );
        if ( configurations == null )
        {
            return;
        }
        String cannonicalPath = requestPath;
        if ( !cannonicalPath.startsWith( "/" ) )
        {
            cannonicalPath = "/" + cannonicalPath;
        }
        for ( final RequestInterceptorConfiguration configuration : configurations )
        {
            final Action expectedAction = configuration.action();
            if ( ( expectedAction == null || action.equals( expectedAction ) )
                && SelectorUtils.matchPath( configuration.mapping(), cannonicalPath ) )
            {
                final RequestInterceptor interceptor = interceptors.get( configuration.generator() );
                if ( interceptor == null )
                {
                    getLogger().warn(
                        "Request [{}] could not be handled as interceptor [{}] could not be found",
                        cannonicalPath, configuration.generator() );
                }
                else
                {
                    try
                    {
                        getLogger().debug(
                            "Matched {}:{}({}) -> {}", new Object[]{ repositoryId, requestPath, action, interceptor }
                        );
                        interceptor.execute( repositories.getRepository( repositoryId ), cannonicalPath, action );
                        // first generator wins
                        break;
                    }
                    catch ( final NoSuchRepositoryException e )
                    {
                        getLogger().warn(
                            "Request [{}] could not be handled as repository [{}] could not be found",
                            cannonicalPath, repositoryId );
                    }
                }
            }
        }
    }

    @Override
    public void addConfiguration( final RequestInterceptorConfiguration configuration )
    {
        getLogger().debug( "Adding {}", configuration );
        List<RequestInterceptorConfiguration> configs = configurations.get( configuration.repositoryId() );
        if ( configs == null )
        {
            configs = new ArrayList<RequestInterceptorConfiguration>();
            configurations.put( configuration.repositoryId(), configs );
        }
        configs.add( configuration );
        try
        {
            final Repository repository = repositories.getRepository( configuration.repositoryId() );
            repository.getRequestProcessors().put( requestProcessorKey( configuration.repositoryId() ),
                                                   requestProcessor );
        }
        catch ( final NoSuchRepositoryException e )
        {
            getLogger().warn( "Could not enable request interceptors for repository [{}] as repository does not exist",
                              configuration.repositoryId() );
        }
    }

    @Override
    public void removeConfiguration( final RequestInterceptorConfiguration configuration )
    {
        getLogger().debug( "Removing {}", configuration );
        final List<RequestInterceptorConfiguration> configs = configurations.get( configuration.repositoryId() );
        if ( configs != null )
        {
            configs.remove( configuration );
        }
        try
        {
            final Repository repository = repositories.getRepository( configuration.repositoryId() );
            repository.getRequestProcessors().remove( requestProcessorKey( configuration.repositoryId() ) );
        }
        catch ( final NoSuchRepositoryException e )
        {
            getLogger().warn( "Could not disable request interceptors for repository [{}] as repository does not exist",
                              configuration.repositoryId() );
        }
    }

    @Override
    public Collection<RequestInterceptorConfiguration> getConfigurations( final String repositoryId )
    {
        return configurations.get( repositoryId );
    }

    private String requestProcessorKey( final String repositoryId )
    {
        return RequestInterceptorRequestProcessor.class.getName() + "/" + repositoryId;
    }

}
