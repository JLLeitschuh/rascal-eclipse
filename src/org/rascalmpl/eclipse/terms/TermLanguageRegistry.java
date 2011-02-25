package org.rascalmpl.eclipse.terms;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.rascalmpl.interpreter.IEvaluatorContext;
import org.rascalmpl.interpreter.result.ICallableValue;

public class TermLanguageRegistry {
	private final Map<String, Language> languages = new HashMap<String,Language>();
	private final Map<String, IEvaluatorContext> evals = new HashMap<String, IEvaluatorContext>();
	private final Map<String, ICallableValue> parsers = new HashMap<String,ICallableValue>();
	private final Map<String, ICallableValue> analyses = new HashMap<String,ICallableValue>();

	static private class InstanceKeeper {
		public static TermLanguageRegistry sInstance = new TermLanguageRegistry();
	}
	
	public static TermLanguageRegistry getInstance() {
		return InstanceKeeper.sInstance;
	}
	
	private TermLanguageRegistry() { }
	
	public void clear() {
		languages.clear();
		evals.clear();
		parsers.clear();
		analyses.clear();
	}
	
	public void clear(String value) {
		Language lang = LanguageRegistry.findLanguage(value);
		if (lang != null) {
			LanguageRegistry.deregisterLanguage(lang);
		}
		languages.remove(value);
		evals.remove(value);
		parsers.remove(value);
		analyses.remove(value);
	}
	
	public void registerLanguage(String name, String extension, ICallableValue parser, IEvaluatorContext ctx) {
		Language l = new Language(name, "", "demo editor for " + name, "Terms", "icons/rascal3D_2-32px.gif", "http://www.rascal-mpl.org","rascal-eclipse",extension,"",null);
		languages.put(extension, l);
		evals.put(name, ctx);
		parsers.put(name, parser);
		LanguageRegistry.registerLanguage(l);
	}
	
	public void registerAnnotator(String lang, ICallableValue function) {
		analyses.put(lang, function);
	}

	public Language getLanguage(String fileExtension) {
		return languages.get(fileExtension);
	}
	
	public IEvaluatorContext getEvaluator(Language lang) {
		return evals.get(lang.getName());
	}
	
	public ICallableValue getParser(Language lang) {
		return  parsers.get(lang.getName());
	}

	public ICallableValue getAnnotator(String name) {
		return analyses.get(name);
	}
}
