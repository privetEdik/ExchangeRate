package kettlebell.controller.currency;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import kettlebell.dao.CurrencyRepository;
import kettlebell.exceptions.AppException;
import kettlebell.exceptions.ErrorMessage;
import kettlebell.mapper.RespMapper;
import kettlebell.model.Currency;
import kettlebell.repository.JdbcCurrencyRepository;
import static kettlebell.utils.Validation.isValidCurrencyCode;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {

	private final CurrencyRepository repository = new JdbcCurrencyRepository();
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			List<Currency> currenciesList = repository.getAll();
			new RespMapper(resp, currenciesList).getMapperLuck();
		} catch (AppException e) {
			new RespMapper(resp, e).getMapperErr();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String name = req.getParameter("name");
		String code = req.getParameter("code");
		String sign = req.getParameter("sign");

		if (name == null || name.isBlank() || code == null || code.isBlank() || sign == null || sign.isBlank()) {
			new RespMapper(resp, new AppException(ErrorMessage.FIELD_MISS)).getMapperErr();
			return;
		}

		if (!isValidCurrencyCode(code)) {
			new RespMapper(resp, new AppException(ErrorMessage.CURRENCY_STANDART)).getMapperErr();
			return;
		}

		try {
			Currency currency = new Currency(code, name, sign);
			Integer addCurrencyId = repository.add(currency);
			currency.setId(addCurrencyId);
			new RespMapper(resp, currency).getMapperLuck();
		} catch (AppException e) {
			new RespMapper(resp, e).getMapperErr();

		}
	}

}
