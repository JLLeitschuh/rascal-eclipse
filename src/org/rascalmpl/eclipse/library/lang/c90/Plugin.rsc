@license{
  Copyright (c) 2009-2011 CWI
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html
}
@contributor{Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI}
module lang::c90::Plugin

import lang::c90::\syntax::C;
import util::IDE;
import ParseTree;

public TranslationUnit parseTU(str input, loc l) {
  return parse(#TranslationUnit, input, l);
}

public void main() {
  registerLanguage("C90 program", "c", parseTU);
  registerLanguage("C90 header file", "h", parseTU);
}