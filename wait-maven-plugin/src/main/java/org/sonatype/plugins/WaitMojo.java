/**
 * Copyright (C) 2008 Sonatype Inc. 
 * Sonatype Inc, licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.sonatype.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * The Class WaitMojo.
 * @author Roman Stumm 
 * @goal wait
 */
public class WaitMojo
    extends AbstractMojo
{

    /** @parameter default-value="http" */
    String protocol;

    /** @parameter default-value="localhost" */
    String host;

    /** @parameter default-value="8080" */
    int port;

    /** @parameter default-value="" */
    String file;

    /** @parameter default-value="30000" */
    int timeout;

    /** @parameter default-value="0" */
    int maxcount;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        URL url = getURL();
        int count = maxcount;
        int trials = 1;
        getLog().info( "(timeout: " + timeout + " maxcount: " + maxcount + ")" );
        while ( true )
        {
            try
            {
                getLog().info( trials + ": Try to connect to " + url );
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout( timeout );
                InputStream stream = connection.getInputStream();
                getLog().info( "success - reached " + url );
                stream.close();
                break;
            }
            catch ( IOException e )
            {
                if ( count > 1 )
                {
                    count--;
                }
                else if ( count != 0 )
                {
                    getLog().warn( "cannot connect to " + url, e );
                    throw new MojoExecutionException( "cannot connect to " + url, e );
                }
                try
                {
                    Thread.sleep( timeout );
                }
                catch ( InterruptedException e1 )
                {
                }
                trials++;
            }
        }
    }

    /**
     * Gets the URL.
     * 
     * @return the URL
     * @throws MojoExecutionException the mojo execution exception
     */
    public URL getURL()
        throws MojoExecutionException
    {
        try
        {
            return new URL( protocol, host, port, file );
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( protocol + ", " + host + ", " + port + ", " + file +
                ": cannot create URL", e );
        }
    }
}
