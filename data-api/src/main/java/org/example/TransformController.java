package org.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/transform")
public class TransformController {
    @Value("${dataapi.internal-token}")
    private String internalToken;

    @PostMapping
    public ResponseEntity<TransformResponse> transform(
            @RequestHeader(name = "X-Internal-Token", required = false) String token,
            @RequestBody TransformRequest request
    ) {
        System.out.println("Internal token is: "+internalToken);
        System.out.println("Transform function launched");
        System.out.println("Token is" + token);
        System.out.println("Request text is"+request.text());
        if (token == null || !token.equals(internalToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (request.text() == null) {
            return ResponseEntity.badRequest().build();
        }

        String transformed = new StringBuilder(request.text())
                .reverse()
                .toString()
                .toUpperCase();
        System.out.println("Transformed text is: "+transformed);

        return ResponseEntity.ok(new TransformResponse(transformed));
    }
}
