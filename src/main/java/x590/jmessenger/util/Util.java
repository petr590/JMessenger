package x590.jmessenger.util;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.function.Failable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import x590.jmessenger.entity.EntityWithPicture;
import x590.jmessenger.entity.User;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public final class Util {

	private final long maxPictureSizeBytes;
	private final Object[] errorMessageFormatArgs;

	private final MessageSource messageSource;

	public Util(@Value("${jmessenger.max-picture-size}") DataSize maxPictureSize,
	            @Autowired MessageSource messageSource) {

		this.maxPictureSizeBytes = maxPictureSize.toBytes();
		this.errorMessageFormatArgs = new Object[] { maxPictureSizeBytes };
		this.messageSource = messageSource;
	}

	public static User getAuthenticatedUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();

		var principal = authentication.getPrincipal();

		if (principal instanceof User user) {
			return user;
		}

		if (principal == null) {
			throw new AccessDeniedException("");
		}

		throw new IllegalStateException("Cannot get user: " + principal + " is not instanceof User");
	}

	/**
	 * Записывает в {@code response} переданный {@code blob} с типом {@code contentType}.
	 * @apiNote Закрывает поток ответа
	 */
	public static void writeBlobToResponse(HttpServletResponse response, String contentType, Blob blob)
			throws IOException, SQLException {

		var out = response.getOutputStream();
		response.setContentType(contentType);
		blob.getBinaryStream().transferTo(out);
		out.close();
	}


	/**
	 * Записывает в {@code response} изображение или возвращает редирект на указанный адрес
	 */
	public static void writePictureOrRedirect(HttpServletResponse response, @Nullable Blob picture,
	                                          String defaultRedirectUrl) throws SQLException, IOException {
		if (picture == null) {
			response.setStatus(HttpStatus.PERMANENT_REDIRECT.value());
			response.setHeader("Location", defaultRedirectUrl);
		} else {
			writeBlobToResponse(response, "image", picture);
		}
	}


	/**
	 * @return Content-Type файла, указанный в самом файле или полученный из сигнатуры.
	 * Если Content-Type неизвестен, возвращает пустую строку
	 */
	public static String getOrGuessContentType(MultipartFile file) {
		return ObjectUtils.getIfNull(
				file.getContentType(),
				Failable.asSupplier(() -> ObjectUtils.defaultIfNull(
						URLConnection.guessContentTypeFromStream(file.getInputStream()),
						""
				))
		);
	}

	public void setPictureIfNotEmpty(@Nullable MultipartFile picture, EntityWithPicture entity,
	                                 BindingResult bindingResult) throws IOException, SQLException {

		if (picture != null && picture.getSize() > 0) {
			if (picture.getSize() > maxPictureSizeBytes) {
				bindingResult.rejectValue(null, "error.pictureFile.tooBig", errorMessageFormatArgs, null);
			}

			if (!getOrGuessContentType(picture).startsWith("image/")) {
				bindingResult.rejectValue(null, "error.pictureFile.incorrectType");
			} else {
				entity.setPicture(new SerialBlob(picture.getBytes()));
			}
		}
	}

	public String getMessage(String code) {
		return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
	}

	public String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}

	public static <T> void copyFieldIfNotNull(Consumer<T> setter, Supplier<T> getter) {
		T value = getter.get();
		if (value != null) {
			setter.accept(value);
		}
	}
}
