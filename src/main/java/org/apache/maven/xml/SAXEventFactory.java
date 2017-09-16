package org.apache.maven.xml;

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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public final class SAXEventFactory
{
    private final ContentHandler contentHandler;

    protected SAXEventFactory( ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public SAXEvent characters( final char[] ch, final int start, final int length )
        throws SAXException
    {
        return () -> contentHandler.characters( ch, start, length );
    }

    public SAXEvent endDocument()
        throws SAXException
    {
        return () -> contentHandler.endDocument();
    }

    public SAXEvent endElement( final String uri, final String localName, final String qName )
        throws SAXException
    {
        return () -> contentHandler.endElement( uri, localName, qName );
    }

    public SAXEvent endPrefixMapping( final String prefix )
        throws SAXException
    {
        return () ->  contentHandler.endPrefixMapping( prefix );
    }

    public SAXEvent ignorableWhitespace( final char[] ch, final int start, final int length )
        throws SAXException
    {
        return () ->  contentHandler.ignorableWhitespace( ch, start, length );
    }

    public SAXEvent processingInstruction( final String target, final String data )
        throws SAXException
    {
        return () -> contentHandler.processingInstruction( target, data );
    }

    public SAXEvent setDocumentLocator( final Locator locator )
    {
        return () -> contentHandler.setDocumentLocator( locator );
    }

    public SAXEvent skippedEntity( final String name )
        throws SAXException
    {
        return () -> contentHandler.skippedEntity( name );
    }

    public SAXEvent startDocument()
        throws SAXException
    {
        return () -> contentHandler.startDocument();
    }

    public SAXEvent startElement( final String uri, final String localName, final String qName, final Attributes atts )
        throws SAXException
    {
        return () -> contentHandler.startElement( uri, localName, qName, atts );
    }

    public SAXEvent startPrefixMapping( final String prefix, final String uri )
        throws SAXException
    {
        return () -> contentHandler.startPrefixMapping( prefix, uri );
    }
    
    public static SAXEventFactory newInstance( ContentHandler handler )
    {
        return new SAXEventFactory( handler );
    }
}
