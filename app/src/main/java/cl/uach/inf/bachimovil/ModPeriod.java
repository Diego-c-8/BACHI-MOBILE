package cl.uach.inf.bachimovil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class ModPeriod extends AppCompatActivity {
    private int id;
    private AutoCompleteTextView edit1;
    private EditText edit2;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_period);

        edit1 = (AutoCompleteTextView) findViewById(R.id.editText_name);
        edit2 = (EditText) findViewById(R.id.editText_place);

        intent = getIntent();
        id = intent.getIntExtra("period", -1);

        SharedPreferences pref = getSharedPreferences("pref_horario", MODE_PRIVATE);
        String name = pref.getString("period_name" + id, " ");
        String place = pref.getString("period_place" + id, " ");

        if (name != " ") edit1.setText(name);
        if (place != " ") edit2.setText(place);


        String[] asignaturas = getResources().getStringArray(R.array.asignaturas);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, asignaturas);
        edit1.setAdapter(adapter);
    }

    public void savePeriod(View view) {

        String name = edit1.getText().toString();
        String place = edit2.getText().toString();

        SharedPreferences.Editor editor = getSharedPreferences("pref_horario", MODE_PRIVATE).edit();
        editor.putString("period_name" + id, (name.length() != 0 ? name : " "));
        editor.putString("period_place" + id, (place.length() != 0 ? place : " "));
        editor.commit();

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
