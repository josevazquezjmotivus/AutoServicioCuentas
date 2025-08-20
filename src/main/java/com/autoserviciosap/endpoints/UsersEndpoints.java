package com.autoserviciosap.endpoints;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.autoserviciosap.logic.UsersLogic;
import com.autoserviciosap.model.User;

@Path("users")
public class UsersEndpoints {

	@Inject
	private UsersLogic usersLogic;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerUsuarios() {
		List<User> usuarios = usersLogic.obtenerUsuarios();
		usuarios.forEach(u -> {
			u.setPassword(null);
			u.setPermissions(null);
		});
		return Response.ok(usuarios).build();
	}

    // @DELETE
    @POST
    @Path("delete-method")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response borrarUsuarios(@QueryParam("username") String[] usernames) {
		usersLogic.borrarUsuarios(usernames);
		return Response.ok().build();
	}

	@GET
	@Path("{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtenerUsuario(@PathParam("username") String username) {
		User usuario = usersLogic.obtenerUsuarioPorUsername(username);
		usuario.setPassword(null);
		return Response.ok(usuario).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response crearUsuario(User user) {
		User usuario = usersLogic.crearUsuario(user);
		usuario.setPassword(null);
		return Response.ok(usuario).build();
	}

	// @PUT
    // @Path("{username}")
    @POST
	@Path("{username}/put-method")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response actualizarUsuario(@PathParam("username") String username, User user) {
		User usuario = usersLogic.actualizarUsuario(username, user);
		usuario.setPassword(null);
		return Response.ok(usuario).build();
	}

	// @DELETE
    // @Path("{username}")
    @POST
	@Path("{username}/delete-method")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eliminarUsuario(@PathParam("username") String username) {
		usersLogic.eliminarUsuario(username);
		return null;
	}

	// @PUT
    // @Path("/{username}/password")
    @POST
    @Path("/{username}/password/method-put")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response cambiarContraseña(@PathParam("username") String username, JsonObject json) {
		if (!json.containsKey("oldPassword")) return Response.status(400).entity("Falta campo \"oldPassword\"").build();
		if (!json.containsKey("newPassword")) return Response.status(400).entity("Falta campo \"newPassword\"").build();
		String oldPassword = json.getString("oldPassword");
		String newPassword = json.getString("newPassword");
		usersLogic.cambiarContraseña(username, oldPassword, newPassword);
		return null;
	}

	// @PUT
    // @Path("/{username}/email")
    @POST
    @Path("/{username}/email/method-put")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response cambiarEmail(@PathParam("username") String username, JsonObject json) {
		if (!json.containsKey("password")) return Response.status(400).entity("Falta campo \"password\"").build();
		if (!json.containsKey("newEmail")) return Response.status(400).entity("Falta campo \"newEmail\"").build();
		String password = json.getString("password");
		String email = json.getString("newEmail");
		usersLogic.cambiarEmail(username, password, email);
		return null;
	}

}
