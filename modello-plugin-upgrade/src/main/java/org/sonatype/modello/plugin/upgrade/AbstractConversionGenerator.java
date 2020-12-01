package org.sonatype.modello.plugin.upgrade;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.codehaus.modello.ModelloException;
import org.codehaus.modello.model.ModelClass;
import org.codehaus.modello.model.Version;
import org.codehaus.modello.plugin.java.AbstractJavaModelloGenerator;
import org.codehaus.plexus.util.WriterFactory;

public abstract class AbstractConversionGenerator
    extends AbstractJavaModelloGenerator
{

    protected Writer getFileWriter( File dir, String name )
        throws ModelloException
    {
        File f = new File( dir, name );

        Writer writer;
        try
        {
            writer = getEncoding() == null ? WriterFactory.newPlatformWriter( f )
                            : WriterFactory.newWriter( f, getEncoding() );
        }
        catch ( IOException e )
        {
            throw new ModelloException( "Unable to generate: " + f + "; reason: " + e.getMessage(), e );
        }
        return writer;
    }

    protected static String getSourceClassName( ModelClass modelClass, Version generatedVersion )
    {
        return modelClass.getPackageName( true, generatedVersion ) + "." + modelClass.getName();
    }

}
