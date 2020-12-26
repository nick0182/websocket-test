package com.shaidulin.rgbexample.repository;

import com.shaidulin.rgbexample.domain.Color;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ColorRepository extends MongoRepository<Color, String> {}
