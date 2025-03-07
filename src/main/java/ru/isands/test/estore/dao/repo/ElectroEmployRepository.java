package ru.isands.test.estore.dao.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.isands.test.estore.dao.entity.ElectroEmployee;
import ru.isands.test.estore.dao.entity.ElectroEmployeePK;

import javax.transaction.Transactional;
import java.util.List;


public interface ElectroEmployRepository extends JpaRepository<ElectroEmployee, ElectroEmployeePK> {
    @Query("select p from ElectroEmployee p where p.electroTypeId = :electroTypeId and p.employeeId = :employeeId")
    public List<ElectroEmployee> findByElectroTypeIdAndEmployeeId(@Param("electroTypeId") Long electroTypeId, @Param("employeeId") Long employeeId);

    @Modifying
    @Transactional
    @Query("delete from ElectroEmployee p where p.electroTypeId = :electroTypeId and p.employeeId = :employeeId")
    public void deleteByElectroTypeIdAndEmployeeId(@Param("electroTypeId") Long electroTypeId, @Param("employeeId") Long employeeId);
}
