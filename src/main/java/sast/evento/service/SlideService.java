package sast.evento.service;

import sast.evento.entitiy.Slide;
import sast.evento.model.SlidePageModel;

import java.util.List;

public interface SlideService {
    /* event */
    List<Slide> getEventSlides(Integer eventId);

    Integer addEventSlide(Integer eventId, String url, String link, String title);

    void deleteEventSlide(Integer slideId, Integer eventId);

    void patchEventSlide(Integer eventId, Integer slideId, String url, String link, String title);

    /* home */
    SlidePageModel getHomeSlides(Integer current, Integer size);

    Integer addHomeSlide(String url, String link, String title);

    void deleteHomeSlide(Integer slideId);

    void patchHomeSlide(Integer slideId, String url, String link, String title);

}
