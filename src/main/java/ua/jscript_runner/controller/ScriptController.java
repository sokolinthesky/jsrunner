package ua.jscript_runner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.jscript_runner.entity.Script;
import ua.jscript_runner.exception.ScriptServiceException;
import ua.jscript_runner.service.ScriptService;
import ua.jscript_runner.thread.ScriptExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ScriptController {

    @Autowired
    private ScriptService service;

    @GetMapping("/all")
    public ResponseEntity<Response> all() {
        Response responseBody = new Response();
        List<ScriptExecutor> scriptExecutors = service.getAll();
        responseBody.setContent(scriptExecutors);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/execute")
    public ResponseEntity<Response> executeScript(@RequestBody String script) throws ScriptServiceException {
        Response responseBody = new Response();
        Script jScript = new Script();
        jScript.setId(UUID.randomUUID().toString());
        jScript.setScript(script);
        ScriptExecutor scriptExecutor = service.executeScript(jScript);
        responseBody.setContent(scriptExecutor);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity stopAndRemoveScript(@PathVariable String id) throws ScriptServiceException {
        service.removeScript(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/script/{id}")
    public ResponseEntity<Response> getScriptExecutorById(@PathVariable String id) {
        Response responseBody = new Response();
        ScriptExecutor scriptExecutor = service.getById(id);
        responseBody.setContent(scriptExecutor);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @ExceptionHandler(ScriptServiceException.class)
    public ResponseEntity<Object> exceptionHandler(Exception e) {
        Map<String,String> responseBody = new HashMap<>();
        if (e instanceof ScriptServiceException) {
            responseBody.put("code", "400");
        } else {
            responseBody.put("code", "500");
        }
        responseBody.put("message", e.getMessage());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
