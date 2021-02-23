package biz.gelicon.guspring.utils;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Параметры запроса для выборки данных методом read")
public class GelRequestParam {

    public static final int DEFAULT_PAGE_SIZE = 25; // Рзамер страницы в строках по умолчанию

    @Schema(description = "Текущая страница, нумерация с 0", defaultValue = "0")
    private Integer pageNumber;

    @Schema(description = "Размер страницы в строках", defaultValue = "25")
    private Integer pageSize;

    @Schema(description = "Сортировки")
    private List<Sorter> sort;

    @Schema(description = "Фильтры на выборку")
    private List<Filter> filters;

    /**
     * Вспомогательный класс для сортировки Имя поля - как в базе
     */
    @Schema(description = "Сортировка по полю")
    public static class Sorter {

        @Schema(description = "Имя поля для сортировки", example = "edizm_name")
        private String field;
        @Schema(description = "Направление сортировки, null и ascend - по возрастанию, descend - по убыванию",
                example = "null и ascend - по возрастанию, descend - по убыванию")
        private String order;

        public Sorter() {
        }

        public Sorter(String fieldName, String direction) {
            this.field = fieldName;
            this.order = direction;
        }

        public void setOrder(String order) {
            this.order = order;
        }

        public String getOrder() {
            return order;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }

        @Override
        public String toString() {
            return field + " " + order;
        }

    }

    /**
     * Вспомогательный класс для фильтра
     */
    @Schema(description = "Фильтр для выборки")
    public static class Filter {

        @Schema(description = "Имя фильтра", example = "onlyBlock")
        private String key;
        @Schema(description = "Значение фильра", example = "true")
        private String value;

        public Filter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public Filter() {
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Filter{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public GelRequestParam(
            Integer pageNumber,
            Integer pageSize,
            List<Sorter> sort,
            List<Filter> filters
    ) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = sort;
        this.filters = filters;
    }

    public GelRequestParam() {
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public List<Sorter> getSort() {
        return sort;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setSort(List<Sorter> sort) {
        this.sort = sort;
    }



    @Override
    public String toString() {
        return "GelRequestParam{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", sort=" + sort +
                ", filters=" + filters +
                '}';
    }
}
