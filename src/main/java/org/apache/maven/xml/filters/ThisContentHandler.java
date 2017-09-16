package org.apache.maven.xml.filters;

import java.util.function.Function;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Replaces all ${this.*} occurrences
 * 
 * @author Robert Scholte
 *
 */
public class ThisContentHandler
    extends XMLFilterImpl
{
    private final Function<String, String> resolver;
    
    public ThisContentHandler( Function<String, String> resolver )
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
