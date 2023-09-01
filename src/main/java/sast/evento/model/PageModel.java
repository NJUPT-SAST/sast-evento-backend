package sast.evento.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @projectName: sast-evento-backend
 * @author: mio
 * @date: 2023/8/27 20:50
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageModel<T> {

    private Integer total;

    private List<T> result;

}
