/*******************************************************************************
 * Copyright (c) 2009-2012 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   * Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI
 *   * Emilie Balland - (CWI)
 *   * Arnold Lankamp - Arnold.Lankamp@cwi.nl
 *   * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
*******************************************************************************/
package org.rascalmpl.eclipse.debug.ui.presentation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.rascalmpl.eclipse.Activator;
import org.rascalmpl.eclipse.IRascalResources;
import org.rascalmpl.eclipse.debug.core.model.RascalDebugTarget;
import org.rascalmpl.eclipse.debug.core.model.RascalStackFrame;
import org.rascalmpl.eclipse.debug.core.model.RascalThread;
import org.rascalmpl.eclipse.debug.core.model.RascalValue;
import org.rascalmpl.eclipse.debug.core.model.RascalVariable;
import org.rascalmpl.eclipse.uri.URIEditorInput;
import org.rascalmpl.eclipse.uri.URIStorage;

/**
 * Renders Rascal debug elements
 */
public class RascalModelPresentation extends LabelProvider implements IDebugModelPresentation {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attribute, Object value) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
	  try {
	    if (element instanceof RascalDebugTarget) {
	      return getTargetText((RascalDebugTarget)element);
	    } else if (element instanceof RascalThread) {
	      return getThreadText((RascalThread)element);
	    } else if (element instanceof RascalStackFrame) {
	      return getStackFrameText((RascalStackFrame)element);
	    } else if (element instanceof RascalValue) {
	      return ((RascalValue) element).getReferenceTypeName();
	    } else if (element instanceof RascalVariable) {
	      return ((RascalVariable) element).getName();
	    }
	  } catch (DebugException e) {
	    return null;
	  }
	  finally {}
	  return null;
	}

	/**
	 * Returns a label for the given stack frame
	 * 
	 * @param frame a stack frame
	 * @return a label for the given stack frame 
	 */
	private String getStackFrameText(RascalStackFrame frame) {
		try {
			StringBuffer text = new StringBuffer();
			
			text.append(frame.getName());
			text.append(" [line: ");
			text.append(frame.getLineNumber());
			
			if (frame.hasSourceName()) {
				text.append(", source: ");
				text.append(frame.getSourceName());
			}
			
			text.append("]");
			
			return text.toString();
		} catch (DebugException e) {
		}
		return null;

	}

	/**
	 * Returns a label for the given debug target
	 * 
	 * @param target debug target
	 * @return a label for the given debug target
	 */
	private String getTargetText(RascalDebugTarget target) {
		try {
			String pgmPath = target.getLaunch().getLaunchConfiguration().getAttribute(IRascalResources.ATTR_RASCAL_PROGRAM, (String)null);
			if (pgmPath != null) {
				IPath path = new Path(pgmPath);
				String label = "";
				if (target.isTerminated()) {
					label = "<terminated>";
				}
				return label + "Rascal [" + path.lastSegment() + "]";
			}
		} catch (CoreException e) {
		}
		return "Rascal";

	}

	/**
	 * Returns a label for the given thread
	 * 
	 * @param thread a thread
	 * @return a label for the given thread
	 */
	private String getThreadText(RascalThread thread) {
		String label;
		try {
			label = thread.getName();
		} catch (DebugException e) {
			//TODO: to improve
			label = "noname";
		}
		if (thread.isTerminated()) {
			label = "<terminated> " + label;
		} else if (thread.isStepping()) {
			label += " (stepping)";
		} else if (thread.isSuspendedByBreakpoint()) {
			label += " (suspended by line breakpoint)";
		} else if (thread.isSuspended()) {
			label += " (suspended)";
		}
		return label;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue, org.eclipse.debug.ui.IValueDetailListener)
	 */
	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
		  Activator.log("unexpected problem in debug view", e);
		}
		listener.detailComputed(value, detail);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile)element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile)((ILineBreakpoint)element).getMarker().getResource());
		}
		if (element instanceof URIStorage) {
			return new URIEditorInput((URIStorage) element);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint || element instanceof URIStorage) {
			return UniversalEditor.EDITOR_ID;
		}
		return null;
	}

}
