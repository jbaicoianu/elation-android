package com.elation.demo;

//import android.preference.PreferenceFragment;
import android.support.v4.app.PreferenceFragment;

import android.preference.Preference;
import android.preference.EditTextPreference;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;



public class ElationDemoDebugPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
 
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);

    EditTextPreference host = (EditTextPreference) findPreference("config_hostname");
    EditTextPreference cobrand = (EditTextPreference) findPreference("config_cobrand");
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    host.setSummary(host.getText());
    cobrand.setSummary(cobrand.getText());
  }
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference preference = findPreference(key);
    if (preference instanceof EditTextPreference) {
      EditTextPreference editTextPreference = (EditTextPreference)preference;
      editTextPreference.setSummary(editTextPreference.getText());
    }
  }
}


