package com.modive.dashboard.tools;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modive.dashboard.dto.DriveFeedbacksDto;
import com.modive.dashboard.dto.ScoreDto;
import com.modive.dashboard.enums.ScoreType;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


public class TypeConverter {

    //<editor-folder desc="# DynamoDB instant">
    // Instant 변환기
    public static class InstantConverter implements DynamoDBTypeConverter<String, Instant> {
        @Override
        public String convert(Instant object) {
            return object.toString();
        }

        @Override
        public Instant unconvert(String object) {
            return Instant.parse(object);
        }
    }

    // List<Instant> 변환기
    public static class InstantListConverter implements DynamoDBTypeConverter<List<String>, List<Instant>> {
        @Override
        public List<String> convert(List<Instant> instants) {
            return instants.stream().map(Instant::toString).collect(Collectors.toList());
        }

        @Override
        public List<Instant> unconvert(List<String> strings) {
            return strings.stream().map(Instant::parse).collect(Collectors.toList());
        }
    }
    //</editor-folder>
}
