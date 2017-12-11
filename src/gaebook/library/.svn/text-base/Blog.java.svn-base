package gaebook.blog;

import gaebook.util.PMF;
import java.util.*;
import java.util.logging.*;

import javax.jdo.*;
import javax.jdo.Query;
import javax.jdo.annotations.*;
import com.google.appengine.api.users.User;

/**
 *   ブログを表現するクラス 
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION,
        detachable = "true")
public class Blog {
    static Logger logger = Logger.getLogger(Blog.class.getName());
    static String defaultPostFix = "mail";
    
    @PrimaryKey  @Persistent
    private String key; // ブログの名前

    @Persistent  private User   owner; // ブログのオーナ

    @Persistent(mappedBy = "blog")
    @Element(dependent = "true")
    private List<BlogEntry> entries;    // ブログのエントリ

    @Persistent(serialized = "true")
    private Map<String, Integer> tagCount; // タグとそのカウント
    
    @Persistent  private String mail;           // メイル投稿のためのアドレス
    @Persistent  private Boolean useMail = false; // メイル投稿を利用するかどうか
    @Persistent  private Integer perPage = 5;   // ページあたりの表示数

    
    public Integer getPerPage() {
        return perPage;
    }
    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }
    public Boolean getUseMail() {
        return useMail;
    }
    public void setUseMail(Boolean useMail) {
        this.useMail = useMail;
    }
    
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
    public List<BlogEntry> getEntries() {
        return entries;
    }
    public void setEntries(List<BlogEntry> entries) {
        this.entries = entries;
    }

    public Map<String, Integer> getTagCount() {
        return tagCount;
    }
    public void setTagCount(Map<String, Integer> tagCount) {
        this.tagCount = tagCount;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public Blog() {
        this.entries = new ArrayList<BlogEntry>();
        this.tagCount = new HashMap<String, Integer>();
    }


    /* 指定されたブログを削除．ownerをチェックする．*/
    public static boolean deleteBlog(String blogName, User owner) {
        PersistenceManager pm = null;
        try {
            pm = PMF.get().getPersistenceManager();
            Blog blog = pm.getObjectById(Blog.class, blogName);
            if (!blog.owner.equals(owner)) {
                logger.warning("User " + owner + 
                        " tried to delete blog " + blogName +
                        " which is owned by " + blog.owner);
                return false;
            } 
            pm.deletePersistent(blog);
        } catch (JDOObjectNotFoundException e) {
            logger.warning("Blog " + blogName + " not found");
            return false;
        } finally {
            if (pm != null && !pm.isClosed())
                pm.close();
        }        
        return true;
    }

    /* 指定されたブログを返す. PersistentManager は外部で管理する． */
    public static Blog getBlog(PersistenceManager pm, String blogName){
        try {
            return pm.getObjectById(Blog.class, blogName);        
        } catch (JDOObjectNotFoundException e) {
            return null;            
        }        
    }

    /* メイルアドレスからブログを取得 */
    public static Blog getBlogFromEmail(PersistenceManager pm, 
                                        String gotAddress) {
        Query query = pm.newQuery(Blog.class);
        query.setFilter("mail == gotAddress && useMail == true");
        query.declareParameters("String gotAddress");                                
        List<Blog> blogs = (List<Blog>) query.execute(gotAddress);
        if (blogs.size() > 0)
            return blogs.get(0);
        return null;        
    }
    
    /* 指定した名前のブログを取得．もし無ければ作成． */
    public static boolean createBlogIfNotExist(String blogName, User owner){
        PersistenceManager pm = null;
        Transaction tx = null;
        try {
            pm = PMF.get().getPersistenceManager();
            tx = pm.currentTransaction();            
            tx.begin();
            try {
                pm.getObjectById(Blog.class, blogName);
                // すでに存在する．
                tx.rollback();
                return false;
            } catch (JDOObjectNotFoundException e) {
                // なかった．
                Blog blog = new Blog();
                blog.setKey(blogName);
                blog.setOwner(owner);
                
                blog.setMail( getMailPrefix(owner) + "." + defaultPostFix);
                pm.makePersistent(blog);
                try {
                    tx.commit();
                    // 更新に成功
                    return true;
                } catch (JDOCanRetryException e2) {
                    // 別のユーザが同名のブログを作成 
                    logger.log(Level.SEVERE, e2.getMessage(), e2);
                    return false;
                }
            }
        } finally {
            if (tx != null && tx.isActive())
                tx.rollback();
            if (pm != null && !pm.isClosed())
                pm.close();
        }
    }

    /* ユーザのメイルアドレスの固定Prefixを取得 */
    private static String getMailPrefix(User owner) {
        String mailAddr = owner.getEmail();  
        return mailAddr.substring(0, mailAddr.indexOf('@'));
    }
    
    /* あるブログへの投稿に用いるメイルアドレスのユーザ指定部分を取得 */
    public String getMailPostFix() {
        String prefix = getMailPrefix(getOwner());
        return getMail().substring(prefix.length() + 1);
    }
    
    /* ユーザ指定部を登録 */
    public void setMailPostFix(String postFix) {
        String prefix = getMailPrefix(getOwner());
        setMail(prefix + "." + postFix);
    }
    
    /* タグ管理マップを更新 */
    void incrementTags(PersistenceManager pm, List<String> tagList) {
        Map<String, Integer> map = getTagCount();
        for (String tag: tagList) {        
            Integer count = map.get(tag);
            if (count == null) 
                map.put(tag, 1);
            else {
                map.put(tag, count + 1);
            }
        }
    }
    
    /* タグ管理マップを更新 */
    void decrementTags(PersistenceManager pm, List<String> tagList) {
        Map<String, Integer> map = getTagCount();
        for (String tag: tagList) {        
            Integer count = map.get(tag);
            if (count == null) 
                logger.warning("tried to decrement non existing counter. ignore");
            else {
                map.put(tag, count - 1);
            }
        }
    }

    /* タグ管理マップの更新をデータストアに反映させる */
    public void updateTagList() {
        Map<String, Integer> map = getTagCount(); // backup
        this.setTagCount(null);   // 一度クリアして
        this.setTagCount(map);    // 再度セットする．これで反映される
    }
}
