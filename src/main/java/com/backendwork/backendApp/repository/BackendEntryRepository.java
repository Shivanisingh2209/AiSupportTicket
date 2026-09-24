package com.backendwork.backendApp.repository;

import com.backendwork.backendApp.entity.BackendEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BackendEntryRepository extends MongoRepository<BackendEntry, String> {

}
