package com.jakubstas.swagger.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;
import com.jakubstas.swagger.model.Product;

@Service
public class ProductService {

    private final Map<String, Product> products = new ConcurrentHashMap<String, Product>();

    @PostConstruct
    public void init() throws IOException {
        final Product product1 = new Product();
        product1.setCode("prod1");
        product1.setName("Soap");
        product1.setDescription("a bar of soap");

        final Product product2 = new Product();
        product2.setCode("prod2");
        product2.setName("Water");
        product2.setDescription("a bottle of water");

        products.put(product1.getCode(), product1);
        products.put(product2.getCode(), product2);
    }

    public Product createProduct(final Product product) throws EntityAlreadyExistsException {
        Preconditions.checkArgument(StringUtils.hasText(product.getCode()), "Invalid product definition! Missing product code.");
        Preconditions.checkArgument(StringUtils.hasText(product.getName()), "Invalid product definition! Missing name.");
        Preconditions.checkArgument(StringUtils.hasText(product.getDescription()), "Invalid product definition! Missing description.");

        if (products.containsKey(product.getCode())) {
            throw new EntityAlreadyExistsException();
        }

        products.put(product.getCode(), product);

        return product;
    }

    public Product findByCode(final String code) {
        return products.get(code);
    }

    public Product updateProduct(final String productCode, final Product newProduct) throws EntityNotFoundException {
        Preconditions.checkArgument(StringUtils.hasText(newProduct.getCode()), "Invalid product definition! Missing product code.");
        Preconditions.checkArgument(productCode.equals(newProduct.getCode()), "Product code mismatch.");
        Preconditions.checkArgument(StringUtils.hasText(newProduct.getName()), "Invalid product definition! Missing name.");
        Preconditions.checkArgument(StringUtils.hasText(newProduct.getDescription()), "Invalid product definition! Missing description.");

        if (!products.containsKey(newProduct.getCode())) {
            throw new EntityNotFoundException();
        }

        final Product product = products.get(newProduct.getCode());
        product.setName(newProduct.getName());
        product.setDescription(newProduct.getDescription());

        return product;
    }

    public Product deleteProduct(final String code) throws EntityNotFoundException {
        if (!products.containsKey(code)) {
            throw new EntityNotFoundException();
        }

        return products.remove(code);
    }

    public Collection<Product> getAll() {
        return products.values();
    }
}
