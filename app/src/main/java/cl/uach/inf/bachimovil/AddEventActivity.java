package cl.uach.inf.bachimovil;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.Parcel;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.List;

public class AddEventActivity extends AppCompatActivity
{
    // Entradas de texto
    EditText title, description, tags, date, time;

    // Botón para guardar
    Button save, file;

    // Botón para cancelar
    ImageButton cancelButton;

    // Alerta para descartar
    private CancelDialog salir;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int año = c.get(Calendar.YEAR);

    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    // Almacena la lista de archivos
    JSONArray files = new JSONArray();

    // Lista visual de archivos
    ListView fileList;
    FileItem fileAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Configuracion inicial de la actividad
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_event);

        // Obtiene el input para la fecha
        date = findViewById(R.id.date);

        // Desactiva el teclado
        date.setShowSoftInputOnFocus(false);
        date.setInputType(0);

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(!b) return;

                // Esconde el teclado
                InputMethodManager imm = (InputMethodManager) getSystemService(AddEventActivity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                // Crea el seleccionador de fechas
                DatePickerDialog recogerFecha = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final int mesActual = month + 1;
                        String diaFormateado = (dayOfMonth < 10)? "0" + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                        String mesFormateado = (mesActual < 10)? "0" + String.valueOf(mesActual):String.valueOf(mesActual);
                        date.setText(diaFormateado + "/" + mesFormateado + "/" + year);
                    }
                },año, mes, dia);

                // Lo muestra
                recogerFecha.show();
            }
        });

        // Obtiene el input para la hora
        time = findViewById(R.id.time);

        // Desactiva el teclado
        time.setShowSoftInputOnFocus(false);
        time.setInputType(0);


        time.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                TimePickerDialog recogerHora = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String horaFormateada =  (hourOfDay < 10)? String.valueOf("0" + hourOfDay) : String.valueOf(hourOfDay);
                        String minutoFormateado = (minute < 10)? String.valueOf("0" + minute):String.valueOf(minute);
                        String AM_PM;
                        if(hourOfDay < 12) {
                            AM_PM = "a.m.";
                        } else {
                            AM_PM = "p.m.";
                        }
                        time.setText(horaFormateada + ":" + minutoFormateado + " " + AM_PM);
                    }
                }, hora, minuto, false);

                recogerHora.show();
            }
        });

        fileAdapter = new FileItem();
        fileList = findViewById(R.id.fileList);
        fileList.setAdapter(fileAdapter);

        file = findViewById(R.id.addFile);
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);


                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 100);

                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(view.getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Obtiene el botón para cancelar
        cancelButton = findViewById(R.id.cancelButton);

        // Al hacer click en el botón de cancelar
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Crea la alerta
                salir = new CancelDialog();

                // Muestra la alerta
                salir.show(getSupportFragmentManager(), "cancelar");
            }
        });

    }

    public String getFileName(String result) {
        if (result == null) {
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    files.put(uri.toString());
                    fileAdapter.notifyDataSetChanged();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void onClick(View v)
    {
        //if (v.getId() == R.id.okButton)
    }



    class FileItem extends BaseAdapter
    {

        @Override
        public int getCount() {
            return files.length();
        }

        @Override
        public Object getItem(int i) {
            try { return files.getJSONObject(i); }
            catch(Exception e) { e.printStackTrace(); }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.event_file_item,null);
            TextView title = (TextView)view.findViewById(R.id.name);
            ImageButton deleteBtn = (ImageButton)view.findViewById(R.id.delete);

            try
            {
                title.setText(getFileName(files.getString(i)));
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        files.remove(i);
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                });
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return view;
        }
    }
}




