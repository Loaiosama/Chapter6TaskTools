package app;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ejbs.Calculation;

import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class CalculationApp {
    @PersistenceContext
    EntityManager entityManager;

    @POST
    @Path("calc")
    public Response createCalculation(Calculation calculation) {
        try {
            int result = 0;
            switch (calculation.getOperation()) {
                case "+":
                    result = calculation.getNumber1() + calculation.getNumber2();
                    break;
                case "-":
                    result = calculation.getNumber1() - calculation.getNumber2();
                    break;
                case "*":
                    result = calculation.getNumber1() * calculation.getNumber2();
                    break;
                case "/":
                    if (calculation.getNumber2() == 0) {
                        return Response.status(400).entity("Division by zero is not allowed").build();
                    }
                    result = calculation.getNumber1() / calculation.getNumber2();
                    break;
                default:
                    return Response.status(400).entity("Invalid operation").build();
            }

            entityManager.persist(calculation);
            return Response.ok().entity("{\"Result\": " + result + "}").build();
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging purposes
            return Response.status(500).entity("Internal Server Error").build();
        }
    }


    @GET
    @Path("calculations")
    public Response getAllCalculations() {
        List<Calculation> calculations = entityManager.createQuery("SELECT c FROM Calculation c", Calculation.class)
                .getResultList();

        return Response.ok().entity(calculations).build();
    }
}