package kettlebell.mapper;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import kettlebell.exceptions.AppException;

public class RespMapper {
	private ObjectMapper mapper = new ObjectMapper();
	private HttpServletResponse response;
	private AppException exception;
	private Object object;

	public RespMapper(HttpServletResponse response, AppException exception) {
		this.response = response;
		this.exception = exception;
	}

	public RespMapper(HttpServletResponse response, Object object) {
		this.response = response;
		this.object = object;
	}

	public void getMapperErr() throws ServletException, IOException {
		response.setStatus(exception.getErrorMessage().getStatus());
		mapper.writeValue(response.getWriter(), new ObjectForOut(exception.getErrorMessage().getMassage()));
	}

	public void getMapperLuck() throws ServletException, IOException {
		mapper.writeValue(response.getWriter(), object);
	}
}
