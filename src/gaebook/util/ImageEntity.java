package gaebook.util;

import java.util.*;
import javax.jdo.annotations.*;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

import gaebook.library.BlogEntry;

/* 画像をデータストア内にblobとして保持 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ImageEntity {
	static Map<Integer, String> contentTypeMap = new HashMap<Integer, String>();
	static {
		contentTypeMap.put(Image.Format.BMP.ordinal(), "image/bmp");
		contentTypeMap.put(Image.Format.GIF.ordinal(), "image/gif");
		contentTypeMap.put(Image.Format.ICO.ordinal(), "image/x-icon");
		contentTypeMap.put(Image.Format.JPEG.ordinal(), "image/jpeg");
		contentTypeMap.put(Image.Format.PNG.ordinal(), "image/png");
		contentTypeMap.put(Image.Format.TIFF.ordinal(), "image/tiff");
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent private Blob      blob;
	@Persistent private Integer   formatOrdinal;
	@Persistent private String    name;
	@Persistent private BlogEntry entry;

	public Key getKey() {
		return key;
	}
	public String getKeyAsString() {
		return KeyFactory.keyToString(key);
	}
	public void setKey(Key key) {
		this.key = key;
	}

	public Blob getBlob() {
		return blob;
	}

	public int getFormatOrdinal() {
		return formatOrdinal;
	}

	public void setFormatOrdinal(int formatOrdinal) {
		this.formatOrdinal = formatOrdinal;
	}

	public void setBlob(Blob blob) {
		this.blob = blob;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BlogEntry getEntry() {
		return entry;
	}

	public void setEntry(BlogEntry entry) {
		this.entry = entry;
	}


	public String getName() {
		return name;
	}


	public byte[] getBytes() {
		return blob.getBytes();
	}

	public Image getImage() {
		return ImagesServiceFactory.makeImage(blob.getBytes());
	}

	public String getContentType() {
		return contentTypeMap.get(this.formatOrdinal);
	}


	public ImageEntity(byte[] bytes, int isbn) {
		Image image = ImagesServiceFactory.makeImage(bytes);
		Transform resize = ImagesServiceFactory.makeResize(320, 320);
		image = ImagesServiceFactory.getImagesService().applyTransform(resize, image);
		this.blob = new Blob(image.getImageData());
		this.formatOrdinal = image.getFormat().ordinal();
		this.isbn = isbn;
	}


}
