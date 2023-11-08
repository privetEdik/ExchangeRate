package kettlebell.controller.currency;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kettlebell.dao.CurrencyRepository;
import kettlebell.mapper.ResponseMapper;
import kettlebell.model.Currency;
import kettlebell.repository.JdbcCurrencyRepository;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {

	private final CurrencyRepository repository = new JdbcCurrencyRepository();
	private final static Integer SQLITE_CONSTRAINT_UNIQUE = 19;

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			List<Currency> currenciesList = repository.getAll();
			new ResponseMapper(resp).successfulOut(currenciesList);
		} catch (SQLException e) {
			new ResponseMapper(resp).errorDatabase();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String name = req.getParameter("name");
		String code = req.getParameter("code");
		String sign = req.getParameter("sign");

		String missParam = "";
		if (name == null || name.isBlank()) {
			missParam = "name";
		} else if (code == null || code.isBlank()) {
			missParam = "code";
		} else if (sign == null || sign.isBlank()) {
			missParam = "sign";
		}
		if (!missParam.equals("")) {
			new ResponseMapper(resp, missParam);
			return;
		}

		try {
			Currency currency = new Currency(code, name, sign);
			Integer addCurrencyId = repository.add(currency);
			currency.setId(addCurrencyId);
			new ResponseMapper(resp).successfulOut(currency);
		} catch (SQLException e) {

			if (e.getErrorCode() == SQLITE_CONSTRAINT_UNIQUE) {
				new ResponseMapper(resp, "Currency").alreadyExists();
				return;
			}
			new ResponseMapper(resp).errorDatabase();
		}
	}

}
