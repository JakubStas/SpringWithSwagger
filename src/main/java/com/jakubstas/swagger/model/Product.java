package com.jakubstas.swagger.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel
@XmlRootElement(name = "product", namespace = "com.jakubstas.swagger")
@XmlAccessorType(XmlAccessType.NONE)
public class Product {

    private String name;

    private String code;

    private String description;

    private final String oneToBeHidden = "hiddenOne";

    @ApiModelProperty(required = true)
    @XmlElement(name = "name", nillable = false, required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(required = true)
    @XmlElement(name = "code", nillable = false, required = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ApiModelProperty(required = true)
    @XmlElement(name = "description", nillable = false, required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(access = "hidden")
    public String getOneToBeHidden() {
        return oneToBeHidden;
    }
}
