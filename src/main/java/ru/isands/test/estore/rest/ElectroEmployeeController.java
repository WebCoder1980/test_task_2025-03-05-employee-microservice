package ru.isands.test.estore.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.isands.test.estore.dto.ElectroEmployeeDTO;
import ru.isands.test.estore.dto.ErrorDTO;
import ru.isands.test.estore.service.ElectroEmployeeService;

import java.util.List;

@RestController
@Tag(name = "ElectroEmployee", description = "Сервис для выполнения операций над сотрудниками-товар")
@RequestMapping("/estore/api/electroemployee")
public class ElectroEmployeeController {
    private final ElectroEmployeeService electroEmployeeService;

    public ElectroEmployeeController(ElectroEmployeeService electroEmployeeService) {
        this.electroEmployeeService = electroEmployeeService;
    }

    @PostMapping
    @Operation(summary = "Добавить сотрудник-товар", responses = {
            @ApiResponse(responseCode = "200", description = "Сотрудник-товар добавлен"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<ElectroEmployeeDTO> add(@RequestBody ElectroEmployeeDTO electroEmployeeDTO) {
        return ResponseEntity.ok(electroEmployeeService.add(electroEmployeeDTO));
    }

    @GetMapping
    @Operation(summary = "Получить все сотрудник-товар", parameters = {
            @Parameter(name = "start", description = "Номер первого в результате сотрудник-товар", schema = @Schema(type = "integer", defaultValue = "0")),
            @Parameter(name = "limit", description = "Максимальное колличество сотрудник-товар в результате", schema = @Schema(type = "integer", defaultValue = "1000000"))
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Список сотрудник-товар"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<List<ElectroEmployeeDTO>> getAll(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "1000000") int limit) {
        return ResponseEntity.ok(electroEmployeeService.getAll(start, limit));
    }

    @GetMapping("/id")
    @Operation(summary = "Получить сотрудник-товар по IDs", parameters = {
            @Parameter(name = "electroid"),
            @Parameter(name = "shopid")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Информация о сотрудник-товар"),
            @ApiResponse(responseCode = "404", description = "Сотрудник-товар не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
    })
    public ResponseEntity<ElectroEmployeeDTO> getById(@RequestParam(value = "electroid") Long electroId, @RequestParam(value = "shopid") Long shopId) {
        return ResponseEntity.ok(electroEmployeeService.getByElectroIdAndShopId(electroId, shopId));
    }

    @PutMapping
    @Operation(summary = "Обновить информацию о сотрудник-товаре", parameters = {
            @Parameter(name = "electroid"),
            @Parameter(name = "shopid")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Сотрудник-товар обновлен"),
            @ApiResponse(responseCode = "404", description = "Сотрудник-товар не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<ElectroEmployeeDTO> update(@RequestParam(value = "electroid") Long electroId, @RequestParam(value = "shopid") Long shopId, @RequestBody ElectroEmployeeDTO electroEmployeeDTO) {
        return ResponseEntity.ok(electroEmployeeService.update(electroId, shopId, electroEmployeeDTO));
    }

    @DeleteMapping
    @Operation(summary = "Удалить сотрудник-товар", parameters = {
            @Parameter(name = "electroid"),
            @Parameter(name = "shopid")
    }, responses = {
            @ApiResponse(responseCode = "204", description = "Сотрудник-товар удален"),
            @ApiResponse(responseCode = "404", description = "Сотрудник-товар не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<Void> delete(@RequestParam(value = "electroid") Long electroId, @RequestParam(value = "shopid") Long shopId) {
        electroEmployeeService.delete(electroId, shopId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-csv")
    @Operation(summary = "Загрузить сотрудник-товары из CSV", responses = {
            @ApiResponse(responseCode = "200", description = "Сотрудник-товар успешно загружены"),
            @ApiResponse(responseCode = "404", description = "Сотрудник-товар не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    })
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пожалуйста, загрузите корректный CSV файл.");
        }

        try {
            electroEmployeeService.processCSVFile(file);
            return ResponseEntity.ok("Данные сотрудник-товаров успешно загружены.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обработке файла.");
        }
    }
}
