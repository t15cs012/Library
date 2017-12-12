package gaebook.library;

import gaebook.util.ImageEntity;
import gaebook.util.PMF;
import java.util.*;
import java.util.logging.*;

import javax.jdo.*;
import javax.jdo.Query;
import javax.jdo.annotations.*;

/**
 * ブログを表現するクラス
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class BookInfo {
	static Logger logger = Logger.getLogger(BookInfo.class.getName());
	static String defaultPostFix = "mail";

	@PrimaryKey
	@Persistent
	private int ISBN; // ISBNコード
	@Persistent
	private String name; // 書籍名
	@Persistent
	private String author; // 著者名
	@Persistent
	private String publisher; // 出版社名
	@Persistent
	private ImageEntity image; // 表紙の画像

	public BookInfo(int ISBN, String name, String author, String publisher, ImageEntity  image) {
		this.ISBN = ISBN;
		this.name = name;
		this.author = author;
		this.publisher = publisher;
		this.image = image;
	}

	public void setISBN(int ISBN) {
		this.ISBN = ISBN;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setImage(ImageEntity image) {
		this.image = image;
	}

	/* 指定された図書情報を返す. PersistentManager は外部で管理する． */
	public static BookInfo getISBN(PersistenceManager pm, int ISBN) {
		try {
			return pm.getObjectById(BookInfo.class, ISBN);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
	}
	
	public static BookInfo getName(PersistenceManager pm, String name) {
		try {
			return pm.getObjectById(BookInfo.class, name);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}

	}

	public static BookInfo getAuthor(PersistenceManager pm, String author) {
		try {
			return pm.getObjectById(BookInfo.class, author);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}

	}

	public static BookInfo getPublisher(PersistenceManager pm, String publisher) {
		try {
			return pm.getObjectById(BookInfo.class, publisher);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}

	}

}
