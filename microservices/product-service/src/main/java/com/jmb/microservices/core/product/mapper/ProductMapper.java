package com.jmb.microservices.core.product.mapper;

import com.jmb.core.product.Product;
import com.jmb.microservices.core.product.persistence.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    /*
     * Since the entity class does not have a field for 'serviceAddress', the entityToApi() method is annotated to ignore
     * the serviceAddress field
     */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    ProductEntity mapApiToEntity(Product product);

    /*
    * In the same way, the apiToEntity() method is annotated to ignore the 'id' and 'version' fields that are missing
    * in the API model class.
    */
    @Mapping(target = "serviceAddress", ignore = true)
    Product mapEntityToApi(ProductEntity entity);
}
