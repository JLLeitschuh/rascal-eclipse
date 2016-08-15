@license{
  Copyright (c) 2009-2011 CWI
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html
}
@contributor{Jurgen J. Vinju - Jurgen.Vinju@cwi.nl - CWI}
module util::Prompt

import String;

@javaClass{org.rascalmpl.eclipse.library.util.Prompt}
public java str prompt(str msg);

@javaClass{org.rascalmpl.eclipse.library.util.Prompt}
public java void alert(str msg);
 

public int promptForInt(str msg) {
  return toInt(prompt(msg));
}

public real promptForReal(str msg) {
  return toReal(prompt(msg));
}

