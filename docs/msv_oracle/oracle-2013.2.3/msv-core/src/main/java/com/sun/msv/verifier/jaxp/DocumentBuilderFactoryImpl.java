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

package com.sun.msv.verifier.jaxp;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.msv.verifier.jarv.TheFactoryImpl;

/**
 * DocumentBuilderFactory implementation that supports validation.
 * 
 * <p>
 * This class uses another DocumentBuilderFactory implementation and 
 * adds the validation capability to it.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {
    
    /**
     * Wrapped DocumentBuilderFactory that does everything else.
     */
    private final DocumentBuilderFactory core;

    /**
     * JARV VerifierFactory implementation, which will be used to parse schemas.
     */
    private final VerifierFactory jarvFactory;

    /**
     * The validation will be performed against this schema object.
     */
    private Schema schema;
    
    /**
     * Creates a new instance by using the default DocumentBuilderFactory implementation
     * as the underlying parser. This constructor does not set any schema.
     */
    public DocumentBuilderFactoryImpl() {
        this( DocumentBuilderFactory.newInstance() );
    }
    
    /**
     * Creates a new instance by specifying the underlying SAXParserFactory
     * implementation. This constructor does not set any schema.
     */
    public DocumentBuilderFactoryImpl( DocumentBuilderFactory _factory ) {
        this(_factory,null);
    }
    
    public DocumentBuilderFactoryImpl( DocumentBuilderFactory _factory, Schema _schema ) {
        this.core = _factory;
        this.jarvFactory = new TheFactoryImpl();
        this.schema = _schema;
    }
    
    
    
    public Object getAttribute( String name ) {
        if( name.equals(com.sun.msv.verifier.jarv.Const.PANIC_MODE_FEATURE) )
            try {
                return jarvFactory.isFeature(name)?Boolean.TRUE:Boolean.FALSE;
            } catch( SAXException e ) {
                throw new IllegalArgumentException(e.getMessage());
            }
        return core.getAttribute(name);
    }
    public void setAttribute( String name, Object value ) {
        if( name.equals(com.sun.msv.verifier.jarv.Const.PANIC_MODE_FEATURE) )
            try {
                jarvFactory.setFeature(name,((Boolean)value).booleanValue());
            } catch( SAXException e ) {
                throw new IllegalArgumentException(e.getMessage());
            }
        
        if(Const.SCHEMA_PROPNAME.equals(name)) {
            try {
                if(value instanceof String) {
                    schema = jarvFactory.compileSchema( (String)value );
                    return;
                }
                if(value instanceof File) {
                    schema = jarvFactory.compileSchema( (File)value );
                    return;
                }
                if(value instanceof InputSource) {
                    schema = jarvFactory.compileSchema( (InputSource)value );
                    return;
                }
                if(value instanceof InputStream) {
                    schema = jarvFactory.compileSchema( (InputStream)value );
                    return;
                }
                if(value instanceof Schema) {
                    schema = (Schema)value;
                    return;
                }
                throw new IllegalArgumentException("unrecognized value type: "+value.getClass().getName() );
            } catch( Exception e ) {
                throw new IllegalArgumentException(e.toString());
            }
        }
        core.setAttribute(name,value);
    }
    
    
    
    public boolean isCoalescing() {
        return core.isCoalescing();
    }
    public boolean isExpandEntityReference() {
        return core.isExpandEntityReferences();
    }
    public boolean isIgnoringComments() {
        return core.isIgnoringComments();
    }
    public boolean isIgnoringElementContentWhitespace() {
        return core.isIgnoringElementContentWhitespace();
    }
    public boolean isNamespaceAware() {
        return core.isNamespaceAware();
    }
    public boolean isValidating() {
        return core.isValidating();
    }
    
    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        if(schema==null)        return core.newDocumentBuilder();
        
        return new DocumentBuilderImpl(core.newDocumentBuilder(),schema);
    }
    
    
    public void setCoalescing( boolean newVal ) {
        core.setCoalescing(newVal);
    }
    public void setExpandEntityReference( boolean newVal ) {
        core.setExpandEntityReferences(newVal);
    }
    public void setIgnoringComments( boolean newVal ) {
        core.setIgnoringComments(newVal);
    }
    public void setIgnoringElementContentWhitespace( boolean newVal ) {
        core.setIgnoringElementContentWhitespace(newVal);
    }
    public void setNamespaceAware( boolean newVal ) {
        core.setNamespaceAware(newVal);
    }
    public void setValidating( boolean newVal ) {
        core.setValidating(newVal);
    }
    public boolean getFeature(String name) {
        throw new UnsupportedOperationException();
    }
    public void setFeature(String name,boolean value) {
        throw new UnsupportedOperationException();
    }
}
