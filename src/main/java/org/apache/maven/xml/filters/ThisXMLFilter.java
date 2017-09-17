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

import java.util.function.Function;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Replaces all ${this.*} occurrences
 * 
 * @author Robert Scholte
 *
 */
public class ThisXMLFilter
    extends XMLFilterImpl
{
    private final Function<String, String> resolver;
    
    public ThisXMLFilter( Function<String, String> resolver )
    {
        this.resolver = resolver;
    }

    @Override
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        String text = new String( ch, start, length );

        if ( text.contains( "${this." ) )
        {
            String newText = resolver.apply( text );
            super.characters( newText.toCharArray(), 0, newText.length() );
        }
        else
        {
            super.characters( ch, start, length );
        }
    }
}
