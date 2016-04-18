package com.android.ocasa.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.ocasa.R;
import com.android.ocasa.activity.ReadFieldActvivity;
import com.android.ocasa.barcode.BarcodeActivity;
import com.android.ocasa.loader.RecordTaskLoaderTest;
import com.android.ocasa.loader.SaveFormTask;
import com.android.ocasa.model.Column;
import com.android.ocasa.model.Field;
import com.android.ocasa.model.FieldType;
import com.android.ocasa.model.Record;
import com.android.ocasa.model.Table;
import com.android.ocasa.util.DatePickerDialogFragment;
import com.android.ocasa.util.DateTimeHelper;
import com.android.ocasa.util.TimePickerDialogFragment;

import com.android.ocasa.viewmodel.CellViewModel;
import com.android.ocasa.viewmodel.FieldViewModel;
import com.android.ocasa.viewmodel.FormViewModel;
import com.android.ocasa.widget.FieldComboView;
import com.android.ocasa.widget.FieldDateView;
import com.android.ocasa.widget.FieldListView;
import com.android.ocasa.widget.FieldMapView;
import com.android.ocasa.widget.FieldPhoneView;
import com.android.ocasa.widget.FieldTimeView;
import com.android.ocasa.widget.FieldViewActionListener;
import com.android.ocasa.widget.FieldViewAdapter;
import com.android.ocasa.widget.factory.FieldViewFactory;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ignacio Oviedo on 07/03/16.
 */
public abstract class FormRecordFragment extends LocationFragment implements LoaderManager.LoaderCallbacks<FormViewModel>,
        FieldViewActionListener, TimePickerDialogFragment.OnTimeChangeListener,
        DatePickerDialogFragment.OnDateChangeListener, ComboPickerDialog.OnComboPickerListener{

    static final int REQUEST_QR_SCANNER = 1000;
    static final int REQUEST_MAP = 2000;

    static final String ARG_RECORD_ID = "record_id";

    static final String MAP_TAG = "Map";
    static final String DATE_TAG = "Date";
    static final String TIME_TAG = "Time";
    static final String COMBO_TAG = "Combo";

    private Map<String, String> formValues;

    private FormViewModel record;

    private LinearLayout container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        formValues = new HashMap<>();

        getLoaderManager().initLoader(0, getArguments(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_record, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        container = (LinearLayout) view.findViewById(R.id.detail_container);
    }

    @Override
    public Loader<FormViewModel> onCreateLoader(int id, Bundle args) {
        return new RecordTaskLoaderTest(getActivity(), args.getLong(ARG_RECORD_ID));
    }

    @Override
    public void onLoadFinished(Loader<FormViewModel> loader, FormViewModel data) {

        if(data != null){
            record = data;
            fillForm(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<FormViewModel> loader) {

    }

    public abstract void fillForm(FormViewModel record);

    public void fillFields(List<FieldViewModel> fields){
        fillFields(fields, false);
    }

    public void fillFields(List<FieldViewModel> fields, boolean isEditMode){

        if(container.getChildCount() > 1)
            container.removeViews(1, fields.size() + 1);

        for (FieldViewModel field : fields){
            FieldViewFactory factory = field.getType().getFieldFactory();

            View view = factory.createView(container, field, isEditMode);

            if(view != null) {
                formValues.put(field.getTag(), field.getValue());
                view.setTag(field.getTag());

                FieldViewAdapter adapter = (FieldViewAdapter) view;
                adapter.setFieldViewActionListener(this);
                container.addView(view);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_QR_SCANNER){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data == null)
                    return;

                Barcode barcode = data.getParcelableExtra(BarcodeActivity.BarcodeObject);

                FieldViewAdapter view = (FieldViewAdapter) container.findViewWithTag(data.getStringExtra(ReadFieldActvivity.QR_FIELD_TAG));

                try {
                    view.setValue(barcode.displayValue);
                } catch (FormatException e) {
                    Toast.makeText(getActivity(), "Formato invalido", Toast.LENGTH_SHORT).show();
                }
            }
        }else if(requestCode == REQUEST_MAP){
            if(resultCode == Activity.RESULT_OK){

                LatLng location = data.getParcelableExtra(MapFragment.DATA_NEW_LOCATION);

                String columnId = data.getStringExtra(MapFragment.MAP_FIELD_TAG);

                FieldMapView view = (FieldMapView) container.findViewWithTag(columnId);
                view.setValue(FieldType.MAP.format(location));
            }
        }
    }

    @Override
    public void onHistoryClick(View view) {
        FieldHistoricalFragment fragment = FieldHistoricalFragment.newInstance(record.getId(),
                view.getTag().toString());

        showFragment("Detail", fragment, "Historical");
    }

    @Override
    public void onQrClick(View view) {

        Intent intent =  new Intent(getActivity(), ReadFieldActvivity.class);
        intent.putExtra(ReadFieldActvivity.QR_FIELD_TAG, view.getTag().toString());

        startActivityForResult(intent, REQUEST_QR_SCANNER);
    }

    @Override
    public void onMapClick(FieldMapView view) {

        LatLng location = (LatLng) FieldType.MAP.parse(view.getValue());

        MapFragment fragment = MapFragment.newInstance((String) view.getTag(), view.getLabel(),
                location);
        fragment.setTargetFragment(this, REQUEST_MAP);

        showFragment("Detail", fragment, MAP_TAG);
    }

    @Override
    public void onDateClick(FieldDateView view) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeHelper.parseDate(view.getValue()));

        DatePickerDialogFragment datePickerDialog = DatePickerDialogFragment.newInstance((String) view.getTag(), calendar);
        showDialog(DATE_TAG, datePickerDialog);
    }

    @Override
    public void onTimeClick(FieldTimeView view) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateTimeHelper.parseTime(view.getValue()));

        TimePickerDialogFragment timePickerDialog = TimePickerDialogFragment.newInstance((String) view.getTag(), calendar);
        showDialog(TIME_TAG, timePickerDialog);
    }

    @Override
    public void onPhoneClick(FieldPhoneView view) {
        Intent call = new Intent(Intent.ACTION_CALL,  Uri.parse("tel:" + view.getValue()));
        startActivity(call);
    }

    @Override
    public void onComboClick(FieldComboView view) {

        FieldViewModel field = record.getFieldForTag(view.getTag().toString());

        ComboPickerDialog dialog = ComboPickerDialog.newInstance(view.getTag().toString(), field.getRelationshipTable());
        showDialog(COMBO_TAG, dialog);
    }

    @Override
    public void onListClick(FieldListView view) {

        /*Column column = record.getFieldForColumn(view.getTag().toString()).getColumn();

        String value = record.getFieldForColumn(view.getValue()).getValue();

        DetailListFragment fragment = DetailListFragment.newInstance(view.getLabel(), column.getRelationship().getId(), "571", value);

        showFragment("Detail", fragment, "ListDetail");*/
    }

    @Override
    public void onPick(String fieldTag, CellViewModel record) {

        FieldComboView comboView = (FieldComboView) container.findViewWithTag(fieldTag);
        comboView.setValue(record.getValue());

        for (FieldViewModel field : record.getFields()){

            FieldViewAdapter adapter = (FieldViewAdapter) comboView.findViewWithTag(field.getTag());
            try {
                adapter.setValue(field.getValue());
            } catch (FormatException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTimeChange(String fieldTag, Calendar calendar) {
        FieldViewAdapter view = (FieldViewAdapter) container.findViewWithTag(fieldTag);

        try {
            view.setValue(DateTimeHelper.formatTime(calendar.getTime()));
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDateChange(String fieldTag, Calendar calendar){
        FieldViewAdapter view = (FieldViewAdapter) container.findViewWithTag(fieldTag);

        try {
            view.setValue(DateTimeHelper.formatDate(calendar.getTime()));
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    public FormViewModel getRecord(){
        return record;
    }

    public Map<String, String> getFormValues(){
        for (Map.Entry<String, String> pair : formValues.entrySet()) {
            FieldViewAdapter view = (FieldViewAdapter) container.findViewWithTag(pair.getKey());

            pair.setValue(view.getValue());
        }

        return formValues;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_form_send, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.send:
                onSaveButtonClick();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public abstract void onSaveButtonClick();

    public void save(SaveFormTask.FormData formData){

        new SaveFormTask(getActivity()).execute(formData);

        Toast.makeText(getContext(), "Enviando...", Toast.LENGTH_SHORT).show();

        getActivity().onBackPressed();
    }
}
