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

import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.xmlschema.RedefinableExp;
import com.sun.msv.reader.ExpressionWithChildState;

/** state that parses redefinable declaration.
 * 
 * "Declarations" are attribute, element, complexType, group, and attributeGroup.
 * simpleType is treated differently.
 * 
 * <p>
 * When this state is used under states other than RedefineState, this class
 * doesn't do anything. When used under RedefineState, this class does several
 * tricks to make redefinition easy.
 * 
 * <p>
 * Redefinition is done in the following steps.
 * 
 * <h2> Step.1 </h2>
 * <p>
 * First, say redefinition of declaration "ABC" is found, "ABC" has already
 * defined once and it is
 * 
 * <ul>
 *  <li>referenced from {@link ReferenceContainer} by name, and
 *  <li>referenced from other expressions directly.
 * </ul>
 * 
 * <img src="doc-files/redefine1.gif" />
 * 
 * <h2> Step.2 </h2>
 * <p>
 * In startSelf method, this class clones the current definition, and updates
 * ReferenceContainer to point to the cloned definition (right side).
 * 
 * <img src="doc-files/redefine2.gif" />
 * 
 * <p>
 * Note that other expressions hold direct reference to the original definition
 * (left side), and these references are not affected by this update.
 * 
 * <h2> Step.3 </h2>
 * <p>
 * Body of redefinition is parsed and corresponding expression is created by
 * derived class. This step is done no differently.
 * 
 * <p>
 * Since ReferenceContainer has updated, any reference to this expression found
 * during this step is bound to the cloned definition. This self reference usually
 * happens.
 * 
 * <h2> Step.4 </h2>
 * <p>
 * After the body of redefinition is parsed, the original definition (left side)
 * is updated by using new expression.
 * 
 * <img src="doc-files/redefine3.gif" />
 * 
 * <p>
 * From now on, redefinition becomes visible to all expressions that hold
 * direct reference to the original definition. The cloned definition is kept
 * as-is so that any self reference found in the body will be maintained correctly.
 * 
 * <h2> Step.5 </h2>
 * <p>
 * Finally, ReferenceContainer is updated again to point to the updated definition.
 * Therefore successive reference to "ABC" will be bound to the updated definition.
 * Cloned old definition is kept as-is.
 * 
 * <img src="doc-files/redefine4.gif" />
 */
public abstract class RedefinableDeclState extends ExpressionWithChildState {

    protected boolean isGlobal() {
        return parentState instanceof GlobalDeclState;
    }
    /**
     * Returns true if this declaration is a redefinition of an
     * existing declaration.
     */
    protected boolean isRedefine() {
        return parentState instanceof RedefineState;
    }

    /**
     * keeps a reference to previous declaration.
     * 
     * this field is used only when in redefine mode. Derived class should use
     * this declaration instead of getting one from ReferenceContainer through
     * XMLSchemaSchema.
     */
    protected RedefinableExp oldDecl;
    
    /** gets appropriate ReferenceContainer to store this declaration. */
    protected abstract ReferenceContainer getContainer();
    
    protected void startSelf() {
        super.startSelf();
        
        if( isRedefine() ) {
            final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
            
            String name = startTag.getAttribute("name");
            if( name==null )
                // ignore this error just for now.
                // this error will be reported in annealExpression method.
                return;
            
            oldDecl = (RedefinableExp)getContainer()._get(name);
            if(oldDecl==null) {
                reader.reportError( XMLSchemaReader.ERR_REDEFINE_UNDEFINED, name );
                // recover by creating a dummy object.
                oldDecl = (RedefinableExp)getContainer()._getOrCreate(name);
                return;
            }
            
            getContainer().redefine( name, oldDecl.getClone() );
        }
    }
    
    protected void endSelf() {
        if( oldDecl!=null )
            getContainer().redefine( oldDecl.name, oldDecl );
        
        super.endSelf();
    }
}
