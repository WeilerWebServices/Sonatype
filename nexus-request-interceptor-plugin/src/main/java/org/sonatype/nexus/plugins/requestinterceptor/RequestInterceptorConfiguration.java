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
package org.sonatype.nexus.plugins.requestinterceptor;

import java.util.Map;

import org.sonatype.nexus.plugins.requestinterceptor.capabilities.RequestInterceptorActionFormField;
import org.sonatype.nexus.plugins.requestinterceptor.capabilities.RequestInterceptorCapabilityDescriptor;
import org.sonatype.nexus.plugins.requestinterceptor.capabilities.RequestInterceptorGeneratorFormField;
import org.sonatype.nexus.plugins.requestinterceptor.capabilities.RequestInterceptorMappingFormField;
import org.sonatype.nexus.proxy.access.Action;

public class RequestInterceptorConfiguration
{

    private final String repositoryId;

    private final Action action;

    private final String mapping;

    private final String generator;

    public RequestInterceptorConfiguration( final Map<String, String> properties )
    {
        repositoryId = repository( properties );
        action = action( properties );
        mapping = mapping( properties );
        generator = generator( properties );
    }

    public String repositoryId()
    {
        return repositoryId;
    }

    public Action action()
    {
        return action;
    }

    public String mapping()
    {
        return mapping;
    }

    public String generator()
    {
        return generator;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( action == null ) ? 0 : action.hashCode() );
        result = prime * result + ( ( generator == null ) ? 0 : generator.hashCode() );
        result = prime * result + ( ( mapping == null ) ? 0 : mapping.hashCode() );
        result = prime * result + ( ( repositoryId == null ) ? 0 : repositoryId.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final RequestInterceptorConfiguration other = (RequestInterceptorConfiguration) obj;
        if ( action != other.action )
        {
            return false;
        }
        if ( generator == null )
        {
            if ( other.generator != null )
            {
                return false;
            }
        }
        else if ( !generator.equals( other.generator ) )
        {
            return false;
        }
        if ( mapping == null )
        {
            if ( other.mapping != null )
            {
                return false;
            }
        }
        else if ( !mapping.equals( other.mapping ) )
        {
            return false;
        }
        if ( repositoryId == null )
        {
            if ( other.repositoryId != null )
            {
                return false;
            }
        }
        else if ( !repositoryId.equals( other.repositoryId ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "RequestInterceptorConfiguration [" );
        if ( repositoryId != null )
        {
            builder.append( "repositoryId=" );
            builder.append( repositoryId );
            builder.append( ", " );
        }
        if ( action != null )
        {
            builder.append( "action=" );
            builder.append( action );
            builder.append( ", " );
        }
        if ( mapping != null )
        {
            builder.append( "mapping=" );
            builder.append( mapping );
            builder.append( ", " );
        }
        if ( generator != null )
        {
            builder.append( "generator=" );
            builder.append( generator );
        }
        builder.append( "]" );
        return builder.toString();
    }

    private static String repository( final Map<String, String> properties )
    {
        final String value = properties.get( RequestInterceptorCapabilityDescriptor.REPOSITORY );
        return value;
    }

    private static Action action( final Map<String, String> properties )
    {
        final String value = properties.get( RequestInterceptorActionFormField.ID );
        return Action.valueOf( value );
    }

    private static String mapping( final Map<String, String> properties )
    {
        final String value = properties.get( RequestInterceptorMappingFormField.ID );
        return value;
    }

    private static String generator( final Map<String, String> properties )
    {
        final String value = properties.get( RequestInterceptorGeneratorFormField.ID );
        return value;
    }

}
