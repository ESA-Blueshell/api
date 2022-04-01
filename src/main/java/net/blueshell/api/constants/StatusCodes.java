package net.blueshell.api.constants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class StatusCodes {

    // 2xx
    public static final ResponseEntity<Object> OK = new ResponseEntity<>(HttpStatus.OK);
    public static final ResponseEntity<Object> CREATED = new ResponseEntity<>(HttpStatus.CREATED);

    // 4xx
    public static final ResponseEntity<Object> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    public static final ResponseEntity<Object> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    public static final ResponseEntity<Object> FORBIDDEN = new ResponseEntity<>(HttpStatus.FORBIDDEN);
    public static final ResponseEntity<Object> UNAUTHORIZED = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

    // 5xx
    public static final ResponseEntity<Object> INTERNAL_SERVER_ERROR = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
}
