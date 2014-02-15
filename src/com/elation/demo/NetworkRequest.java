package com.elation.demo;
import static android.os.SystemClock.elapsedRealtime;

public class NetworkRequest {
  public String url;
  public boolean finished = false;
  public long timeStart = 0;
  public long timeEnd = 0;

  public NetworkRequest(String url) {
    this.url = url;
    this.timeStart = elapsedRealtime();
  }
  public void setFinished() {
    this.finished = true;
    this.timeEnd = elapsedRealtime();
  }
  public long getLoadTime() {
    if (timeStart > 0 && timeEnd > 0) {
      return timeEnd - timeStart;
    }
    return -1;
  }
}



