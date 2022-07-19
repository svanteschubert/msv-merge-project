/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.msv.reader.datatype.xsd;

import org.relaxng.datatype.DatatypeException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.TypeOwner;
import com.sun.msv.util.StartTagInfo;

/**
 * Base implementation for those states which produce a type object
 * as its parsing result.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class TypeState extends SimpleState
{
    /** Gets the parent state as TypeOwner. */
    private XSTypeOwner getParent() {
        if( parentState instanceof XSTypeOwner )
            return (XSTypeOwner)parentState;
        else
            return null;    // parent is allowed not to implement this interface
    }

    public final String getTargetNamespaceUri() {
        XSTypeOwner parent = getParent();
        if(parent!=null)    
            return getParent().getTargetNamespaceUri();
        else
            return ""; // we don't have the notion of the namespace URI in this context
    }
    
    public void endSelf() {
        super.endSelf();
        
        XSDatatypeExp type = _makeType();
        
        if( parentState instanceof XSTypeOwner ) {
            ((XSTypeOwner)parentState).onEndChild(type);
            return;
        }
        if( parentState instanceof TypeOwner ) {
            // if the parent can understand what we are creating,
            // then pass the result.
            ((TypeOwner)parentState).onEndChildType(type,type.name);
            return;
        }
        if( parentState instanceof ExpressionOwner ) {
            ((ExpressionOwner)parentState).onEndChild(type);
            return;
        }
        
        // we have no option to let the parent state know our result.
        throw new Error(parentState.getClass().getName()+" doesn't implement any of TypeOwner");
    }
    
    /** the makeType method with protection against possible exception. */
    XSDatatypeExp _makeType() {
        try {
            return makeType();
        } catch( DatatypeException be ) {
            reader.reportError( be, GrammarReader.ERR_BAD_TYPE );
            // recover by assuming a valid type.
            return new XSDatatypeExp(StringType.theInstance,reader.pool);
        }
    }
        
    /**
     * This method is called from endElement method.
     * Implementation has to provide DataType object that represents the content of
     * this element.
     */
    protected abstract XSDatatypeExp makeType() throws DatatypeException;


    public final void startElement( String namespaceURI, String localName, String qName, Attributes atts )
    {// within the island of XSD, foreign namespaces are prohibited.
        final StartTagInfo tag = new StartTagInfo(
            namespaceURI,localName,qName,new AttributesImpl(atts));
        // we have to copy Attributes, otherwise it will be mutated by SAX parser
            
        State nextState = createChildState(tag);
        if(nextState!=null) {
            reader.pushState(nextState,this,tag);
            return;
        }
                
        // unacceptable element
        reader.reportError(GrammarReader.ERR_MALPLACED_ELEMENT, tag.qName );
        // try to recover from error by just ignoring it.
        // element of a foreign namespace. skip subtree
        reader.pushState(new IgnoreState(),this,tag);
    }
}
    
