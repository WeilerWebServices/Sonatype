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

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UnstableFileServerServlet
    extends FileServerServlet
{

    private static final long serialVersionUID = -6940218205740360901L;

    private int numberOfTries;

    private final int returnCode;

    public UnstableFileServerServlet( int numberOfTries, int returnCode, File content )
    {
        super( content, -1 );
        this.numberOfTries = numberOfTries;
        this.returnCode = returnCode;
    }

    @Override
    public void service( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( numberOfTries > 0 )
        {
            numberOfTries--;
            res.sendError( returnCode );
            return;
        }
        else
        {
            super.service( req, res );
        }

    }

}
