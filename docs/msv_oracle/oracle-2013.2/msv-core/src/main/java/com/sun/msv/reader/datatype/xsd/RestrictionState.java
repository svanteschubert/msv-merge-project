/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.sun.msv.reader.datatype.xsd;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * state that parses &lt;restriction&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RestrictionState extends TypeWithOneChildState implements FacetStateParent {
    
    protected final String newTypeUri;
    protected final String newTypeName;
    
    protected RestrictionState( String newTypeUri, String newTypeName ) {
        this.newTypeUri  = newTypeUri;
        this.newTypeName = newTypeName;
    }
    
    protected XSTypeIncubator incubator;
    public final XSTypeIncubator getIncubator() {
        return incubator;
    }

    protected XSDatatypeExp annealType( XSDatatypeExp baseType ) throws DatatypeException {
        return incubator.derive(newTypeUri,newTypeName);
    }
    
    public void onEndChild( XSDatatypeExp child ) {
        super.onEndChild(child);
        createTypeIncubator();
    }
    
    private void createTypeIncubator() {
        incubator = type.createIncubator();
    }

    
    protected void startSelf() {
        super.startSelf();
        
        // if the base attribute is used, try to load it.
        String base = startTag.getAttribute("base");
        if(base!=null)
            onEndChild( ((XSDatatypeResolver)reader).resolveXSDatatype(base) );
    }

    protected State createChildState( StartTagInfo tag ) {
        // accepts elements from the same namespace only.
        if( !startTag.namespaceURI.equals(tag.namespaceURI) )    return null;
        
        if( tag.localName.equals("annotation") )    return new IgnoreState();
        if( tag.localName.equals("simpleType") )    return new SimpleTypeState();
        if( FacetState.facetNames.contains(tag.localName) ) {
            if( incubator==null ) {
                reader.reportError( GrammarReader.ERR_MISSING_ATTRIBUTE, "restriction", "base" );
                onEndChild(new XSDatatypeExp(StringType.theInstance,reader.pool));
            }
            return new FacetState();
        }
        
        return null;    // unrecognized
    }
}
