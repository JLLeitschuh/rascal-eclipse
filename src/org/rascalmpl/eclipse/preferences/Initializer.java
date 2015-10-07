package org.rascalmpl.eclipse.preferences;

import io.usethesource.impulse.preferences.IPreferencesService;
import io.usethesource.impulse.preferences.PreferencesInitializer;
import org.rascalmpl.eclipse.Activator;

public class Initializer extends PreferencesInitializer {
	@Override
	public void initializeDefaultPreferences() {
		IPreferencesService service = Activator.getInstance().getPreferencesService();
		service.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, RascalPreferences.enableStaticChecker, false);
	}

	@Override
	public void clearPreferencesOnLevel(String level) {
		IPreferencesService service = Activator.getInstance().getPreferencesService();
		service.clearPreferenceAtLevel(IPreferencesService.DEFAULT_LEVEL, RascalPreferences.enableStaticChecker);
	}
}
