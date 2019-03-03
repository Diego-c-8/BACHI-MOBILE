package cl.uach.inf.bachimovil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;

public class PlanColors extends AppCompatActivity {

    private AutoCompleteTextView edit;
    private int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_colors);

        edit = findViewById(R.id.color_textview);
        String[] asignaturas = getResources().getStringArray(R.array.asignaturas);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, asignaturas);
        edit.setAdapter(adapter);

        ColorPickerView colorPickerView = findViewById(R.id.colorPickerView);
        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                AutoCompleteTextView colorTextView = findViewById(R.id.color_textview);
                color = colorEnvelope.getColor();
                colorTextView.setBackgroundColor(color);
            }
        });
    }

    public void saveColor(View view) {

        String name = edit.getText().toString();

        SharedPreferences.Editor editor = getSharedPreferences("pref_horario", MODE_PRIVATE).edit();
        editor.putInt(name, color);
        editor.commit();

        finish();
    }


}
