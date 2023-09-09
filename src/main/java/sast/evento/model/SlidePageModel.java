package sast.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sast.evento.entitiy.Slide;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlidePageModel {

    private List<Slide> slides;

    private Integer total;

}
