package org.maven.ide.eclipse.io;

import java.io.IOException;

/**
 * http response 403
 * @author mkleint
 *
 */
public class ForbiddenException
    extends IOException
{
    private static final long serialVersionUID = -3931433246316614538L;

    public ForbiddenException( String message )
    {
        super( message );
    }

}
