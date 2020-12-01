package org.apache.maven.plugin.version.internal;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.plugin.version.PluginVersionResult;
import org.sonatype.aether.repository.ArtifactRepository;

/**
 * Describes the result of a plugin version resolution request.
 * 
 * @since 3.0
 * @author Benjamin Bentmann
 */
class DefaultPluginVersionResult
    implements PluginVersionResult
{

    private String version;

    private ArtifactRepository repository;

    public DefaultPluginVersionResult()
    {
        // does nothing
    }

    public DefaultPluginVersionResult( String version )
    {
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public ArtifactRepository getRepository()
    {
        return repository;
    }

    public void setRepository( ArtifactRepository repository )
    {
        this.repository = repository;
    }

}
