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

package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

/**
 * Feeds AttributeToken to the expression and obtains the residual (content model).
 * 
 * AttributeTokens are fed in order-less fashion.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeFeeder implements ExpressionVisitorExpression {
    protected final REDocumentDeclaration    docDecl;
    protected final ExpressionPool            pool;
    
    private Token                            token;
        
    public AttributeFeeder( REDocumentDeclaration docDecl ) {
        this.docDecl    = docDecl;
        this.pool        = docDecl.pool;
    }

    
    public final Expression feed( Expression exp, AttributeToken token, boolean ignoreUndeclaredAttribute ) {
        this.token = token;
        Expression r = exp.visit(this);
        
        if(r!=Expression.nullSet || !ignoreUndeclaredAttribute)    return r;
        
        // if ignoreUndeclaredAttribute==true and expression is nullSet,
        // we have to check which of the following is the case.
        //   (1) attribute is undefined
        //   (2) value of the attribute was rejected.
        
        this.token = token.createRecoveryAttToken();
        r = exp.visit(this);
        
        // if wild card token is rejected, then it must be the absence of declaration.
        if(r==Expression.nullSet)    return exp;
        
        // otherwise the value was wrong.
        return Expression.nullSet;
        
//            if( com.sun.msv.driver.textui.Debug.debug )
//                System.out.println("after feeding "+atts.getQName(i)+" attribute");
//                System.out.println(com.sun.msv.grammar.trex.util.TREXPatternPrinter.printContentModel(exp));
    }
    

    public Expression onAttribute( AttributeExp exp ) {
        if( token.match(exp) )    return Expression.epsilon;
        else                    return Expression.nullSet;
    }
    
//    /**
//     * checks if the given expression is attribute-free.
//     * 
//     * if a expression is attribute free, then the residual must be nullSet.
//     */
//    protected final boolean isAttributeFree( Expression exp ) {
//        Object o = exp.verifierTag;
//        return o!=null && ((OptimizationTag)o).isAttributeFree==Boolean.TRUE;
//    }
    
    public Expression onChoice( ChoiceExp exp ) {
//        if( isAttributeFree(exp) )    return Expression.nullSet;
        return pool.createChoice( exp.exp1.visit(this), exp.exp2.visit(this) );
    }
    public Expression onElement( ElementExp exp ) {
        return Expression.nullSet;
    }
    public Expression onOneOrMore( OneOrMoreExp exp ) {
//        if( isAttributeFree(exp) )    return Expression.nullSet;
        return pool.createSequence(
            exp.exp.visit(this),
            pool.createZeroOrMore(exp.exp) );
    }
    public Expression onMixed( MixedExp exp ) {
        return pool.createMixed( exp.exp.visit(this) );
    }
    public Expression onList( ListExp exp ) {
        return Expression.nullSet;
    }
    public Expression onEpsilon()        { return Expression.nullSet; }
    public Expression onNullSet()        { return Expression.nullSet; }
    public Expression onAnyString()        { return Expression.nullSet; }
    public Expression onRef( ReferenceExp exp ) {
        return exp.exp.visit(this);
    }
    public Expression onOther( OtherExp exp ) {
        return exp.exp.visit(this);
    }
    public Expression onSequence( SequenceExp exp ) {
//        if( isAttributeFree(exp) )    return Expression.nullSet;
        // for attributes only, sequence acts as orderless
        return pool.createChoice(
            pool.createSequence( exp.exp1.visit(this), exp.exp2 ),
            pool.createSequence( exp.exp1, exp.exp2.visit(this) ) );
    }
    public Expression onData( DataExp exp ) {
        return Expression.nullSet;
    }
    public Expression onValue( ValueExp exp ) {
        return Expression.nullSet;
    }
    public Expression onConcur( ConcurExp exp ) {
        return pool.createConcur( exp.exp1.visit(this), exp.exp2.visit(this) );
    }
    public Expression onInterleave( InterleaveExp exp ) {
        return pool.createChoice(
            pool.createInterleave( exp.exp1.visit(this), exp.exp2 ),
            pool.createInterleave( exp.exp1, exp.exp2.visit(this) ) );
    }

}
