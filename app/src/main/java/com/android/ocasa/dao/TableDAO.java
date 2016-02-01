package com.android.ocasa.dao;

import android.content.Context;

import com.android.ocasa.model.Record;
import com.android.ocasa.model.Table;

import java.util.List;

/**
 * Created by ignacio on 18/01/16.
 */
public class TableDAO extends GenericDAOImpl<Table,String> {

    public TableDAO(Context context) {
        super(Table.class, context);
    }
}
