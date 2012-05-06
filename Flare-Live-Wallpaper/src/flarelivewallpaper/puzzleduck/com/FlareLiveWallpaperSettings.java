

package flarelivewallpaper.puzzleduck.com;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class FlareLiveWallpaperSettings extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle newBundle) {
        super.onCreate(newBundle);
        getPreferenceManager().setSharedPreferencesName(FlareLiveWallpaper.SHARED_PREFS_NAME);
        addPreferencesFromResource(R.xml.flare_lwp_settings);
        
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override 
    protected void onResume() { 
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
    	
    }
    
    
}
