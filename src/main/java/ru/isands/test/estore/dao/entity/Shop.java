package ru.isands.test.estore.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "shop")
public class Shop implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "shop_counter")
    @TableGenerator(name = "shop_counter", pkColumnName = "name", pkColumnValue = "ru.isands.test.estore.dao.entity.Shop", table = "counter", valueColumnName = "currentid", allocationSize = 1)
    @Column(name = "id_", unique = true, nullable = false)
    @NotNull
    private Long id;

    @Column(name = "name", nullable = false, length = 250)
    @NotNull
    private String name;

    @Column(name = "address", nullable = false)
    @NotNull
    private String address;
}