package com.jakubstas.swagger.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.base.Joiner;
import com.google.common.net.HttpHeaders;
import com.jakubstas.swagger.model.Employee;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Api(value = "/employees", description = "Endpoint for employee listing")
@Path("/employees")
public class EmployeeEndpoint {

    private List<Employee> employees = new ArrayList<Employee>();

    {
        final Employee employee = new Employee();
        employee.setEmployeeNumber(1);
        employee.setFirstName("Jakub");
        employee.setSurname("Stas");

        employees.add(employee);
    }

    @OPTIONS
    @ApiOperation(
            value = "Returns resource options",
            notes = "This method allows the client to determine the options and/or requirements associated with a resource, or the capabilities of a server, without implying a resource action or initiating a resource retrieval.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of resource options"), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getProductsOptions() {
        final String header = HttpHeaders.ALLOW;
        final String value = Joiner.on(", ").join(RequestMethod.GET, RequestMethod.OPTIONS).toString();

        return Response.noContent().header(header, value).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Returns all employees", notes = "Returns a complete list of employees.", responseContainer = "array", response = Employee.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful retrieval of all employees", response = Employee.class), @ApiResponse(code = 500, message = "Internal server error") })
    public Response getEmployees() {
        return Response.ok(employees).build();
    }
}
