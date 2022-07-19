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

package com.sun.msv.grammar;

/**
 * Visitor interface for Expression and its derived types.
 * 
 * <p>
 * You may want to use ExpressionVisitorXXXX class if you want to
 * return boolean, void, or {@link Expression}.
 * 
 * <p>
 * It is the callee's responsibility to traverse child expression.
 * Expression and its derived classes do not provide any traversal.
 * See {@link ExpressionCloner} for example.
 * 
 * <p>
 * onRef method is called for all subclass of ReferenceExp. So you can safely use this
 * interface to visit AGMs from RELAX grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface ExpressionVisitor {
    
    Object onAttribute( AttributeExp exp );
    Object onChoice( ChoiceExp exp );
    Object onElement( ElementExp exp );
    Object onOneOrMore( OneOrMoreExp exp );
    Object onMixed( MixedExp exp );
    Object onList( ListExp exp );
    Object onRef( ReferenceExp exp );
    Object onOther( OtherExp exp );
    Object onEpsilon();
    Object onNullSet();
    Object onAnyString();
    Object onSequence( SequenceExp exp );
    Object onData( DataExp exp );
    Object onValue( ValueExp exp );
    Object onConcur( ConcurExp p );
    Object onInterleave( InterleaveExp p );
}
