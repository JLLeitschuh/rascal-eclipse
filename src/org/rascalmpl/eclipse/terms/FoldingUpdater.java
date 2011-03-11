package org.rascalmpl.eclipse.terms;

import java.util.HashMap;
import java.util.List;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.visitors.VisitorException;
import org.eclipse.imp.services.base.FolderBase;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.rascalmpl.eclipse.Activator;
import org.rascalmpl.values.ValueFactoryFactory;
import org.rascalmpl.values.uptr.Factory;
import org.rascalmpl.values.uptr.ProductionAdapter;
import org.rascalmpl.values.uptr.TreeAdapter;
import org.rascalmpl.values.uptr.visitors.TreeVisitor;

public class FoldingUpdater extends FolderBase {

	@Override
	protected void sendVisitorToAST(
			HashMap<Annotation, Position> newAnnotations,
			List<Annotation> annotations, Object ast) {
		if (ast instanceof IConstructor) {
			try {
				((IConstructor) ast).accept(new TreeVisitor() {
					@Override
					public IConstructor visitTreeCycle(IConstructor arg)
							throws VisitorException {
						return null;
					}
					
					@Override
					public IConstructor visitTreeChar(IConstructor arg) throws VisitorException {
						return null;
					}
					
					@Override
					public IConstructor visitTreeAppl(IConstructor arg) throws VisitorException {
						IConstructor prod = TreeAdapter.getProduction(arg);
						IValueFactory VF = ValueFactoryFactory.getValueFactory();
						
						if (ProductionAdapter.hasAttribute(prod, Factory.Attr_Term.make(VF, VF.node("Foldable")))) {
							makeAnnotation(arg);	
						}
						else if (arg.getAnnotation("foldable") != null) {
							makeAnnotation(arg);
						}
						
						if (!TreeAdapter.isLexical(arg)) {
							for (IValue kid :  TreeAdapter.getASTArgs(arg)) {
								kid.accept(this);
							}
						}
						
						return null;
					}
					
					@Override
					public IConstructor visitTreeAmb(IConstructor arg) throws VisitorException {
						return null;
					}
				});
			} catch (VisitorException e) {
				Activator.getInstance().logException(e.getMessage(), e);
			}
		}
	}
}