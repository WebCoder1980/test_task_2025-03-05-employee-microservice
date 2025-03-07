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
import ru.isands.test.estore.dto.PositionTypeDTO;
import ru.isands.test.estore.dto.ErrorDTO;
import ru.isands.test.estore.service.PositionTypeService;

import java.util.List;

@RestController
@Tag(name = "PositionType", description = "Сервис для выполнения операций над типами сотрудников магазина")
@RequestMapping("/estore/api/positiontype")
public class PositionTypeController {

	private final PositionTypeService positionTypeService;

	public PositionTypeController(PositionTypeService positionTypeService) {
		this.positionTypeService = positionTypeService;
	}

	@PostMapping
	@Operation(summary = "Добавить тип сотрудника", responses = {
			@ApiResponse(responseCode = "200", description = "Сотрудник добавлен"),
			@ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
	})
	public ResponseEntity<PositionTypeDTO> add(@RequestBody PositionTypeDTO positionTypeDTO) {
		return ResponseEntity.ok(positionTypeService.add(positionTypeDTO));
	}

	@GetMapping
	@Operation(summary = "Получить всех типы сотрудников", parameters = {
			@Parameter(name = "start", description = "Номер первого в результате типа сотрудника", schema = @Schema(type = "integer", defaultValue = "0")),
			@Parameter(name = "limit", description = "Максимальное колличество типов сотрудников в результате", schema = @Schema(type = "integer", defaultValue = "1000000"))
			}, responses = {
			@ApiResponse(responseCode = "200", description = "Список типов сотрудников"),
			@ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
	})
	public ResponseEntity<List<PositionTypeDTO>> getAll(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "limit", defaultValue = "1000000") int limit) {
		return ResponseEntity.ok(positionTypeService.getAll(start, limit));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Получить тип сотрудника по ID", responses = {
			@ApiResponse(responseCode = "200", description = "Информация о типе сотруднике"),
			@ApiResponse(responseCode = "404", description = "Сотрудник не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
			@ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
	})
	public ResponseEntity<PositionTypeDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(positionTypeService.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Обновить информацию о типе сотруднике", responses = {
			@ApiResponse(responseCode = "200", description = "Сотрудник обновлен"),
			@ApiResponse(responseCode = "404", description = "Сотрудник не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
			@ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
	})
	public ResponseEntity<PositionTypeDTO> update(@PathVariable Long id, @RequestBody PositionTypeDTO positionTypeDTO) {
		return ResponseEntity.ok(positionTypeService.update(id, positionTypeDTO));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Удалить тип сотрудника", responses = {
			@ApiResponse(responseCode = "204", description = "Сотрудник удален"),
			@ApiResponse(responseCode = "404", description = "Сотрудник не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
			@ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
	})
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		positionTypeService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/upload-csv")
	@Operation(summary = "Загрузить типы сотрудников из CSV", responses = {
			@ApiResponse(responseCode = "200", description = "Сотрудники успешно загружены"),
			@ApiResponse(responseCode = "404", description = "Сотрудник не найден", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))),
			@ApiResponse(responseCode = "500", description = "Ошибка сервера", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
	})
	public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty() || !file.getOriginalFilename().endsWith(".csv")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пожалуйста, загрузите корректный CSV файл.");
		}

		try {
			positionTypeService.processCSVFile(file);
			return ResponseEntity.ok("Данные типов сотрудников успешно загружены.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при обработке файла.");
		}
	}
}
