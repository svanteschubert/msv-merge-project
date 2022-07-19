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

package com.sun.msv.reader.xmlschema;

import java.util.HashSet;
import java.util.Set;

import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * used to parse &lt;schema&gt; element of included schema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SchemaIncludedState extends GlobalDeclState {
    
    /**
     * target namespace that the caller expects.
     * 
     * If this field is null, that indicates caller doesn't
     * expect particular target namespace.
     * 
     * If this field is non-null and schema element has a different
     * value as targetNamespace, then error will be reported.
     */
    protected String expectedTargetNamespace;
    
    protected SchemaIncludedState( String expectedTargetNamespace ) {
        this.expectedTargetNamespace = expectedTargetNamespace;
    }
    
    // these fields keep the previous values.
    private String previousElementFormDefault;
    private String previousAttributeFormDefault;
    private String previousFinalDefault;
    private String previousBlockDefault;
    private String previousChameleonTargetNamespace;
    
    /**
     * this flag is set to true to indicate all the contents of this element
     * will be skipped (due to the double inclusion).
     */
    private boolean ignoreContents = false;
    
    protected State createChildState( StartTagInfo tag ) {
        if( ignoreContents    )        return new IgnoreState();
        else                        return super.createChildState(tag);
    }

    
    protected void startSelf() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        super.startSelf();
        
        // back up the current values
        previousElementFormDefault = reader.elementFormDefault;
        previousAttributeFormDefault = reader.attributeFormDefault;
        previousFinalDefault = reader.finalDefault;
        previousBlockDefault = reader.blockDefault;
        previousChameleonTargetNamespace = reader.chameleonTargetNamespace;

        
        // the chameleonTargetNamespace is usually null, unless we are parsing 
        // a chameleon schema.
        reader.chameleonTargetNamespace = null;
        
        String targetNs = startTag.getAttribute("targetNamespace");
        if( targetNs==null ) {
            if( expectedTargetNamespace==null ) {
                // this is not an error. It just means that the target namespace is absent.
                // reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "schema", "targetNamespace" );
                targetNs = "";    // recover by assuming "" namespace.
            }
            else {
                // this is a chameleon schema.
                targetNs = expectedTargetNamespace;
                reader.chameleonTargetNamespace = expectedTargetNamespace;
            }
        } else {
            if( expectedTargetNamespace!=null
            && !expectedTargetNamespace.equals(targetNs) )
                reader.reportError( XMLSchemaReader.ERR_INCONSISTENT_TARGETNAMESPACE, targetNs, expectedTargetNamespace );
                // recover by adopting the one specified in the schema.
        }
        
        // check double inclusion.
        Set<String> s = reader.parsedFiles.get(targetNs);
        if(s==null) {
            reader.parsedFiles.put( targetNs, s = new HashSet<String>() );
        }
        
        if( s.contains(this.location.getSystemId()) ) {
            // this file is already included. So skip processing it.
            ignoreContents = true;
        } else {
            s.add(this.location.getSystemId());
        }
        
        /*
         * onTargetNamespace complains if we've seen this schema before, so we
         * cannot call it before establishing ignoreContents.
         */
        onTargetNamespaceResolved(targetNs, ignoreContents);


        // process other attributes.
        
        String form;
        form = startTag.getDefaultedAttribute("elementFormDefault","unqualified");
        if( form.equals("qualified") )
            reader.elementFormDefault = targetNs;
        else {
            reader.elementFormDefault = "";
            if( !form.equals("unqualified") )
                reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "elementFormDefault", form );
        }
        
        form = startTag.getDefaultedAttribute("attributeFormDefault","unqualified");
        if( form.equals("qualified") )
            reader.attributeFormDefault = targetNs;
        else {
            reader.attributeFormDefault = "";
            if( !form.equals("unqualified") )
                reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "attributeFormDefault", form );
        }
        
        reader.finalDefault = startTag.getAttribute("finalDefault");
        reader.blockDefault = startTag.getAttribute("blockDefault");
        
    }

    /**
     * This is called when the target namespace is determined for a new schema.
     * @param targetNs namespace of the schema
     * @param ignoreContents TODO
     */
    protected void onTargetNamespaceResolved( String targetNs, boolean ignoreContents ) {
    }
    
    protected void endSelf() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        reader.elementFormDefault = previousElementFormDefault;
        reader.attributeFormDefault = previousAttributeFormDefault;
        reader.finalDefault = previousFinalDefault;
        reader.blockDefault = previousBlockDefault;
        reader.chameleonTargetNamespace = previousChameleonTargetNamespace;
        
        super.endSelf();
    }
}
