package com.grepp.spring.infra.response;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@RequiredArgsConstructor
public class PageResponse<T> {
    
    private final String url;
    private final Page<T> page;
    private final int pageButtonCnt;
    
    public String url(){
        return url;
    }
    
    public int currentNumber(){
        return page.getNumber() + 1;
    }
    
    public int prevPage(){
        return Math.max(currentNumber() - 1, 1);
    }
    
    public int nextPage(){
        return Math.min(currentNumber() + 1, calcTotalPage());
    }
    
    public int startNumber(){
        return Math.floorDiv(page.getNumber(), pageButtonCnt) * pageButtonCnt + 1;
    }
    
    public int endNumber(){
        return Math.min(startNumber() + pageButtonCnt - 1, calcTotalPage());
    }

    public List<T> content(){
        return page.getContent();
    }
    
    private int calcTotalPage(){
        int totalPage = page.getTotalPages();
        return totalPage == 0 ? 1 : totalPage;
    }
}
