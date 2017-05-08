package shine.com.doorscreen.entity;

/**
 * Created by 李晓林 on 2016/11/30
 * qq:1220289215
 */

public abstract class Person {
     String title;
     String img;

    public abstract String getName() ;
   public abstract int getFlag() ;

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }
}
