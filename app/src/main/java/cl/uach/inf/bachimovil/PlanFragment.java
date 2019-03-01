package cl.uach.inf.bachimovil;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
        return inflater.inflate(R.layout.fragment_plan, container, false);
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

    }


}
