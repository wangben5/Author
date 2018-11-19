package com.shadt.ui.db;

import java.util.List;

public class WenjianjiaInfo {

    private List<String> COLUMNS;
    private List<List<String>> DATA;

    public List<String> getCOLUMNS() {
        return COLUMNS;
    }

    public void setCOLUMNS(List<String> COLUMNS) {
        this.COLUMNS = COLUMNS;
    }

    public List<List<String>> getDATA() {
        return DATA;
    }

    public void setDATA(List<List<String>> DATA) {
        this.DATA = DATA;
    }
}
