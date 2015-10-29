/*******************************************************************************
 * Copyright (c) 2014, 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *  
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License v1.0 which accompanies this distribution,
 *  and is available at http://www.eclipse.org/legal/epl-v10.html
 *  
 *******************************************************************************/
/**
 *
 * $Id$
 */
package com.cisco.yangide.ext.model.validation;

import com.cisco.yangide.ext.model.Node;

/**
 * A sample validator interface for {@link com.cisco.yangide.ext.model.Node}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface NodeValidator {
    boolean validate();

    boolean validateParent(Node value);
}
