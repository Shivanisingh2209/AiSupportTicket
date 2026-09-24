package com.backendwork.backendApp.services;

import com.backendwork.backendApp.entity.BackendEntry;
import com.backendwork.backendApp.repository.BackendEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackendEntryService {

    @Autowired
    private BackendEntryRepository backendEntryRepository;

    public BackendEntry saveEntry(BackendEntry backendEntry) {
        return backendEntryRepository.save(backendEntry);
    }

    public List<BackendEntry> getAllEntries() {
        return backendEntryRepository.findAll();
    }

    public BackendEntry getEntryById(String id) {
        return backendEntryRepository.findById(id).orElse(null);
    }

    public boolean deleteEntryById(String id) {

        if (!backendEntryRepository.existsById(id)) {
            return false;
        }

        backendEntryRepository.deleteById(id);
        return true;
    }
}