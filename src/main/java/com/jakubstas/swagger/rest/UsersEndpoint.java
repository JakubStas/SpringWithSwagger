package com.jakubstas.swagger.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
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

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.Joiner;
import com.google.common.net.HttpHeaders;
import com.jakubstas.swagger.model.User;
import com.jakubstas.swagger.model.UserList;
import com.jakubstas.swagger.service.EntityAlreadyExistsException;
import com.jakubstas.swagger.service.EntityNotFoundException;
import com.jakubstas.swagger.service.UserService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * REST endpoint for user manipulation.
 */
@Api(value = "users", description = "Endpoint for user management")
@Path("/users")
public class UsersEndpoint {

    @Inject
    private UserService userService;

    @Context
    private UriInfo uriInfo;

    @OPTIONS
    @ApiOperation(
            value = "Returns resource options",
            notes = "This method allows the client to determine the options and/or requirements associated with a resource, or the capabilities of a server, without implying a resource action or initiating a resource retrieval.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of resource options"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getUsersOptions() {
        final String header = HttpHeaders.ALLOW;
        final String value = Joiner.on(", ").join(RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS).toString();

        return Response.noContent().header(header, value).build();
    }

    /**
     * Returns a complete list of users registered within application.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returns all users", notes = "Returns a complete list of users registered within application.", response = UserList.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of all users", response = UserList.class), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getUsers() {
        final UserList users = new UserList(userService.getAll());

        return Response.ok(users, MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Creates and registers user in application.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Creates single user", notes = "Creates and registers user in application.", response = User.class)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Successful registration of new user", response = User.class), @ApiResponse(code = 406, message = "Malformed definition of new user"),
            @ApiResponse(code = 409, message = "User with specified username already exists"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response createUser(@ApiParam(name = "user", required = true) User user) {
        try {
            final User newUser = userService.createUser(user);

            return Response.status(Status.CREATED).entity(newUser).location(getLocation(newUser)).links(getUserLinks(newUser)).build();
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Status.CONFLICT).entity("Specified username is already taken.").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }
    }

    @OPTIONS
    @Path("/{userName}")
    @ApiOperation(
            value = "Returns resource options",
            notes = "This method allows the client to determine the options and/or requirements associated with a resource, or the capabilities of a server, without implying a resource action or initiating a resource retrieval.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of resource options"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getUserOptions() {
        final String header = HttpHeaders.ALLOW;
        final String value = Joiner.on(", ").join(RequestMethod.GET, RequestMethod.OPTIONS).toString();

        return Response.noContent().header(header, value).build();
    }

    @GET
    @Path("/{userName}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returns user details", notes = "Returns a complete list of users details with a date of last modification.", response = User.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of user detail", response = User.class),
            @ApiResponse(code = 404, message = "User with given username does not exist"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getUser(@ApiParam(name = "userName", value = "Alphanumeric login to application", required = true) @PathParam("userName") String userName) {
        final User user = userService.findByUserName(userName);

        if (user != null) {
            return Response.status(Status.OK).entity(user).build();
        } else {
            return Response.status(Status.NOT_FOUND).entity("User with specified username does not exist.").build();
        }
    }

    @OPTIONS
    @Path("/{userName}/avatar")
    @ApiOperation(
            value = "Returns resource options",
            notes = "This method allows the client to determine the options and/or requirements associated with a resource, or the capabilities of a server, without implying a resource action or initiating a resource retrieval.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of resource options"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getAvatarOptions() {
        final String header = HttpHeaders.ALLOW;
        final String value = Joiner.on(", ").join(RequestMethod.GET, RequestMethod.OPTIONS).toString();

        return Response.noContent().header(header, value).build();
    }

    /**
     * Updates users avatar.
     */
    @GET
    @Path("/{userName}/avatar")
    @Produces("image/png")
    @ApiOperation(value = "Returns users avatar", notes = "Provides means to download avatar based on username")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of users avatar"),
            @ApiResponse(code = 404, message = "User with given username does not exist"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getUsersAvatar(@ApiParam(name = "userName", value = "Alphanumeric login to application", required = true) @PathParam("userName") String userName) {
        final User user = userService.findByUserName(userName);

        if (user != null) {
            return Response.status(Status.OK).entity(user.getAvatar()).build();
        } else {
            return Response.status(Status.NOT_FOUND).entity("User with specified username does not exist.").build();
        }
    }

    /**
     * Updates users avatar.
     */
    @PUT
    @Path("/{userName}/avatar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiImplicitParams(@ApiImplicitParam(dataType = "file", name = "avatar", paramType = "body"))
    @ApiOperation(value = "Updates users avatar", notes = "Provides means to upload new versions of avatar based on username")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of users avatar"),
            @ApiResponse(code = 404, message = "User with given username does not exist"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response updateUsersAvatar(@ApiParam(name = "userName", value = "Alphanumeric login to application", required = true) @PathParam("userName") String userName,
            @ApiParam(access = "hidden") @FormDataParam("avatar") InputStream avatarInputStream) {
        try {
            final User user = userService.updateAvatar(userName, avatarInputStream);

            return Response.status(Status.OK).location(getAvatarLocation(user)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity("User with specified username does not exist.").build();
        } catch (IOException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Internal server error.").build();
        }
    }

    private Link[] getUserLinks(final User user) {
        final Link[] links = new Link[2];

        links[0] = Link.fromUri("users/{userName}").rel("update").type(MediaType.APPLICATION_XML).build(user.getUserName());
        links[1] = Link.fromUri("users").rel("listAll").type(MediaType.APPLICATION_XML).build();

        return links;
    }

    private URI getLocation(final User user) {
        return UriBuilder.fromUri("users/{userName}").build(user.getUserName());
    }

    private URI getAvatarLocation(final User user) {
        return UriBuilder.fromUri("users/{userName}/avatar").build(user.getUserName());
    }
}
