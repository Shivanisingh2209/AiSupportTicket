package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.entity.BackendEntry;
import com.backendwork.backendApp.services.BackendEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/backend")
public class BackendEntryControllerV2 {

    @Autowired
    private BackendEntryService backendEntryService;

    @GetMapping
    public List<BackendEntry> getAll() {
        return backendEntryService.getAllEntries();
    }

    @PostMapping
    public BackendEntry createEntry(@RequestBody BackendEntry myEntry) {
        return backendEntryService.saveEntry(myEntry);
    }

    @GetMapping("/id/{id}")
    public BackendEntry getBackendEntryById(@PathVariable String id) {
        return backendEntryService.getEntryById(id);
    }

    @DeleteMapping("/id/{id}")
    public boolean deleteBackendEntryById(@PathVariable String id) {
        return backendEntryService.deleteEntryById(id);
    }
}