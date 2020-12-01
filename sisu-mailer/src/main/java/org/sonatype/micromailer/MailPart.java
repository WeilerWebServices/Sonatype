/*
 * Copyright (c) 2008-2012 Sonatype, Inc. All rights reserved.
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
package org.sonatype.micromailer;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.internet.MimeBodyPart;

/**
 * MIME part of a mail being assembled for sending.
 * 
 * @see MailRequest#addPart(MailPart)
 */
public class MailPart
{

    private String disposition = MimeBodyPart.ATTACHMENT;

    private String filename;

    private String contentId;

    private String contentLocation;

    private Map<String, String> headers = new LinkedHashMap<String, String>();

    private DataHandler content;

    public String getDisposition()
    {
        return disposition;
    }

    public void setDisposition( String disposition )
    {
        this.disposition = disposition;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename( String filename )
    {
        this.filename = filename;
    }

    public String getContentId()
    {
        return contentId;
    }

    public void setContentId( String contentId )
    {
        this.contentId = contentId;
    }

    public String getContentLocation()
    {
        return contentLocation;
    }

    public void setContentLocation( String contentLocation )
    {
        this.contentLocation = contentLocation;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public void setHeader( String key, String value )
    {
        if ( value == null )
        {
            headers.remove( key );
        }
        else
        {
            headers.put( key, value );
        }
    }

    public DataHandler getContent()
    {
        return content;
    }

    public void setContent( Object content, String mimeType )
    {
        this.content = new DataHandler( content, mimeType );
    }

    public void setContent( DataSource content )
    {
        this.content = new DataHandler( content );
    }

}
