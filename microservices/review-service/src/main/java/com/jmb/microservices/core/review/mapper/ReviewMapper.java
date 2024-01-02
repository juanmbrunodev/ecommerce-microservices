package com.jmb.microservices.core.review.mapper;

import com.jmb.core.review.Review;
import com.jmb.microservices.core.review.persistence.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mappings(
            @Mapping(target = "serviceAddress", ignore = true)
    )
    Review entityToApi(final ReviewEntity reviewEntity);

    @Mappings({
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "id", ignore = true)
    })
    ReviewEntity apiToEntity(final Review review);
}
