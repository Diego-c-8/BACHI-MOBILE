package cl.uach.inf.bachimovil;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends Fragment {


    private View view;

    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_plan, container, false);

        SharedPreferences.Editor editor = getContext().getSharedPreferences("pref_horario", MODE_PRIVATE).edit();
        editor.commit();
        defaultColors();

        return view;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.horario_button) {
            Intent intent = new Intent(getActivity(), Horario.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.calendario_button) {
            Intent intent = new Intent(getActivity(), Calendario.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.colors_button) {
            Intent intent = new Intent(getActivity(), PlanColors.class);
            startActivity(intent);
        }

    }

    public void defaultColors() {
        Context context = getContext();
        SharedPreferences pref2 = context.getSharedPreferences("pref_horario", MODE_PRIVATE);

        if (!pref2.contains("Algebra")) {
            SharedPreferences.Editor editor = context.getSharedPreferences("pref_horario", MODE_PRIVATE).edit();
            editor.putInt("Algebra", 0xFF00FF00);
            editor.putInt("Fisica", 0xFF0000FF);
            editor.putInt("Lenguaje", 0xFFFF0000);
            editor.commit();
        }
    }

}
