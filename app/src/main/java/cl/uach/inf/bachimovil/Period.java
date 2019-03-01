package cl.uach.inf.bachimovil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class Period extends android.support.v7.widget.AppCompatButton {
    private int id;
    private Context context;
    public Period(Context context, int id) {
        super(context);
        this.context = context;
        this.id = id;
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        setLayoutParams(new TableRow.LayoutParams(width, height));
        setId(id);
        setAllCaps(false);
        setBackgroundColor(0xFFFFFFFF);
        //setBackgroundResource(R.drawable.border);

    }

    public void setPeriodText() {
            SharedPreferences pref = context.getSharedPreferences("pref_horario", MODE_PRIVATE);
            String name = pref.getString("period_name" + id, " ");
            String place = pref.getString("period_place" + id, " ");
            Spannable span = new SpannableString(name + "\n" + place);
            span.setSpan(new RelativeSizeSpan(0.8f), name.length(), name.length() + place.length() + 1, 0);
            setText(span);
            int color;
            switch (name) {
                case "Algebra": color = 0xFF00FF00; break;
                case "Lenguaje": color = 0xFFFF0000; break;
                default: color = 0xFFFFFFFF; break;
            }
            setBackgroundColor(color);

    }
}
