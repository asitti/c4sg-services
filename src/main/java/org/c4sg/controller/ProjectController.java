package org.c4sg.controller;

import io.swagger.annotations.*;

import org.c4sg.dto.ApplicantDTO;
import org.c4sg.dto.ApplicationDTO;
import org.c4sg.dto.CreateProjectDTO;
import org.c4sg.dto.JobTitleDTO;
import org.c4sg.dto.ProjectDTO;
import org.c4sg.exception.BadRequestException;
import org.c4sg.exception.NotFoundException;
import org.c4sg.exception.ProjectServiceException;
import org.c4sg.exception.UserProjectException;
import org.c4sg.service.ApplicationService;
import org.c4sg.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/projects")
@Api(description = "Operations about Projects", tags = "project")
@Validated
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ApplicationService applicationService;
    
    private final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Find all projects", notes = "Returns a collection of projects")
    public List<ProjectDTO> getProjects() {
    	
    	System.out.println("************** ProjectController.getProjects() **************");
    	
        return projectService.findProjects();
    }

    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Find project by ID", notes = "Returns a single project")
    public ProjectDTO getProject(
    		@ApiParam(value = "ID of project to return", required = true) @PathVariable("id") int id) {
    	
    	System.out.println("************** ProjectController.getProject()" 
                + ": id=" + id 
                + " **************");
        
    	return projectService.findById(id);
    }

    @CrossOrigin
    @RequestMapping(value = "/organization", method = RequestMethod.GET)
    @ApiOperation(value = "Find projects by Organization ID and projet status", notes = "Returns a list of projects")
    public List<ProjectDTO> getProjectsByOrganization(
    		@ApiParam(value = "ID of an organization", required = true) @RequestParam("organizationId") int organizationId,
            @ApiParam(value = "project status, A-ACTIVE, C-Closed, N-New", allowableValues = "A, C, N")	@RequestParam (required = false) String projectStatus)	
            throws ProjectServiceException {
    	
    	System.out.println("************** ProjectController.getProjectsByOrganization()"
    			+ ": organizationId=" + organizationId 
    			+ "; projectStatus=" + projectStatus 
    			+ " **************");
    	
        return projectService.findByOrganization(organizationId, projectStatus);
    }

    @CrossOrigin
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "Find ACTIVE project by keyWord or skills", notes = "Returns a collection of active projects")
    public Page<ProjectDTO> getProjects(
    		@ApiParam(value = "Keyword of the project") @RequestParam(required=false) String keyWord,
            @ApiParam(value = "Job Titles of the project") @RequestParam(required = false)  List<Integer> jobTitles,
            @ApiParam(value = "Skills of the project") @RequestParam(required = false) List<Integer> skills,
            @ApiParam(value = "Status of the project") @Pattern(regexp="[AC]")  @RequestParam(required = false) String status,
    		@ApiParam(value = "Location of the project") @Pattern(regexp="[YN]") @RequestParam(required = false) String remote,
    		@ApiParam(value = "Results page you want to retrieve (0..N)", required=false) @RequestParam(required=false) Integer page,
    		@ApiParam(value = "Number of records per page",required=false) @RequestParam(required=false) Integer size) {
    	
    	System.out.println("************** ProjectController.getProjects()"
    			+ ": keyWord=" + keyWord 
    			+ "; jobTitles=" + jobTitles 
    			+ "; skills=" + skills 
    			+ "; status=" + status 
    			+ "; remote=" + remote 
    			+ "; page=" + page 
    			+ "; size=" + size 
    			+ " **************");
    	
        return projectService.search(keyWord, jobTitles, skills, status, remote, page, size);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Add a new project")
    public Map<String, Object> createProject(
    		@ApiParam(value = "Project object to return", required = true)  @RequestBody @Valid CreateProjectDTO createProjectDTO) {

    	logger.debug("************** ProjectController.createProject()" 
                + ": createProjectDTO=" + createProjectDTO 
                + " **************");

        Map<String, Object> responseData = null;
        try {
            ProjectDTO createProject = projectService.createProject(createProjectDTO);
            responseData = Collections.synchronizedMap(new HashMap<>());
            responseData.put("project", createProject);
        } catch (Exception e) {
            logger.error("Exception -", e);
        }

        return responseData;
    }

    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Deletes a project")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(
    			@ApiParam(value = "Project id to delete", required = true) @PathVariable("id") int id) {

    	System.out.println("************** ProjectController.deleteProject()" 
                + ": id=" + id 
                + " **************");

        try {
            projectService.deleteProject(id);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update an existing project")
    public Map<String, Object> updateProject(
    		@ApiParam(value = "Updated project object", required = true) @RequestBody @Valid ProjectDTO project) {

    	System.out.println("************** ProjectController.updateProject()" 
                + ": project=" + project 
                + " **************");

        Map<String, Object> responseData = null;

        try {
            ProjectDTO updateProject = projectService.updateProject(project);
            responseData = Collections.synchronizedMap(new HashMap<>());
            responseData.put("project", updateProject);
        } catch (Exception e) {
            System.out.println(e);
        }

        return responseData;
    }
    

   /* @CrossOrigin
    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    @ApiOperation(value = "Find projects by user")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "Missing required input")})  
    public List<ApplicationDTO> getApplications(
    		@ApiParam(value = "User ID", required = true) @RequestParam Integer userId,
    		@ApiParam(value = "User project status, A-Applied, B-Bookmarked, C-Accepted, D-Declined", allowableValues = "A, B, C, D")
    		@RequestParam (required = false) String status)	
            throws ProjectServiceException {
    	
    	System.out.println("************** ProjectController.getUserProjects()" 
                  + ": UserId=" + userId 
                  + "; Status=" + status 
                  + " **************");
    	List<ApplicationDTO> applications = applicationService.getApplicationsByUser(userId, status);     	
    	return applications;
    }  */
    
    @CrossOrigin
    @RequestMapping(value = "/{id}/applicants", method = RequestMethod.GET)
    @ApiOperation(value = "Find applicants of a given project", notes = "Returns a collection of users")
    public List<ApplicantDTO> getApplicants(
    		@ApiParam(value = "ID of project", required = true) @PathVariable("id") Integer projectId) {
    	
    	System.out.println("************** UserController.getApplicants()" 
                + ": projectId=" + projectId  
                + " **************");
    	
        return applicationService.getApplicants(projectId);
    }
    
  //TODO: Replace explicit user{id} with AuthN user id. 
    @CrossOrigin
    @RequestMapping(value = "/applications", method = RequestMethod.POST)
    @ApiOperation(value = "Create new application")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "ID of project or user invalid")})       
    public ApplicationDTO createApplication(
            @ApiParam(value = "Application object", required = true) @RequestBody @Valid ApplicationDTO application) {
    	   	    
    	ApplicationDTO applicationDto = null;
        try {      
        	applicationDto = applicationService.createApplication(application);        	        	
        	
        } catch (NullPointerException e) {
            throw new NotFoundException("Error in the application");
        }
        catch (UserProjectException | BadRequestException e) {
        	throw e;
        }
        
        return applicationDto;
    } 
    
  //TODO: Replace explicit user{id} with AuthN user id. 
    @CrossOrigin
    @RequestMapping(value = "/applications", method = RequestMethod.PUT)
    @ApiOperation(value = "Create new application")
    @ApiResponses(value = {@ApiResponse(code = 404, message = "ID of project or user invalid")})       
    public ApplicationDTO updateApplication(
            @ApiParam(value = "Application object", required = true) @RequestBody @Valid ApplicationDTO application) {
    	   	    
    	ApplicationDTO applicationDto = null;
        try {        	
        	applicationDto = applicationService.updateApplication(application);         	
        } catch (NullPointerException e) {
            throw new NotFoundException("Error in the application");
        }
        catch (UserProjectException | BadRequestException e) {
        	throw e;
        }
        
        return applicationDto;
    } 
    
    

    @CrossOrigin
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ApiOperation(
    		value = "Find projects by user", 
    		notes = "Returns a list of projects searched by user ID and user-project status (applied/bookmarked). "
    				+ "If user-project status is not provided, returns all projects related to the user. "
    				+ "The projects are sorted in descending order of the timestamp they are bounded to the user.",
    		response =ProjectDTO.class , 
    		responseContainer = "List")
    	@ApiResponses(value = {@ApiResponse(code = 404, message = "Missing required input")})  
    public List<ProjectDTO> getUserProjects(
    		@ApiParam(value = "User ID", required = true) @RequestParam Integer userId,
    		@ApiParam(value = "User project status, A-Applied, B-Bookmarked, C-Accepted, D-Declined", allowableValues = "A, B, C, D")
    		@RequestParam (required = false) String userProjectStatus)	
            throws ProjectServiceException {
    	
    	System.out.println("************** ProjectController.getUserProjects()" 
                  + ": UserId=" + userId 
                  + "; Status=" + userProjectStatus 
                  + " **************");
    	List<ProjectDTO> projects = new ArrayList<ProjectDTO>();
    	if(userProjectStatus.equals("B")){
    		projects = projectService.getBookmarkByUser(userId);
    	}else{
    		projects = projectService.getApplicationByUserAndStatus(userId, userProjectStatus);	
    	}
    	return projects;
    }

    @CrossOrigin
    @RequestMapping(value = "/{id}/users/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "Create a relation between user and project")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "ID of project or user invalid")
    })
    //TODO: Replace explicit user{id} with AuthN user id.
    /*
     *  @ApiParam(value = "User project status, A-Applied, B-Bookmarked, C-Approved, D-Declined", allowableValues = "A, B, C, D", required = true)
            @RequestParam("userProjectStatus") String userProjectStatus,
            @ApiParam(value="Application comment", required=false) @RequestParam("comment") String comment,
            @ApiParam(value="Resume flag", required=true) @RequestParam("resumeFlag") String resumeFlag
     * */
    public ResponseEntity<?> createUserProject(
    		@ApiParam(value = "ID of user", required = true) @PathVariable("userId") Integer userId,
            @ApiParam(value = "ID of project", required = true) @PathVariable("id") Integer projectId,
            @ApiParam(value = "User project status, A-Applied, B-Bookmarked, C-Approved, D-Declined", allowableValues = "A, B, C, D", required = true)
            @RequestBody ApplicationDTO application) {
    	
    	System.out.println("************** ProjectController.createUserProject()" 
                + ": userId=" + userId 
                + "; projectId=" + projectId 
                + "; userProjectStatus=" + application 
                + " **************");
    	
    	
    	
        try {
        	//comment and resumeFlag will be accepted as inputs to the REST API in the future
        	String comment = application.getComment();
        	String resumeFlag = "N";
        	if(application.getResumeFlag()){
        		resumeFlag = "Y";
        	}
        	String status = application.getStatus();
        	if(status.equals("B"))
        	{
        		projectService.saveBookmark(userId, projectId);
        	}
        	else{
        		projectService.saveApplication(userId, projectId, status, comment, resumeFlag);
        	}        	
            //projectService.saveUserProject(userId, projectId, userProjectStatus);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                                      .path("/{id}/users/{userId}")
                                                      .buildAndExpand(projectId, userId, status).toUri();
            return ResponseEntity.created(location).build();
        } catch (NullPointerException e) {
            throw new NotFoundException("Error in the application");
        }
        catch (UserProjectException | BadRequestException e) {
        	throw e;
        }
    }
        
    @CrossOrigin
    @RequestMapping(value = "/{id}/image", params = "imgUrl", method = RequestMethod.PUT)
	@ApiOperation(value = "Upload a project image")
	public void saveImage(
			@ApiParam(value = "project Id", required = true) @PathVariable("id") Integer id,
			@ApiParam(value = "Image Url", required = true)	@RequestParam("imgUrl") String url) {

    	System.out.println("************** ProjectController.saveImage()" 
                + ": id=" + id 
                + "; url=" + url 
                + " **************");
    	
    	projectService.saveImage(id, url);
	}
    
    @CrossOrigin
    @RequestMapping(value="/jobTitles", method = RequestMethod.GET)
    @ApiOperation(value = "Get a list of job titles")
    public List<JobTitleDTO> getJobTitles() {
    	
    	System.out.println("************** ProjectController.getJobTitles() **************");
    	
        return projectService.findJobTitles();
    }
}

