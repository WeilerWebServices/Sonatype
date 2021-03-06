package org.maven.ide.eclipse.authentication.internal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.equinox.security.storage.EncodingUtils;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.maven.ide.eclipse.authentication.AnonymousAccessType;
import org.maven.ide.eclipse.authentication.AuthRegistryException;
import org.maven.ide.eclipse.authentication.AuthenticationType;
import org.maven.ide.eclipse.authentication.IAuthData;
import org.maven.ide.eclipse.authentication.IAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAuthService
    implements IAuthService
{
    private final static Logger log = LoggerFactory.getLogger( SimpleAuthService.class );

    private static final String SECURE_NODE_PATH = "org.maven.ide.eclipse.authentication.urls";

    private static final String SECURE_USERNAME = "username";

    private static final String SECURE_PASSWORD = "password";

    private static final String SECURE_SSL_CERTIFICATE_PATH = "sslCertificatePath";

    private static final String SECURE_SSL_CERTIFICATE_PASSPHRASE = "sslCertificatePassphrase";

    private static final String SECURE_AUTH_TYPE = "authenticationType";

    private static final String SECURE_ANONYMOUS_ACCESS_TYPE = "anonymousAccessType";

    private final ISecurePreferences secureStorage;

    public SimpleAuthService( ISecurePreferences secureStorage )
    {
        this.secureStorage = secureStorage;
    }

    private ISecurePreferences findNode( URI uri )
        throws StorageException
    {
        ISecurePreferences authNode = secureStorage.node( SECURE_NODE_PATH );
        String relativeUrl = "./";
        boolean firstTime = true;
        while ( true )
        {
            String sURI = uri.toString();
            if ( sURI.trim().length() == 0 )
            {
                return null;
            }
            if ( sURI.endsWith( "/" ) )
            {
                sURI = sURI.substring( 0, sURI.length() - 1 );
            }
            if ( firstTime )
            {
                firstTime = false;
                log.debug( "Looking up URL: '{}'", sURI );
            }
            String sURIEncoded = encode( sURI );
            if ( authNode.nodeExists( sURIEncoded ) )
            {
                return authNode.node( sURIEncoded );
            }
            if ( uri.getPath() == null || uri.getPath().length() <= 1 )
            {
                log.debug( "Looked up URL: '{}'", sURI );
                return null;
            }
            uri = uri.resolve( relativeUrl );
            relativeUrl = "../";
        }
    }

    public IAuthData select( String sUri )
    {
        log.debug( "Loading authentication for URI '{}'", sUri );
        if ( sUri == null || sUri.trim().length() == 0 )
        {
            return null;
        }

        try
        {
            URI uri = URIHelper.normalize( sUri );
            ISecurePreferences uriNode = findNode( uri );
            if ( uriNode == null )
            {
                log.debug( "Did not find authentication data for URI {}.", uri );
                return null;
            }

            AuthenticationType authenticationType = null;
            String sAuthType = uriNode.get( SECURE_AUTH_TYPE, null );
            if ( sAuthType != null )
            {
                authenticationType = AuthenticationType.valueOf( sAuthType );
            }
            AuthData authData = new AuthData( authenticationType );
            if ( authData.allowsUsernameAndPassword() )
            {
                String username = uriNode.get( SECURE_USERNAME, "" );
                String password = uriNode.get( SECURE_PASSWORD, "" );
                authData.setUsernameAndPassword( username, password );
                log.debug( "Found authentication data for URI {}: username={}", uri, username );
            }
            if ( authData.allowsCertificate() )
            {
                File certificatePath = null;
                String pathname = uriNode.get( SECURE_SSL_CERTIFICATE_PATH, null );
                if ( pathname != null )
                {
                    certificatePath = new File( pathname );
                }
                String certificatePassphrase = uriNode.get( SECURE_SSL_CERTIFICATE_PASSPHRASE, null );
                authData.setSSLCertificate( certificatePath, certificatePassphrase );
                log.debug( "Found authentication data for URI {}: certificatePath={}", uri,
                           certificatePath != null ? certificatePath.getAbsolutePath() : null );
            }

            AnonymousAccessType anonymousAccessType = null;
            String sAnonymousAccessType = uriNode.get( SECURE_ANONYMOUS_ACCESS_TYPE, null );
            if ( sAnonymousAccessType != null )
            {
                anonymousAccessType = AnonymousAccessType.valueOf( sAnonymousAccessType );
                authData.setAnonymousAccessType( anonymousAccessType );
            }

            return authData;
        }
        catch ( Exception e )
        {
            log.error( "Error loading authentication for URI " + sUri, e );
        }
        return null;
    }

    public IAuthData select( URI uri )
    {
        return select( uri.toString() );
    }

    public boolean save( String uri, String username, String password )
    {
        IAuthData authData = new AuthData( username, password, AnonymousAccessType.NOT_ALLOWED );
        return save( uri, authData );
    }

    public boolean save( String uri, File certificatePath, String certificatePassphrase )
    {
        IAuthData authData = new AuthData( AuthenticationType.CERTIFICATE );
        authData.setSSLCertificate( certificatePath, certificatePassphrase );
        return save( uri, authData );
    }

    public void removeURI( URI uri )
        throws CoreException
    {
        uri = URIHelper.normalize( uri.toString() );
        String sURI;
        log.debug( "Removing authentication for URI {}", uri.toString() );
        try
        {
            sURI = encode( uri.toString() );

            ISecurePreferences authNode = secureStorage.node( SECURE_NODE_PATH );
            if ( !authNode.nodeExists( sURI ) )
            {
                // Nothing to do
                return;
            }
            authNode.node( sURI ).removeNode();

            authNode.flush();
        }
        catch ( StorageException e )
        {
            log.error( "Error removing auth data for URI '" + uri.toString() + "': " + e.getMessage(), e );
            throw new AuthRegistryException( e );
        }
        catch ( IOException e )
        {
            log.error( "Error removing auth data for URI '" + uri.toString() + "': " + e.getMessage(), e );
            throw new AuthRegistryException( e );
        }
    }

    private static String encode( String s )
        throws StorageException
    {
        try
        {
            return EncodingUtils.encodeSlashes( EncodingUtils.encodeBase64( s.getBytes( "UTF-8" ) ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new StorageException( StorageException.INTERNAL_ERROR, e );
        }
    }

    public boolean save( String sUri, IAuthData authData )
    {
        log.debug( "Saving authentication for URI '{}'", sUri );
        if ( sUri == null || sUri.trim().length() == 0 )
        {
            return false;
        }
        URI uri = URIHelper.normalize( sUri );
        try
        {
            String sURI = uri.toString();
            if ( sURI.endsWith( "/" ) )
            {
                sURI = sURI.substring( 0, sURI.length() - 1 );
            }
            log.debug( "Saving authentication for normalized URI '{}': authentication type: {}", uri.toString(),
                       authData.getAuthenticationType() );

            IAuthData oldAuthData = select( sURI );
            if ( authData.equals( oldAuthData ) )
            {
                log.debug( "The authentication data has not changed for URI '{}'. Ignoring request to save authentication data.",
                           sUri );
                return false;
            }

            sURI = encode( sURI );

            ISecurePreferences authNode = secureStorage.node( SECURE_NODE_PATH );
            ISecurePreferences realmNode = authNode.node( sURI );

            if ( authData.allowsUsernameAndPassword() )
            {
                log.debug( "\tUser name: '{}'", authData.getUsername() );
                realmNode.put( SECURE_USERNAME, authData.getUsername(), true );
                realmNode.put( SECURE_PASSWORD, authData.getPassword(), true );
            }
            if ( authData.allowsCertificate() )
            {
                String sslCertificatePathString =
                    authData.getCertificatePath() != null ? authData.getCertificatePath().getCanonicalPath() : null;
                log.debug( "\tCertificate file name: '{}'", sslCertificatePathString );
                realmNode.put( SECURE_SSL_CERTIFICATE_PATH, sslCertificatePathString, false );
                realmNode.put( SECURE_SSL_CERTIFICATE_PASSPHRASE, authData.getCertificatePassphrase(), true );
            }
            if ( authData.getAuthenticationType() != null )
            {
                realmNode.put( SECURE_AUTH_TYPE, authData.getAuthenticationType().toString(), false );
            }
            if ( authData.getAnonymousAccessType() != null )
            {
                realmNode.put( SECURE_ANONYMOUS_ACCESS_TYPE, authData.getAnonymousAccessType().toString(), false );
            }

            authNode.flush();

            return true;
        }
        catch ( StorageException e )
        {
            log.error( "Error saving auth data for URI '" + uri.toString() + "': " + e.getMessage(), e );
            throw new AuthRegistryException( e );
        }
        catch ( IOException e )
        {
            log.error( "Error saving auth data for URI '" + uri.toString() + "': " + e.getMessage(), e );
            throw new AuthRegistryException( e );
        }
    }
}
