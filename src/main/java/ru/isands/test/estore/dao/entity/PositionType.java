package ru.isands.test.estore.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "position_types")
public class PositionType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "positiontype_counter")
    @TableGenerator(name = "positiontype_counter", pkColumnName = "name", pkColumnValue = "ru.isands.test.estore.dao.entity.PositionType", table = "counter", valueColumnName = "currentid", allocationSize = 1)
    @Column(name = "id_", unique = true, nullable = false)
    @NotNull
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    @NotNull
    private String name;
}