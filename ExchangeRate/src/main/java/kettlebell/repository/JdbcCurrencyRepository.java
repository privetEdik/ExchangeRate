package kettlebell.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import kettlebell.dao.CurrencyRepository;
import kettlebell.exceptions.AppException;
import kettlebell.exceptions.ErrorMessage;
import kettlebell.model.Currency;

public class JdbcCurrencyRepository extends Connector implements CurrencyRepository {
	private final static Integer SQLITE_CONSTRAINT_UNIQUE = 19;

	@Override
	public Optional<Currency> getById(Integer id) throws AppException {
		//@formatter:off
		final String sql = "SELECT id, code, full_name, sign "
							  + "FROM currencies WHERE id = ?;";
		//@formatter:on
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return Optional.of(getCurrency(resultSet));
			}
		} catch (SQLException e) {
			throw new AppException(ErrorMessage.SOMETHING_DATABASE);
		}
		return Optional.empty();
	}

	@Override
	public List<Currency> getAll() throws AppException {
		List<Currency> list = new ArrayList<>();
		final String sql = "SELECT id, code, full_name, sign FROM currencies;";
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				list.add(getCurrency(resultSet));
			}
		} catch (SQLException e) {
			throw new AppException(ErrorMessage.SOMETHING_DATABASE);
		}
		return list;
	}

	@Override
	public Integer add(Currency model) throws AppException {
		final String sql = "INSERT INTO currencies(code,full_name,sign) VALUES(?,?,?);";
		Integer id = 0;
		//@formatter:off
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, model.getCode());
			statement.setString(2, model.getFullName());
			statement.setString(3, model.getSign());
			statement.executeUpdate();
			PreparedStatement statementKey = connection.prepareStatement("SELECT last_insert_rowid();");
			ResultSet resultSet = statementKey.executeQuery();
			if (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			//@formatter:on
		} catch (SQLException e) {
			if (e.getErrorCode() == SQLITE_CONSTRAINT_UNIQUE) {
				throw new AppException(ErrorMessage.CURRENCY_ALREADY_EXISTS);
			}
			throw new AppException(ErrorMessage.SOMETHING_DATABASE);
		}
		return id;
	}

	@Override
	public void put(Currency model) throws AppException {
		final String sql = "UPDATE currencies SET code=?,full_name=?,sign=? " + "WHERE id=?";
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, model.getCode());
			preparedStatement.setString(2, model.getFullName());
			preparedStatement.setString(3, model.getSign());
			preparedStatement.setInt(4, model.getId());

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new AppException(ErrorMessage.SOMETHING_DATABASE);
		}
	}

	@Override
	public Optional<Currency> findByCode(String code) throws AppException {
		final String sql = "SELECT * FROM currencies WHERE code = ?";

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, code);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return Optional.of(getCurrency(resultSet));
			}

		} catch (SQLException e) {
			throw new AppException(ErrorMessage.SOMETHING_DATABASE);
		}
		return Optional.empty();
	}

	private static Currency getCurrency(ResultSet resultSet) throws SQLException {
		//@formatter:off
		return new Currency(resultSet.getInt("id"),
								  resultSet.getString("code"),
								  resultSet.getString("full_name"),
								  resultSet.getString("sign"));
		//@formatter:on
	}
}
