package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.entitiy.Slide;
import sast.evento.mapper.SlideMapper;
import sast.evento.service.SlideService;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: feelMoose
 * @date: 2023/8/7 23:10
 */
@Service
public class SlideServiceImpl implements SlideService {
    @Resource
    private SlideMapper slideMapper;

    @Override
    public List<Slide> getEventSlides(Integer eventId) {
        return slideMapper.selectList(new LambdaQueryWrapper<Slide>()
                .eq(Slide::getEventId,eventId));
    }

    @Override
    public Integer addEventSlide(Integer eventId, String url, String link, String title) {
        Slide slide = new Slide(null,title,link,url,eventId);
        slideMapper.insert(slide);
        return slide.getId();
    }

    @Override
    public void deleteEventSlide(Integer slideId) {
        slideMapper.deleteById(slideId);
    }

    @Override
    public void patchEventSlide(Integer eventId, Integer slideId, String url, String link, String title) {
        slideMapper.updateSlide(eventId,slideId,url,link,title);
    }

    @Override
    public List<Slide> getHomeSlides(Integer size) {
        return slideMapper.selectList(new LambdaQueryWrapper<Slide>()
                .eq(Slide::getEventId, 0)
                .orderByDesc(Slide::getId)
                .last("limit " + size));
    }

    @Override
    public Integer addHomeSlide(String url, String link, String title) {
        return addEventSlide(0, url, link, title);
    }

    @Override
    public void deleteHomeSlide(Integer slideId) {
        deleteEventSlide(slideId);
    }

    @Override
    public void patchHomeSlide(Integer slideId, String url, String link, String title) {
        patchEventSlide(0, slideId, url, link, title);
    }
}
