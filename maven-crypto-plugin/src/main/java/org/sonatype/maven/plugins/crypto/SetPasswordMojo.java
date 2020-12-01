package org.sonatype.maven.plugins.crypto;

import org.apache.maven.artifact.manager.CredentialsChangeRequest;
import org.apache.maven.artifact.manager.CredentialsDataSource;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.codehaus.plexus.components.cipher.PlexusCipher;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.util.StringUtils;

/**
 * 
 * @author Oleg Gusakov
 *
 *  @goal set-server-password
 */

public class SetPasswordMojo
extends AbstractMojo
{
	//----------------------------------------------------------------
	/**
	 * @parameter expression="${serverid}"
	 */
	String serverid;
	//----------------------------------------------------------------
	/**
	 * @parameter expression="${username}"
	 */
	String username;
	//----------------------------------------------------------------
	/**
	 * @parameter expression="${password}"
	 */
	String password;
	//----------------------------------------------------------------
	/**
	 * @parameter expression="${oldpassword}"
	 */
	String oldpassword;
	//----------------------------------------------------------------

	/**
	  * @parameter expression="${session}"
	  */
	MavenSession _session;
//
//	/**
//	  * @parameter expression="${project}"
//	  */
//	MavenProject _project;
//
//	/**
//	  * @component
//	  */
//	Prompter _prompter;
	//----------------------------------------------------------------
	public void execute()
	throws MojoExecutionException, MojoFailureException
	{
		try {

			if( _session == null )
				throw new Exception("session not injected");

getLog().info("\n------------------------------->");

			AuthenticationInfo auth = new AuthenticationInfo();
			auth.setUserName(username);
			auth.setPassword(password);
getLog().info("Auth = "+auth);
			
			CredentialsChangeRequest req = new CredentialsChangeRequest( serverid, auth, oldpassword);
getLog().info("Req = "+req);
			CredentialsDataSource cds = (CredentialsDataSource) _session.getContainer().lookup(CredentialsDataSource.class);
getLog().info("Cds = "+cds);
			cds.set(req);
			
			getLog().info("Password for "+serverid+" succesfully "+(oldpassword==null?"set":"reset") );

		} catch( Exception e ) {
			getLog().error("Error setting password for "+serverid+": "+e.getMessage() );
			throw new MojoExecutionException( e.getMessage() );
		}
	}
	
	//----------------------------------------------------------------
	//----------------------------------------------------------------
}
