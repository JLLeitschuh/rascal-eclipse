package org.rascalmpl.eclipse.box;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;
import org.rascalmpl.eclipse.editor.RascalEditor;

public class PrintHandler extends AbstractHandler {
	static final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
	static final ITheme currentTheme = themeManager.getCurrentTheme();
	static final FontRegistry fontRegistry = currentTheme.getFontRegistry();
	
	void print(final Shell shell, final StyledText st, final Font printerFont) {
		StyledTextContent content = st.getContent();
		final StyledText nst = new StyledText(st.getParent(), SWT.READ_ONLY);
		nst.setContent(content);
		nst.setStyleRanges(st.getStyleRanges());
		nst.setFont(printerFont);
		nst.setLineSpacing(2);
		PrintDialog dialog = new PrintDialog(shell, SWT.PRIMARY_MODAL);
		final PrinterData data = dialog.open();
		if (data == null)
			return;
		final Printer printer = new Printer(data);
		nst.print(printer).run();
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Font printerFont = fontRegistry.get("rascal-eclipse.printerFontDefinition");
		if (HandlerUtil.getCurrentSelection(event) != null
				&& HandlerUtil.getCurrentSelectionChecked(event) instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) HandlerUtil
					.getCurrentSelectionChecked(event);
			if (sel.getFirstElement() instanceof IFile) {
				IFile f = (IFile) sel.getFirstElement();
				String ext = f.getFileExtension();
				URI uri = f.getLocationURI();
				IProject p = f.getProject();
				BoxPrinter boxPrinter = new BoxPrinter(p);
				boxPrinter.updateFont(printerFont);
				if (ext != null) {
					if (ext.equals("rsc"))
						boxPrinter.preparePrint(uri);
					else 
						boxPrinter.preparePrint(uri, ext);
					boxPrinter.menuPrint();
				}
				return null;
			}
		}
		if (HandlerUtil.getActiveEditor(event) != null
				&& HandlerUtil.getActiveEditor(event) instanceof BoxViewer)
						 {
			BoxViewer ate = ((BoxViewer) HandlerUtil
					.getActiveEditorChecked(event));
			Shell shell = ate.getEditorSite().getShell();
			print(shell, ate.getTextWidget(), printerFont);
			return null;
		}
		if (HandlerUtil.getActiveEditor(event) != null
								&&			HandlerUtil.getActiveEditor(event) instanceof RascalEditor)
		{
			RascalEditor ate = ((RascalEditor) HandlerUtil
					.getActiveEditorChecked(event));
			Shell shell = ate.getEditorSite().getShell();
			print(shell, ate.getTextWidget(), printerFont);
			return null;
			
		}
		System.err.println("Wrong:" + this.getClass());
		return null;
	}
}