package biz.gelicon.model.cr;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

/* Сущность сгенерирована 25.12.2020 14:11 */
@Table(
    name = "streettype",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"streettype_code"}),
        @UniqueConstraint(columnNames = {"streettype_name"})
    }
)
public class StreettypeEntity {

    @Id
    @Column(name = "streettype_id")
    public Integer id;

    @Column(name = "streettype_name", nullable = false)
    public String name;

    @Column(name = "streettype_code", nullable = false)
    public String code;

    public StreettypeEntity() {}

    public StreettypeEntity(
            Integer id,
            String name,
            String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}

