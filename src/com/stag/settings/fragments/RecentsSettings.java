/*
 * Copyright (C) 2018 StagOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stag.settings.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.os.UserHandle;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import com.android.internal.util.stag.StagUtils;
import com.stag.settings.R;

public class RecentsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String RECENTS_COMPONENT_TYPE = "recents_component";
    private static final String IMMERSIVE_RECENTS = "immersive_recents"; 
    private static final String RECENTS_DATE = "recents_full_screen_date"; 
    private static final String RECENTS_CLOCK = "recents_full_screen_clock";

    private ListPreference mRecentsClearAllLocation;
    private ListPreference mRecentsComponentType;
    private SwitchPreference mRecentsClearAll;
    private ListPreference mImmersiveRecents;
    private SwitchPreference mClock;
    private SwitchPreference mDate;

    private SharedPreferences mPreferences;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.stag_settings_recents);
        ContentResolver resolver = getActivity().getContentResolver();
        mContext = getActivity().getApplicationContext();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        // clear all recents
        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);

        // recents component type
        mRecentsComponentType = (ListPreference) findPreference(RECENTS_COMPONENT_TYPE);
        int type = Settings.System.getInt(resolver,
                Settings.System.RECENTS_COMPONENT, 0);
        mRecentsComponentType.setValue(String.valueOf(type));
        mRecentsComponentType.setSummary(mRecentsComponentType.getEntry());
        mRecentsComponentType.setOnPreferenceChangeListener(this);
       // immersive recents
        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        mImmersiveRecents.setValue(String.valueOf(Settings.System.getIntForUser(
                resolver, Settings.System.IMMERSIVE_RECENTS, 0, UserHandle.USER_CURRENT)));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);

        mClock = (SwitchPreference) findPreference(RECENTS_CLOCK);
        mDate = (SwitchPreference) findPreference(RECENTS_DATE);
        updateDisablestate(mode);
    }



    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.STAG_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void openAOSPFirstTimeWarning() { 
        new AlertDialog.Builder(getActivity()) 
                .setTitle(getResources().getString(R.string.aosp_first_time_title))
                .setMessage(getResources().getString(R.string.aosp_first_time_message))
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                }).show();
    }

    public void updateDisablestate(int mode) { 
        if (mode == 0 || mode == 2) { 
           mClock.setEnabled(false); 
           mDate.setEnabled(false); 
        } else { 
           mClock.setEnabled(true); 
           mDate.setEnabled(true); 
        } 

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRecentsClearAllLocation) {
            int value = Integer.parseInt((String) newValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, value, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
        return true;
 } else if (preference == mRecentsComponentType) {
            int type = Integer.valueOf((String) newValue);
            int index = mRecentsComponentType.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_COMPONENT, type);
            mRecentsComponentType.setSummary(mRecentsComponentType.getEntries()[index]);
            if (type == 1) { // Disable swipe up gesture, if oreo type selected
               Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0);
            }
            StagUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mImmersiveRecents) {
                   int mode = Integer.valueOf((String) newValue); 
            Settings.System.putIntForUser(getActivity().getContentResolver(), Settings.System.IMMERSIVE_RECENTS,
                    Integer.parseInt((String) objValue), UserHandle.USER_CURRENT);
            mImmersiveRecents.setValue((String) newValue);
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            updateDisablestate(mode);
            mPreferences = mContext.getSharedPreferences("recent_settings", Activity.MODE_PRIVATE);
            if (!mPreferences.getBoolean("first_info_shown", false) && newValue != null) {
                getActivity().getSharedPreferences("recent_settings", Activity.MODE_PRIVATE)
                        .edit()
                        .putBoolean("first_info_shown", true)
                        .commit();
                openAOSPFirstTimeWarning();
            }
            return true;
    }
        return false;
    }
}
