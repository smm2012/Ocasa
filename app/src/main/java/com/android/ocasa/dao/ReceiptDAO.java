package com.android.ocasa.dao;

import android.content.Context;

import com.android.ocasa.model.Receipt;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by Ignacio Oviedo on 28/03/16.
 */
public class ReceiptDAO extends GenericDAOImpl<Receipt,Long> {

    public ReceiptDAO(Context context) {
        super(Receipt.class, context);
    }

    public List<Receipt> findForAction(String actionId){
        try {
            return dao.queryBuilder()
                    .orderBy("createdAt", false)
                    .where()
                    .eq("action_id", actionId)
                    .and()
                    .eq("isConfirmed", true)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
