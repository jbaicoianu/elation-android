package com.elation.demo;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnAttachStateChangeListener;
import android.view.GestureDetector;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TabWidget;
import android.widget.TabHost;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ElationDebugFragment extends Fragment implements OnTouchListener {
  private RelativeLayout debugPanel;
  private ViewGroup.LayoutParams debugPanelParams;
  private RelativeLayout debugPanelHeader;
  private TabWidget debugTabs;
  private FragmentTabHost debugTabHost;
  private float xpos, ypos;
  private float lastx, lasty;
  private int height;
  private long lastTouchTime;
  private boolean hidden;
  private int hiddenheight;
  private Handler updateSettingsHandler;
  private Runnable updateSettingsRunnable;
  private boolean updateSettingsPending = false;
  private long updateSettingsDelay = 500;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.debug_fragment, container, false);

    debugPanel = (RelativeLayout) view.findViewById(R.id.debug_panel);
    debugPanelHeader = (RelativeLayout) view.findViewById(R.id.debug_panel_header);
    //debugTabs = (TabWidget) view.findViewById(R.id.debug_tabs);

    lastTouchTime = 0;
    lastx = lasty = 0;
    hidden = false;

    view.setOnTouchListener(this);

    debugTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);

    // Added an AttachStateChangeListener to prevent the TabHost from stealing focus from keyboards
    final ElationDebugFragment self = this;
    debugTabHost.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
        @Override
        public void onViewDetachedFromWindow(View v) {}
        @Override
        public void onViewAttachedToWindow(View v) {
            debugTabHost.getViewTreeObserver().removeOnTouchModeChangeListener(debugTabHost);
        }
    });
    updateSettingsRunnable = new Runnable() {
      public void run() {
        self.saveHeight();
        self.updateSettingsPending = false;
      }
    };

    FragmentActivity fragact = (FragmentActivity) getActivity();
    debugTabHost.setup(fragact, fragact.getSupportFragmentManager(), android.R.id.tabcontent);
    debugTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

    updateSettingsHandler = view.getHandler();

    TabHost.TabSpec spec;
    spec = debugTabHost.newTabSpec("Events").setIndicator(createTabView(getActivity(), "Events"));
    debugTabHost.addTab(spec, ElationDebugEventsFragment.class, null);

    spec = debugTabHost.newTabSpec("Console").setIndicator(createTabView(getActivity(), "Console"));
    debugTabHost.addTab(spec, ElationDebugConsoleFragment.class, null);

    spec = debugTabHost.newTabSpec("Network").setIndicator(createTabView(getActivity(), "Network"));
    debugTabHost.addTab(spec, ElationDebugNetworkFragment.class, null);

    spec = debugTabHost.newTabSpec("Settings").setIndicator(createTabView(getActivity(), "Settings"));
    debugTabHost.addTab(spec, ElationDebugPreferenceFragment.class, null);

    return view;
  }
  @Override
  public void onResume () {
    super.onResume();

    // set initial height
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    int lastheight = prefs.getInt("debug_panel_height", 200);
    this.setHeight(lastheight);
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
      hidden = false;
      this.setHeight(newheight);

      // FIXME - this probably isn't the best way to trigger a redraw
      debugPanel.requestLayout();

      eventHandled = true;
    }
    lastx = xpos;
    lasty = ypos;
    lastTouchTime = now;

    return eventHandled;
  }
  public void maximize() {
      this.setHeight(100);
  }
  public void toggleVisible() {
    if (hidden) {
      this.setHeight(hiddenheight);
    } else {
      hiddenheight = height;
      height = debugPanelHeader.getHeight();
      this.setHeight(height);
    }
    debugPanel.requestLayout();
    hidden = !hidden;
  }
  public void setHeight(int height) {
    if (debugPanelParams == null) {
      debugPanelParams = debugPanel.getLayoutParams();
    }
    if (debugPanelParams != null) {
      int headerheight = debugPanelHeader.getHeight();
      if (height < headerheight) {
        height = headerheight;
      }
      debugPanelParams.height = height;

      // If the Handler is null, get it from the view
      if (this.updateSettingsHandler == null) {
        this.updateSettingsHandler = getView().getHandler();
      }
      if (this.updateSettingsHandler != null && this.updateSettingsRunnable != null) {
        // If an update is pending, clear the previous callbacks and set a new one
        if (this.updateSettingsPending) {
          this.updateSettingsHandler.removeCallbacks(this.updateSettingsRunnable);
        }
        this.updateSettingsHandler.postDelayed(this.updateSettingsRunnable, this.updateSettingsDelay);
        this.updateSettingsPending = true;
      }
    }
  }
  public void saveHeight() {
    int height = debugPanelParams.height;
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    prefs.edit().putInt("debug_panel_height", height).apply();
  }

  private View createTabView(final Context context, final String text) {
    View view = LayoutInflater.from(context).inflate(R.layout.debug_tabs, null);
    TextView tv = (TextView) view.findViewById(R.id.tabsText);
    tv.setText(text);
    return view;
  }

};
