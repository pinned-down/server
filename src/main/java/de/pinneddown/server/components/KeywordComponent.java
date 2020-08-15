package de.pinneddown.server.components;

import de.pinneddown.server.EntityComponent;

import java.util.ArrayList;

public class KeywordComponent implements EntityComponent {
    private ArrayList<String> keywords;

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }
}
