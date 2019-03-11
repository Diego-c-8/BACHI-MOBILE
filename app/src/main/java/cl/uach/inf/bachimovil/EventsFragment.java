package cl.uach.inf.bachimovil;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


public class EventsFragment extends Fragment implements AsyncResponse
{
    // Variables
    int state = 0; // 0 - Anuncios 1 - Eventos ( Para identificar en que pestaña se está )
    boolean leftMenuOn = false; // Si se está mostrando el menu lateral (tags)

    JSONArray tags = new JSONArray(); // Lista de tags
    JSONArray events = new JSONArray(); // Lista de eventos
    JSONArray ads = new JSONArray(); // Lista de anuncios

    CustomAdapter tagListAdapter;
    EventsAdapter eventListAdapter;
    AdsAdapter adsListAdapter;

    // UI Elements
    LinearLayout leftMenu; // Menu lateral (tags)
    TabLayout tabs;
    HorizontalScrollView hScroll; // Scroll horizontal usado para crear el menú lateral
    SwipeRefreshLayout tagRefresher,eventRefresher; // Para refrescar resultados deslizando hacia abajo
    View view; // Vista actual
    ListView tagList,eventList; // Listas visuales
    AppCompatImageButton postButton; // Botón para un nuevo post

    Intent intent; // Para cargar actividades
    Bundle bundle; // Para pasar datos entre actividades

    // Constructor
    public EventsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_eventos, container, false);
        tabs = view.findViewById(R.id.tabs);
        leftMenu = view.findViewById(R.id.LeftMenu);
        tagList = view.findViewById(R.id.tagList);
        eventList = view.findViewById(R.id.eventList);
        hScroll = view.findViewById(R.id.HScroll);
        tagRefresher = view.findViewById(R.id.tagListRefresher);
        eventRefresher = view.findViewById(R.id.eventListRefresher);

        tagListAdapter = new CustomAdapter();
        eventListAdapter = new EventsAdapter();
        adsListAdapter = new AdsAdapter();

        tagList.setAdapter(tagListAdapter);
        eventList.setAdapter(eventListAdapter);

        postButton = view.findViewById(R.id.PostButton);

        postButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

                  // Nuevo anuncio
                  if( state == 0 )
                      intent = new Intent(getActivity(), AddAdActivity.class );


                  // Nuevo evento
                  else
                      intent = new Intent(getActivity(), AddEventActivity.class );


                  startActivity(intent);
              }
          });

        tagRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                loadTags();
            }
        });

        eventRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                if(state == 0)
                    loadEvents();
                else
                    loadAds();
            }
        });

        // Desactiva el scroll horizontal
        hScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                state = tab.getPosition();
                eventRefresher.setRefreshing(true);
                if(state == 1)
                    loadEvents();
                else
                    loadAds();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // Seleciona el tab de eventos
        tabs.getTabAt(1).select();



        return view;

    }


    // Muestra / Esconde el menú lateral
    public void toggleLeftMenu(boolean state)
    {
        if(state) {
            animateMenu((int) ((float) getResources().getDisplayMetrics().widthPixels * 0.8f), 128);
            tagRefresher.setRefreshing(true);
            loadTags();
        }
        else
            animateMenu(0, 128);

        leftMenuOn = state;
    }

    // Animación del menú lateral
    public void animateMenu(int width, int duration)
    {
        ValueAnimator anim = ValueAnimator.ofInt(leftMenu.getMeasuredWidth(), width);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = leftMenu.getLayoutParams();
                layoutParams.width = val;
                leftMenu.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(duration);
        anim.start();
    }

    public void loadTags()
    {
        ServiceManager serviceManager = new ServiceManager(this.getActivity(),new AsyncResponse(){

            @Override
            public void obtainServiceResult(JSONObject jsonObject)
            {
                tagRefresher.setRefreshing(false);
                try
                {
                    tags = jsonObject.getJSONArray("tags");
                    tagListAdapter.notifyDataSetChanged();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        serviceManager.callService("http://puyuhuapi.com/json/tags.json");
    }

    public void loadEvents()
    {
        state = 1;
        eventList.setAdapter(eventListAdapter);
        ServiceManager serviceManager = new ServiceManager(this.getActivity(),new AsyncResponse(){

            @Override
            public void obtainServiceResult(JSONObject jsonObject)
            {
                eventRefresher.setRefreshing(false);
                try
                {
                    events = jsonObject.getJSONArray("events");
                    eventListAdapter.notifyDataSetChanged();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        serviceManager.callService("http://puyuhuapi.com/json/events.json");
    }

    public void loadAds()
    {
        state = 0;
        eventList.setAdapter(adsListAdapter);
        ServiceManager serviceManager = new ServiceManager(this.getActivity(),new AsyncResponse(){

            @Override
            public void obtainServiceResult(JSONObject jsonObject)
            {
                eventRefresher.setRefreshing(false);
                try
                {
                    ads = jsonObject.getJSONArray("ads");
                    adsListAdapter.notifyDataSetChanged();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        serviceManager.callService("http://puyuhuapi.com/json/ads.json");
    }

    // Eventos Click
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View v) {

        switch (v.getId())
        {
            // Left Menu Button
            case R.id.MenuButton:
                {
                    toggleLeftMenu(!leftMenuOn);
                }
                break;
            case R.id.PostButton:
            {
                //myIntent = new Intent(getActivity(), AddEventActivity.class );
                //startActivity(myIntent);
            }
        }

    }

    @Override
    public void obtainServiceResult(JSONObject jsonObject) {

    }


    class EventsAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return events.length();
        }

        @Override
        public Object getItem(int i) {
            try { return events.getJSONObject(i); }
            catch(Exception e) { e.printStackTrace(); }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.event_event_item,null);
            TextView title = (TextView)view.findViewById(R.id.EventTitle);
            TextView date = (TextView)view.findViewById(R.id.EventDate);
            TextView poster = (TextView)view.findViewById(R.id.EventPoster);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //para enviar  datos
                    bundle = new Bundle();

                    //Para la pantalla CursosActivity
                    intent = new Intent(getActivity(), DisplayEventActivity.class );

                    try {
                        //dato a enviar
                        intent.putExtra("info", events.getJSONObject(i).toString());
                    }
                    catch(Exception e) { e.printStackTrace(); }

                    //ir a pantalla CursosActivity
                    startActivity(intent);
                }
            });

            try
            {
                title.setTextColor(Color.parseColor(events.getJSONObject(i).getString("color")));
                title.setText(events.getJSONObject(i).getString("title"));
                date.setText(events.getJSONObject(i).getString("date"));
                poster.setText(events.getJSONObject(i).getString("poster"));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return view;
        }
    }

    class AdsAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return ads.length();
        }

        @Override
        public Object getItem(int i) {
            try { return ads.getJSONObject(i); }
            catch(Exception e) { e.printStackTrace(); }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.event_event_item,null);
            TextView title = (TextView)view.findViewById(R.id.EventTitle);
            TextView date = (TextView)view.findViewById(R.id.EventDate);
            TextView poster = (TextView)view.findViewById(R.id.EventPoster);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //para enviar  datos
                    bundle = new Bundle();

                    //Para la pantalla CursosActivity
                    intent = new Intent(getActivity(), DisplayEventActivity.class );

                    try {
                        //dato a enviar
                        intent.putExtra("info", ads.getJSONObject(i).toString());
                    }
                    catch(Exception e) { e.printStackTrace(); }

                    //ir a pantalla CursosActivity
                    startActivity(intent);
                }
            });

            try
            {
                title.setTextColor(Color.parseColor("#0071BD"));
                title.setText(ads.getJSONObject(i).getString("title"));
                date.setText(ads.getJSONObject(i).getString("date"));
                poster.setText(ads.getJSONObject(i).getString("poster"));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return view;
        }
    }

    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return tags.length();
        }

        @Override
        public Object getItem(int i) {
            try { return tags.getJSONObject(i); }
            catch(Exception e) { e.printStackTrace(); }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.event_hashtag_item,null);
            TextView text = (TextView)view.findViewById(R.id.tagName);
            CheckBox cb = (CheckBox)view.findViewById(R.id.followCheckbox);

            try
            {
                text.setText("#" + tags.getJSONObject(i).getString("tag"));
                cb.setChecked(tags.getJSONObject(i).getBoolean("following"));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return view;
        }
    }
}
