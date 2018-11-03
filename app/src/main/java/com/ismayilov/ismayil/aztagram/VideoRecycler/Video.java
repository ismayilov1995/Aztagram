package com.ismayilov.ismayil.aztagram.VideoRecycler;


public class Video{
    private String urlVideo;
    private int seekTo;

    public Video(String urlVideo, int seekTo) {
        this.urlVideo = urlVideo;
        this.seekTo = seekTo;
    }

    public Video(String urlPhoto, String urlVideo, int seekTo) {
        this.urlVideo = urlVideo;
        this.seekTo = seekTo;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }


    public int getSeekTo() {
        return seekTo;
    }

    public void setSeekTo(int seekTo) {
        this.seekTo = seekTo;
    }
}
