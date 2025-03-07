package ru.isands.test.estore.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@IdClass(ElectroEmployeePK.class)
@Table(name = "electro_employee")
public class ElectroEmployee {
	@Id
	@Column(name = "electrotypeid", nullable = false)
	Long electroTypeId;

	@Id
	@Column(name = "employeeid", nullable = false)
	Long employeeId;
}
