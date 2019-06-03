/*******************************************************************************
 * Copyright (c) 2009-2013 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package org.rascalmpl.eclipse.library.vis.figure.compose;

import org.rascalmpl.eclipse.library.vis.figure.interaction.swtwidgets.Scrollable;
import org.rascalmpl.eclipse.library.vis.properties.PropertyManager;
import org.rascalmpl.eclipse.library.vis.swt.IFigureConstructionEnv;
import org.rascalmpl.eclipse.library.vis.util.FigureMath;
import org.rascalmpl.eclipse.library.vis.util.vector.BoundingBox;
import org.rascalmpl.eclipse.library.vis.util.vector.Dimension;
import org.rascalmpl.eclipse.library.vis.util.vector.Rectangle;

import io.usethesource.vallang.IConstructor;

public class WidthDependsOnHeightWrapper extends Scrollable{

	Dimension major;
	
	public WidthDependsOnHeightWrapper(Dimension major, IFigureConstructionEnv env, IConstructor inner, PropertyManager properties) {
		super(major != Dimension.X, major != Dimension.Y, env,inner,properties);
		this.major = major;
		prop.stealExternalPropertiesFrom(innerFig.prop);
	}
	
	@Override
	public void computeMinSize(){
		//super.computeMinSize();
		BoundingBox iminSize = widget.getFigure().minSize;
		org.eclipse.swt.graphics.Rectangle r = widget.computeTrim(0, 0, FigureMath.ceil(iminSize.getX()), FigureMath.ceil(iminSize.getY()));
		minSize.set(r.width +1 ,r.height +1);
		Dimension minor = major.other();
		minSize.set(minor, Math.ceil(iminSize.get(minor))  );
	}
	
	@Override
	public boolean widthDependsOnHeight(){
		return true;
	}
	
	@Override
	public void resizeElement(Rectangle view) {
		widget.getFigure().size.set(size);
	}

}
