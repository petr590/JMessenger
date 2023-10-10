package x590.jmessenger.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.SQLException;

@Embeddable
@Table(name = "files")
@Getter @Setter @ToString
@NoArgsConstructor
public class File {

	@Column(name = "file_name")
	private String name;

	@Column(name = "file_type")
	@JsonIgnore
	private String contentType;

	@Transient
	@JsonIgnore
	private MediaType mediaType;

	@JsonIgnore
	private Blob data;

	public File(MultipartFile multipartFile) throws SQLException, IOException {
		this.name = multipartFile.getOriginalFilename();
		this.data = new SerialBlob(multipartFile.getBytes());

		this.contentType = multipartFile.getContentType();

		if (contentType == null) {
			contentType = URLConnection.guessContentTypeFromStream(data.getBinaryStream());
		}
	}

	public MediaType getType() {
		var mediaType = this.mediaType;

		if (mediaType != null) {
			return mediaType;
		}

		return this.mediaType = MediaType.byContentType(contentType);
	}

	@AllArgsConstructor
	public enum MediaType {
		IMAGE("image/"),
		AUDIO("audio/"),
		VIDEO("video/"),
		OTHER("");

		private static final MediaType[] VALUES = values();

		private final String beginning;

		private final String jsonValue = name().toLowerCase();

		@JsonValue
		public String jsonValue() {
			return jsonValue;
		}

		public static MediaType byContentType(String contentType) {
			if (contentType != null) {
				for (var mediaType : VALUES) {
					if (contentType.startsWith(mediaType.beginning)) {
						return mediaType;
					}
				}
			}

			return OTHER;
		}
	}
}
