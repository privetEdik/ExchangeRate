package kettlebell.dao;

import java.util.List;
import java.util.Optional;

import kettlebell.exceptions.AppException;

public interface CRUDRepository<T> { 
	 Integer add(T model) throws AppException;  
	 Optional<T> getById(Integer id) throws AppException;
	 List<T> getAll() throws AppException;
	 void put(T model) throws AppException; 
}
