package kettlebell.controller.currency;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static kettlebell.utils.Validation.isValidCurrencyCode;

import kettlebell.dao.CurrencyRepository;
import kettlebell.exceptions.AppException;
import kettlebell.exceptions.ErrorMessage;
import kettlebell.mapper.RespMapper;
import kettlebell.model.Currency;
import kettlebell.repository.JdbcCurrencyRepository;

@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final CurrencyRepository currencyRepository = new JdbcCurrencyRepository();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String code = req.getPathInfo().replaceAll("/", "");

		if (code == null || code.isBlank()) {
			new RespMapper(resp, new AppException(ErrorMessage.CURRENCY_MISS)).getMapperErr();
		}

		if (!isValidCurrencyCode(code)) {
			new RespMapper(resp, new AppException(ErrorMessage.CURRENCY_STANDART)).getMapperErr();
			return;
		}

		try {
			Optional<Currency> currencyOptional = currencyRepository.findByCode(code);
			if (currencyOptional.isEmpty()) {
				new RespMapper(resp, new AppException(ErrorMessage.CURRENCY_NOT_FOUND)).getMapperErr();
				return;
			}
			new RespMapper(resp, currencyOptional.get()).getMapperLuck();

		} catch (AppException e) {
			new RespMapper(resp, e);
		}
	}
}
