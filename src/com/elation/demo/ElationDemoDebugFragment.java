package com.elation.demo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.GestureDetector;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TabWidget;
import android.widget.TabHost;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentActivity;

import com.elation.demo.ElationDemoDebugConsoleFragment;

public class ElationDemoDebugFragment extends Fragment implements OnTouchListener {
  private RelativeLayout debugPanel;
  private RelativeLayout debugPanelHeader;
  private TabWidget debugTabs;
  private FragmentTabHost debugTabHost;
  private float xpos, ypos;
  private float lastx, lasty;
  private int height;
  private long lastTouchTime;
  private boolean hidden;
  private int hiddenheight;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_debug, container, false);

    debugPanel = (RelativeLayout) view.findViewById(R.id.debug_panel);
    debugPanelHeader = (RelativeLayout) view.findViewById(R.id.debug_panel_header);
    //debugTabs = (TabWidget) view.findViewById(R.id.debug_tabs);

    lastTouchTime = 0;
    lastx = lasty = 0;
    hidden = false;

    view.setOnTouchListener(this);

    debugTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
    FragmentActivity fragact = (FragmentActivity) getActivity();
    debugTabHost.setup(fragact, fragact.getSupportFragmentManager(), R.id.debug_tab_content);


    TabHost.TabSpec spec;
    spec = debugTabHost.newTabSpec("Console").setIndicator("Console");
    //spec.setContent("kewl");
    debugTabHost.addTab(spec, ElationDemoDebugConsoleFragment.class, null);

    spec = debugTabHost.newTabSpec("Settings").setIndicator("Settings");
    debugTabHost.addTab(spec, ElationDemoDebugPreferenceFragment.class, null);


    return view;
  }
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    int action = event.getAction();
    boolean eventHandled = false;

    long now = event.getEventTime();

    if (action == MotionEvent.ACTION_DOWN) {
      xpos = event.getRawY();
      ypos = event.getRawY();
      height = debugPanel.getLayoutParams().height;
      eventHandled = true;
      if (now - lastTouchTime < 250) {
        toggleVisible();
      }
    } else if (action == MotionEvent.ACTION_MOVE) {
      float newypos = event.getRawY();
      float ydiff = ypos - newypos;
      int newheight = (int) (height + ydiff);
      int headerheight = debugPanelHeader.getHeight();
      if (newheight < headerheight) {
        newheight = headerheight;
      }
      hidden = false;
      debugPanel.getLayoutParams().height = newheight;

      //debugPanel.invalidate();
      // FIXME - this probably isn't the best way to trigger a redraw
      debugPanel.requestLayout();

      eventHandled = true;
    }
    lastx = xpos;
    lasty = ypos;
    lastTouchTime = now;

    return eventHandled;
  }
  public void toggleVisible() {
    if (hidden) {
      debugPanel.getLayoutParams().height = hiddenheight;
    } else {
      hiddenheight = height;
      height = debugPanelHeader.getHeight();
      debugPanel.getLayoutParams().height = height;
    }
    debugPanel.requestLayout();
    hidden = !hidden;
  }


};
