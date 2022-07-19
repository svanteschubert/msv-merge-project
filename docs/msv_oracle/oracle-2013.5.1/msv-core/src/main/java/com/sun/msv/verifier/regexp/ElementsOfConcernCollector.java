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

import java.util.Collection;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionVisitor;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

/**
 * Collects "elements of concern".
 * 
 * "Elements of concern" are ElementExps that are possibly applicable to
 * the next element. These gathered element declarations are then tested against
 * next XML element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementsOfConcernCollector implements ExpressionVisitor
{
    private Collection result;
    
    public ElementsOfConcernCollector() {}
    
    public final void collect( Expression exp, Collection result )
    {
        this.result = result;
        exp.visit(this);
    }
    
    public final Object onAttribute( AttributeExp exp )    { return null; }
    
    public final Object onChoice( ChoiceExp exp )
    {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
        return null;
    }
    
    public final Object onElement( ElementExp exp )
    {
        // found.
        result.add( exp );
        return null;
    }
    
    public final Object onOneOrMore( OneOrMoreExp exp )
    {
        exp.exp.visit(this);
        return null;
    }
    
    public final Object onMixed( MixedExp exp )
    {
        exp.exp.visit(this);
        return null;
    }
    
    public final Object onEpsilon()        { return null; }
    public final Object onNullSet()        { return null; }
    public final Object onAnyString()    { return null; }
    public final Object onData( DataExp exp )    { return null; }
    public final Object onValue( ValueExp exp )    { return null; }
    public final Object onList( ListExp exp )    { return null; }
    
    public final Object onRef( ReferenceExp exp ) {
        return exp.exp.visit(this);
    }
    
    public final Object onOther( OtherExp exp ) {
        return exp.exp.visit(this);
    }
    
    public final Object onSequence( SequenceExp exp )
    {
        exp.exp1.visit(this);
        if(exp.exp1.isEpsilonReducible())
            exp.exp2.visit(this);
        return null;
    }

    public final Object onConcur( ConcurExp exp )
    {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
        return null;
    }
    
    public final Object onInterleave( InterleaveExp exp )
    {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
        return null;
    }
    
}
