package ru.mos.polls.common.model;

/**
 * Структура данных, описывающая объект старницы для запроса части списка чего-либо, использующих пагинацию
 *
 * @since 1.8
 */
public class PageInfo {
    public static final int DEFAULT_COUNT_PER_PAGE = 20;

    private int countPerPage;
    private int pageNumber;

    public PageInfo() {
        this(DEFAULT_COUNT_PER_PAGE, 1);
    }

    public PageInfo(int countPerPage, int pageNumber) {
        this.countPerPage = countPerPage;
        this.pageNumber = pageNumber;
    }

    public int getCountPerPage() {
        return countPerPage;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void incrementPageNumber() {
        ++pageNumber;
    }

    public void setCountPerPage(int countPerPage) {
        this.countPerPage = countPerPage;
    }

    public void clear() {
        pageNumber = 1;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
