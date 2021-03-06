package com.android.ocasa.widget;

import android.content.Context;
import android.nfc.FormatException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ocasa.R;
import com.android.ocasa.model.FieldType;
import com.android.ocasa.util.IconizedMenu;

/**
 * Created by ignacio on 04/02/16.
 */
public class FieldPhoneView extends LinearLayout implements FieldViewAdapter, IconizedMenu.OnMenuItemClickListener {

    private FieldViewActionListener listener;

    private TextView label;
    private InputFieldView field;

    public FieldPhoneView(Context context) {
        this(context, null);
    }

    public FieldPhoneView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FieldPhoneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init(){

        setOrientation(VERTICAL);

        LayoutInflater.from(getContext()).inflate(R.layout.field_phone_layout, this, true);

        label = (TextView) findViewById(R.id.label);
        field = (InputFieldView) findViewById(R.id.field);

        field.getAction().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });

        ImageView callButton = (ImageView) findViewById(R.id.call_button);
        callButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPhoneClick(FieldPhoneView.this);
            }
        });
    }

    @Override
    public void setFieldViewActionListener(FieldViewActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void setLabel(String label) {
        this.label.setText(label);
    }

    @Override
    public void setValue(String value) throws FormatException{

        if(!FieldType.PHONE.isValidValue(value))
            throw new FormatException();

        field.getInput().setText(value);
    }

    @Override
    public void changeLabelVisbility(boolean visibility) {

    }

    @Override
    public String getValue() {
        return field.getInput().getText().toString();
    }

    public InputFieldView getField() {
        return field;
    }

    private void showMenu(){
        IconizedMenu actionMenu = new IconizedMenu(getContext(), field.getAction());
        actionMenu.setOnMenuItemClickListener(this);
        MenuInflater inflater = actionMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_form_field, actionMenu.getMenu());
        actionMenu.show();
    }

    public FieldViewActionListener getListener() {
        return listener;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.history:
                listener.onHistoryClick(this);
                break;
            case R.id.qr_scanner:
                listener.onQrClick(this);
                break;
        }

        return true;
    }
}
