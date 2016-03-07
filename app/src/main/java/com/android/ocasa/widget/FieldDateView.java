package com.android.ocasa.widget;

import android.content.Context;
import android.nfc.FormatException;
import android.util.AttributeSet;
import android.view.View;

import com.android.ocasa.model.FieldType;

/**
 * Created by ignacio on 01/02/16.
 */
public class FieldDateView extends FormTextFieldView {

    public FieldDateView(Context context) {
        this(context, null);
    }

    public FieldDateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FieldDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getField().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getListener().onDateClick(FieldDateView.this);
            }
        });
    }

    @Override
    public void setValue(String value) throws FormatException {
        if(!FieldType.DATE.isValidValue(value))
            throw new FormatException();

        super.setValue(value);
    }
}
