package sokolovska.sushchak.projektphonebook.fragmets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import sokolovska.sushchak.projektphonebook.R;


public class DataPickFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {


    /**
     * metoda do tworzenia dialogu dla wybierania daty
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH) ;
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * funkcja dla wpisania wybranej daty w odpowiednie miejsce
     * @param datePicker
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        EditText date = (EditText) getActivity().findViewById(R.id.et_happy_birthday);
        GregorianCalendar calendar = new GregorianCalendar(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(dateFormat.format(calendar.getTime()));
    }
}
