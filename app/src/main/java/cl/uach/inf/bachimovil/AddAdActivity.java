package cl.uach.inf.bachimovil;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.AbsListView;
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
import android.widget.Toolbar;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.List;

public class AddAdActivity extends AppCompatActivity
{
    // Entradas de texto
    EditText title, description, tags;

    // Botón para guardar
    Button save, file;

    // Botón para cancelar
    ImageButton cancelButton;

    // Alerta para descartar
    private CancelDialog salir;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

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
        setContentView(R.layout.activity_add_ad);

        // Crea y asigna el adapter a la lista de archivos adjuntos
        fileAdapter = new FileItem();
        fileList = findViewById(R.id.fileList);
        fileList.setAdapter(fileAdapter);

        // Obtiene el botón para añadir archivos
        file = findViewById(R.id.addFile);

        // Al seleccionar un archivo
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Filtros
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Abre el file manager disponible
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

        // Obtiene el botón para guardar
        save = findViewById(R.id.save);

        // Al hacer click en guardar
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*

                Aqui debe ir el codigo para enviar evento al sevidor

                 */

                // Termina la actividad
                finish();
            }
        });

    }

    // Cuando se selecciona un archivo en el file manager
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                // Obtiene el URL y nombre del archivo
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri.getScheme().equals("content")) {
                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                        try {
                            if (cursor != null && cursor.moveToFirst()) {
                                files.put(cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                            }
                        } finally {
                            cursor.close();
                        }
                    }

                    //files.put(uri.getPath());
                    fileAdapter.notifyDataSetChanged();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // Para crear lista de archivos a partir de un JSON
    class FileItem extends BaseAdapter
    {

        @Override
        public int getCount() {
            android.view.ViewGroup.LayoutParams pars = fileList.getLayoutParams();
            final float scale = fileList.getContext().getResources().getDisplayMetrics().density;
            int pixels = (int) ((files.length() * 60) * scale + 0.5f);
            pars.height = pixels;
            fileList.setLayoutParams(pars);
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
                title.setText(files.getString(i));
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        files.remove(i);
                        fileAdapter.notifyDataSetChanged();
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




