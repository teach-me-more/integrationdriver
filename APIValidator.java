package com.rbasystems.api.validator.apivalidator;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rbasystems.api.validator.FieldTypeEntity;

@RestController
public class APIController {
	@GetMapping("v1/api/validate/")
	public List<String> validateAPI() {
		SwaggerParseResult result = new OpenAPIParser().readLocation("https://petstore3.swagger.io/api/v3/openapi.json",
				null, null);
		// SwaggerParseResult result = new
		// OpenAPIParser().readLocation("./path/to/openapi.yaml", null, null);

		OpenAPI openAPI = result.getOpenAPI();

		if (result.getMessages() != null) {
			result.getMessages().forEach(System.err::println); // validation errors and warnings
		}
		if (openAPI != null) {
			Paths paths = openAPI.getPaths();
			if (paths != null && paths.entrySet() != null) {
				paths.entrySet().forEach(entry -> {
					PathItem item = entry.getValue();
					System.out.println("Key =" + entry.getKey() + "Item ==" + item.toString());
				});
			}
		}
		return result.getMessages();
	}

	@PostMapping("v1/api/validate/file")
	public String handleFileUpload(@RequestPart("file") MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			return "Error: File is empty";
		}
		Map<String, Set<FieldTypeEntity>> fieldsData = new HashMap<>();
		String jsonFile = new String(file.getBytes());
		SwaggerParseResult result = new OpenAPIParser().readContents(jsonFile, null, null);
		OpenAPI openAPI = result.getOpenAPI();
		openAPI.getComponents().getSchemas().entrySet().forEach(schema->{
		Set<FieldTypeEntity> filedsPerSchema = new HashSet<>();
		//System.out.println("schema.getKey()=="+schema.getKey() + schema.getValue().getProperties());
		Set<Entry<String, Object>> mapSet =  schema.getValue().getProperties().entrySet();
		mapSet.forEach(item->{
			filedsPerSchema.add(new FieldTypeEntity(item.getKey()));
		});
		fieldsData.put(schema.getKey(), filedsPerSchema);
		});
		
		fieldsData.entrySet().forEach(schema->{
			System.out.println(schema.getKey()+">>\n");
			schema.getValue().forEach(field->{
				System.out.println(field.getName());
				});
		});
		if (result.getMessages() != null) {
			result.getMessages().forEach(System.err::println); // validation errors and warnings
		}
		if (openAPI != null) {
			Paths paths = openAPI.getPaths();
			if (paths != null && paths.entrySet() != null) {
				
				paths.entrySet().forEach(entry -> {
					PathItem item = entry.getValue();
					System.out.println("Validating path ="+entry.getKey() );

					if (item.getGet() != null) {
						Operation operation = item.getGet();
						
						//validateInputBody(operation);
						validateResponseFields(operation);
					}
					if (item.getPost() != null) {
						Operation operation = item.getPost();
						validateInputBody(operation);
						validateResponseFields(operation);
					}
					if (item.getDelete() != null) {
						Operation operation = item.getDelete();
						validateInputBody(operation);
						validateResponseFields(operation);
					}
					if (item.getPut() != null) {
						Operation operation = item.getPut();
						validateResponseFields(operation);
						validateInputBody(operation);
					}

					// System.out.println("Key =" + entry.getKey()+ "Item ==" + item.toString());
				});
			}
		}
		return "File uploaded successfully";
	}

	private void validateResponseFields(Operation operation) {
		ApiResponses responses = operation.getResponses();
		responses.entrySet().forEach(responseEntry -> {
			ApiResponse response = responseEntry.getValue();
			Content fields = response.getContent();
			
			if (fields != null) {
				fields.entrySet().forEach(field -> {
					System.out.print("filed ="+ field.getKey() +",type ="+ field.getValue());
				});
			}
		});
	}

	private void validateInputBody(Operation operation) {
		System.out.println("Validating the input fields for operation ="+operation.getOperationId());
		
		RequestBody  requestBody = operation.getRequestBody();
			if(requestBody!=null) {
			Content fields = requestBody.getContent();
			if (fields != null) {
				fields.entrySet().forEach(field -> {
					System.out.print("filed ="+ field.getKey() +",type ="+ field.getValue());
				});
			}
			}
	}
}
