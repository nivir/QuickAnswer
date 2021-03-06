package fr.castorflex.android.quickanswer.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Handler;
import fr.castorflex.android.quickanswer.pojos.Message;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: castorflex
 * Date: 18/09/12
 * Time: 11:16
 * To change this template use File | Settings | File Templates.
 */
public class SmsProvider {

    public static final Uri SMS_CONTENT_URI = Uri.parse("content://sms/");

    public static void setSmsAsRead(final Context context, final List<Message> messages) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Message msg : messages) {
                    if (msg.getType() == Message.TYPE_SMS)
                        setSmsAsRead(context, msg);
                }
            }
        }, 4000);

    }

    public static void setSmsAsRead(Context context, Message message) {

        if (message.getType() == Message.TYPE_SMS) {

            long id = findSmsId(context, message);

            ContentValues values = new ContentValues();
            values.put("read", true);
            context.getContentResolver()
                    .update(SMS_CONTENT_URI, values, "_id=" + id, null);
        }
    }

    public static long findSmsId(Context context, Message message) {
        long id = -1;

        String selection = "body=" + DatabaseUtils.sqlEscapeString(message.getMessage())
                + " and read=0";
        String[] projection = new String[]{"_id", "date", "thread_id", "body"};
        String order = "date DESC";

        Cursor cursor = context.getContentResolver().query(
                Uri.withAppendedPath(SMS_CONTENT_URI, "inbox"),
                projection,
                selection,
                null,
                order);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                id = cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return id;
    }
}
