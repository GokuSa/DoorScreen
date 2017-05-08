package shine.com.doorscreen.database;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import shine.com.doorscreen.entity.DoctorInfo;
import shine.com.doorscreen.entity.DripInfo;
import shine.com.doorscreen.entity.Elements;
import shine.com.doorscreen.entity.NurseInfo;
import shine.com.doorscreen.entity.PatientInfo;
import shine.com.doorscreen.entity.PushMessage;
import shine.com.doorscreen.entity.PushMission;
import shine.com.doorscreen.util.LogUtil;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Administrator on 2016/9/8.
 * 使用单例模式可以全局访问，可以防止数据库没关闭导致内存泄漏
 * getReadableDatabase()" and "getWriteableDatabase()" 返回相同的对象，除非在存储空间不足时
 *sqlite3 data/data/shine.com.doorscreen/databases/DoorScreen.db
 */
public class DoorScreenDataBase extends SQLiteOpenHelper{
    private static final String TAG = "DoorScreenDataBase";
    private static final int VERSION=1;
    private static final String NAME="DoorScreen.db";
    private static final String TABLE_MARQUEE="marquee";
    private static final String TABLE_MARQUEE_TIME="marquee_time";
    private static final String TABLE_MEDIA="media";
    private static final String TABLE_MEDIA_TIME="media_time";
    private static final String TABLE_DRIP="drip";
    private static final String TABLE_DOCTOR="doctor";
    private static final String TABLE_PATIENT="patient";

    private static DoorScreenDataBase sDoorScreenDataBase;
    private DoorScreenDataBase(Context context) {
        super(context, NAME, null, VERSION);
    }
    public static synchronized DoorScreenDataBase getInstance(Context context){
        if (sDoorScreenDataBase == null) {
            sDoorScreenDataBase=new DoorScreenDataBase(context.getApplicationContext());
        }
        return sDoorScreenDataBase;
    }
    //跑马灯
    private static final String CREATE_TABLE_MARQUEE=
            "create table marquee (_id integer primary key autoincrement," +
                    "id integer  not null ,message text,speed integer,status integer,unique (id) on conflict replace)";
    private static final String CREATE_TABLE_MARQUEE_TIME = "create table marquee_time (_id integer primary key,id integer  not null," +
            "startdate text,starttime text,stopdate text,stoptime text,status integer)";
    //多媒体
    private static final String CREATE_TABLE_MEDIA = "create table media (_id integer primary key," +
            "id integer,type integer,name text,path text,src text,life integer,status integer)";

    //多媒体播放时间
    private static final String CREATE_TABLE_MEDIA_TIME = "create table media_time (_id integer primary key," +
            "id integer  not null,allday integer,tasktype integer,orderno integer,startdate text," +
            "starttime text,stopdate text,stoptime text,status integer)";
    //输液信息
    private static final String CREATE_TABLE_DRIP="create table drip (_id integer primary key,id integer," +
            "name text,age text,bedno text,start integer,begin text,current integer,total integer,left integer)";
    //医生信息
    private static final String CREATE_TABLE_DOCTOR = "create table doctor(_id integer primary key,name text," +
            "title text,img text,flag integer)";
    //患者信息
    private static final String CREATE_TABLE_PATIENT = "create table patient (_id integer primary key,bedno text,name text)";
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MARQUEE);
        db.execSQL(CREATE_TABLE_MARQUEE_TIME);
        db.execSQL(CREATE_TABLE_MEDIA);
        db.execSQL(CREATE_TABLE_MEDIA_TIME);
//        db.execSQL(CREATE_TABLE_DOCTOR);
//        db.execSQL(CREATE_TABLE_PATIENT);
//        db.execSQL(CREATE_TABLE_DRIP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion!=newVersion ){
            db.execSQL("drop table if exists marquee");
            db.execSQL("drop table if exists marquee_time");
            db.execSQL("drop table if exists media");
            db.execSQL("drop table if exists media_time");
//            db.execSQL("drop table if exists drip");
//            db.execSQL("drop table if exists doctor");
//            db.execSQL("drop table if exists patient");
            onCreate(db);
        }
    }

    public void insertMarquee(PushMessage pushMessage) {
        List<PushMessage.PlayTime> playTime = pushMessage.getPlayTimes();
        if (playTime == null||playTime.size()==0) {
            LogUtil.e(TAG,"invalid play times");
            return;
        }
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            //先删除要插入的旧的跑马灯id
            int delete = database.delete(TABLE_MARQUEE_TIME, "id=?", new String[]{String.valueOf(pushMessage.getId())});
            LogUtil.d(TAG, "delete old marque time id:" + delete);
            //插入新的跑马灯时间
            for (PushMessage.PlayTime time : playTime) {
                if (time == null) {
                    LogUtil.e(TAG,"play time is null");
                    continue;
                }
                LogUtil.d(TAG, "insert  marque time :" +time.getStart());
                ContentValues contentValues=new ContentValues();
                contentValues.put("id", pushMessage.getId());
                contentValues.put("startdate", pushMessage.getStartdate());
                contentValues.put("stopdate",pushMessage.getStopdate());
                contentValues.put("starttime", time.getStart());
                contentValues.put("stoptime", time.getStop());
                contentValues.put("status", 0);
                database.insertOrThrow(TABLE_MARQUEE_TIME, null, contentValues);
            }
            //插入新的跑马灯信息，如果id相同直接替代
            LogUtil.d(TAG, "insert marquee:" +pushMessage.getId());
            ContentValues contentValues=new ContentValues();
            contentValues.put("id",pushMessage.getId());
            contentValues.put("message",pushMessage.getMessage());
            contentValues.put("speed",pushMessage.getSpeed());
            contentValues.put("status",0);
            database.insertOrThrow(TABLE_MARQUEE, null, contentValues);

            database.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e(TAG,"fail to delete or insert marquee");
        } finally {
            database.endTransaction();
        }
    }

    /**
     *
     * @param pushMessage 要保存的数据
     *如果要保持的对象存在就更新，否则就插入
     */
    public void updateOrInsert(PushMessage pushMessage) {
        LogUtil.d(TAG, "begin to update marquee");
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues=new ContentValues();
            contentValues.put("id",pushMessage.getId());
            contentValues.put("message",pushMessage.getMessage());
            contentValues.put("status",0);
            int row = writableDatabase.update(TABLE_MARQUEE, contentValues, "id=?", new String[]{String.valueOf(pushMessage.getId())});
            if(row==1){
                //更新成功,设置交易成功
                writableDatabase.setTransactionSuccessful();
            }else{
                //更新失败，说明信息不存在，执行插入
                long l = writableDatabase.insertOrThrow(TABLE_MARQUEE, null, contentValues);
                LogUtil.d(TAG, "rowId:" + l);
                writableDatabase.setTransactionSuccessful();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "fail to insert or undate ");
        }finally {
            writableDatabase.endTransaction();
        }
    }

    /**
     * 停止跑马灯，更改时间表status为-1，因为检索时是根据时间表和status获取id再检索跑马灯的
     * @param id 跑马灯id
     */
    public void stopMarquee(int id) {
        LogUtil.d(TAG,"begin to stop marquee");
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("status",-1);
            int update = writableDatabase.update(TABLE_MARQUEE_TIME, contentValues, "id=?", new String[]{String.valueOf(id)});
            LogUtil.d(TAG,"update "+update);
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.d(TAG,"fail to stop marquee");
        } finally {
            writableDatabase.endTransaction();
        }

    }

    /**
     *
     * @param currentDate
     * @param currentTime
     * @return 根据当前时间检索跑马灯id,后台启动后每分钟建设一次
     */
    public String queryMarqueeIds(String currentDate, String currentTime) {
        LogUtil.d(TAG,"begin to query MarqueeIds");
        SQLiteDatabase database = getWritableDatabase();
        StringBuilder stringBuilder = new StringBuilder();
        String sql=String.format(Locale.CHINA,"select * from %s where time('%s')" +
                " between time(starttime) and time(stoptime) " +
                "and date('%s') between date(startdate) and date(stopdate) and status=0 order by id asc",TABLE_MARQUEE_TIME,currentTime,currentDate);
        Cursor cursor = database.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                stringBuilder.append(",").append(id);
            }
        } catch (Exception e) {
            LogUtil.d(TAG,"fail to query MarqueeIds");
        } finally {
            if (cursor != null&&!cursor.isClosed()) {
                cursor.close();
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(0);
        }
        LogUtil.d(TAG,"marquee ids "+stringBuilder.toString());
        return stringBuilder.toString();
    }

    public List<String> queryMarquee(String ids) {
        List<String> messages= new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String sql = String.format(Locale.CHINA, "select * from %s where status=0 and id in (%s)", TABLE_MARQUEE, ids);
        Cursor cursor = database.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                String message = cursor.getString(cursor.getColumnIndex("message"));
                messages.add(message);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "error while get  message from marquee");
        } finally {
            if (cursor != null&&!cursor.isClosed()) {
                cursor.close();
            }
        }

        return messages;
    }

    public List<PushMessage> getMarquee() {
        LogUtil.d(TAG,"begin to retrieve marquee");
        List<PushMessage> messageList= new ArrayList<>();;
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String sql=String.format("select * from %s where status=0",TABLE_MARQUEE);
        Cursor cursor=readableDatabase.rawQuery(sql,null);
        try {
            while (cursor.moveToNext()) {
                PushMessage pushMessage=new PushMessage();
                int id=cursor.getInt(cursor.getColumnIndex("id"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                pushMessage.setId(id);
                pushMessage.setMessage(message);
                messageList.add(pushMessage);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "error while getTitle message from marquee");
        } finally {
            if (cursor != null&&!cursor.isClosed()) {
                cursor.close();
            }
        }
        return messageList;
    }

    public void deleteMarquee(int id){
        SQLiteDatabase writableDatabase = getWritableDatabase();
        int rowsMarqueedelete = writableDatabase.delete(TABLE_MARQUEE, "id =?", new String[]{String.valueOf(id)});
        int rowsMarqueeTimedelete = writableDatabase.delete(TABLE_MARQUEE_TIME, "id =?", new String[]{String.valueOf(id)});
        LogUtil.d(TAG, "delete marquee by id:" + rowsMarqueedelete);
        LogUtil.d(TAG, "delete marquee time by id:" + rowsMarqueeTimedelete);
    }

    /**
     * 插入宣教信息多时间播放段
     */
    public void insertMediaTime(PushMission pushMission) {
        List<PushMission.PlayTime> playTime = pushMission.getPlayTime();
        if (playTime == null||playTime.size()==0) {
            LogUtil.e(TAG,"invalid play times");
            return;
        }
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            //先删除要插入的旧播单信息
            int delete = database.delete(TABLE_MEDIA_TIME, "id=?", new String[]{String.valueOf(pushMission.getId())});
            LogUtil.d(TAG, "delete old media time id:" + delete);
            for (PushMission.PlayTime time : playTime) {
                if (time == null) {
                    LogUtil.e(TAG,"play time is null");
                    continue;
                }
                LogUtil.d(TAG, "insert play media time :" + time.getStart());
                ContentValues contentValues=new ContentValues();
                contentValues.put("id", pushMission.getId());
                contentValues.put("allday", pushMission.getAllday());
                contentValues.put("tasktype", pushMission.getTasktype());
                contentValues.put("orderno", pushMission.getOrderno());
                contentValues.put("startdate", pushMission.getStartdate());
                contentValues.put("stopdate",pushMission.getStopdate());
                contentValues.put("starttime", time.getStart());
                contentValues.put("stoptime", time.getStop());
                contentValues.put("status", 0);
                database.insertOrThrow(TABLE_MEDIA_TIME, null, contentValues);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e(TAG,"fail to delete or insert media time");
        } finally {
            database.endTransaction();
        }
    }
    /*
    *   有些播单下载失败，导致视频为空，所以此时删除无效的播单
     */
    public void deleteInValidMediaTime(String ids) {
        LogUtil.d(TAG, "beging to delete invalid media time");
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
          database.execSQL(String.format(Locale.CHINA,"delete from  %s where id in (%s)", TABLE_MEDIA, ids));
          database.execSQL(String.format(Locale.CHINA,"delete from  %s where id in (%s)", TABLE_MEDIA_TIME, ids));
            database.setTransactionSuccessful();
            Log.d(TAG, "success ");
        } catch (Exception e) {
            LogUtil.d(TAG, e.toString());
        } finally {
            database.endTransaction();
        }
    }
    /**
     * 根据当前时间先从媒体播发时刻表找到播单号，
     * 再根据播单添加宣教元素
     */
    public String queryMediaIds(String currentDate, String currentTime) {
        LogUtil.d(TAG,"begin to query media");
        SQLiteDatabase database = getWritableDatabase();
        StringBuilder stringBuilder = new StringBuilder();
        String sql=String.format(Locale.CHINA,"select id from %s where time('%s')" +
                " between time(starttime) and time(stoptime) " +
                "and date('%s') between date(startdate) and date(stopdate) and status=0 order by id asc",TABLE_MEDIA_TIME,currentTime,currentDate);
        Cursor cursor = database.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                stringBuilder.append(",").append(id);
            }
        } catch (Exception e) {
            LogUtil.d(TAG,"fail to query media");
        } finally {
            if (cursor != null&&!cursor.isClosed()) {
                cursor.close();
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(0);
        }
        LogUtil.d(TAG,"ids "+stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 批量插入多媒体元素
     * @param elementsList
     */
    public void bulkInsertMedia(int id,List<Elements> elementsList) {
        if (elementsList == null||elementsList.size()==0) {
            LogUtil.d(TAG, "invalid data");
            return;
        }
        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        int rowInsert=0;
        try {
            //先删除此播单的数据，再重新插入
            if (id!=-1) {
                int   delete = database.delete(TABLE_MEDIA, "id=? ", new String[]{String.valueOf(id)});
                LogUtil.d(TAG, "delete old media by id:" + delete);
            }
            for (Elements element : elementsList) {
                if (element == null) {
                    LogUtil.d(TAG, "invalid element");
                    continue;
                }
                LogUtil.d(TAG, "insert media element:" +element.getName());
                long _id=-1;
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", element.getId());
                    contentValues.put("type", element.getType());
                    contentValues.put("life", element.getLife());
                    contentValues.put("name", element.getName());
                    contentValues.put("path", element.getPath());
                    contentValues.put("src", element.getSrc());
                    //此时还未下载成功
                    contentValues.put("status",-1);
                    _id=database.insertOrThrow(TABLE_MEDIA, null, contentValues);
                } catch (SQLException e) {
                    LogUtil.e(TAG,"fail to bulk insert media");
                }
                if (_id != -1) {
                    rowInsert++;
                }
            }
            if (rowInsert>0) {
                database.setTransactionSuccessful();
            }
        } finally {
            database.endTransaction();
        }

    }

    //当素材下载成功，更新状态
    public void activateMediaStatus(int id) {
        Log.d(TAG, "activateMediaStaus() ");
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("status",0);
            int update = writableDatabase.update(TABLE_MEDIA, contentValues, "id=?", new String[]{String.valueOf(id)});
            LogUtil.d(TAG, "update with id:" + update);
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.d(TAG, "fail to update");
            e.printStackTrace();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    /**
     *
     * @param id 播單id
     *  根据id更新播单status为-1，即为停止状态
     */
    public void updateMediaStaus(int id) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("status",-1);
            int update = writableDatabase.update(TABLE_MEDIA_TIME, contentValues, "id=?", new String[]{String.valueOf(id)});
            LogUtil.d(TAG, "update with id:" + update);
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.d(TAG, "fail to update");
            e.printStackTrace();
        } finally {
            writableDatabase.endTransaction();
        }
    }


    /**
     * 查询媒体库文件
     * @param ids 播单
     * @return 多媒体路径
     */
    public   List<Elements> queryMedia(String ids) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        List<Elements> paths=new ArrayList<>();
        String sql=String.format(Locale.CHINA,"select * from %s  where status=0 and id in (%s) ",TABLE_MEDIA,ids);
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                Elements elements=new Elements();
                String path = cursor.getString(cursor.getColumnIndex("path"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                elements.setPath(path);
                elements.setType(type);
                paths.add(elements);
            }
        } catch (Exception e) {
            LogUtil.d(TAG, "error to getTitle media path");
        } finally {
            if (cursor != null&&!cursor.isClosed()) {
                cursor.close();
            }
        }
        return paths;
    }

   /**
     * 根据播单删除 素材 和播发时间
     * @param id 播单id
     */
    public void deleteMedia(int id) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            int   mediaDelete = writableDatabase.delete(TABLE_MEDIA, "id=? ", new String[]{String.valueOf(id)});
            int   mediaTimeIdDelete = writableDatabase.delete(TABLE_MEDIA_TIME, "id=? ", new String[]{String.valueOf(id)});
            LogUtil.d(TAG, "delete from media:" + mediaDelete);
            LogUtil.d(TAG, "delete from media time:" + mediaTimeIdDelete);
                writableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.d(TAG, "fail to delete media");

        } finally {
            writableDatabase.endTransaction();
        }
    }

    /**
     * 插入输液信息
     * 考虑到病人出院数据可能残留所以每次更新都
     * 先清空表内容，重新插入，
     * @param dripInfos 输液信息
     */
    @Deprecated
    public void insertDrip(List<DripInfo.Infusionwarnings> dripInfos) {
        LogUtil.d(TAG,"begin to insert drip info");
        if (dripInfos == null || dripInfos.size() == 0) {
            LogUtil.d(TAG,"invaid drip info");
            return;
        }
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            //先清空表
            writableDatabase.execSQL(String.format("delete from %s", TABLE_DRIP));
            ContentValues contentValues = new ContentValues();
            for (DripInfo.Infusionwarnings dripInfo : dripInfos) {
                contentValues.put("id", dripInfo.getPatientid());
                contentValues.put("name", dripInfo.getPatientname());
                contentValues.put("age", dripInfo.getPatientage());
                contentValues.put("bedno", dripInfo.getBedno());
                //输液开始时间的毫秒值
                contentValues.put("start",dripInfo.getStart());
                //输液开始时间的格式化字符串
                contentValues.put("begin", dripInfo.getBegin());
                //本机当前时间
                contentValues.put("current", currentTimeMillis());
                //总时长，用来显示输液袋的动画
                contentValues.put("total", dripInfo.getTotal());
                contentValues.put("left", dripInfo.getLeft());
                writableDatabase.insertOrThrow(TABLE_DRIP, null, contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } catch (SQLException e) {
            LogUtil.e(TAG,e.toString());
        } finally {
            writableDatabase.endTransaction();
        }
    }
    //清空输液信息
    @Deprecated
    public void clearDripInfo() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL(String.format("delete from %s", TABLE_DRIP));
    }
    @Deprecated
    public void deleteDripInfo(String bedNO) {
        LogUtil.d(TAG, "begin to delete drip info"+bedNO);
        SQLiteDatabase database = getWritableDatabase();
        int delete = database.delete(TABLE_DRIP, "bedno=?", new String[]{bedNO});
        Log.d(TAG, "delete drip info result:" + delete);

    }

    @Deprecated
    public List<DripInfo.Infusionwarnings> queryDripInfo() {
        LogUtil.d(TAG,"begin to query drip info");
        List<DripInfo.Infusionwarnings> dripInfos = new ArrayList<>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String sql = String.format("select * from %s order by %s desc", TABLE_DRIP,"left");
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                DripInfo.Infusionwarnings dripInfo=new DripInfo.Infusionwarnings();
                String bedno=cursor.getString(cursor.getColumnIndex("bedno"));
                int total=cursor.getInt(cursor.getColumnIndex("total"));
                long current=cursor.getLong(cursor.getColumnIndex("current"));
                //计算过了多少分钟
                int margin = (int) ((System.currentTimeMillis() - current)/(60*1000));
                if (margin >= total) {
                    dripInfo.setLeft(0);
                }else{
                    dripInfo.setLeft(total-margin);
                }
                dripInfo.setTotal(total);
                dripInfo.setBedno(bedno);
                dripInfo.setPatientage(cursor.getString(cursor.getColumnIndex("age")));
                dripInfo.setPatientname(cursor.getString(cursor.getColumnIndex("name")));
                dripInfo.setPatientid(cursor.getInt(cursor.getColumnIndex("id")));
                dripInfo.initilize(cursor.getString(cursor.getColumnIndex("begin")));
                dripInfos.add(dripInfo);
            }
        } catch (Exception e) {
            LogUtil.d(TAG,"fail to query drip info ");

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return dripInfos;
    }

    /**
     * 插入病人信息，由于信息是全部更新，所以先清表
     * @param patients
     */
    @Deprecated
    public void insertPatient(List<PatientInfo.Patient> patients) {
        LogUtil.d(TAG, "begin to insert patient info");
        if (patients == null || patients.size() == 0) {
            LogUtil.d(TAG, "invalid patient info ");
            return;
        }
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.execSQL(String.format("delete from %s",TABLE_PATIENT));
            ContentValues contentValues = new ContentValues();
            for (PatientInfo.Patient patient : patients) {
                contentValues.put("name", patient.getPatientname());
                contentValues.put("bedno", patient.getBedno());
                writableDatabase.insertOrThrow(TABLE_PATIENT, null, contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    /**
     *
     * @return 查询病人信息
     */
    @Deprecated
    public List<PatientInfo.Patient> queryPatients() {
        LogUtil.d(TAG,"begin to query patient");
        List<PatientInfo.Patient> patients = new ArrayList<>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String sql = String.format("select * from %s ", TABLE_PATIENT);
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                String bedno=cursor.getString(cursor.getColumnIndex("bedno"));
                String name=cursor.getString(cursor.getColumnIndex("name"));
                PatientInfo.Patient patient=new PatientInfo.Patient(bedno,name);
                patients.add(patient);
            }
        } catch (Exception e) {
            LogUtil.d(TAG,"fail to query patient");
        } finally {
            if (cursor != null&&!cursor.isClosed()) {
                cursor.close();
            }
        }
        return patients;
    }

    /**
     * 插入医生信息
     * @param doctors
     */
    @Deprecated
    public void insertDoctors(List<DoctorInfo.Doctor> doctors) {
        LogUtil.d(TAG,"begin to insert doctor info");
        if (doctors == null||doctors.size()==0) {
            LogUtil.d(TAG, "invid doctor info ");
            return;
        }
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.execSQL(String.format(Locale.CHINA,"delete from %s where flag=1",TABLE_DOCTOR));
            ContentValues contentValues = new ContentValues();
            for (DoctorInfo.Doctor doctor : doctors) {
                contentValues.put("name",doctor.getDoctorname());
                contentValues.put("title",doctor.getTitle());
                contentValues.put("img",doctor.getImg());
                contentValues.put("flag",1);
                writableDatabase.insertOrThrow(TABLE_DOCTOR, null, contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e(TAG,"fail to insert doctor info");
        } finally {
            writableDatabase.endTransaction();
        }
    } /**

     * 插入护士信息
     * @param nurses
     */
    @Deprecated
    public void insertNurses(List<NurseInfo.Nurse> nurses) {
        LogUtil.d(TAG,"begin to insert nurses info");
        if (nurses == null||nurses.size()==0) {
            LogUtil.d(TAG, "invid doctor info ");
            return;
        }
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.execSQL(String.format(Locale.CHINA,"delete from %s where flag=2",TABLE_DOCTOR));
            ContentValues contentValues = new ContentValues();
            for (NurseInfo.Nurse nurse : nurses) {
                contentValues.put("name",nurse.getNursename());
                contentValues.put("title",nurse.getTitle());
                contentValues.put("img",nurse.getImg());
                contentValues.put("flag",2);
                writableDatabase.insertOrThrow(TABLE_DOCTOR, null, contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e(TAG,"fail to insert nurse info");
        } finally {
            writableDatabase.endTransaction();
        }
    }

    /**
     * 查询医护信息
     * @return
     */
    @Deprecated
    public List<DoctorInfo.Doctor> queryDoctor() {
        LogUtil.d(TAG,"begin to query doctor info");
        List<DoctorInfo.Doctor> doctors = new ArrayList<>();
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String sql = String.format(Locale.CHINA, "select * from %s", TABLE_DOCTOR);
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String img = cursor.getString(cursor.getColumnIndex("img"));
                DoctorInfo.Doctor doctor = new DoctorInfo.Doctor(name, title, img);
                doctors.add(doctor);
            }
        } catch (Exception e) {
            LogUtil.e(TAG,"fail to getTitle doctor info");
        } finally {
            if (cursor != null&&!cursor.isClosed()) {
                cursor.close();
            }
        }
        return doctors;
    }

}
