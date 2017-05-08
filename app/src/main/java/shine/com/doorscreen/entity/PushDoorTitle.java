package shine.com.doorscreen.entity;

/**
 * Created by Administrator on 2016/9/21.
 */
public class PushDoorTitle {

    /**
     * action : pushdoortitleinfo
     * sender : server
     * hospitalname : 第一人民医院
     * departname : 心内科
     * week : 星期二
     * date : 2016年08月02日
     * time : 14:23
     * weather : 晴
     * weatherpic :
     * temperature : 26
     * watchtime : {"morning":"07:00~08:00","noon":"11:00~13:00","night":"17:00~22:00"}
     */

    private String action;
    private String sender;
    private String hospitalname;
    private String departname;
    private String week;
    private String date;
    private String time;
    private String weather;
    private String weatherpic;
    private String temperature;
    /**
     * morning : 07:00~08:00
     * noon : 11:00~13:00
     * night : 17:00~22:00
     */

    private WatchTime watchtime=new WatchTime();

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getHospitalname() {
        return hospitalname;
    }

    public void setHospitalname(String hospitalname) {
        this.hospitalname = hospitalname;
    }

    public String getDepartname() {
        return departname;
    }

    public void setDepartname(String departname) {
        this.departname = departname;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeatherpic() {
        return weatherpic;
    }

    public void setWeatherpic(String weatherpic) {
        this.weatherpic = weatherpic;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public WatchTime getWatchtime() {
        return watchtime;
    }

    public void setWatchtime(WatchTime watchtime) {
        this.watchtime = watchtime;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PushDoorTitle)){
            return false;
        }
        PushDoorTitle title= (PushDoorTitle) obj;
        String morning = title.getWatchtime().getMorning();
        String noon = title.getWatchtime().getNoon();
        String night = title.getWatchtime().getNight();
        String departname = title.getDepartname();
//        Log.d("PushDoorTitle", morning+"----"+this.getWatchtime().getMorning());
//        Log.d("PushDoorTitle", noon+"----"+this.getWatchtime().getNoon());
//        Log.d("PushDoorTitle", night+"----"+this.getWatchtime().getNight());
//        Log.d("PushDoorTitle", departname+"----"+this.departname);
        return departname.equals(this.departname)&&
                morning.equals(this.getWatchtime().getMorning())&&
                noon.equals(this.getWatchtime().getNoon())&&
                night.equals(this.getWatchtime().getNight());

    }

    public static class WatchTime {
        private String morning;
        private String noon;
        private String night;

        public String getMorning() {
            return morning;
        }

        public void setMorning(String morning) {
            this.morning = morning;
        }

        public String getNoon() {
            return noon;
        }

        public void setNoon(String noon) {
            this.noon = noon;
        }

        public String getNight() {
            return night;
        }

        public void setNight(String night) {
            this.night = night;
        }

        @Override
        public String toString() {
            return "WatchTime{" +
                    "morning='" + morning + '\'' +
                    ", noon='" + noon + '\'' +
                    ", night='" + night + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PushDoorTitle{" +
                "action='" + action + '\'' +
                ", sender='" + sender + '\'' +
                ", hospitalname='" + hospitalname + '\'' +
                ", departname='" + departname + '\'' +
                ", week='" + week + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", weather='" + weather + '\'' +
                ", weatherpic='" + weatherpic + '\'' +
                ", temperature='" + temperature + '\'' +
                ", watchtime=" + watchtime +
                '}';
    }
}
