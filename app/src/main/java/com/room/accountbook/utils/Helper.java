package com.room.accountbook.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by subash on 3/11/17.
 */

public class Helper {

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going
     *                to start.
     * @param msg     - Message String that represents alert box message.
     * @throws Exception
     */
    public static void confirmDialog(Context context, String msg, String positiveBtnText, String negativeBtnText, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (il != null)
                        il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (il != null)
                        il.onCancel();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (il != null)
                            il.onCancel();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create
            // alert
            // dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close from current screen.
     *
     * @param context - Object of Context, context from where the activity is going
     *                to start.
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void alert(Context context, String msg, boolean isCancelable, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setCancelable(isCancelable);
            alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    if (il != null) {
                        il.onSuccess();
                    }
                }
            });

            if (isCancelable) {
                alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                        }
                        return false;
                    }
                });
            }
            AlertDialog alertDialog = alertDialogBuilder.create(); // create
            // alert
            // dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void alert(Context context, String msg) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton(android.R.string.ok, null);
            AlertDialog alertDialog = alertDialogBuilder.create(); // create
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void ting(View parent, String msg) {
        if (parent != null)
            Snackbar.make(parent, msg, Snackbar.LENGTH_SHORT).show();
    }


    public interface IL {

        void onSuccess();

        void onCancel();
    }

    private static AlertDialog.Builder getBuilder(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder;
    }

    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
