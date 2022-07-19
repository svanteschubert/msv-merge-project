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

package com.sun.msv.reader.xmlschema;

import org.relaxng.datatype.DatatypeException;
import org.xml.sax.Locator;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.relax.NoneType;
import com.sun.msv.grammar.xmlschema.AttributeDeclExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.ExpressionWithChildState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.reader.datatype.xsd.XSTypeIncubator;
import com.sun.msv.reader.datatype.xsd.XSTypeOwner;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;


/**
 * used to parse &lt;attribute &gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends ExpressionWithChildState implements XSTypeOwner {
    
    protected State createChildState( StartTagInfo tag ) {
        if( tag.localName.equals("simpleType") )
            return ((XMLSchemaReader)reader).sfactory.simpleType(this,tag);
        
        return super.createChildState(tag);
    }
    
    protected Expression initialExpression() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        if( startTag.containsAttribute("ref") ) {
            if( isGlobal() ) {
                reader.reportError( XMLSchemaReader.ERR_DISALLOWED_ATTRIBUTE,
                    startTag.qName, "ref" );
                return Expression.epsilon;
            }
            
            // this tag has @ref.
            Expression exp = reader.resolveQNameRef(
                startTag, "ref",
                new XMLSchemaReader.RefResolver() {
                    public ReferenceContainer get( XMLSchemaSchema g ) {
                        return g.attributeDecls;
                    }
                } );
            if( exp==null )        return Expression.epsilon;    // couldn't resolve QName.
            return exp;
        }
        
        final String typeAttr = startTag.getAttribute("type");
        if( typeAttr==null )
            // return null to indicate that no type definition is given.
            return null;
        
        // if <attribute> element has @type, then
        // it shall be used as content type.
        return reader.resolveXSDatatype( typeAttr );
    }

    protected Expression defaultExpression() {
        // if no type definition is given, assume ur-type.
        return Expression.anyString;
    }
    
    protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
        if( halfCastedExpression!=null )
            // only one child is allowed.
            // recover by ignoring previously found child expressions.
            reader.reportError( XMLSchemaReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
        
        return newChildExpression;
    }
    
    protected Expression annealExpression(Expression contentType) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        final String fixed = startTag.getAttribute("fixed");
        final String name = startTag.getAttribute("name");
        final String use = startTag.getAttribute("use");


        Expression exp;
        
        if( startTag.containsAttribute("ref") ) {
            if( fixed!=null )
                reader.reportWarning( XMLSchemaReader.ERR_UNIMPLEMENTED_FEATURE,
                    "<attribute> element with both 'ref' and 'fixed' attributes" );
            
            exp = contentType;
        } else {
            // TODO: form attribute is prohibited in several occasions.
            String targetNamespace;
            
            // @name is mandatory
            if( name==null ) {
                reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE,
                    "attribute","name");
                return Expression.nullSet;
            }
        
            if( isGlobal() )    targetNamespace = reader.currentSchema.targetNamespace;
            else
                // in local attribute declaration,
                // targetNamespace is affected by @form and schema's @attributeFormDefault.
                targetNamespace = reader.resolveNamespaceOfAttributeDecl(
                    startTag.getAttribute("form") );
        
            if( fixed!=null ) {
                if(contentType instanceof XSDatatypeExp ) {
                    // we know that the attribute value is of XSDatatypeExp
                    final XSDatatypeExp baseType = (XSDatatypeExp)contentType;
                    
                    try {
                        XSTypeIncubator inc = baseType.createIncubator();
                        inc.addFacet("enumeration",fixed,false,reader);
                    
                        contentType = inc.derive(null,null);
                    } catch( DatatypeException e ) {
                        reader.reportError( e, XMLSchemaReader.ERR_BAD_TYPE, e.getMessage() );
                        return Expression.nullSet;
                    }
                } else {
                    // this is strange, as the type of the attribute
                    // must be a simple type in theory.
                    // but I'm not sure if we receive XSDatatypeExp as
                    // the content type --- it maybe some ReferenceExp,
                    // for example. So just degrade and assume token here.
                    
                    // I know  this is a sloppy work
                    contentType = reader.pool.createValue(
                        com.sun.msv.datatype.xsd.TokenType.theInstance,
                        new StringPair("","token"), // emulate RELAX NG built-in "token" type
                        fixed );
                }
            }
            
            if( "prohibited".equals(use) )
                // use='prohibited' is implemented through NoneType
                contentType = reader.pool.createData( NoneType.theInstance );
            
            exp = createAttribute(
                new SimpleNameClass( targetNamespace, name ),
                contentType );
        }
        
        if( isGlobal() ) {
            
            // register this expression as a global attribtue declaration.
            AttributeDeclExp decl = reader.currentSchema.attributeDecls.getOrCreate(name);
            if(decl.exp!=null)
                reader.reportError( 
                    new Locator[]{this.location,reader.getDeclaredLocationOf(decl)},
                    XMLSchemaReader.ERR_DUPLICATE_ATTRIBUTE_DEFINITION,
                    new Object[]{name} );
            reader.setDeclaredLocationOf(decl);
            if( exp instanceof AttributeExp )
                decl.set( (AttributeExp)exp );
            else {
                // sometimes, because of the error recovery,
                // exp can be something other than an AttributeExp.
                if( !reader.controller.hadError() )    throw new Error();
            }
            
            // TODO: @use is prohibited in global
            
        } else {
            // handle @use
            
            if( "optional".equals(use) || use==null || "prohibited".equals(use) )
                exp = reader.pool.createOptional(exp);
            else
            if( !"required".equals(use) )
                reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "use", use );
                // recover by assuming "required" (i.e., do nothing)
        }
        
        return exp;
    }
    
    /**
     * Allows the derived class to change it.
     */
    protected Expression createAttribute( NameClass nc, Expression exp ) {
        return reader.pool.createAttribute(nc,exp);
    }
    
    public String getTargetNamespaceUri() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.currentSchema.targetNamespace;
    }
    
    public void onEndChild( XSDatatypeExp type ) {
        super.onEndChild(type);
    }


    protected boolean isGlobal() {
        return parentState instanceof GlobalDeclState;
    }
}
