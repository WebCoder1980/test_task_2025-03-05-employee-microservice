package ru.isands.test.estore.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.isands.test.estore.dao.entity.ElectroEmployee;
import ru.isands.test.estore.dao.repo.ElectroEmployRepository;
import ru.isands.test.estore.dto.ElectroEmployeeDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ElectroEmployeeService {

    private final ElectroEmployRepository electroEmployRepository;

    public ElectroEmployeeService(ElectroEmployRepository electroEmployRepository) {
        this.electroEmployRepository = electroEmployRepository;
    }

    public ElectroEmployeeDTO add(ElectroEmployeeDTO electroEmployeeDTO) {
        ElectroEmployee electroEmployee = mapToEntity(electroEmployeeDTO);
        ElectroEmployee savedElectroEmployee = electroEmployRepository.save(electroEmployee);
        return mapToDTO(savedElectroEmployee);
    }

    public List<ElectroEmployeeDTO> getAll(int start, int limit) {
        return electroEmployRepository.findAll().stream()
                .sorted(Comparator.comparing(ElectroEmployee::getElectroTypeId).thenComparing(ElectroEmployee::getEmployeeId))
                .skip(start)
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ElectroEmployeeDTO getByElectroIdAndShopId(Long electroTypeId, Long employeeId) {
        return Optional.ofNullable(electroEmployRepository.findByElectroTypeIdAndEmployeeId(electroTypeId, employeeId))
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник-товар не найден"));
    }


    public ElectroEmployeeDTO update(Long electroTypeId, Long employeeId, ElectroEmployeeDTO electroEmployeeDTO) {
        ElectroEmployee existingElectroEmployee = mapToEntity(getByElectroIdAndShopId(electroTypeId, employeeId));

        updateEntityFromDTO(electroEmployeeDTO, existingElectroEmployee);
        ElectroEmployee updatedElectroEmployee = electroEmployRepository.save(existingElectroEmployee);
        return mapToDTO(updatedElectroEmployee);
    }

    public void delete(Long electroTypeId, Long employeeId) {
        mapToEntity(getByElectroIdAndShopId(electroTypeId, employeeId));

        electroEmployRepository.deleteByElectroTypeIdAndEmployeeId(electroTypeId, employeeId);
    }

    private ElectroEmployee mapToEntity(ElectroEmployeeDTO electroEmployeeDTO) {
        ElectroEmployee electroEmployee = new ElectroEmployee();
        updateEntityFromDTO(electroEmployeeDTO, electroEmployee);
        return electroEmployee;
    }

    private void updateEntityFromDTO(ElectroEmployeeDTO electroEmployeeDTO, ElectroEmployee electroEmployee) {
        electroEmployee.setElectroTypeId(electroEmployeeDTO.getElectroTypeId());
        electroEmployee.setEmployeeId(electroEmployeeDTO.getEmployeeId());
    }

    public void processCSVFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName("Windows-1251")))) {
            String line;
            List<ElectroEmployeeDTO> electroEmploys = new ArrayList<>();

            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] data = line.split(";");

                ElectroEmployeeDTO electroEmployeeDTO = new ElectroEmployeeDTO();
                electroEmployeeDTO.setEmployeeId(Long.parseLong(data[0].trim()));
                electroEmployeeDTO.setElectroTypeId(Long.parseLong(data[1].trim()));

                electroEmploys.add(electroEmployeeDTO);
            }

            saveAll(electroEmploys);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке файла", e);
        }
    }

    public void saveAll(List<ElectroEmployeeDTO> electroEmployeeDTOS) {
        List<ElectroEmployee> electroEmployees = electroEmployeeDTOS.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
        electroEmployees.forEach(i -> electroEmployRepository.save(i));
    }

    private ElectroEmployeeDTO mapToDTO(ElectroEmployee electroEmployee) {
        ElectroEmployeeDTO electroEmployeeDTO = new ElectroEmployeeDTO();

        electroEmployeeDTO.setElectroTypeId(electroEmployee.getElectroTypeId());
        electroEmployeeDTO.setEmployeeId(electroEmployee.getEmployeeId());

        return electroEmployeeDTO;
    }
}