package com.android.ocasa.widget.factory;

import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.ocasa.model.Field;
import com.android.ocasa.widget.FieldTextView;

/**
 * Created by ignacio on 04/02/16.
 */
public class IntegerFieldFactory extends TextFieldFactory {

    @Override
    public View createView(ViewGroup container, Field field) {

        FieldTextView textView = (FieldTextView) super.createView(container, field);

        EditText textField = textView.getTextField();
        textField.setInputType(InputType.TYPE_CLASS_NUMBER);

        return textView;
    }
}