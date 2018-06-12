/*
 * Child Growth Monitor - quick and accurate data on malnutrition
 * Copyright (c) 2018 Markus Matiaschek <mmatiaschek@gmail.com> for Welthungerhilfe
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package de.welthungerhilfe.cgm.scanner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

//import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import de.welthungerhilfe.cgm.scanner.R;

import de.welthungerhilfe.cgm.scanner.activities.CreateDataActivity;
import de.welthungerhilfe.cgm.scanner.activities.RecorderActivity;
import de.welthungerhilfe.cgm.scanner.adapters.RecyclerMeasureAdapter;
import de.welthungerhilfe.cgm.scanner.dialogs.ManualMeasureDialog;
import de.welthungerhilfe.cgm.scanner.helper.AppConstants;
import de.welthungerhilfe.cgm.scanner.models.Loc;
import de.welthungerhilfe.cgm.scanner.models.Measure;

public class MeasuresDataFragment extends Fragment implements View.OnClickListener, ManualMeasureDialog.OnManualMeasureListener {
    private Context context;


    private RecyclerView recyclerMeasure;
    private RecyclerMeasureAdapter adapterMeasure;

    private FloatingActionButton fabCreate;

    public static MeasuresDataFragment newInstance(Context context) {
        MeasuresDataFragment fragment = new MeasuresDataFragment();
        fragment.context = context;

        return fragment;
    }

    public void onResume() {
        super.onResume();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: when coming from automatic scan show ManualMeasureDialog before displaying auto
        View view = inflater.inflate(R.layout.fragment_measure, container, false);
        
        recyclerMeasure = view.findViewById(R.id.recyclerMeasure);
        adapterMeasure = new RecyclerMeasureAdapter(context, ((CreateDataActivity)context).measures);
        recyclerMeasure.setAdapter(adapterMeasure);
        recyclerMeasure.setLayoutManager(new LinearLayoutManager(context));

        fabCreate = view.findViewById(R.id.fabCreate);
        fabCreate.setOnClickListener(this);
        
        return view;
    }

    public void addMeasure(Measure measure) {
        adapterMeasure.addMeasure(measure);
    }

    public void addMeasures(List<Measure> measures) {
        if (adapterMeasure != null)
            adapterMeasure.addMeasures(measures);
    }

    public void createMeasure() {
		// TODO: Strings.xml
        final CharSequence[] options = {"Add Manual Measure", "Scan Measure"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Measure");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                if (which == 0) {
                    ManualMeasureDialog dialog = new ManualMeasureDialog(context);
                    dialog.setManualMeasureListener(MeasuresDataFragment.this);
                    dialog.show();
                } else if (which == 1) {
                    //Intent intent = new Intent(getContext(), ScreenRecordActivity.class);
                    Intent intent = new Intent(getContext(), RecorderActivity.class);
                    intent.putExtra(AppConstants.EXTRA_PERSON, ((CreateDataActivity)context).person);
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabCreate:
                Crashlytics.log("Add Measure to person");
                if (((CreateDataActivity)context).person == null) {
                    Snackbar.make(fabCreate, "Please register person first", Snackbar.LENGTH_LONG).show();
                } else {
                    createMeasure();
                }
                break;
        }
    }

    @Override
    public void onManualMeasure(float height, float weight, float muac, float headCircumference, Loc location) {
        ((CreateDataActivity)context).setMeasureData(height, weight, muac, headCircumference,"No Additional Info", location);
    }
}
