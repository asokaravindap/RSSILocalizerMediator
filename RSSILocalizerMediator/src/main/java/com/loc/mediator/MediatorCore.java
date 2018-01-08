package com.loc.mediator;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.loc.mediator.beans.LocResult;

@Path("/localizer/")
public interface MediatorCore {

	@POST
	@Path("/setLocResults")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean setLocResults(List<LocResult> locResult);
	
	@GET
	@Path("/getRegions")
	public String getRegions();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLocResults")
	public List<LocResult> getLocResults();
}
