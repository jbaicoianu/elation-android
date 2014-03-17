package com.elation.demo;

import android.net.Uri;

public class ElationEvent {
    public String type;
    public String target;
    public String dataRaw;

    public ElationEvent(String raw) {
        type = raw;
    }

    public ElationEvent(String evType, String evData) {
        type = evType;
        dataRaw = evData;
    }

    public ElationEvent(Uri evUri) {
        String contents = evUri.getSchemeSpecificPart();
        type = "unknown";
        dataRaw = "";

        if (contents.substring(0, 2).equals("//")) {
            // properly-formed URL, <prefix>://<event.type>/<event.data>
            // Use Uri's built-in parsing
            this.setType(evUri.getHost());
            this.setTarget(evUri.getPath());
            this.setData(evUri.getQuery());
        } else {
            // improperly-formed URL, <prefix>:<event.type>/<event.data>
            // Perform basic string parsing
            int pos = contents.indexOf('/');
            if (pos == -1) {
                this.setType(contents);
            } else {
                this.setType(contents.substring(0, pos));
                this.setData(contents.substring(pos + 1));
            }
        }
    }

    public void setType(String ttype) {
        type = ttype;
    }

    public void setTarget(String tid) {
        if (tid.charAt(0) == '/') {
            target = tid.substring(1);
        } else {
            target = tid;
        }
    }

    public void setData(String tdata) {
        if (tdata != null) {
            if (tdata.length() > 0 && tdata.charAt(0) == '/') {
                // Strip leading / if present
                dataRaw = tdata.substring(1);
            } else {
                dataRaw = tdata;
            }
        }
    }

    public boolean hasData() {
        if (dataRaw == null || dataRaw.length() < 1)
            return false;
        else return true;
    }
}


