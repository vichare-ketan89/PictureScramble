package com.example.keto.picturescramble.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by root on 22/4/17.
 */

public class FlickrModel implements Serializable {

    private String title;
    private String modified;
    private String link;
    private String description;
    private String generator;
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public static class Item implements Serializable{
        private String title;
        private String link;
        private Media media;

        public Media getMedia() {
            return media;
        }

        public void setMedia(Media media) {
            this.media = media;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public class Media implements Serializable{
            private String m;

            public String getM() {
                return m;
            }

            public void setM(String m) {
                this.m = m;
            }
        }
    }
}
