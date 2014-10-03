package com.jakubstas.swagger.rest;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.Joiner;
import com.google.common.net.HttpHeaders;
import com.jakubstas.swagger.model.Product;
import com.jakubstas.swagger.model.ProductList;
import com.jakubstas.swagger.service.EntityAlreadyExistsException;
import com.jakubstas.swagger.service.EntityNotFoundException;
import com.jakubstas.swagger.service.ProductService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Api(value = "/products", description = "Endpoint for product management")
@Path("/products")
public class ProductsEndpoint {

    @Inject
    private ProductService productService;

    @Context
    private UriInfo uriInfo;

    @OPTIONS
    @ApiOperation(
            value = "Returns resource options",
            notes = "This method allows the client to determine the options and/or requirements associated with a resource, or the capabilities of a server, without implying a resource action or initiating a resource retrieval.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of resource options"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getProductsOptions() {
        final String header = HttpHeaders.ALLOW;
        final String value = Joiner.on(", ").join(RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS).toString();

        return Response.noContent().header(header, value).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Returns all products", notes = "Returns a complete list of products from catalog.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of all products"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getProducts() {
        return Response.ok(new ProductList(productService.getAll())).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Creates a product", notes = "Creates a product and puts it in the catalog.")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Successful catalogization of new product"), @ApiResponse(code = 406, message = "Malformed definition of new product"),
            @ApiResponse(code = 409, message = "Product with specified code already exists"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response createProduct(@ApiParam(name = "product", required = true) Product product) {
        try {
            final Product newProduct = productService.createProduct(product);

            return Response.status(Status.CREATED).entity(newProduct).location(getLocation(newProduct)).links(getProductLinks(newProduct.getCode())).build();
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Status.CONFLICT).entity("Specified productCode is already taken.").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }
    }

    @OPTIONS
    @Path("/{productCode}")
    @ApiOperation(
            value = "Returns resource options",
            notes = "This method allows the client to determine the options and/or requirements associated with a resource, or the capabilities of a server, without implying a resource action or initiating a resource retrieval.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of resource options"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getProductOptions(@ApiParam("product identifier") @PathParam("productCode") String productCode) {
        final String header = HttpHeaders.ALLOW;
        final String value = Joiner.on(", ").join(RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS).toString();

        return Response.ok().header(header, value).build();
    }

    @GET
    @Path("/{productCode}")
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Returns product details", notes = "This method provides detailed product description.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of product details"), @ApiResponse(code = 404, message = "Product with given code does not exists"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response getProduct(@ApiParam("product identifier") @PathParam("productCode") String productCode) {
        final Product product = productService.findByCode(productCode);

        if (product == null) {
            return Response.status(Status.NOT_FOUND).entity("Product with given code does not exists").links(Link.fromUri("products").rel("create").type(MediaType.APPLICATION_XML).build(productCode)).build();
        } else {
            return Response.status(Status.OK).entity(product).links(getProductLinks(productCode)).build();
        }
    }

    @PUT
    @Path("/{productCode}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Updates product details", notes = "Updates a product from the catalog.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful update of product details"), @ApiResponse(code = 404, message = "Product with given code does not exists"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response updateProduct(@ApiParam("product identifier") @PathParam("productCode") String productCode, @ApiParam(name = "product", required = true) Product product) {
        try {
            final Product updatedProduct = productService.updateProduct(productCode, product);

            return Response.status(Status.OK).entity(updatedProduct).links(getProductLinks(productCode)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity("Product with given code does not exists").links(Link.fromUri("products").rel("create").type(MediaType.APPLICATION_XML).build()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{productCode}")
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "Deletes a product", notes = "Deletes a product and removes it from the catalog.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful removal of product details"), @ApiResponse(code = 404, message = "Product with given code does not exists"),
            @ApiResponse(code = 500, message = "Internal server error") })
    public Response deleteProduct(@ApiParam("product identifier") @PathParam("productCode") String productCode) {
        try {
            final Product deletedProduct = productService.deleteProduct(productCode);

            return Response.status(Status.OK).entity(deletedProduct).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity("Product with given code does not exists").links(Link.fromUri("products").rel("create").type(MediaType.APPLICATION_XML).build()).build();
        }
    }

    private Link[] getProductLinks(final String productCode) {
        final Link[] links = new Link[2];

        links[0] = Link.fromUri("products/{productCode}").rel("update").type(MediaType.APPLICATION_XML).build(productCode);
        links[1] = Link.fromUri("products").rel("listAll").type(MediaType.APPLICATION_XML).build(productCode);

        return links;
    }

    private URI getLocation(final Product product) {
        return UriBuilder.fromUri("products/{productCode}").build(product.getCode());
    }
}
