package biz.gelicon.guspring.utils;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Параметры запроса для выборки данных методом read")
public class GelRequestParam {

    public static final int DEFAULT_PAGE_SIZE = 25; // Рзамер страницы в строках по умолчанию

    @Schema(description = "Текущая страница, нумерация с 0", defaultValue = "0")
    private int pageNumber;

    @Schema(description = "Размер страницы в строках", defaultValue = "25")
    private int pageSize;

    @Schema(description = "Сортировки")
    private List<OrderBy> sort;

    /**
     * Вспомогательный класс для сортировки Имя поля - как в базе
     */
    @Schema(description = "Сортировка по полю")
    public static class OrderBy {

        @Schema(description = "Имя поля для сортировки", example = "edizm_name")
        private String fieldName;
        @Schema(description = "Направление сортировки, null и 0 - по возрастанию, 1 - по убыванию", example = "null и 0 - по возрастанию, 1 - по убыванию")
        private int direction;

        public OrderBy() {
        }

        public OrderBy(String fieldName, int direction) {
            this.fieldName = fieldName;
            this.direction = direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public int getDirection() {
            return direction;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldName() {
            return fieldName;
        }

        @Override
        public String toString() {
            return fieldName + " " + direction;
        }
    }

    public GelRequestParam(int pageNumber, int pageSize,
            List<OrderBy> sort) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = sort;
    }

    public GelRequestParam() {
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<OrderBy> getSort() {
        return sort;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setSort(List<OrderBy> sort) {
        this.sort = sort;
    }
}
