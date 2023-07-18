package main.server.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class EWMPageRequest extends PageRequest {
    public EWMPageRequest(int from, int size) {
        super(from > 0 ? from / size : 0, size, Sort.unsorted());
    }
}
