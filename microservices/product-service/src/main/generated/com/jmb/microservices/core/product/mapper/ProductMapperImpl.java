package com.jmb.microservices.core.product.mapper;

import com.jmb.core.product.Product;
import com.jmb.microservices.core.product.persistence.ProductEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-02T11:48:05+0200",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 20 (Homebrew)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductEntity mapApiToEntity(Product product) {
        if ( product == null ) {
            return null;
        }

        int productId = 0;
        String name = null;
        int weight = 0;

        productId = product.getProductId();
        name = product.getName();
        weight = product.getWeight();

        ProductEntity productEntity = new ProductEntity( productId, name, weight );

        return productEntity;
    }

    @Override
    public Product mapEntityToApi(ProductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Product product = new Product();

        product.setProductId( entity.getProductId() );
        product.setName( entity.getName() );
        product.setWeight( entity.getWeight() );

        return product;
    }
}
