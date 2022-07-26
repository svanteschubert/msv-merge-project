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

package com.sun.msv.verifier;

import org.iso_relax.verifier.VerifierHandler;
import org.relaxng.datatype.Datatype;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

/**
 * Interface of verifier.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface IVerifier extends VerifierHandler {

    /**
     * checks if the document was valid.
     * This method may not be called before verification was completed.
     */
    boolean isValid();
    
    /**
     * returns current element type.
     * 
     * Actual java type depends on the implementation.
     * This method works correctly only when called immediately
     * after handling startElement event.
     * 
     * @return null
     *        this method returns null when it doesn't support
     *        type-assignment feature, or type-assignment is impossible
     *        for the current element (for example due to the ambiguous grammar).
     */
    Object getCurrentElementType();
    
    /**
     * gets DataType that validated the last characters.
     * 
     * <p>
     * This method works correctly only when called immediately
     * after startElement and endElement method. When called, this method
     * returns DataType object that validated the last character literals.
     * 
     * <p>
     * For RELAX NG grammar, this method can return an array of length 2 or more.
     * This happens when the last character matches &lt;list&gt; pattern.
     * In that case, each type corresponds to each token (where tokens are the
     * white-space separation of the last characters).
     * 
     * <p>
     * For any other grammar, this method always returns an array of length 1
     * (or null, if the type assignment failed).
     * 
     * <p>
     * So when you are using VerifierFilter, you can call this method only
     * in your startElement and endElement method.
     * 
     * @return null
     *        if type-assignment was not possible.
     */
    Datatype[] getLastCharacterType();

    /**
     * Turns on/off the panic mode.
     * Panic mode is on by default. Turning it on is good
     * for general use. It prevents false error messages to appear.
     * <p>
     * However, turning it off is sometimes useful, when
     * you are sure that the structure of the document is
     * almost valid (e.g., validating a document generated by an
     * application or when you are sure about the validity of the
     * structure and only interested in validating datatypes,)
     */
    void setPanicMode( boolean usePanicMode );

    Locator getLocator();
    ErrorHandler getErrorHandler();
    void setErrorHandler( ErrorHandler handler );
}
