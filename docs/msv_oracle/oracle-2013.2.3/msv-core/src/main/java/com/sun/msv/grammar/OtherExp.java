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

package com.sun.msv.grammar;

/**
 * Base class for application-specific AGM annotation.
 * 
 * <p>
 * This primitive has no meaning to MSV. For example, the following expression
 * <pre>
 * Expression exp = new OtherExp( pool.createSequence(a,b) );
 * </pre>
 * is treated as if MSV sees the following, OtherExp-less expression:
 * <pre>
 * Expression exp = pool.createSequence(a,b);
 * </pre>
 * 
 * <p>
 * By using this "transparency", application can implement derived classes
 * of <code>OtherExp</code> and add application-specific information to AGM.
 * 
 * <p>
 * For example, you can implement <code>AnnotationInfoExp</code> class that derives
 * <code>OtherExp</code> and introduces "documentation" field.
 * Then you'll write a customized <code>XMLSchemaReader</code> that
 * parses &lt;annotation&gt; tag and mix <code>AnnotationInfoExp</code> into an AGM.
 * Your application can then examine it and do some useful things.
 * 
 * <p>
 * Those application-specific information added through OtherExp are completely
 * ignored by MSV. So the annotated AGM can still be used just like anormal AGM.
 * 
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class OtherExp extends Expression {
    
    /**
     * returns the string which will be used by ExpressionPrinter
     * to print this expression.
     */
    public String printName() {
        String className = this.getClass().getName();
        int idx = className.lastIndexOf('.');
        if(idx>=0)    className = className.substring(idx+1);
        return className;
    }
    
    /**
     * child expression.
     */
    public Expression exp;
    
    public OtherExp() {
    }
    protected final int calcHashCode() {
        return System.identityHashCode(this);
    }

    public OtherExp( Expression exp ) {
        this();
        this.exp = exp;
    }
    
    public boolean equals( Object o ) {
        return this==o;
    }
    
    protected boolean calcEpsilonReducibility() {
        if(exp==null)
//            // actual expression is not supplied yet.
//            // actual definition of the referenced expression must be supplied
//            // before any computation over the grammar.
//            throw new Error();    // assertion failed.
            return false;
        // this method can be called while parsing a grammar.
        // in that case, epsilon reducibility is just used for approximation.
        // therefore we can safely return false.
        
        return exp.isEpsilonReducible();
    }
    
    // derived class must be able to behave as a ReferenceExp
    public final Object visit( ExpressionVisitor visitor )                { return visitor.onOther(this); }
    public final Expression visit( ExpressionVisitorExpression visitor ){ return visitor.onOther(this); }
    public final boolean visit( ExpressionVisitorBoolean visitor )        { return visitor.onOther(this); }
    public final void visit( ExpressionVisitorVoid visitor )            { visitor.onOther(this); }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
