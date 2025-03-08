package ru.isands.test.estore.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.isands.test.estore.dao.entity.PositionType;
import ru.isands.test.estore.dao.entity.PositionType;
import ru.isands.test.estore.dao.entity.Shop;
import ru.isands.test.estore.dao.repo.PositionTypeRepository;
import ru.isands.test.estore.dto.PositionTypeDTO;

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
public class PositionTypeService {

    private final PositionTypeRepository positionTypeRepository;

    public PositionTypeService(PositionTypeRepository positionTypeRepository) {
        this.positionTypeRepository = positionTypeRepository;
    }

    public PositionTypeDTO add(PositionTypeDTO positionTypeDTO) {
        PositionType positionType = mapToEntity(positionTypeDTO);
        PositionType savedPositionType = positionTypeRepository.save(positionType);
        return mapToDTO(savedPositionType);
    }

    public List<PositionTypeDTO> getAll(int start, int limit) {
        return positionTypeRepository.findAll().stream()
                .sorted(Comparator.comparing(PositionType::getId))
                .skip(start)
                .limit(limit)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PositionTypeDTO getById(Long id) {
        return positionTypeRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник не найден"));
    }

    public PositionTypeDTO update(Long id, PositionTypeDTO positionTypeDTO) {
        PositionType existingPositionType = positionTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник не найден"));

        updateEntityFromDTO(positionTypeDTO, existingPositionType);
        PositionType updatedPositionType = positionTypeRepository.save(existingPositionType);
        return mapToDTO(updatedPositionType);
    }

    public void delete(Long id) {
        if (!positionTypeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Сотрудник не найден");
        }
        positionTypeRepository.deleteById(id);
    }

    private PositionType mapToEntity(PositionTypeDTO positionTypeDTO) {
        PositionType positionType = new PositionType();
        updateEntityFromDTO(positionTypeDTO, positionType);
        return positionType;
    }

    private void updateEntityFromDTO(PositionTypeDTO positionTypeDTO, PositionType positionType) {
        positionType.setName(positionTypeDTO.getName());
    }

    public void processCSVFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName("Windows-1251")))) {
            String line;
            List<PositionTypeDTO> positionTypes = new ArrayList<>();

            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(";");

                if (data.length < 2) {
                    throw new RuntimeException("Файл содержит меньше столбцов, чем нужно");
                }

                PositionTypeDTO positionTypeDTO = new PositionTypeDTO();
                positionTypeDTO.setName(data[1].trim());

                positionTypes.add(positionTypeDTO);
            }

            saveAll(positionTypes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обработке файла", e);
        }
    }

    public void saveAll(List<PositionTypeDTO> positionTypeDTOs) {
        List<PositionType> positionTypes = positionTypeDTOs.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
        positionTypes.forEach(i -> positionTypeRepository.save(i));
    }

    private PositionTypeDTO mapToDTO(PositionType positionType) {
        PositionTypeDTO positionTypeDTO = new PositionTypeDTO();

        positionTypeDTO.setId(positionType.getId());
        positionTypeDTO.setName(positionType.getName());

        return positionTypeDTO;
    }
}
