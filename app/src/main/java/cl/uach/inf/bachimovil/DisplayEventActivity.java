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
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class DisplayEventActivity extends AppCompatActivity
{
    // Texto
    TextView title, date, poster, tags, description;

    // Almacena la lista de archivos
    JSONObject info;

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
        setContentView(R.layout.activity_display_event);

        // Crea y asigna el adapter a la lista de archivos adjuntos
        fileAdapter = new FileItem();
        fileList = findViewById(R.id.fileList);
        fileList.setAdapter(fileAdapter);

        // Obtiene los elementos
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        poster = findViewById(R.id.poster);
        tags = findViewById(R.id.tags);
        description = findViewById(R.id.desc);


        Bundle bundle = getIntent().getExtras();
        try
        {
            info = new JSONObject(getIntent().getStringExtra("info"));
            title.setText(info.getString("title"));
            date.setText(info.getString("date"));
            poster.setText(info.getString("poster"));
            description.setText(info.getString("desc"));

            String tgs = "";
            for(int i = 0; i < info.getJSONArray("tags").length(); i++)
            {
                tgs+= "#" + info.getJSONArray("tags").getString(i) + " ";
            }
            tags.setText(tgs);
            fileAdapter.notifyDataSetChanged();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }





    }

    // Para crear lista de archivos a partir de un JSON
    class FileItem extends BaseAdapter
    {

        @Override
        public int getCount() {
            android.view.ViewGroup.LayoutParams pars = fileList.getLayoutParams();
            final float scale = fileList.getContext().getResources().getDisplayMetrics().density;
            int len = 0;
            try{
                len = info.getJSONArray("files").length();
                int pixels = (int) ((len * 60) * scale + 0.5f);
                pars.height = pixels;
                fileList.setLayoutParams(pars);

            }
            catch(Exception e) { e.printStackTrace(); }
            return len;
        }

        @Override
        public Object getItem(int i) {
            try { return info.getJSONArray("files").getJSONObject(i); }
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
                title.setText(info.getJSONArray("files").getJSONObject(i).getString("name"));
                deleteBtn.setVisibility(View.INVISIBLE);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getJSONArray("files").getJSONObject(i).getString("url")));
                            startActivity(browserIntent);
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
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




