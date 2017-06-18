package sokolovska.sushchak.projektphonebook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Communication {

    private Context context;

    public Communication(Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context cannot be null");

        this.context = context;
    }

    /**
     * funkcja otwarcia aplikacji do napisania SMS
     * @param number
     */
    public void sendSMS(String number) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
        context.startActivity(intent);
    }

    public void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: " + email));
        context.startActivity(emailIntent);
    }

    /**
     * funkcja do wysylania pełnej informacji kontaktu
     * na adres mailowy
     * @param email adres mailowy
     * @param body
     */
    public void sendEmail(String email, String body){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: " + email));
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(emailIntent);
    }

    /**
     * funkcja dla wykonania połączenie
     * @param number numer komórkowy
     */
    public void call(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else context.startActivity(intent);

    }

    /**
     * metoda do wyszukiwania adresu na mapię
     * @param address adres do wyszukiwania
     */
    public void viewAddressOnMap(String address){
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    /**
     * metoda która dodaje do kolendarzu
     * przypominianie urodzin kontaktu
     * @param dateString data urodzin
     * @param name imię oraz nazwisko kontaktu
     */
    public void addDateIntoCalendar(String dateString, String name) {
        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Calendar calendar = Calendar.getInstance();
            Date date = formatter.parse(dateString);
            calendar.setTime(date);

            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", calendar.getTimeInMillis());
            intent.putExtra("allDay", true);
            intent.putExtra("title", context.getString(R.string.title_add_into_calendar) + " " + name);
            context.startActivity(intent);
        } catch (ParseException e) {
            Toast.makeText(context, context.getString(R.string.exception_add_into_calendar),Toast.LENGTH_SHORT).show();
        }




    }
}
