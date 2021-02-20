package biz.gelicon.guspring.entities;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/* Сущность сгенерирована 25.12.2020 14:11 */
@Table(
    name = "edizm",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"edizm_code"})
    }
)
@Schema(description = "Единица измерения")
public class EdizmEntity {

    @Schema(description = "Идентификатор")
    @Id
    @Column(name = "edizm_id")
    public Integer id;

    @Schema(description = "Наименование")
    @Column(name = "edizm_name", nullable = true)
    public String name;

    @Schema(description = "Обозначение")
    @Column(name = "edizm_notation", nullable = false)
    public String notation;

    @Schema(description = "Флаг блокировки", example = "0 - Разрешено, 1 - Заблокированно")
    @Column(name = "edizm_blockflag", nullable = false)
    public Integer blockflag;

    @Schema(description = "Код")
    @Column(name = "edizm_code", nullable = false)
    public String code;

    public EdizmEntity() {}

    public EdizmEntity(
            Integer id,
            String name,
            String notation,
            Integer blockflag,
            String code) {
        this.id = id;
        this.name = name;
        this.notation = notation;
        this.blockflag = blockflag;
        this.code = code;
    }
}

