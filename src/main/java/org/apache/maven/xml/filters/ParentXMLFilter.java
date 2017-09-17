package org.apache.maven.xml.filters;

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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.maven.xml.SAXEvent;
import org.apache.maven.xml.SAXEventFactory;
import org.apache.maven.xml.SAXEventUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * <p>
 * Transforms relativePath to version.
 * </p>
 * <strong>NOTE: </strong>The calling filter is responsible to only pass the parent-fragment
 * 
 * @author Robert Scholte
 */
public class ParentXMLFilter
    extends XMLFilterImpl
{
    // states
    private static final int OTHER = 0;

    private static final int RELATIVEPATH = 1;

    private int state;

    /**
     * If parent has no version-element, rewrite relativePath to version.<br>
     * If parent has version-element, then remove relativePath.<br>
     * Order of elements must stay the same.
     */
    private boolean hasVersion;

    private List<SAXEvent> saxEvents = new ArrayList<>();

    private SAXEventFactory eventFactory;

    private final Function<String, String> relativePathMapper;

    public ParentXMLFilter( Function<String, String> relativePathMapper )
    {
        this.relativePathMapper = relativePathMapper;
    }

    private SAXEventFactory getEventFactory()
    {
        if ( eventFactory == null )
        {
            eventFactory = SAXEventFactory.newInstance( getContentHandler() );
        }
        return eventFactory;
    }

    private void addEvent( final SAXEvent event )
    {
        final int eventState = state;

        saxEvents.add( () -> {
            if ( !( eventState == RELATIVEPATH && hasVersion ) )
            {
                event.execute();
            }
        } );
    }

    @Override
    public void startElement( String uri, String localName, String qName, Attributes atts )
    {
        if ( "relativePath".equals( localName ) )
        {
            state = RELATIVEPATH;
            addEvent( () -> {
                String versionQName = SAXEventUtils.renameQName( qName, "version" );

                getEventFactory().startElement( uri, "version", versionQName, null ).execute();
            } );
            return;
        }
        else
        {
            state = OTHER;
        }

        if ( "version".equals( localName ) )
        {
            hasVersion = true;
        }
        addEvent( getEventFactory().startElement( uri, localName, qName, atts ) );
    }

    @Override
    public void characters( char[] ch, int start, int length )
    {
        if ( state == RELATIVEPATH )
        {
            addEvent( () -> {
                String relativePath = new String( ch, start, length );
                String version = relativePathToVersion( relativePath );

                getEventFactory().characters( version.toCharArray(), 0, version.length() ).execute();
            } );
        }
        else
        {
            addEvent( getEventFactory().characters( ch, start, length ) );
        }

    }

    @Override
    public void endDocument()
    {
        addEvent( getEventFactory().endDocument() );
    }

    @Override
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        if ( "relativePath".equals( localName ) )
        {
            addEvent( () -> {
                String versionQName = SAXEventUtils.renameQName( qName, "version" );
                getEventFactory().endElement( uri, "version", versionQName ).execute();
            } );
        }
        else
        {
            addEvent( getEventFactory().endElement( uri, localName, qName ) );

            if ( "parent".equals( localName ) )
            {
                // not with streams due to checked SAXException
                for ( SAXEvent saxEvent : saxEvents )
                {
                    saxEvent.execute();
                }
            }
        }
    }

    @Override
    public void endPrefixMapping( String prefix )
    {
        addEvent( getEventFactory().endPrefixMapping( prefix ) );
    }

    @Override
    public void ignorableWhitespace( char[] ch, int start, int length )
    {
        addEvent( getEventFactory().ignorableWhitespace( ch, start, length ) );
    }

    @Override
    public void processingInstruction( String target, String data )
    {
        addEvent( getEventFactory().processingInstruction( target, data ) );

    }

    @Override
    public void setDocumentLocator( Locator locator )
    {
        addEvent( getEventFactory().setDocumentLocator( locator ) );
    }

    @Override
    public void skippedEntity( String name )
    {
        addEvent( getEventFactory().skippedEntity( name ) );
    }

    @Override
    public void startDocument()
    {
        addEvent( getEventFactory().startDocument() );
    }

    @Override
    public void startPrefixMapping( String prefix, String uri )
    {
        addEvent( getEventFactory().startPrefixMapping( prefix, uri ) );
    }

    protected String relativePathToVersion( String relativePath )
    {
        return relativePathMapper.apply( relativePath );
    }
}
