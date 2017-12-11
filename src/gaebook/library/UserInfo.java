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
public class UserInfo {
	static Logger logger = Logger.getLogger(BookInfo.class.getName());
	static String defaultPostFix = "mail";

	@PrimaryKey
	@Persistent
	private String userID; // ユーザID
	@Persistent
	private String password; // 利用者の氏名
	@Persistent
	private String name; // 利用者の氏名
	@Persistent
	private ; // 著者名
	@Persistent
	private String publisher; // 出版社名
	@Persistent
	private ImageEntity image; // 表紙の画像

	public UserInfo(int ISBN, String name, String author, String publisher, ImageEntity  image) {
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

	/* 指定されたISBNコードを返す. PersistentManager は外部で管理する． */
	public static int getISBN(PersistenceManager pm, int ISBN) {
		try {
			return pm.getObjectById(BookInfo.class, ISBN);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}
	}
	public String getName(PersistenceManager pm, String name) {
		try {
			return pm.getObjectById(BookInfo.class, name);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}

	}

	public String getAuthor(PersistenceManager pm, String author) {
		try {
			return pm.getObjectById(BookInfo.class, author);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}

	}

	public String getPublisher(PersistenceManager pm, String publisher) {
		try {
			return pm.getObjectById(BookInfo.class, publisher);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}

	}

	public ImageEntity getImage(PersistenceManager pm, String) {
		try {
			return pm.getObjectById(BookInfo.class, ISBN);
		}
		catch (JDOObjectNotFoundException e) {
			return null;
		}

	}
}
