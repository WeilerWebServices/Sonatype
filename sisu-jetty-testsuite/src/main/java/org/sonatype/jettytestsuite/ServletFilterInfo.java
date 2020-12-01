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

import java.util.Properties;

public class ServletFilterInfo
{

    /** The mapping. */
    private String mapping;
    
    /** The filter class. */
    private String filterClass;

    /** The parameters. */
    private Properties parameters;

    public String getMapping()
    {
        return mapping;
    }

    public void setMapping( String mapping )
    {
        this.mapping = mapping;
    }

    public String getFilterClass()
    {
        return filterClass;
    }

    public void setFilterClass( String filterClass )
    {
        this.filterClass = filterClass;
    }

    public Properties getParameters()
    {
        return parameters;
    }

    public void setParameters( Properties parameters )
    {
        this.parameters = parameters;
    }
}
