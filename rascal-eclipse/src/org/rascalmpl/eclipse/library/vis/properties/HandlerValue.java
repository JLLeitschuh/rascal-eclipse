/*******************************************************************************
 * Copyright (c) 2009-2013 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package org.rascalmpl.eclipse.library.vis.properties;

import org.rascalmpl.eclipse.library.vis.swt.ICallbackEnv;

import io.usethesource.vallang.IValue;
import io.usethesource.vallang.type.Type;

public class HandlerValue extends PropertyValue<IValue>  {
	
	IValue fun;
	IValue value;

	
	public HandlerValue(IValue fun){
		this.fun = fun;
	}
	
	public IValue execute(ICallbackEnv env,Type[] types,IValue[] args){
		value = env.executeRascalCallBack(fun, types, args).getValue();
		return value;
	}

	@Override
	public IValue getValue() {
		return value;
	}

}
