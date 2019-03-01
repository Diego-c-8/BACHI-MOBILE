package cl.uach.inf.bachimovil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ModPeriod extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_period);

    }

    public void savePeriod(View view) {
        EditText edit1 = (EditText) findViewById(R.id.editText_name);
        EditText edit2 = (EditText) findViewById(R.id.editText_place);

        Intent intent = getIntent();
        int id = intent.getIntExtra("period", -1);
        String name = edit1.getText().toString();
        String place = edit2.getText().toString();

        SharedPreferences.Editor editor = getSharedPreferences("pref_horario", MODE_PRIVATE).edit();
        editor.putString("period_name" + id, name);
        editor.putString("period_place" + id, place);
        editor.commit();

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
