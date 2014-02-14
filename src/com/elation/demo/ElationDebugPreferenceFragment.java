package com.elation.demo;

//import android.preference.PreferenceFragment;
//import android.support.v4.app.PreferenceFragment;
import android.support.v4.preference.PreferenceFragment;

import android.preference.Preference;
import android.preference.EditTextPreference;
import android.preference.CheckBoxPreference;
import android.preference.SwitchPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;



public class ElationDebugPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
  private EditTextPreference global_cobrand;
  private SwitchPreference global_reset_defaults;

  private EditTextPreference web_hostname;
  private EditTextPreference web_startpage;
                                  
  private PreferenceCategory api_category;
  private EditTextPreference api_hostname;
  private CheckBoxPreference api_sameasweb;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
 
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);

    web_hostname = (EditTextPreference) findPreference("config_web_hostname");
    web_startpage = (EditTextPreference) findPreference("config_web_startpage");

    api_category = (PreferenceCategory) findPreference("config_api");
    api_hostname = (EditTextPreference) findPreference("config_api_hostname");
    api_sameasweb = (CheckBoxPreference) findPreference("config_api_sameasweb");

    global_cobrand = (EditTextPreference) findPreference("config_cobrand");
    global_reset_defaults = (SwitchPreference) findPreference("config_reset_defaults");

    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    web_hostname.setSummary(web_hostname.getText());
    web_startpage.setSummary(web_startpage.getText());
    global_cobrand.setSummary(global_cobrand.getText());

    setApiHostVisibility();
  }
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference preference = findPreference(key);
    if (preference instanceof EditTextPreference) {
      EditTextPreference editTextPreference = (EditTextPreference)preference;
      editTextPreference.setSummary(editTextPreference.getText());
    } else if (preference == api_sameasweb) {
      setApiHostVisibility();
    } else if (preference == global_reset_defaults) {
      this.resetDefaults();
    }
  }
  public void setApiHostVisibility() {
    if (api_category != null && api_sameasweb.isChecked()) {
      //api_category.removePreference(api_hostname);
      api_hostname.setSummary(web_hostname.getText());
      api_hostname.setEnabled(false);
    } else {
      api_hostname.setSummary(api_hostname.getText());
      //api_category.addPreference(api_hostname);
      api_hostname.setEnabled(true);
    }
  }
  public void resetDefaults() {
    web_hostname.setText(getActivity().getString(R.string.config_web_hostname_default));
    web_startpage.setText(getActivity().getString(R.string.config_web_startpage_default));
    api_hostname.setText(getActivity().getString(R.string.config_api_hostname_default));
    global_cobrand.setText(getActivity().getString(R.string.config_cobrand_default));

    //global_reset_defaults.setChecked(false);
    global_reset_defaults.getEditor().putBoolean("config_reset_defaults", false).apply();

  }
}


