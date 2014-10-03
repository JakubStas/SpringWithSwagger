package com.jakubstas.swagger.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel
@XmlRootElement(name = "productList", namespace = "com.jakubstas.swagger")
public class ProductList {
    private List<Product> products;

    public ProductList() {
    }

    public ProductList(final Collection<Product> products) {
        this.products = new ArrayList<>(products);
    }

    @ApiModelProperty(required = true, position = 1)
    @XmlElement(name = "product", required = true)
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @ApiModelProperty(required = true, position = 2)
    @XmlAttribute(name = "size", namespace = "com.jakubstas.swagger", required = true)
    public int getCount() {
        return products.size();
    }
}
