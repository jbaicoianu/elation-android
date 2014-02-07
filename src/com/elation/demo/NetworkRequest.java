package com.elation.demo;
import android.net.Uri;
import android.os.SystemClock;

public class NetworkRequest {
  public String url;
  public boolean finished = false;
  public long timeStart = 0;
  public long timeEnd = 0;

  public NetworkRequest(String url) {
    this.url = url;
    this.timeStart = System.currentTimeMillis();
  }
  public void setFinished() {
    this.finished = true;
    this.timeEnd = System.currentTimeMillis();
  }
  public long getLoadTime() {
    if (timeStart > 0 && timeEnd > 0) {
      return timeEnd - timeStart;
    }
    return -1;
  }
}



