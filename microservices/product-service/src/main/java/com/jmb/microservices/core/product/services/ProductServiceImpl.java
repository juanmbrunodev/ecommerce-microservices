package com.jmb.microservices.core.product.services;

import com.jmb.microservices.core.product.mapper.ProductMapper;
import com.jmb.microservices.core.product.persistence.ProductRepository;
import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.jmb.core.product.Product;
import com.jmb.core.product.ProductService;
import com.jmb.util.exceptions.InvalidInputException;
import com.jmb.util.exceptions.NotFoundException;
import com.jmb.util.http.ServiceUtil;

@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ServiceUtil serviceUtil, ProductRepository productRepository,
                              ProductMapper productMapper) {
        this.serviceUtil = serviceUtil;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Product getProduct(int productId) {
        LOG.debug("/product return the found product for productId={}", productId);
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        var retrievedProductEntity = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found for id provided: "
                        + productId));
        Product response = productMapper.mapEntityToApi(retrievedProductEntity);
        response.setServiceAddress(serviceUtil.getServiceAddress());
        return response;
    }

    @Override
    public Product createProduct(Product body) {
        try {
            var productEntity = productMapper.mapApiToEntity(body);
            var savedEntity = productRepository.save(productEntity);
            return productMapper.mapEntityToApi(savedEntity);
        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " +
                    body.getProductId());
        }
    }

    @Override
    public void deleteProduct(int productId) {
        productRepository.findByProductId(productId)
                .ifPresent(productRepository::delete);
    }
}
