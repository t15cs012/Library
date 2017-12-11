package gaebook.blog;

import gaebook.util.ImageEntity;

import java.util.*;
import javax.jdo.*;
import javax.jdo.annotations.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Key;


@PersistenceCapable(identityType = IdentityType.APPLICATION,
        detachable = "true")
public class BlogEntry {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent private Date              date;
    @Persistent private String            title;
    @Persistent private Text              text;
    @Persistent private List<String>      tags;

    @Persistent(mappedBy = "entry")
    @Element(dependent = "true")
    private List<ImageEntity> images;


    /** uplink */
    @Persistent private Blog         blog;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public List<ImageEntity> getImages() {
        return images;
    }

    public void setImages(List<ImageEntity> images) {
        this.images = images;
    }

    public String getKeyInString(){
        return KeyFactory.keyToString(key);
    }
    
    public static BlogEntry getEntry(PersistenceManager pm, String entryId){
        try {
            Key k = KeyFactory.stringToKey(entryId);
            return pm.getObjectById(BlogEntry.class, k);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (JDOObjectNotFoundException e) {
            return null;            
        }        
    }

    public Object getTagsString() {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String tag: tags) {
            if (first) 
                first = false;
            else
                sb.append(",");
            sb.append(tag);
        }
        return sb.toString();
    }
    
    
    
}
