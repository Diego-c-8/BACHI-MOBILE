package cl.uach.inf.bachimovil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static java.io.File.separator;

public class Horario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

        SharedPreferences.Editor editor = getSharedPreferences("pref_horario", MODE_PRIVATE).edit();
        editor.commit();

        Period periods[][] = new Period[7][5];

        TableLayout table = (TableLayout) findViewById(R.id.table);
        for (int i = 0; i < 7; ++i) {
            TableRow row = new TableRow(this);
            row.setDividerDrawable(getResources().getDrawable(R.drawable.separator));
            row.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            table.addView(row);
            for (int j = 0; j < 5; ++j) {
                int id = 5 * i + j;
                periods[i][j] = new Period(this, id);
                periods[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        Intent intent = new Intent(Horario.this, ModPeriod.class);
                        intent.putExtra("period", id);
                        //startActivity(intent);
                        startActivityForResult(intent, 1);
                    }
                });
                row.addView(periods[i][j]);
                periods[i][j].setPeriodText();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Period period = (Period) findViewById(intent.getIntExtra("period", -1));
                period.setPeriodText();
            }
        }
    }
}
