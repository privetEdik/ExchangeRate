package kettlebell.controller.currency;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static kettlebell.utils.Validation.isValidCurrencyCode;

import kettlebell.dao.CurrencyRepository;
import kettlebell.mapper.ResponseMapper;
import kettlebell.model.Currency;
import kettlebell.repository.JdbcCurrencyRepository;

@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String code = req.getPathInfo().replaceAll("/", "");

		if (!isValidCurrencyCode(code)) {
			new ResponseMapper(resp, "C").formatISO();
			return;
		}

		try {
			Optional<Currency> currencyOptional = currencyRepository.findByCode(code);
			if (currencyOptional.isEmpty()) {
				new ResponseMapper(resp).nothingInDatabase();
				return;
			}
			new ResponseMapper(resp).successfulOut(currencyOptional.get());

		} catch (SQLException e) {
			new ResponseMapper(resp).errorDatabase();
		}
	}
}
