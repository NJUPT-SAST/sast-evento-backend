package sast.evento.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import sast.evento.entitiy.Slide;
import sast.evento.mapper.SlideMapper;
import sast.evento.model.SlidePageModel;
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
                .eq(Slide::getEventId, eventId));
    }

    @Override
    public Integer addEventSlide(Integer eventId, String url, String link, String title) {
        Slide slide = new Slide(null, title, link, url, eventId);
        slideMapper.insert(slide);
        return slide.getId();
    }

    @Override
    public void deleteEventSlide(Integer slideId, Integer eventId) {
        slideMapper.delete(new LambdaQueryWrapper<Slide>()
                .eq(Slide::getEventId, eventId)
                .and(wrapper -> wrapper.eq(Slide::getId, slideId)));
    }

    @Override
    public void patchEventSlide(Integer eventId, Integer slideId, String url, String link, String title) {
        slideMapper.updateSlide(eventId, slideId, url, link, title);
    }

    @Override
    public SlidePageModel getHomeSlides(Integer current ,Integer size) {
        Page<Slide> slidePage = slideMapper.selectPage(new Page<>(current,size), new LambdaQueryWrapper<Slide>()
                .eq(Slide::getEventId,0)
                .orderByDesc(Slide::getId));
        slidePage.getRecords().forEach(slide -> slide.setEventId(null));
        return new SlidePageModel(slidePage.getRecords(),(int) slidePage.getTotal());
    }

    @Override
    public Integer addHomeSlide(String url, String link, String title) {
        return addEventSlide(0, url, link, title);
    }

    @Override
    public void deleteHomeSlide(Integer slideId) {
        deleteEventSlide(slideId, 0);
    }

    @Override
    public void patchHomeSlide(Integer slideId, String url, String link, String title) {
        patchEventSlide(0, slideId, url, link, title);
    }
}
