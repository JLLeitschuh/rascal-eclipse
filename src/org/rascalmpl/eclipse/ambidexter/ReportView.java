package org.rascalmpl.eclipse.ambidexter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;

import nl.cwi.sen1.AmbiDexter.AmbiDexterConfig;
import nl.cwi.sen1.AmbiDexter.IAmbiDexterMonitor;
import nl.cwi.sen1.AmbiDexter.Main;
import nl.cwi.sen1.AmbiDexter.grammar.Character;
import nl.cwi.sen1.AmbiDexter.grammar.Grammar;
import nl.cwi.sen1.AmbiDexter.grammar.NonTerminal;
import nl.cwi.sen1.AmbiDexter.grammar.SymbolString;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.exceptions.FactTypeUseException;
import org.eclipse.imp.pdb.facts.io.StandardTextReader;
import org.eclipse.imp.pdb.facts.visitors.VisitorException;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.rascalmpl.eclipse.Activator;
import org.rascalmpl.values.ValueFactoryFactory;
import org.rascalmpl.values.uptr.Factory;
import org.rascalmpl.values.uptr.SymbolAdapter;
import org.rascalmpl.values.uptr.TreeAdapter;
import org.rascalmpl.values.uptr.visitors.IdentityTreeVisitor;

public class ReportView extends ViewPart implements IAmbiDexterMonitor {
	public static final String ID = "rascal-eclipse.ambidexter.report";
	private static final IValueFactory VF = ValueFactoryFactory.getValueFactory();
	private final PrintStream out = RuntimePlugin.getInstance().getConsoleStream();
	private TableColumn nonterminals;
	private TableColumn sentences;
	private Table table;
	private final StandardTextReader reader = new StandardTextReader();
	
	public ReportView() { super(); }
	
	public void run(final Grammar grammar, final AmbiDexterConfig cfg) {
		table.removeAll();
		
		Runnable run = new Runnable() {
			public void run() {
				Main m = new Main(ReportView.this);
				m.setGrammar(grammar);
				m.setConfig(cfg);
				m.printGrammar(grammar);
				m.checkGrammar(grammar);
			};
		};

		Thread thread = new Thread(run);
		thread.setName("Ambidexter Fred");
		thread.start();
	}

	@Override
	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.BORDER);
	    nonterminals = new TableColumn(table, SWT.CENTER);
	    sentences = new TableColumn(table, SWT.CENTER);
	    nonterminals.setText("Symbol");
	    sentences.setText("Sentence");
	    nonterminals.setWidth(70);
	    sentences.setWidth(70);
	    table.setHeaderVisible(true);

	    IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
	    installAction(toolbar, new EditSentenceAction());
	    installAction(toolbar, new EditTreeAction());
	    installAction(toolbar, new BrowseTreeAction());
	    installAction(toolbar, new DiagnoseAction());
	}

	private void installAction(IToolBarManager toolbar, AbstractAmbidexterAction edit) {
		table.addSelectionListener(edit);
	    toolbar.add(edit);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void println() {
		out.println();
	}

	@Override
	public void println(Object o) {
		out.println(o);
	}

	@Override
	public void errPrintln() {
		out.println("error:");
	}

	@Override
	public void errPrintln(Object o) {
		out.println("error:" + o);
	}

	@Override
	public void ambiguousString(final AmbiDexterConfig cfg, final SymbolString s, final NonTerminal n, String messagePrefix) {
		try {
			final IConstructor sym = (IConstructor) reader.read(VF, Factory.uptr, Factory.Symbol, new ByteArrayInputStream(n.prettyPrint().getBytes()));
			final String ascii = toascci(s);
			final String module = getModuleName(cfg.filename);
			final String project = getProjectName(cfg.filename);
			
			addItem(sym, ascii, module, project, null);
		} catch (FactTypeUseException e) {
			Activator.getInstance().logException("failed to register ambiguity", e);
		} catch (IOException e) {
			Activator.getInstance().logException("failed to register ambiguity", e);
		}
	}

	private void addItem(final IConstructor sym, final String ascii,
			final String module, final String project, final IConstructor tree) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(new String[] { SymbolAdapter.toString(sym), ascii});
				item.setData("nonterminal", sym);
				item.setData("sentence", ascii);
				item.setData("module", module);
				item.setData("project", project);
				item.setData("tree", tree);
			}
		});
	}

	private String getProjectName(String filename) {
		int i = filename.indexOf('/');
		if (i != -1) {
			return filename.substring(0, i);
		}
		return null;
	}

	private String getModuleName(String filename) {
		int i = filename.indexOf('/');
		if (i != -1) {
			return filename.substring(i+1);
		}
		return null;
	}

	private String toascci(SymbolString s) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < s.size(); i++) {
			 Character ch = (Character) s.get(i);
			 b.append(ch.toAscii());
		}
		return b.toString();
	}

	public void list(final String project, final String module, IConstructor parseTree) {
		table.removeAll();
		
		try {
			parseTree.accept(new IdentityTreeVisitor() {
				@Override
				public IConstructor visitTreeAppl(IConstructor arg)
						throws VisitorException {
					for (IValue child : TreeAdapter.getArgs(arg)) {
						child.accept(this);
					}
					return arg;
				}
				
				@Override
				public IConstructor visitTreeAmb(IConstructor arg)
						throws VisitorException {
					IConstructor sym = null;
					String sentence = TreeAdapter.yield(arg);
					
					for (IValue child : TreeAdapter.getAlternatives(arg)) {
						sym = TreeAdapter.getType((IConstructor) child);
						child.accept(this);
					}
					
					addItem(sym, sentence, module, project, arg);
					return arg;
				}
			});
		} catch (VisitorException e) {
			// do nothing
		}
	}
}
