package cl.uach.inf.bachimovil;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
    int state = 0; // 0 - Anuncios 1 - Eventos
    boolean leftMenuOn = false;
    JSONArray tags = new JSONArray();
    JSONArray events = new JSONArray();
    CustomAdapter tagListAdapter;
    EventsAdapter eventListAdapter;

    // UI Elements
    LinearLayout leftMenu;
    HorizontalScrollView hScroll;
    SwipeRefreshLayout tagRefresher,eventRefresher;
    View view;
    ListView tagList,eventList;
    Button postButton;

    Intent myIntent;

    // Constructor
    public EventsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_eventos, container, false);
        leftMenu = view.findViewById(R.id.LeftMenu);
        tagList = view.findViewById(R.id.tagList);
        eventList = view.findViewById(R.id.eventList);
        hScroll = view.findViewById(R.id.HScroll);
        tagRefresher = view.findViewById(R.id.tagListRefresher);
        eventRefresher = view.findViewById(R.id.eventListRefresher);

        tagListAdapter = new CustomAdapter();
        eventListAdapter = new EventsAdapter();

        tagList.setAdapter(tagListAdapter);
        eventList.setAdapter(eventListAdapter);

        postButton = view.findViewById(R.id.PostButton);

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
                loadEvents();
            }
        });

        // Desactiva el scroll horizontal
        hScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

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
                myIntent = new Intent(getActivity(), AddEventActivity.class );
                startActivity(myIntent);
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
