package org.apache.maven.model.interpolation;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.model.path.UrlNormalizer;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;

/**
 * Ensures that expressions referring to URLs evaluate to normalized URLs.
 * 
 * @author Benjamin Bentmann
 */
class UrlNormalizingPostProcessor
    implements InterpolationPostProcessor
{

    private static final Set<String> urlExpressions;

    static
    {
        Set<String> expressions = new HashSet<String>();
        expressions.add( "project.url" );
        expressions.add( "project.scm.url" );
        expressions.add( "project.scm.connection" );
        expressions.add( "project.scm.developerConnection" );
        expressions.add( "project.distributionManagement.site.url" );

        urlExpressions = expressions;
    }

    private UrlNormalizer normalizer;

    public UrlNormalizingPostProcessor( UrlNormalizer normalizer )
    {
        this.normalizer = normalizer;
    }

    public Object execute( String expression, Object value )
    {
        if ( value != null && urlExpressions.contains( expression ) )
        {
            return normalizer.normalize( value.toString() );
        }

        return null;
    }

}
