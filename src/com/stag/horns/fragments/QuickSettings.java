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

package com.stag.horns.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import com.stag.horns.R;

import com.stag.horns.preferences.CustomSeekBarPreference;

public class QuickSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private ListPreference mQSTileStyle;
    private static final String QS_TILE_STYLE = "qs_tile_style";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.horns_quicksettings);
        ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        // set qs tile style
        mQSTileStyle = (ListPreference) findPreference(QS_TILE_STYLE);
        int style = Settings.System.getInt(resolver,
                Settings.System.QS_TILE_STYLE, 0);
        mQSTileStyle.setValue(String.valueOf(style));
        mQSTileStyle.setSummary(mQSTileStyle.getEntry());
        mQSTileStyle.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.HORNS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQSTileStyle) {
            int style = Integer.valueOf((String) newValue);
            int index = mQSTileStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.QS_TILE_STYLE, style);
            mQSTileStyle.setSummary(mQSTileStyle.getEntries()[index]);
            return true;
        }
        return false;
    }
}
