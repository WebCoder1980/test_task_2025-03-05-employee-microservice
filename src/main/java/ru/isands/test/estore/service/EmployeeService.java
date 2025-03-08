package ru.isands.test.estore.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.isands.test.estore.dao.entity.Employee;
import ru.isands.test.estore.dao.entity.PositionType;
import ru.isands.test.estore.dao.entity.Shop;
import ru.isands.test.estore.dao.repo.EmployeeRepository;
import ru.isands.test.estore.dto.EmployeeDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeDTO add(EmployeeDTO employeeDTO) {
        Employee employee = mapToEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        return mapToDTO(savedEmployee);
    }

    public List<EmployeeDTO> getAll(int start, int limit) {
        return employeeRepository.findAll().stream()
                .sorted(Comparator.comparing(Employee::getId))
                .skip(start)
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getById(Long id) {
        return employeeRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник не найден"));
    }

    public EmployeeDTO update(Long id, EmployeeDTO employeeDTO) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник не найден"));

        updateEntityFromDTO(employeeDTO, existingEmployee);
        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return mapToDTO(updatedEmployee);
    }

    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник не найден");
        }
        employeeRepository.deleteById(id);
    }

    private Employee mapToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        updateEntityFromDTO(employeeDTO, employee);
        return employee;
    }

    private void updateEntityFromDTO(EmployeeDTO employeeDTO, Employee employee) {
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setPatronymic(employeeDTO.getPatronymic());
        employee.setBirthDate(employeeDTO.getBirthDate());
        employee.setGender(employeeDTO.getGender());

        PositionType position = new PositionType();
        position.setId(employeeDTO.getPositionId());
        employee.setPosition(position);

        Shop shop = new Shop();
        shop.setId(employeeDTO.getShopId());
        employee.setShop(shop);
    }

    public void processCSVFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName("Windows-1251")))) {
            String line;
            List<EmployeeDTO> employees = new ArrayList<>();

            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(";");

                if (data.length < 8) {
                    throw new RuntimeException("Файл содержит меньше столбцов, чем нужно");
                }

                EmployeeDTO employeeDTO = new EmployeeDTO();
                employeeDTO.setLastName(data[1].trim());
                employeeDTO.setFirstName(data[2].trim());
                employeeDTO.setPatronymic(data[3].trim());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                employeeDTO.setBirthDate(LocalDate.parse(data[4].trim(), formatter));

                employeeDTO.setPositionId(Long.parseLong(data[5].trim()));
                employeeDTO.setShopId(Long.parseLong(data[6].trim()));
                employeeDTO.setGender(data[7].trim().equals("1"));

                employees.add(employeeDTO);
            }

            saveAll(employees);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке файла", e);
        }
    }

    public void saveAll(List<EmployeeDTO> employeeDTOs) {
        List<Employee> employees = employeeDTOs.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
        employees.forEach(i -> employeeRepository.save(i));
    }

    private EmployeeDTO mapToDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setPatronymic(employee.getPatronymic());
        employeeDTO.setBirthDate(employee.getBirthDate());
        employeeDTO.setPositionId(employee.getPosition().getId());
        employeeDTO.setShopId(employee.getShop().getId());
        employeeDTO.setGender(employee.isGender());
        return employeeDTO;
    }
}
