package x590.jmessenger.entity;

import java.sql.Blob;

public interface EntityWithPicture {
	Blob getPicture();

	void setPicture(Blob picture);
}
