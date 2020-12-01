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
package org.sonatype.nexus.plugins.requestinterceptor.capabilities;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.plugins.requestinterceptor.capabilities.RequestInterceptorCapabilityDescriptor.TYPE_ID;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.plugins.capabilities.Condition;
import org.sonatype.nexus.plugins.capabilities.support.CapabilitySupport;
import org.sonatype.nexus.plugins.capabilities.support.condition.Conditions;
import org.sonatype.nexus.plugins.capabilities.support.condition.RepositoryConditions;
import org.sonatype.nexus.plugins.requestinterceptor.RequestInterceptorConfiguration;
import org.sonatype.nexus.plugins.requestinterceptor.RequestInterceptors;

@Named( TYPE_ID )
public class RequestInterceptorCapability
    extends CapabilitySupport
{

    private final RequestInterceptors requestInterceptors;

    private final Conditions conditions;

    private RequestInterceptorConfiguration configuration;

    @Inject
    public RequestInterceptorCapability( final RequestInterceptors requestInterceptors,
                                         final Conditions conditions )
    {
        this.requestInterceptors = checkNotNull( requestInterceptors );
        this.conditions = checkNotNull( conditions );
    }

    @Override
    public void onCreate()
        throws Exception
    {
        configuration = createConfiguration( context().properties() );
    }

    @Override
    public void onLoad()
        throws Exception
    {
        onCreate();
    }

    @Override
    public void onUpdate()
        throws Exception
    {
        onRemove();
        onCreate();
    }

    @Override
    public void onRemove()
        throws Exception
    {
        configuration = null;
    }

    @Override
    public void onActivate()
    {
        requestInterceptors.addConfiguration( configuration );
    }

    @Override
    public void onPassivate()
    {
        requestInterceptors.removeConfiguration( configuration );
    }

    @Override
    public Condition activationCondition()
    {
        return conditions.logical().and(
            conditions.repository().repositoryIsInService( new RepositoryConditions.RepositoryId()
            {
                @Override
                public String get()
                {
                    return isConfigured() ? configuration.repositoryId() : null;
                }
            } ),
            conditions.capabilities().passivateCapabilityDuringUpdate( context().id() )
        );
    }

    @Override
    public Condition validityCondition()
    {
        return conditions.repository().repositoryExists( new RepositoryConditions.RepositoryId()
        {
            @Override
            public String get()
            {
                return isConfigured() ? configuration.repositoryId() : null;
            }
        } );
    }

    private boolean isConfigured()
    {
        return configuration != null;
    }

    protected RequestInterceptorConfiguration createConfiguration( final Map<String, String> properties )
    {
        return new RequestInterceptorConfiguration( properties );
    }

    protected RequestInterceptorConfiguration getConfiguration()
    {
        return configuration;
    }

    @Override
    public String toString()
    {
        String id = null;
        if ( context() != null )
        {
            id = "'" + context().id() + "'";
        }
        return getClass().getSimpleName() + "{" +
            "id=" + id +
            ", config=" + configuration +
            '}';
    }

}
