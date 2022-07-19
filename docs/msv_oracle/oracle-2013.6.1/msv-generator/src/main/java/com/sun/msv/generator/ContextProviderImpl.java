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

package com.sun.msv.generator;

import org.relaxng.datatype.ValidationContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.sun.msv.datatype.SerializationContext;

/**
 * dummy implementation of ValidationContextProvider.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final public class ContextProviderImpl
	implements ValidationContext, SerializationContext {
	
	public ContextProviderImpl( Element parent ) {
		this.element = parent;
	}
	
	protected final Element element;
	
	public String getNamespacePrefix( String uri ) {
		// find the already declared prefix.
		String prefix = findPredeclaredPrefix(element,uri);
		if(prefix!=null)	return prefix;
		
		// make sure that this prefix is not in use.
		int cnt=1;
		while( resolvePrefix(element,"qns"+cnt)!=null )		cnt++;
		
		// declare attribute
		element.setAttributeNS( XMLNS_URI, "xmlns:qns"+cnt, uri );
		return "qns"+cnt;
	}
	
	public String resolveNamespacePrefix( String prefix ) {
		return resolvePrefix(element,prefix);
	}
	
	public boolean isUnparsedEntity( String name ) {
		// accept anything.
		// ENTITY is used with enumeration, so again
		// this implementation is not a problem.
		return true;
	}
	public boolean isNotation( String name ) {
		// accept anything.
		return true;
	}

	public String getBaseUri() { return null; }
	
	public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
	
	/**
	 * finds a prefix for this URI. If no prefix is declared for this URI,
	 * returns null.
	 */
	protected static String findPredeclaredPrefix( Element e, String uri ) {
		NamedNodeMap m = e.getAttributes();
		for( int i=0; i<m.getLength(); i++ ) {
			Attr a = (Attr)m.item(i);
			if(a.getNamespaceURI().equals(XMLNS_URI)
			&& a.getValue().equals(uri)) {
				int idx = a.getName().indexOf(':');
				if(idx<0)	return "";	// default mapping
				else		return a.getName().substring(idx+1);
			}
		}
		// not found. try parent
		if( e.getParentNode() instanceof Element )
			return findPredeclaredPrefix( (Element)e.getParentNode(), uri );
		return null;	// not found
	}
	
	protected static String resolvePrefix( Element e, String prefix ) {
		String qName = prefix.equals("")?"xmlns":("xmlns:"+prefix);
		
		if(e.getAttributeNode(qName)!=null)
			return e.getAttribute(qName);	// find it.
		
		// not found. try parent
		if( e.getParentNode() instanceof Element )
			return resolvePrefix( (Element)e.getParentNode(), prefix );
		return null;	// not found
	}
}
